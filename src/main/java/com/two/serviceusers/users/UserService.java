package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * @return a pair of JSON web tokens.
     */
    public Tokens storeUser(UserRegistration userRegistration) {
        int uid = this.userDao.storeUser(userRegistration);
        return this.userDao.storeCredentials(uid, userRegistration.getPassword());
    }

}
