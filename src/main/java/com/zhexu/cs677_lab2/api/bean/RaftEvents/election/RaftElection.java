package com.zhexu.cs677_lab2.api.bean.RaftEvents.election;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class RaftElection extends RaftTransBase implements Serializable {
    private UUID newLeader;
    private Map<UUID, Boolean> voteMap;
    private Boolean elected = Boolean.FALSE;

    public UUID getNewLeader() {
        return newLeader;
    }

    public void setNewLeader(UUID newLeader) {
        this.newLeader = newLeader;
    }

    public Map<UUID, Boolean> getVoteMap() {
        return voteMap;
    }

    public void setVoteMap(Map<UUID, Boolean> voteMap) {
        this.voteMap = voteMap;
    }

    public Boolean getElected() {
        return elected;
    }

    public void setElected(Boolean elected) {
        this.elected = elected;
    }
}
