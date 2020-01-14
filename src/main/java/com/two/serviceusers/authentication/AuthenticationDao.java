package com.two.serviceusers.authentication;

import com.two.http_api.api.AuthenticationServiceApi;
import com.two.http_api.exceptions.PropagateHttpResponseException;
import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import com.two.http_api.model.UserWithCredentials;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Repository
@AllArgsConstructor
public class AuthenticationDao {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDao.class);
    private final AuthenticationServiceApi authenticationServiceApi;

    /**
     * @return JWTs from the Authentication Service if the storage was successful.
     */
    public Tokens storeCredentials(UserWithCredentials cr) {
        try {
            logger.info("Messaging the Authentication Service to store the users credentials, and generate tokens.");
            return this.authenticationServiceApi.storeCredentialsAndGenerateTokens(cr);
        } catch (WebClientResponseException e) {
            logger.error("Failed to store user credentials or generate tokens in authentication service.");
            logger.error("Potential data-mismatch between users service and authentication service.");
            logger.error("Response for UID {}.", cr.getUid(), e);
            throw new PropagateHttpResponseException(e);
        }
    }

    /**
     * @return JWTs from the Authentication Service if the authentication was successful.
     */
    public Tokens authenticateAndCreateTokens(UserWithCredentials cr) {
        try {
            logger.info("Messaging the Authentication Service to authenticate UID {}, and generate tokens.", cr.getUid());
            return this.authenticationServiceApi.authenticateCredentialsAndGenerateTokens(cr);
        } catch (WebClientResponseException e) {
            logger.warn("Failed to authenticate user {}.", cr.getUid());
            throw new PropagateHttpResponseException(e);
        }
    }

    /**
     * @return JWTs from the Authentication Service.
     */
    public Tokens getTokens(User user) {
        try {
            logger.info("Messaging the Authentication Service to generate tokens for UID {}.", user.getUid());
            return this.authenticationServiceApi.getToken(user);
        } catch (WebClientResponseException e) {
            logger.error("Failed to generate tokens in authentication service.");
            throw new PropagateHttpResponseException(e);
        }
    }
}
