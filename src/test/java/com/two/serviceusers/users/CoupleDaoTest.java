package com.two.serviceusers.users;

import com.two.http_api.model.User;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.CoupleRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.generated.Tables.COUPLE;
import static org.jooq.generated.Tables.USER;

@ExtendWith(SpringExtension.class)
@JooqTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
class CoupleDaoTest {

    private final Flyway flyway;
    private final DSLContext ctx;
    private final CoupleDao coupleDao;
    private final UserDao userDao;

    @Autowired
    CoupleDaoTest(Flyway flyway, DSLContext ctx) {
        this.flyway = flyway;
        this.ctx = ctx;
        this.coupleDao = new CoupleDao(ctx, new UserMapper());
        this.userDao = new UserDao(ctx, new UserMapper());
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Nested
    class StoreCouple {
        @Test
        @DisplayName("it should correctly store the UID and PID, generating a CID")
        void generatesCID() {
            int uid = 3, pid = 4;

            int cid = coupleDao.storeCouple(uid, pid);

            assertThat(cid).isEqualTo(1);

            Optional<CoupleRecord> coupleRecord = ctx.selectFrom(COUPLE)
                    .where(COUPLE.CID.eq(cid))
                    .fetchOptional();

            assertThat(coupleRecord).isPresent();
            assertThat(coupleRecord.get().getUid()).isEqualTo(uid);
            assertThat(coupleRecord.get().getPid()).isEqualTo(pid);
        }

        @Test
        @DisplayName("it should auto increment the CIDs")
        void autoIncrementsCIDs() {
            int firstCid = coupleDao.storeCouple(1, 2);
            int secondCid = coupleDao.storeCouple(3, 4);

            assertThat(firstCid + 1).isEqualTo(secondCid);
        }

        @Test
        @DisplayName("it should store the creation time")
        void storeCreationTime() {
            Instant oneSecondBefore = Instant.now().minusSeconds(1);

            int cid = coupleDao.storeCouple(1, 2);
            Instant connectedAt = ctx.select(COUPLE.CONNECTED_AT).from(COUPLE).where(COUPLE.CID.eq(cid))
                    .fetchOne().value1().toInstant();

            Instant oneSecondAfter = Instant.now().plusSeconds(1);

            assertThat(connectedAt).isBetween(oneSecondBefore, oneSecondAfter);
        }
    }

    private int createUser(int uidx) {
        return userDao.storeUser(
                new UserRegistration("gerry" + uidx + "@two.com", "testPass", "Gerry", LocalDate.parse("1997-08-21"))
        ).getUid();
    }

    @Nested
    class SetUsersPartner {
        @Test
        @DisplayName("it should update the existing records pid and cid")
        void updatesExistingRecord() {
            int uid = createUser(1), pid = createUser(2);
            int cid = coupleDao.storeCouple(uid, pid);

            User user = coupleDao.connectUserToPartner(uid, pid, cid);
            User partner = coupleDao.connectUserToPartner(pid, uid, cid);

            assertThat(user.getPid()).isEqualTo(pid);
            assertThat(user.getCid()).isEqualTo(cid);

            assertThat(partner.getPid()).isEqualTo(uid);
            assertThat(partner.getCid()).isEqualTo(cid);
        }

        @Test
        @DisplayName("it should not create any additional rows, making the change in place")
        void changeInPlace() {
            int uid = createUser(1), pid = createUser(2);
            int cid = coupleDao.storeCouple(uid, pid);
            int previousNumOfUsers = ctx.selectCount().from(USER).fetchOne().value1();

            // cause the update
            coupleDao.connectUserToPartner(uid, pid, cid);
            coupleDao.connectUserToPartner(pid, uid, cid);

            int currentNumOfUsers = ctx.selectCount().from(USER).fetchOne().value1();
            assertThat(previousNumOfUsers).isEqualTo(currentNumOfUsers);
        }
    }

}