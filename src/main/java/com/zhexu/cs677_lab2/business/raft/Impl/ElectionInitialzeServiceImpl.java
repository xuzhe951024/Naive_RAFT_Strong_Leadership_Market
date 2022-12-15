package com.zhexu.cs677_lab2.business.raft.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftCandidateVoteReq;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftElection;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.business.raft.ElectionInitialzeService;
import com.zhexu.cs677_lab2.business.raft.EventApplyInitiateService;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.ElectionService;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import static com.zhexu.cs677_lab2.constants.Consts.ENTER;


/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/4/22
 **/
@Log4j2
@Service
public class ElectionInitialzeServiceImpl implements ElectionInitialzeService {

    /**
     * start election
     */
    @Override
    public void startElection() throws InterruptedException, JsonProcessingException {
        PeerBase peer = SingletonFactory.getRole();
        if (!peer.allowInitElection(peer.getRaftBase().getTerm() + 1)) {
            log.error("Not allowed to initiate election during voting");
            return;
        }

        if (peer.isLeader()) {
            log.debug("Identification of whether is a leader in election initializing service: true");
            log.error("Already being a leader!");
            return;
        }

        log.debug("Term and index: " +
                peer.getRaftBase().toString());

        peer.becomeCandidate();
        log.info("peer: " +
                peer.getSelfAddress().getDomain() +
                " has became candidate");


        peer.getRaftBase().increaseTerm();
        peer.putElectedMap(peer.getRaftBase().getTerm(),
                peer.getRaftBase().getIndex());

        log.debug("Term and index: " +
                peer.getRaftBase().toString());

        peer.setVoteCollector(peer.getId(), Boolean.TRUE);

        log.debug("peer voteCollector map: " +
                peer.getVoteCollector().toString());

        RaftCandidateVoteReq candidateVoteReq = new RaftCandidateVoteReq();
        candidateVoteReq.setTermAndIndex(peer.getRaftBase());
        candidateVoteReq.setCandidateAddress(peer.getSelfAddress());
        candidateVoteReq.setCandidate(peer.getId());
        peer.getNeighbourPeerMap().forEach((k, v) -> {
            log.info("Sending election request to: "
                    + v.getDomain());

            RPCInvocationHandler handler = new RPCInvocationHandler(v);
            ElectionService electionService = ProxyFactory.getInstance(ElectionService.class, handler);
            electionService.electionReqReceiv(candidateVoteReq);

            log.info("Response of election request from:" + ENTER +
                    v.getDomain() + ENTER +
                    electionService.electionReqReceiv(candidateVoteReq).toString());
        });

        log.info("Sleep " +
                TimerConfig.getElectionWaitTime() +
                " to wait for result");
        Thread.sleep(TimerConfig.getElectionWaitTime());

        if (!peer.isCandidate() ||
                (peer.getVoteCollector().size() < peer.getNeighbourPeerMap().size() / 2 + 1)) {
            peer.reSetVoteCollector();
            peer.getRaftBase().decreaseTerm();
            peer.becomeFollwer();
            peer.unlockInitElection(peer.getRaftBase().getTerm(),
                    peer.getRaftBase().getIndex());
            log.error("Election failed, now becom follwer: " + peer.isFollower());
        } else {
            peer.becomeLeader();
            RaftElection election = new RaftElection();
            log.info("Sufficient election response collected! New leader generated:");

            log.info("Vote map: " + ENTER +
                    peer.getVoteCollector());

            peer.getRaftBase().increaseIndex();

            election.setTermAndIndex(peer.getRaftBase());
            election.setNewLeader(peer.getId());
            election.setVoteMap(peer.getVoteCollector());
            ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);

            threadPoolTaskExecutor.submit(new Thread(() -> {
                try {
                    broadCastNewLeader(election);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));


            peer.reSetVoteCollector();
        }
    }

    private void broadCastNewLeader(RaftElection election) throws Exception {
        log.info("Broadcasting new leader: " +
                election.getNewLeader());

        EventApplyInitiateService eventApplyInitiateService = new EventApplyInitiateServiceImpl();

        ObjectMapper objectMapper = new ObjectMapper();

        RaftLogItem logItem = new RaftLogItem();

        logItem.setEventClassName(election.getClass().getName());
        logItem.setEventJSONString(objectMapper.writeValueAsString(election));
        logItem.setTermAndIndex(election);

        eventApplyInitiateService.setLogItem(logItem);
        eventApplyInitiateService.broardCast();

        Thread.sleep(TimerConfig.getLogBroadCastApplyWaitTime());

        PeerBase peer = SingletonFactory.getRole();

        if (!eventApplyInitiateService.collectedEnougthResponse()) {
            log.info("Did not collect enough broadcast respons for elected of electing:" +
                    (election.getNewLeader().equals(peer.getId()) ?
                            peer.getSelfAddress().getDomain() :
                            peer.getNeighbourPeerMap().get(election.getNewLeader()).getDomain()));


            peer.reSetVoteCollector();
            peer.becomeFollwer();
            peer.unlockInitElection(peer.getRaftBase().getTerm() - 1,
                    peer.getRaftBase().getIndex() - 1);
            eventApplyInitiateService.rollback(() -> {
                log.info("Election rolled back!");
            });
            log.error("Election failed, now become follower: " + peer.isFollower());
            return;
        }

        eventApplyInitiateService.commit();
        peer.unlockInitElection(peer.getRaftBase().getTerm(),
                peer.getRaftBase().getIndex());
    }
}
