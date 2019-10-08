package com.two.serviceusers.users;

import com.two.http_api.model.User;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jooq.generated.Tables.USER;

@ExtendWith(SpringExtension.class)
@JooqTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
class UserDaoTest {

    private final Flyway flyway;
    private final DSLContext ctx;
    private final UserDao userDao;
    private UserRegistration userRegistration = new UserRegistration(
            "gerry@two.com", "password", "Gerry", 22
    );

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Autowired
    public UserDaoTest(Flyway flyway, DSLContext ctx) {
        this.flyway = flyway;
        this.ctx = ctx;
        this.userDao = new UserDao(this.ctx);
    }

    @Nested
    class StoreUser {


        @Test
        @DisplayName("it should store the user")
        void userStored() {
            int uid = userDao.storeUser(userRegistration);

            Optional<User> user = ctx.selectFrom(USER).where(USER.EMAIL.eq("gerry@two.com")).fetchOptional().map(
                    u -> new User(u.getUid(), u.getPid(), u.getCid(), u.getEmail(), u.getAge(), u.getName())
            );

            assertThat(user).isPresent().contains(new User(uid, null, null, "gerry@two.com", 22, "Gerry"));
        }

        @Test
        @DisplayName("it should throw an exception if the user exists")
        void uniqueConstraintBrokenException() {
            userDao.storeUser(userRegistration);

            assertThatThrownBy(() -> userDao.storeUser(userRegistration))
                    .isInstanceOf(DuplicateKeyException.class)
                    .hasMessageContaining("Duplicate entry 'gerry@two.com'");
        }

        @Test
        @DisplayName("it should auto-increment the UIDs")
        void autoIncrementsUIDs() {
            int firstUID = userDao.storeUser(userRegistration);
            userRegistration.setEmail("differentEmail@two.com");
            int secondUID = userDao.storeUser(userRegistration);

            assertThat(secondUID).isEqualTo(firstUID + 1);
        }
    }

    @Nested
    class GetUser {
        @Test
        @DisplayName("it should return the created user")
        void returnsCreatedUser() {
            int uid = userDao.storeUser(userRegistration);

            Optional<User> userOptional = userDao.getUser("gerry@two.com");

            assertThat(userOptional).isPresent().contains(
                    new User(uid, null, null, "gerry@two.com", 22, "Gerry")
            );
        }

        @Test
        @DisplayName("it should return an empty optional for an unknown user")
        void unknownUser() {
            Optional<User> userOptional = userDao.getUser("unknown@two.com");

            assertThat(userOptional).isNotPresent();
        }
    }

}