package com.two.serviceusers.users;

import com.two.http_api.api.AuthenticationServiceApi;
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
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@JooqTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
class UserDaoTest {

    private final Flyway flyway;
    private final DSLContext ctx;
    private final AuthenticationServiceApi authenticationServiceApi;
    private final UserDao usersDao;

    @Autowired
    public UserDaoTest(Flyway flyway, DSLContext ctx) {
        this.flyway = flyway;
        this.ctx = ctx;
        this.authenticationServiceApi = mock(AuthenticationServiceApi.class);
        this.usersDao = new UserDao(this.ctx, this.authenticationServiceApi);
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Nested
    class StoreUser {

        UserRegistration userRegistration = new UserRegistration("gerry@two.com", "password", "Gerry", 22);

        @Test
        @DisplayName("it should store the user")
        void userStored() {
            int uid = usersDao.storeUser(userRegistration);

            Optional<User> user = ctx.selectFrom(USER).where(USER.EMAIL.eq("gerry@two.com")).fetchOptional().map(
                    u -> new User(u.getUid(), u.getPid(), u.getCid(), u.getEmail(), u.getAge(), u.getName())
            );

            assertThat(user).isPresent().contains(new User(uid, null, null, "gerry@two.com", 22, "Gerry"));
        }

        @Test
        @DisplayName("it should throw an exception if the user exists")
        void uniqueConstraintBrokenException() {
            usersDao.storeUser(userRegistration);

            assertThatThrownBy(() -> usersDao.storeUser(userRegistration))
                    .isInstanceOf(DuplicateKeyException.class)
                    .hasMessageContaining("Duplicate entry 'gerry@two.com'");
        }
    }

}