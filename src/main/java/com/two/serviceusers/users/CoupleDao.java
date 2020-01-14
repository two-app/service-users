package com.two.serviceusers.users;

import com.two.http_api.model.User;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.CoupleRecord;
import org.jooq.generated.tables.records.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import static org.jooq.generated.Tables.COUPLE;
import static org.jooq.generated.Tables.USER;

@Repository
@AllArgsConstructor
public class CoupleDao {

    private final DSLContext ctx;
    private static final Logger logger = LoggerFactory.getLogger(CoupleDao.class);

    /**
     * @return the CID of the new relationship.
     */
    public int storeCouple(int uid, int pid) {
        logger.info("Creating couple with UID {} and PID {}.", uid, pid);
        CoupleRecord coupleRecord = ctx.newRecord(COUPLE);

        coupleRecord.setUid(uid);
        coupleRecord.setPid(pid);

        coupleRecord.store();
        coupleRecord.refresh();

        logger.info("Successfully stored couple and generated CID {}.", coupleRecord.getCid());
        return coupleRecord.getCid();
    }

    /**
     * @param uid to update.
     * @param pid to set in the uids record.
     * @param cid to set in the uids record.
     * @return a refreshed user object holding the new pid and cid values.
     */
    public User connectUserToPartner(int uid, int pid, int cid) {
        logger.info("Retrieving user record for UID {}.", uid);
        UserRecord userRecord = ctx.selectFrom(USER).where(USER.UID.eq(uid)).fetchOne();

        logger.info("Setting PID to {} and CID to {}.", pid, cid);
        userRecord.setPid(pid);
        userRecord.setCid(cid);

        userRecord.update();
        userRecord.refresh();

        logger.info("Successfully updated the user record.");
        return UserRecordMapper.map(userRecord, User.class);
    }

}
