package com.two.serviceusers.users;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.CoupleRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import static org.jooq.generated.Tables.COUPLE;

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

}
