package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import com.two.serviceusers.authentication.AuthenticationDao;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


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
            return this.authenticationDao.storeCredentials(uid, userRegistration.getPassword());
        } catch (DuplicateKeyException e) {
            logger.warn("The user already exists.", e);
            throw new UserExistsException(userRegistration.getEmail());
        }
    }

}
