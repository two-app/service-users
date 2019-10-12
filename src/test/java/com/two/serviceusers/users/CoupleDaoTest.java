package com.two.serviceusers.users;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.generated.Tables.COUPLE;

@ExtendWith(SpringExtension.class)
@JooqTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
class CoupleDaoTest {

    private final Flyway flyway;
    private final DSLContext ctx;
    private final CoupleDao coupleDao;

    @Autowired
    CoupleDaoTest(Flyway flyway, DSLContext ctx) {
        this.flyway = flyway;
        this.ctx = ctx;
        this.coupleDao = new CoupleDao(ctx);
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

}