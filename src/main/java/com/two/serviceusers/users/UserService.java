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
            logger.info("Storing user details.");
            int uid = this.userDao.storeUser(userRegistration);

            logger.info("Storing user credentials.");
            return this.authenticationDao.storeCredentials(new User.Credentials(uid, userRegistration.getPassword()));
        } catch (DuplicateKeyException e) {
            logger.warn("The user already exists.", e);
            throw new UserExistsException(userRegistration.getEmail());
        }
    }

    public Tokens loginUser(User.Credentials credentials) throws ResponseStatusException {
        logger.info("Retrieving user details.");
        Optional<User> optionalUser = this.userDao.getUser(credentials.getUid());

        if (optionalUser.isEmpty()) {
            logger.info("The UID does not exist.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user does not exist.");
        }

        logger.info("Successfully retrieved user.");
        User user = optionalUser.get();

        logger.info("Verifying user credentials with email {}.", user.getEmail());
        return this.authenticationDao.authenticateAndCreateTokens(
                new User.Credentials(user.getUid(), credentials.getRawPassword())
        );
    }

}
