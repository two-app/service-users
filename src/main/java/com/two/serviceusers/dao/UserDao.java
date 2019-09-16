package com.two.serviceusers.dao;

import com.two.serviceusers.exceptions.UserExistsException;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.generated.Tables.USER;


@Repository
public class UserDao {

    private final DSLContext ctx;

    @Autowired
    public UserDao(DSLContext dslContext) {
        this.ctx = dslContext;
    }

    /**
     * @param email to look up user by.
     * @return the user if they exist.
     */
    public Optional<com.two.http_api.model.User> getUser(String email) {
        return ctx.selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOptional()
                .map(u -> new com.two.http_api.model.User(
                        u.getUid(),
                        u.getPid(),
                        u.getCid(),
                        u.getEmail(),
                        u.getAge(),
                        u.getName()
                ));
    }

    /**
     * @return the created users ID.
     * @throws UserExistsException if the email exists in the users table.
     */
    public int createUser(String email, String name, int age) {
        if (ctx.fetchExists(ctx.selectFrom(USER).where(USER.EMAIL.eq(email)))) {
            throw new UserExistsException(email);
        }

        UserRecord userRecord = ctx.newRecord(USER);

        userRecord.setEmail(email);
        userRecord.setName(name);
        userRecord.setAge(age);

        userRecord.store();
        userRecord.refresh();

        return userRecord.getUid();
    }


}
