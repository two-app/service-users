package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import com.two.serviceusers.authentication.AuthenticationDao;
import dev.testbed.TestBed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static com.two.http_api.model.PublicApiModel.UserRegistration;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private TestBuilder tb;
    private LocalDate dob = LocalDate.parse("1997-08-21");

    @BeforeEach
    void setup() {
        this.tb = new TestBuilder();
    }

    @Nested
    class StoreUser {
        private UserRegistration userRegistration = new UserRegistration(
                "gerry@two.com", "test-password", "Gerry", dob
        );

        private User user = new User(99, null, null, "gerry@two.com", dob, "Gerry");

        @Test
        @DisplayName("it should store the user via the DAO")
        void storesUser() {
            tb.build().storeUser(userRegistration);

            verify(tb.getDependency(UserDao.class)).storeUser(userRegistration);
        }

        @Test
        @DisplayName("it should store the user credentials via the DAO")
        void storesCredentials() {
            tb.whenStoreUserReturn(user).build().storeUser(userRegistration);

            User.WithCredentials userWithCredentials = new User.WithCredentials(user, userRegistration.getPassword());

            verify(tb.getDependency(AuthenticationDao.class)).storeCredentials(userWithCredentials);
        }

        @Test
        @DisplayName("it should return the tokens generated by the DAO")
        void returnsTokens() {
            Tokens tokens = new Tokens("refresh-token", "access-token");
            UserService userService = tb.whenStoreUserReturn(user).whenStoreCredentialsReturn(tokens).build();

            Tokens generatedTokens = userService.storeUser(userRegistration);

            assertThat(generatedTokens).isEqualTo(tokens);
        }

        @Test
        @DisplayName("it should throw a Bad Requset Exception if the DAO raises a DuplicateKeyException")
        void throwsBadRequestException() {
            UserService userService = tb.whenStoreUserThrowDuplicateKeyException().build();

            assertThatThrownBy(() -> userService.storeUser(userRegistration))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("This user already exists.")
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    class LoginUser {

        User user = new User(12, 13, 14, "gerry@two.com", dob, "Gerry");

        @Test
        @DisplayName("it should retrieve the user")
        void retrievesUser() {
            UserService userService = tb.whenGetUserReturn(of(user)).build();

            userService.loginUser("gerry@two.com", "rawPassword");

            verify(tb.getDependency(UserDao.class)).getUser("gerry@two.com");
        }

        @Test
        @DisplayName("it should throw a Bad Request Response Status Exception if the user does not exist")
        void throwsBadRequestResponseStatusException() {
            UserService userService = tb.whenGetUserReturn(empty()).build();

            assertThatThrownBy(() -> userService.loginUser("gerry@two.com", "rawPassword"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("This user does not exist.")
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("it should authenticate with the UID and raw password")
        void authenticates() {
            UserService userService = tb.whenGetUserReturn(of(user)).build();

            userService.loginUser("gerry@two.com", "rawPassword");

            verify(tb.getDependency(AuthenticationDao.class)).authenticateAndCreateTokens(
                    new User.WithCredentials(user, "rawPassword")
            );
        }

        @Test
        @DisplayName("it should return the generated tokens on a successful request")
        void returnsTokens() {
            Tokens tokens = new Tokens("refresh", "access");
            UserService userService = tb.whenGetUserReturn(of(user))
                    .whenAuthenticateAndCreateTokensReturn(tokens)
                    .build();

            Tokens createdTokens = userService.loginUser("gerry@two.com", "rawPassword");

            assertThat(createdTokens).isEqualTo(tokens);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    class TestBuilder extends TestBed<UserService, TestBuilder> {
        TestBuilder() {
            super(UserService.class);
        }

        TestBuilder whenStoreUserReturn(User user) {
            when(getDependency(UserDao.class).storeUser(any(UserRegistration.class))).thenReturn(user);
            return this;
        }

        TestBuilder whenStoreUserThrowDuplicateKeyException() {
            when(getDependency(UserDao.class).storeUser(any(UserRegistration.class)))
                    .thenThrow(DuplicateKeyException.class);
            return this;
        }

        TestBuilder whenStoreCredentialsReturn(Tokens tokens) {
            when(getDependency(AuthenticationDao.class).storeCredentials(any(User.WithCredentials.class))).thenReturn(tokens);
            return this;
        }

        TestBuilder whenGetUserReturn(Optional<User> userOptional) {
            when(getDependency(UserDao.class).getUser(anyString())).thenReturn(userOptional);
            return this;
        }

        TestBuilder whenAuthenticateAndCreateTokensReturn(Tokens tokens) {
            when(getDependency(AuthenticationDao.class).authenticateAndCreateTokens(any(User.WithCredentials.class)))
                    .thenReturn(tokens);
            return this;
        }
    }
}