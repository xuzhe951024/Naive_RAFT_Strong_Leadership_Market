package com.zhexu.cs677_lab2.api.bean.RaftEvents.election;

import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;

import java.io.Serializable;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class RaftCandidateVoteReq extends RaftTransBase implements Serializable {
    private UUID candidate;
    private Address candidateAddress;

    public UUID getCandidate() {
        return candidate;
    }

    public void setCandidate(UUID candidate) {
        this.candidate = candidate;
    }

    public Address getCandidateAddress() {
        return candidateAddress;
    }

    public void setCandidateAddress(Address candidateAddress) {
        this.candidateAddress = candidateAddress;
    }
}
