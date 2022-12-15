package com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;

import java.io.Serializable;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class RaftPulse extends RaftTransBase implements Serializable {
    private UUID leaderID;

    public UUID getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(UUID leaderID) {
        this.leaderID = leaderID;
    }

    public Boolean equals(UUID leaderID, Long index, Long term){
        return this.leaderID.equals(leaderID)
                && this.getIndex().longValue() == index.longValue()
                && this.getTerm().longValue() == term.longValue();
    }
}
