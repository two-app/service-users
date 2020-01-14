package com.two.serviceusers.users;

import com.two.http_api.model.PublicApiModel.UserRegistration;
import com.two.http_api.model.User;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
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
     * @throws DuplicateKeyException if the email exists in the users table.
     */
    User storeUser(UserRegistration userRegistration) throws DuplicateKeyException {
        logger.info("Storing user details in DB table 'USER'.");
        UserRecord userRecord = ctx.newRecord(USER);

        userRecord.setEmail(userRegistration.getEmail());
        userRecord.setFirstName(userRegistration.getFirstName());
        userRecord.setLastName(userRegistration.getLastName());
        userRecord.setAcceptedTerms(userRegistration.isAcceptedTerms());
        userRecord.setOfAge(userRegistration.isOfAge());

        userRecord.store();
        userRecord.refresh();

        logger.info("Successfully stored user in DB with generated UID: {}.", userRecord.getUid());
        return UserRecordMapper.map(userRecord, User.class);
    }

    public <T extends User> Optional<T> getUser(String email, Class<T> userType) {
        logger.info("Retrieving user from table 'USER' with email {} and mapping into {}.", email, userType);
        val mapper = UserRecordMapper.resolve(userType);
        return ctx.selectFrom(USER).where(USER.EMAIL.eq(email)).fetchOptional(mapper);
    }

    public <T extends User> Optional<T> getUser(int uid, Class<T> userType) {
        logger.info("Retrieving user from table 'USER' with uid {} and mapping into {}.", uid, userType);
        val mapper = UserRecordMapper.resolve(userType);
        return ctx.selectFrom(USER).where(USER.UID.eq(uid)).fetchOptional(mapper);
    }
}
