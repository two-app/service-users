package com.two.serviceusers.users;

import com.two.http_api.model.Partner;
import com.two.http_api.model.PublicApiModel.UserRegistration;
import com.two.http_api.model.Self;
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

import java.time.Instant;
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
    private UserRegistration userRegistration;

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
        userRegistration = new UserRegistration(
                "gerry@two.com", "password", "Gerry", "Fletcher", true, true
        );
    }

    @Autowired
    public UserDaoTest(Flyway flyway, DSLContext ctx) {
        this.flyway = flyway;
        this.ctx = ctx;
        this.userDao = new UserDao(ctx);
    }

    @Nested
    class StoreUser {
        @Test
        @DisplayName("it should store the user")
        void userStored() {
            User storedUser = userDao.storeUser(userRegistration);

            Optional<User> retrievedUser = userDao.getUser(userRegistration.getEmail(), User.class);

            assertThat(retrievedUser).isPresent().contains(storedUser);
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
            int firstUID = userDao.storeUser(userRegistration).getUid();
            userRegistration.setEmail("differentEmail@two.com");
            int secondUID = userDao.storeUser(userRegistration).getUid();

            assertThat(secondUID).isEqualTo(firstUID + 1);
        }

        @Test
        @DisplayName("it should store the creation time")
        void storesCreationTime() {
            Instant oneSecondBefore = Instant.now().minusSeconds(1);

            int uid = userDao.storeUser(userRegistration).getUid();
            Instant createdAt = ctx.select(USER.CREATED_AT).from(USER).where(USER.UID.eq(uid))
                    .fetchOne().value1().toInstant();

            Instant oneSecondAfter = Instant.now().plusSeconds(1);

            assertThat(createdAt).isBetween(oneSecondBefore, oneSecondAfter);
        }
    }

    @Nested
    class GetUser {
        @Nested
        class ByEmail {
            @Test
            @DisplayName("it should return the created user")
            void returnsCreatedUser() {
                int uid = userDao.storeUser(userRegistration).getUid();

                Optional<User> userOptional = userDao.getUser(userRegistration.getEmail(), User.class);

                assertThat(userOptional).isPresent().contains(
                        User.builder().uid(uid).pid(null).cid(null)
                                .firstName(userRegistration.getFirstName())
                                .lastName(userRegistration.getLastName())
                                .build()
                );
            }

            @Test
            @DisplayName("it should return the created user mapped as Partner")
            void mapsToPartner() {
                int uid = userDao.storeUser(userRegistration).getUid();

                Optional<Partner> userOptional = userDao.getUser(userRegistration.getEmail(), Partner.class);

                assertThat(userOptional).isPresent().contains(
                        Partner.builder().uid(uid).pid(null).cid(null)
                                .firstName(userRegistration.getFirstName())
                                .lastName(userRegistration.getLastName())
                                .build()
                );
            }

            @Test
            @DisplayName("it should return the created user mapped as Self")
            void mapsToSelf() {
                int uid = userDao.storeUser(userRegistration).getUid();

                Optional<Self> userOptional = userDao.getUser(userRegistration.getEmail(), Self.class);

                assertThat(userOptional).isPresent().contains(
                        Self.builder().uid(uid).pid(null).cid(null)
                                .firstName(userRegistration.getFirstName())
                                .lastName(userRegistration.getLastName())
                                .email(userRegistration.getEmail())
                                .build()
                );
            }

            @Test
            @DisplayName("it should return an empty optional for an unknown user")
            void unknownUser() {
                Optional<User> userOptional = userDao.getUser(userRegistration.getEmail(), User.class);

                assertThat(userOptional).isNotPresent();
            }
        }

        @Nested
        class ByUid {
            @Test
            @DisplayName("it should return the created user")
            void returnsCreatedUser() {
                int uid = userDao.storeUser(userRegistration).getUid();

                Optional<User> userOptional = userDao.getUser(uid, User.class);

                assertThat(userOptional).isPresent().contains(
                        User.builder().uid(uid).pid(null).cid(null)
                                .firstName(userRegistration.getFirstName())
                                .lastName(userRegistration.getLastName())
                                .build()
                );
            }

            @Test
            @DisplayName("it should return the created user mapped as Partner")
            void mapsToPartner() {
                int uid = userDao.storeUser(userRegistration).getUid();

                Optional<Partner> partnerOptional = userDao.getUser(uid, Partner.class);

                assertThat(partnerOptional).isPresent().contains(
                        Partner.builder().uid(uid).pid(null).cid(null)
                                .firstName(userRegistration.getFirstName())
                                .lastName(userRegistration.getLastName())
                                .build()
                );
            }

            @Test
            @DisplayName("it should return the created user mapped as Self")
            void mapsToSelf() {
                int uid = userDao.storeUser(userRegistration).getUid();

                Optional<Self> selfOptional = userDao.getUser(uid, Self.class);

                assertThat(selfOptional).isPresent().contains(
                        Self.builder().uid(uid).pid(null).cid(null)
                                .firstName(userRegistration.getFirstName())
                                .lastName(userRegistration.getLastName())
                                .email(userRegistration.getEmail())
                                .build()
                );
            }

            @Test
            @DisplayName("it should return an empty optional for an unknown user")
            void unknownUser() {
                Optional<User> userOptional = userDao.getUser(22, User.class);

                assertThat(userOptional).isNotPresent();
            }
        }
    }

}