package com.two.serviceusers.users;

import com.two.http_api.model.User;
import org.jooq.RecordMapper;
import org.jooq.generated.tables.records.UserRecord;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements RecordMapper<UserRecord, User> {
    @Override
    public User map(UserRecord userRecord) {
        return new User(
                userRecord.getUid(),
                userRecord.getPid(),
                userRecord.getCid(),
                userRecord.getEmail(),
                userRecord.getFirstName(),
                userRecord.getLastName()
        );
    }
}
