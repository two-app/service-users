package com.two.serviceusers.users;

import com.two.http_api.api.AuthenticationServiceApi;
import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.generated.Tables.USER;

@Repository
public class UserDao {

    private final DSLContext ctx;
    private final AuthenticationServiceApi authenticationServiceApi;

    @Autowired
    public UserDao(DSLContext dslContext, AuthenticationServiceApi authenticationServiceApi) {
        this.ctx = dslContext;
        this.authenticationServiceApi = authenticationServiceApi;
    }

    /**
     * @return the created users ID.
     * @throws UserExistsException if the email exists in the users table.
     */
    int storeUser(UserRegistration userRegistration) {
        String email = userRegistration.getEmail();
        if (ctx.fetchExists(ctx.selectFrom(USER).where(USER.EMAIL.eq(email)))) {
            throw new UserExistsException(email);
        }

        UserRecord userRecord = ctx.newRecord(USER);

        userRecord.setEmail(email);
        userRecord.setName(userRegistration.getName());
        userRecord.setAge(userRegistration.getAge());

        userRecord.store();
        userRecord.refresh();

        return userRecord.getUid();
    }

    /**
     * @return JWTs from the Authentication Service if the storage was successful.
     * TODO: Some error handling for WebClientResponseException
     */
    Tokens storeCredentials(int uid, String password) {
        return this.authenticationServiceApi.storeCredentialsAndGenerateTokens(
                new User.Credentials(uid, password)
        );
    }


}
