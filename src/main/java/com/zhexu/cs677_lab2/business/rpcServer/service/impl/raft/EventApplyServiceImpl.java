package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftElection;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.api.repository.impl.CouchdbCURDImpl;
import com.zhexu.cs677_lab2.business.logEventHandler.Impl.LogEventHandlerServiceImpl;
import com.zhexu.cs677_lab2.business.logEventHandler.LogEventHandlerService;
import com.zhexu.cs677_lab2.business.raft.Impl.SyncLogInitiateServiceImpl;
import com.zhexu.cs677_lab2.business.raft.SyncLogInitiateService;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft.basic.BasicImpl;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.EventApplyService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/5/22
 **/
@Log4j2
@Component
public class EventApplyServiceImpl extends BasicImpl implements EventApplyService {

    /**
     * @param logId
     * @return
     */
    @Override
    public BasicResponse response(UUID logId) {
        BasicResponse response = checkIfLogSyncInProgress();

        if (!response.accepted()){
            return response;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);

        threadPoolTaskExecutor.submit(new Thread(() -> {
            peer.putMessageBroadCast(logId);
        }));

       return response;
    }

    /**
     * @param logItem
     * @return
     */
    @Override
    public BasicResponse applyNewMassage(RaftLogItem logItem) throws ClassNotFoundException, JsonProcessingException {

        BasicResponse response = checkIfLogSyncInProgress();

        if (!response.accepted()){
            return response;
        }

        if (!peer.getRaftBase().isLeaderTermAndIndexUptoDate(logItem)){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setDiscription(ResponseCode.GET_DESCRIPTIONS.get(ResponseCode.STATUS_FORBIDDEN));
            response.setMessage("UnUpdated log!");
            return response;
        }


        List logList = new LinkedList(){{
            add(logItem);
        }};

        LogEventHandlerService logEventHandlerService = new LogEventHandlerServiceImpl();
        logEventHandlerService.extractHandlerAndRun(logList);

        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);

        threadPoolTaskExecutor.submit(new Thread(() -> {
            try {
                saveLogAndSendResponse(logItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        return response;
    }

    private void startSyncLog() {
        SyncLogInitiateService syncLogInitiateService = new SyncLogInitiateServiceImpl();
        syncLogInitiateService.startToSyncLog();
    }

    private void saveLogAndSendResponse(RaftLogItem logItem) throws Exception {

        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURDImpl.class);
        peer.getRaftBase().setTermAndIndex(logItem);

        log.info("Log has been applied and saved in couchdb at:" +
                couchdbCURD.addRaftLog(logItem));

        Address leaderAdd = peer.getNeighbourAdd(peer.getLeaderID());

        if (null == leaderAdd){
            log.info("Leader address not found!");
            return;
        }

        log.info("Response to leader:" +
                leaderAdd.getDomain());

        RPCInvocationHandler handler = new RPCInvocationHandler(peer.getNeighbourAdd(peer.getLeaderID()));
        EventApplyService eventApplyService = ProxyFactory.getInstance(EventApplyService.class, handler);

        log.info(eventApplyService.response(logItem.getLogId()));

        Thread.sleep(TimerConfig.getLogBroadCastApplyWaitTime());


        if(peer.getRaftBase().isNeedToSyncLog(logItem)){
            if (logItem.getEventClassName().equals(RaftElection.class.getName())){
                ObjectMapper objectMapper = new ObjectMapper();
                RaftElection election = objectMapper.readValue(logItem.getEventJSONString(), RaftElection.class);
                peer.setLeaderID(election.getNewLeader());
            }

            ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
            threadPoolTaskExecutor.submit(new Thread(() -> startSyncLog()));

            log.info("Found unupdated log of:\n" +
                    peer.getRaftBase().toString() +
                    "\n from:\n" +
                    logItem.printRaftBase() +
                    "\nafter apply election, starting to Sync log from: " +
                    peer.getNeighbourAdd(peer.getLeaderID()).getDomain());
        }
    }
}
