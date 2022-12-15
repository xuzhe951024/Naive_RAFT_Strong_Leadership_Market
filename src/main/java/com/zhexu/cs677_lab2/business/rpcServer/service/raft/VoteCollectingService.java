package com.zhexu.cs677_lab2.business.rpcServer.service.raft;

import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftVoterResp;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/3/22
 **/
public interface VoteCollectingService {
    BasicResponse voteCollection(RaftVoterResp voterResp);
}
