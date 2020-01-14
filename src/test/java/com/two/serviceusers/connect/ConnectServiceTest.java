package com.two.serviceusers.connect;

import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import com.two.serviceusers.authentication.AuthenticationDao;
import com.two.serviceusers.users.CoupleDao;
import com.two.serviceusers.users.UserDao;
import com.two.serviceusers.users.UserNotExistsException;
import dev.testbed.TestBed;
import org.hashids.Hashids;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ConnectServiceTest {

    private TestBuilder tb;
    private User user = User.builder().uid(1).pid(null).cid(null).firstName("Two").lastName("TwoL").build();
    private User partner = User.builder().uid(2).pid(null).cid(null).firstName("One").lastName("OneL").build();
    private User connectedUser = User.builder().uid(1).pid(2).cid(3).firstName("Two").lastName("TwoL").build();
    private Tokens tokens = new Tokens("refresh-token", "access-token");

    @BeforeEach
    void beforeEach() {
        tb = new TestBuilder();
    }

    @Test
    @DisplayName("it should return new tokens for a valid connection")
    void tokensReturned() {
        Tokens tokens = new Tokens("refresh-token", "access-token");
        ConnectService connectService = tb.valid().build();

        Tokens newTokens = connectService.connectUsers(user.getUid(), "testConnectCode");

        assertThat(newTokens).isEqualTo(tokens);
    }

    @Test
    @DisplayName("it should decode the partner connect code")
    void decodesPartnerConnectCode() {
        ConnectService connectService = tb.valid().build();

        connectService.connectUsers(user.getUid(), "testConnectCode");

        verify(tb.getDependency(Hashids.class)).decode("testConnectCode");
    }

    @Test
    @DisplayName("it should retrieve the users for both uid and pid")
    void retrievesUserForUidAndPid() {
        ConnectService connectService = tb.valid().build();

        connectService.connectUsers(user.getUid(), "testConnectCode");

        verify(tb.getDependency(UserDao.class)).getUser(user.getUid(), User.class);
        verify(tb.getDependency(UserDao.class)).getUser(partner.getUid(), User.class);
    }

    @Test
    @DisplayName("it should store the uid and pid as a couple")
    void storesCouple() {
        ConnectService connectService = tb.valid().build();

        connectService.connectUsers(user.getUid(), "testConnectCode");

        verify(tb.getDependency(CoupleDao.class)).storeCouple(user.getUid(), partner.getUid());
    }

    @Test
    @DisplayName("it should connect the user to partner and vice versa")
    void connectsUserAndPartner() {
        ConnectService connectService = tb.valid().build();

        connectService.connectUsers(user.getUid(), "testConnectCode");

        verify(tb.getDependency(CoupleDao.class)).connectUserToPartner(
                user.getUid(), partner.getUid(), connectedUser.getCid()
        );

        verify(tb.getDependency(CoupleDao.class)).connectUserToPartner(
                partner.getUid(), user.getUid(), connectedUser.getCid()
        );
    }

    @Test
    @DisplayName("it should throw a Bad Request exception if the user does not exist")
    void userDoesNotExist() {
        ConnectService connectService = tb.valid().whenGetUserReturn(user.getUid(), empty()).build();

        assertThatThrownBy(() -> connectService.connectUsers(user.getUid(), "testConnectCode"))
                .isInstanceOf(UserNotExistsException.class);
    }

    @Test
    @DisplayName("it should throw a Bad Request exception if the partner does not exist")
    void partnerDoesNotExist() {
        ConnectService connectService = tb.valid().whenGetUserReturn(partner.getUid(), empty()).build();

        assertThatThrownBy(() -> connectService.connectUsers(user.getUid(), "testConnectCode"))
                .isInstanceOf(UserNotExistsException.class);
    }

    @Test
    @DisplayName("it should throw a Bad Request exception if the user already has a partner")
    void userHasPartner() {
        ConnectService connectService = tb.valid().whenGetUserReturn(user.getUid(), of(connectedUser))
                .build();

        assertThatThrownBy(() -> connectService.connectUsers(user.getUid(), "testConnectCode"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You are already connected to a partner.")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("it should throw a Bad Request exception if the partner already has a partner")
    void partnerHasPartner() {
        User connectedPartner = connectedUser.toBuilder().uid(2).build();
        ConnectService connectService = tb.valid().whenGetUserReturn(partner.getUid(), of(connectedPartner)).build();

        assertThatThrownBy(() -> connectService.connectUsers(user.getUid(), "testConnectCode"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("This user is already connected.")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }

    class TestBuilder extends TestBed<ConnectService, TestBuilder> {
        TestBuilder() {
            super(ConnectService.class);
        }

        TestBuilder valid() {
            return this.whenDecodeConnectCodeReturn(partner.getUid())
                    .whenGetUserReturn(user.getUid(), of(user))
                    .whenGetUserReturn(partner.getUid(), of(partner))
                    .whenStoreCoupleReturnCID(connectedUser.getCid())
                    .whenConnectUserReturn(user.getUid(), partner.getUid(), connectedUser.getCid(), connectedUser)
                    .whenGetTokensReturn(tokens);
        }

        TestBuilder whenDecodeConnectCodeReturn(int pid) {
            when(getDependency(Hashids.class).decode(anyString())).thenReturn(new long[]{pid});
            return this;
        }

        TestBuilder whenGetUserReturn(int uid, Optional<User> optionalUser) {
            when(getDependency(UserDao.class).getUser(uid, User.class)).thenReturn(optionalUser);
            return this;
        }

        TestBuilder whenStoreCoupleReturnCID(int cid) {
            when(getDependency(CoupleDao.class).storeCouple(anyInt(), anyInt())).thenReturn(cid);
            return this;
        }

        TestBuilder whenConnectUserReturn(int uid, int pid, int cid, User user) {
            when(getDependency(CoupleDao.class).connectUserToPartner(uid, pid, cid)).thenReturn(user);
            return this;
        }

        TestBuilder whenGetTokensReturn(Tokens tokens) {
            when(getDependency(AuthenticationDao.class).getTokens(any(User.class))).thenReturn(tokens);
            return this;
        }
    }
}