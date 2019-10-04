package com.two.serviceusers.users;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

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

}
