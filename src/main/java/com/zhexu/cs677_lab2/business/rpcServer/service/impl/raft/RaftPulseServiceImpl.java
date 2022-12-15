package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftPulse;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.raft.ElectionInitialzeService;
import com.zhexu.cs677_lab2.business.raft.Impl.SyncLogInitiateServiceImpl;
import com.zhexu.cs677_lab2.business.raft.SyncLogInitiateService;
import com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft.basic.BasicImpl;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.RaftPulseService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import static com.zhexu.cs677_lab2.constants.Consts.ENTER;
import static com.zhexu.cs677_lab2.constants.Consts.SPACE;
import static com.zhexu.cs677_lab2.constants.ResponseCode.*;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/29/22
 **/
@Log4j2
@Service
public class RaftPulseServiceImpl extends BasicImpl implements RaftPulseService {

    @Autowired
    ElectionInitialzeService electionInitialzeServiceDep;

    @Override
    public BasicResponse receiveLeaderPulse(RaftPulse pulse) {
        BasicResponse response = checkIfLogSyncInProgress();

        if (!response.accepted()){
            return response;
        }

        if (pulse.isTermOrIndexLarger(peer.getRaftBase())) {
            if (!peer.isFollower()) {
                log.debug("Expired " +
                        peer.getRaftRole() +
                        " becasuse of receiving of larger term or index of:" +
                        ENTER +
                        (RaftTransBase) pulse +
                        ENTER +
                        "self term and index:" +
                        ENTER +
                        peer.getRaftBase());

                peer.becomeFollwer();
                log.debug("Now become follower");
            }
        }

        if (!peer.isFollower()) {
            response.setStatus(STATUS_FORBIDDEN);
            response.setDiscription(DESCRIPTION_FORBIDDEN);
            response.setMessage("sending pluse to an unexpired " +
                    peer.getRaftRole()+
                    " (" +
                    peer.getSelfAddress().getDomain() +
                    ")!");
            return response;
        }

        if (pulse.equals(peer.getLeaderID(),
                peer.getRaftBase().getIndex(),
                peer.getRaftBase().getTerm())) {

            SingletonFactory.setCurrentTime();

        }

        if (peer.getRaftBase().isNeedToSyncLog(pulse)) {

            if (!pulse.getLeaderID().equals(peer.getLeaderID())) {
                peer.setLeaderID(pulse.getLeaderID());
            }

            log.info("Unupdated clock:\n" +
                    peer.getRaftBase().toString() +
                    "\nfound in pulse service of:\n" +
                    pulse.toString() +
                    "\n, starting sync log from leader:" +
                    peer.getNeighbourAdd(peer.getLeaderID()).getDomain());

            ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
            threadPoolTaskExecutor.submit(new Thread(() -> startSyncLog()));

        } else {
            if (peer.getRaftBase().atSameTermAndIndex(pulse)){
                log.info("Leader: " +
                        peer.getNeighbourPeerMap().get(pulse.getLeaderID()).getDomain() + SPACE +
                        "has same index and term status with self");
                return response;
            }

            log.info("Leader expired with a smaller term/index of" + ENTER +
                    (RaftTransBase)pulse + ENTER +
                    "self term/index: " + ENTER +
                    peer.getRaftBase() +
                    ", \nstarting election:");

            if (peer.isFollower()) {

                ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
                threadPoolTaskExecutor.submit(new Thread(() -> {
                    try {
                        ElectionInitialzeService electionInitialzeService = SpringContextUtils.getBean(ElectionInitialzeService.class);
                        electionInitialzeService.startElection();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }));

            }
        }
        return response;
    }

    private void startSyncLog() {
        SyncLogInitiateService syncLogInitiateService = new SyncLogInitiateServiceImpl();
        syncLogInitiateService.startToSyncLog();
    }


}
