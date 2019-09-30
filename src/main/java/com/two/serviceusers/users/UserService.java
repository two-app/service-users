package com.two.serviceusers.users;

import com.two.http_api.model.Tokens;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;

    /**
     * @return a pair of JSON web tokens.
     */
    public Tokens storeUser(UserRegistration userRegistration) {
        int uid = this.userDao.storeUser(userRegistration);
        return this.userDao.storeCredentials(uid, userRegistration.getPassword());
    }

}
