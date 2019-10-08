package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import com.two.serviceusers.authentication.AuthenticationDao;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final AuthenticationDao authenticationDao;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * @return a pair of JSON web tokens.
     */
    public Tokens storeUser(UserRegistration userRegistration) {
        try {
            logger.info("Storing user registration details.");
            User user = this.userDao.storeUser(userRegistration);

            logger.info("Storing user credentials.");
            return this.authenticationDao.storeCredentials(
                    new User.WithCredentials(user, userRegistration.getPassword())
            );
        } catch (DuplicateKeyException e) {
            logger.warn("The user already exists.", e);
            throw new UserExistsException(userRegistration.getEmail());
        }
    }

    public Tokens loginUser(String email, String rawPassword) throws ResponseStatusException {
        logger.info("Retrieving user details.");
        Optional<User> optionalUser = this.userDao.getUser(email);

        if (optionalUser.isEmpty()) {
            logger.info("The UID does not exist.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user does not exist.");
        }

        logger.info("Successfully retrieved user.");
        User user = optionalUser.get();

        logger.info("Verifying user credentials with email {}.", user.getEmail());
        return this.authenticationDao.authenticateAndCreateTokens(
                new User.WithCredentials(user, rawPassword)
        );
    }

}
