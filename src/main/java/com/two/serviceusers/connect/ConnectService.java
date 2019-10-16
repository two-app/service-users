package com.two.serviceusers.connect;

import com.two.http_api.model.Tokens;
import com.two.http_api.model.User;
import com.two.serviceusers.authentication.AuthenticationDao;
import com.two.serviceusers.users.CoupleDao;
import com.two.serviceusers.users.UserDao;
import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConnectService {

    private static final Logger logger = LoggerFactory.getLogger(ConnectService.class);
    private final UserDao userDao;
    private final CoupleDao coupleDao;
    private final AuthenticationDao authenticationDao;
    private final Hashids hashIds;

    /**
     * This method operates on the assumption that the uid provided exists.
     *
     * @return fresh access tokens.
     */
    Tokens connectUsers(int uid, String partnerConnectCode) {
        logger.info("Connecting UID {} and partner connect code {}.", uid, partnerConnectCode);
        int pid = (int) hashIds.decode(partnerConnectCode)[0];
        logger.info("Decoded partner connect code to PID {}.", pid);

        User user = getUser(uid), partner = getUser(pid);

        if (user.getPid() != null) {
            logger.error("You are already connected to a partner.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already connected to a partner.");
        }

        if (partner.getPid() != null) {
            logger.warn("Partner {} already has a partner.", pid);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is already connected.");
        }

        logger.info("Connecting the UID and PID.");
        User connectedUser = this.connectUsers(uid, pid);

        logger.info("Generating new tokens for user.");
        return authenticationDao.getTokens(connectedUser);
    }

    /**
     * Creates a new CID and connects both the UID and PID to each other using it.
     *
     * @return a refreshed user, from the perspective of the UID.
     */
    private User connectUsers(int uid, int pid) {
        logger.info("Creating new couple.");
        int cid = coupleDao.storeCouple(uid, pid);
        logger.info("Created new couple with CID {}.", cid);

        logger.info("Connecting UID {} to PID {}.", uid, pid);
        User user = coupleDao.connectUserToPartner(uid, pid, cid);

        logger.info("Connecting PID {} to UID {}.", pid, uid);
        // not necessary to retain a reference to the partner, just that they are now connected
        coupleDao.connectUserToPartner(pid, uid, cid);

        logger.info("Finished connecting user and partner.");
        return user;
    }

    /**
     * @return the user.
     * @throws ResponseStatusException Bad Request if the user does not exist.
     */
    private User getUser(int uid) {
        logger.info("Retrieving user {}.", uid);
        Optional<User> userOptional = userDao.getUser(uid);

        if (userOptional.isEmpty()) {
            logger.warn("User {} does not exist.", uid);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return userOptional.get();
    }

}
