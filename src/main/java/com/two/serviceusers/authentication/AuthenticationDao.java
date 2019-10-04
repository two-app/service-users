package com.two.serviceusers.authentication;

import com.two.http_api.api.AuthenticationServiceApi;
import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AuthenticationDao {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDao.class);
    private final AuthenticationServiceApi authenticationServiceApi;

    /**
     * @return JWTs from the Authentication Service if the storage was successful.
     * TODO: Some error handling for WebClient ResponseException
     */
    public Tokens storeCredentials(int uid, String password) {
        logger.info("Messaging the Authentication Service to store the users credentials.");
        return this.authenticationServiceApi.storeCredentialsAndGenerateTokens(
                new User.Credentials(uid, password)
        );
    }

}
