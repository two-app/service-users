package com.two.serviceusers.users;

import com.two.http_api.model.User;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.generated.Tables.USER;

@Repository
@AllArgsConstructor
public class UserDao {

    private final DSLContext ctx;
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    /**
     * @return the created users ID.
     * @exception DuplicateKeyException if the email exists in the users table.
     */
    int storeUser(UserRegistration userRegistration) throws DuplicateKeyException {
        logger.info("Storing user details in DB table 'USER'.");
        UserRecord userRecord = ctx.newRecord(USER);

        userRecord.setEmail(userRegistration.getEmail());
        userRecord.setName(userRegistration.getName());
        userRecord.setAge(userRegistration.getAge());

        userRecord.store();
        userRecord.refresh();

        logger.info("Successfully stored user in DB with generated UID: {}.", userRecord.getUid());
        return userRecord.getUid();
    }

    /**
     * @param uid to look the user up by.
     * @return the user if they exist, an empty optional if not.
     */
    Optional<User> getUser(int uid) {
        logger.info("Retrieving user by UID {} from table 'USER'.", uid);

        return ctx.selectFrom(USER)
                .where(USER.UID.eq(uid))
                .fetchOptional()
                .map(ur -> new User(ur.getUid(), ur.getPid(), ur.getCid(), ur.getEmail(), ur.getAge(), ur.getName()));
    }
}
