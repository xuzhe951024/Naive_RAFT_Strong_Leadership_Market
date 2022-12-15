package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft;

import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftCandidateVoteReq;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftVoterResp;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft.basic.BasicImpl;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.ElectionService;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.VoteCollectingService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/29/22
 **/
@Log4j2
@Service
public class ElectionServiceImpl extends BasicImpl implements ElectionService {
    @Override
    public BasicResponse electionReqReceiv(RaftCandidateVoteReq candidateVoteReq) {
        log.info("Received election proposal from:" +
                candidateVoteReq.getCandidateAddress().getDomain());

        BasicResponse response = checkIfLogSyncInProgress();

        if (!response.accepted()){
            return response;
        }

        RaftVoterResp voterResp = new RaftVoterResp();

        voterResp.setTermAndIndex(peer.getRaftBase());
        voterResp.setVoter(peer.getId());

        if (!peer.getRaftBase().isLeaderTermAndIndexUptoDate(candidateVoteReq)){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setDiscription(ResponseCode.GET_DESCRIPTIONS.get(ResponseCode.STATUS_FORBIDDEN));
            response.setMessage("UnUpdated log!");
            voterResp.setAgree(false);
            voterResp.setCurrentLeader(peer.getLeaderID());
            return response;
        }

//        if (!peer.isPulseTimeout()){
//            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
//            response.setDiscription(ResponseCode.GET_DESCRIPTIONS.get(ResponseCode.STATUS_FORBIDDEN));
//            response.setMessage("Election timeout has not been reached!");
//            voterResp.setAgree(false);
//            voterResp.setCurrentLeader(peer.getLeaderID());
//            return response;
//        }

        if(!peer.isFollower()){
            log.debug("Expired " +
                    peer.getRaftRole() +
                    " with term and index of: " +
                    peer.getRaftBase());
            peer.becomeFollwer();
            log.debug("Caused by election req: " +
                    candidateVoteReq.toString() +
                    " from: " +
                    candidateVoteReq.getCandidateAddress().getDomain());
            log.debug("Now become follower");
        }

        if (peer.checkTermIfElectingAvailable(candidateVoteReq.getTerm(),
                candidateVoteReq.getIndex())){

            new Thread(() -> vote(candidateVoteReq, voterResp)).start();

        }

        return response;
    }

    private void vote(RaftCandidateVoteReq candidateVoteReq, RaftVoterResp voterResp){

        peer.putElectedMap(candidateVoteReq.getTerm(),
                candidateVoteReq.getIndex());

        RPCInvocationHandler handler = new RPCInvocationHandler(candidateVoteReq.getCandidateAddress());
        VoteCollectingService voteCollectingService = ProxyFactory.getInstance(VoteCollectingService.class, handler);
        voterResp.setAgree(Boolean.TRUE);

        log.info("Sending response to candidate: " + candidateVoteReq.getCandidateAddress().getDomain());
        log.info(voteCollectingService.voteCollection(voterResp));

        Long waitingTime = TimerConfig.getElectionWaitTime();
        log.info("Now wait " + waitingTime +
                " ms to check if candidate(" +
                candidateVoteReq.getCandidateAddress().getDomain() +
                ") is elected");
        try {
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (null != peer.getLeaderID() && peer.getLeaderID().equals(candidateVoteReq.getCandidate())){
            SingletonFactory.setCurrentTime();
        }

        peer.unlockInitElection(candidateVoteReq.getTerm(), candidateVoteReq.getIndex());
    }


}
