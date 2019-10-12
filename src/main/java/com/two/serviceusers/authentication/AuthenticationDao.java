package com.two.serviceusers.authentication;

import com.two.http_api.api.AuthenticationServiceApi;
import com.two.http_api.exceptions.PropagateHttpResponseException;
import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
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
    public Tokens storeCredentials(User.WithCredentials cr) {
        logger.info("Messaging the Authentication Service to store the users credentials, and generate tokens.");
        try {
            return this.authenticationServiceApi.storeCredentialsAndGenerateTokens(cr);
        } catch (WebClientResponseException e) {
            logger.error("Failed to store user credentials or generate tokens in authentication service.");
            logger.error("Potential data-mismatch between users service and authentication service.");
            logger.error("Response for UID {}.", cr.getUser().getUid(), e);
            throw new PropagateHttpResponseException(e);
        }
    }

    /**
     * @return JWTs from the Authentication Service if the authentication was successful.
     */
    public Tokens authenticateAndCreateTokens(User.WithCredentials cr) {
        logger.info("Messaging the Authentication Service to authenticate UID {}, and generate tokens.", cr.getUser().getUid());
        try {
            return this.authenticationServiceApi.authenticateCredentialsAndGenerateTokens(cr);
        } catch (WebClientResponseException e) {
            logger.warn("Failed to authenticate user {}.", cr.getUser().getUid());
            throw new PropagateHttpResponseException(e);
        }
    }

    /**
     * @return JWTs from the Authentication Service.
     */
    public Tokens getTokens(User user) {
        logger.info("Messaging the Authentication Service to generate tokens for UID {}.", user.getUid());
        try {
            return this.authenticationServiceApi.getToken(user);
        } catch (WebClientResponseException e) {
            logger.error("Failed to generate tokens in authentication service.");
            throw new PropagateHttpResponseException(e);
        }
    }
}
