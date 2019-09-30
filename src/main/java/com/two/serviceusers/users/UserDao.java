package com.two.serviceusers.users;

import com.two.http_api.api.AuthenticationServiceApi;
import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UserRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import static org.jooq.generated.Tables.USER;

@Repository
@AllArgsConstructor
public class UserDao {

    private final DSLContext ctx;
    private final AuthenticationServiceApi authenticationServiceApi;

    /**
     * @return the created users ID.
     * @exception DuplicateKeyException if the email exists in the users table.
     */
    int storeUser(UserRegistration userRegistration) throws DuplicateKeyException {
        UserRecord userRecord = ctx.newRecord(USER);

        userRecord.setEmail(userRegistration.getEmail());
        userRecord.setName(userRegistration.getName());
        userRecord.setAge(userRegistration.getAge());

        userRecord.store();
        userRecord.refresh();

        return userRecord.getUid();
    }

    /**
     * @return JWTs from the Authentication Service if the storage was successful.
     * TODO: Some error handling for WebClient ResponseException
     */
    Tokens storeCredentials(int uid, String password) {
        return this.authenticationServiceApi.storeCredentialsAndGenerateTokens(
                new User.Credentials(uid, password)
        );
    }


}
