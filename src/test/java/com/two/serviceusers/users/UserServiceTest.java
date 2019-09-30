package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import dev.testbed.TestBed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private TestBuilder tb;
    private UserRegistration userRegistration = new UserRegistration("gerry@two.com", "test-password", "Gerry", 22);

    @BeforeEach
    void setup() { this.tb = new TestBuilder(); }

    @Test
    @DisplayName("it should store the user via the DAO")
    void storesUser() {
        tb.build().storeUser(userRegistration);

        verify(tb.getDependency(UserDao.class)).storeUser(userRegistration);
    }

    @Test
    @DisplayName("it should store the user credentials via the DAO")
    void storesCredentials() {
        tb.whenStoreUserReturn(99).build().storeUser(userRegistration);

        verify(tb.getDependency(UserDao.class)).storeCredentials(99, userRegistration.getPassword());
    }

    @Test
    @DisplayName("it should return the tokens generated by the DAO")
    void returnsTokens() {
        Tokens tokens = new Tokens("refresh-token", "access-token");
        UserService userService = tb.whenStoreUserReturn(99).whenStoreCredentialsReturn(tokens).build();

        Tokens generatedTokens = userService.storeUser(userRegistration);

        assertThat(generatedTokens).isEqualTo(tokens);
    }

    @Test
    @DisplayName("it should throw a UserExistsException if the DAO raises a DuplicateKeyException")
    void throwsUserExistsException() {
        UserService userService = tb.whenStoreUserThrowDuplicateKeyException().build();

        assertThatThrownBy(() -> userService.storeUser(userRegistration))
                .isInstanceOf(UserExistsException.class)
                .hasMessageContaining("An account with the email 'gerry@two.com' already exists.");
    }

    class TestBuilder extends TestBed<UserService, TestBuilder> {
        TestBuilder() { super(UserService.class); }

        TestBuilder whenStoreUserReturn(int userId) {
            when(getDependency(UserDao.class).storeUser(any(UserRegistration.class))).thenReturn(userId);
            return this;
        }

        TestBuilder whenStoreUserThrowDuplicateKeyException() {
            when(getDependency(UserDao.class).storeUser(any(UserRegistration.class)))
                    .thenThrow(DuplicateKeyException.class);
            return this;
        }

        TestBuilder whenStoreCredentialsReturn(Tokens tokens) {
            when(getDependency(UserDao.class).storeCredentials(anyInt(), anyString())).thenReturn(tokens);
            return this;
        }
    }
}