package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft;

import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftVoterResp;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft.basic.BasicImpl;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.VoteCollectingService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/3/22
 **/
@Service
public class VoteCollectingServiceImpl extends BasicImpl implements VoteCollectingService {

    /**
     * @return
     */
    @Override
    public BasicResponse voteCollection(RaftVoterResp voterResp) {
        BasicResponse response = checkIfLogSyncInProgress();

        if (!response.accepted()){
            return response;
        }

        if (!peer.isCandidate()){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setDiscription(ResponseCode.DESCRIPTION_FORBIDDEN);
            response.setMessage("Peer: " +
                    peer.getSelfAddress().getDomain() +
                    "is not a candidate anymore!");
            return response;
        }
        if (voterResp.isTermOrIndexLarger(peer.getRaftBase())){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setDiscription(ResponseCode.DESCRIPTION_FORBIDDEN);
            response.setMessage("Peer: " +
                    peer.getSelfAddress().getDomain() +
                    "is has an smaller term/index number!");
            return response;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
        threadPoolTaskExecutor.submit(new Thread(() -> collectVote(voterResp)));

        return response;
    }

    private void collectVote(RaftVoterResp voterResp) {
        SingletonFactory.getRole().setVoteCollector(voterResp.getVoter(), voterResp.isAgree());
    }
}
