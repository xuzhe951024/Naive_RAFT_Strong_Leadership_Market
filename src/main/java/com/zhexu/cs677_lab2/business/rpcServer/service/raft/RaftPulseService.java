package com.zhexu.cs677_lab2.business.rpcServer.service.raft;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftPulse;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/29/22
 **/
public interface RaftPulseService {
    /**
     * Receive the pulse request from the leader
     * @return status wrapped in BasicResponse
     */
    BasicResponse receiveLeaderPulse(RaftPulse pulse);
}
