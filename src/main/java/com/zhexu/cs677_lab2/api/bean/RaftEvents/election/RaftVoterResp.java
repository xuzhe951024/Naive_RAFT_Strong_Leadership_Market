package com.zhexu.cs677_lab2.api.bean.RaftEvents.election;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;

import java.io.Serializable;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class RaftVoterResp extends RaftTransBase implements Serializable {
    private UUID voter;
    private Boolean agree;

    private UUID CurrentLeader;

    public UUID getVoter() {
        return voter;
    }

    public void setVoter(UUID voter) {
        this.voter = voter;
    }

    public Boolean isAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public UUID getCurrentLeader() {
        return CurrentLeader;
    }

    public void setCurrentLeader(UUID currentLeader) {
        this.CurrentLeader = currentLeader;
    }
}
