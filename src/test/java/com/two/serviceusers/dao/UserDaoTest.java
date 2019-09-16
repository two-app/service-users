package com.two.serviceusers.dao;

import com.two.http_api.model.User;
import com.two.serviceusers.exceptions.UserExistsException;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@JooqTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
class UsersDaoTest {

    private final Flyway flyway;
    private final UserDao usersDao;

    @Autowired
    public UsersDaoTest(Flyway flyway, DSLContext dslContext) {
        this.flyway = flyway;
        this.usersDao = new UserDao(dslContext);
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Nested
    class CreateUser {
        @Test
        @DisplayName("it should store the user")
        void userStored() {
            int uid = usersDao.createUser("gerry@two.com", "Gerry", 22);

            Optional<User> user = usersDao.getUser("gerry@two.com");

            assertThat(user).isPresent().contains(new User(uid, null, null,  "gerry@two.com", 22, "Gerry"));
        }

        @Test
        @DisplayName("it should throw an exception if the user exists")
        void uniqueConstraintBrokenExcepton() {
            usersDao.createUser("gerry@two.com", "Gerry", 22);

            assertThatThrownBy(() -> usersDao.createUser("gerry@two.com", "Gerry", 22))
                    .isInstanceOf(UserExistsException.class)
                    .hasMessageContaining("An account with the email 'gerry@two.com' already exists.");
        }
    }

    @Nested
    class GetUser {
        @Test
        @DisplayName("it should return an empty optional for a non existent user")
        void notExists_EmptyOptional() {
            Optional<User> user = usersDao.getUser("unknown@unknown.com");

            assertThat(user).isEmpty();
        }
    }

}