package com.two.serviceusers.users;

import com.two.http_api.model.PublicApiModel.UserRegistration;
import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import com.two.http_api.model.UserWithCredentials;
import com.two.serviceusers.authentication.AuthenticationDao;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final AuthenticationDao authenticationDao;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * @return a pair of JSON web tokens.
     * @throws ResponseStatusException Bad Request if the user already exists.
     */
    Tokens storeUser(UserRegistration userRegistration) {
        try {
            logger.info("Storing user registration details.");
            User user = this.userDao.storeUser(userRegistration);

            logger.info("Storing user credentials.");
            return this.authenticationDao.storeCredentials(
                    UserWithCredentials.fromUser(user, userRegistration.getEmail(), userRegistration.getPassword())
            );
        } catch (DuplicateKeyException e) {
            logger.warn("The user already exists.", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user already exists.");
        }
    }

    public Tokens loginUser(String email, String rawPassword) throws ResponseStatusException {
        logger.info("Retrieving user details.");
        User self = this.getUser(email, User.class);

        logger.info("Verifying user credentials with email {}.", email);
        return this.authenticationDao.authenticateAndCreateTokens(
                UserWithCredentials.fromUser(self, email, rawPassword)
        );
    }

    <T extends User> T getUser(int uid, Class<T> type) throws UserNotExistsException {
        logger.info("Retrieving user by UID {} and into type {}.", uid, type);
        return this.userDao.getUser(uid, type).orElseThrow(UserNotExistsException::new);
    }

    <T extends User> T getUser(String email, Class<T> type) throws UserNotExistsException {
        logger.info("Retrieving user by email {} and into type {}.", email, type);
        return this.userDao.getUser(email, type).orElseThrow(UserNotExistsException::new);
    }

}
