package com.two.serviceusers.users;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.CoupleRecord;
import org.springframework.stereotype.Repository;

import static org.jooq.generated.Tables.COUPLE;

@Repository
@AllArgsConstructor
public class CoupleDao {

    private final DSLContext ctx;

    /**
     * @return the CID of the new relationship.
     */
    public int storeCouple(int uid, int pid) {
        CoupleRecord coupleRecord = ctx.newRecord(COUPLE);

        coupleRecord.setUid(uid);
        coupleRecord.setPid(pid);

        coupleRecord.store();
        coupleRecord.refresh();

        return coupleRecord.getCid();
    }

}
