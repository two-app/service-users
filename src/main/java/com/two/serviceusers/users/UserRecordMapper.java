package com.two.serviceusers.users;

import com.google.common.collect.ImmutableMap;
import com.two.http_api.model.Partner;
import com.two.http_api.model.Self;
import com.two.http_api.model.User;
import org.jooq.RecordMapper;
import org.jooq.generated.tables.records.UserRecord;

public class UserRecordMapper {

    private static final ImmutableMap<Class<? extends User>, RecordMapper<UserRecord, ? extends User>> mappers = ImmutableMap.of(
            User.class, new UserMapper(),
            Self.class, new SelfMapper(),
            Partner.class, new PartnerMapper()
    );

    /**
     * @param type of mapper, in set {User, Self, Partner}.
     * @param <T> inferred generic, ensuring type extends User.class.
     * @return the JOOQ record mapper for the provided type.
     * @throws IllegalArgumentException if no mapper exists for the type provided.
     */
    public static <T extends User> RecordMapper<UserRecord, T> resolve(Class<T> type) throws IllegalArgumentException {
        @SuppressWarnings("unchecked")
        RecordMapper<UserRecord, T> mapper = (RecordMapper<UserRecord, T>) mappers.get(type);
        if (mapper == null) throw new IllegalArgumentException();
        return mapper;
    }

    /**
     * @param userRecord JOOQ record containing all user data.
     * @param type to map the record to. Must belong to set of {User, Self, Partner}, of which there are mappers.
     * @param <T> inferred generic, ensuring type extends {User.class}.
     * @return the mapped record.
     * @throws IllegalArgumentException if no record mapper exists for the type provided.
     */
    public static <T extends User> T map(UserRecord userRecord, Class<T> type) throws IllegalArgumentException {
        @SuppressWarnings("unchecked")
        RecordMapper<UserRecord, T> mapper = (RecordMapper<UserRecord, T>) mappers.get(type);
        if (mapper == null) throw new IllegalArgumentException();
        return mapper.map(userRecord);
    }

    public static class PartnerMapper implements RecordMapper<UserRecord, Partner> {
        @Override
        public Partner map(UserRecord userRecord) {
            return Partner.builder()
                    .uid(userRecord.getUid())
                    .pid(userRecord.getPid())
                    .cid(userRecord.getCid())
                    .firstName(userRecord.getFirstName())
                    .lastName(userRecord.getLastName())
                    .build();
        }
    }

    public static class SelfMapper implements RecordMapper<UserRecord, Self> {
        @Override
        public Self map(UserRecord ur) {
            return Self.builder()
                    .uid(ur.getUid())
                    .pid(ur.getPid())
                    .cid(ur.getCid())
                    .firstName(ur.getFirstName())
                    .lastName(ur.getLastName())
                    .email(ur.getEmail())
                    .build();
        }
    }

    public static class UserMapper implements RecordMapper<UserRecord, User> {
        @Override
        public User map(UserRecord ur) {
            return User.builder()
                    .uid(ur.getUid())
                    .pid(ur.getPid())
                    .cid(ur.getCid())
                    .firstName(ur.getFirstName())
                    .lastName(ur.getLastName())
                    .build();
        }
    }

}
