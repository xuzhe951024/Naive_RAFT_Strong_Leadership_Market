package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.SerializableRaftLogListWrapper;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogStateCapture;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.ReceiveSyncDataService;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.SyncLogService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/9/22
 **/
@Log4j2
public class SyncLogServiceImpl implements SyncLogService {
    PeerBase peer = SingletonFactory.getRole();

    /**
     * @param capture
     * @return
     */
    @Override
    public Boolean foundLogTermAndIndex(RaftLogStateCapture capture) {
        log.debug("Received log align try: " +
                capture.toString());
        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURD.class);
        RaftLogItem logItem = couchdbCURD.querayByTermAndIndex(capture);

        Boolean result = null != logItem
                && logItem.getJsonStringHashCode().equals(capture.getLogHashCode());

        if (result) {
            log.debug("log alignment baseline is found : " + logItem);
        } else {
            log.debug(capture + " not found!");
        }

        return result;
    }

    /**
     * @param capture
     * @return
     */
    @Override
    public BasicResponse getSyncData(RaftLogStateCapture capture) {
        BasicResponse response = new BasicResponse();

        if (!peer.isLeader()) {
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Do not request for sync from a non-leader peer!");
            log.info(peer.getSelfAddress().getDomain() + " is not leader any more!");
        }

        new Thread(() -> collectAndSendSyncData(capture)).start();

        return response;
    }

    private void collectAndSendSyncData(RaftLogStateCapture capture) {
        log.info("Received collect log alignment req, start searching from: " + capture);
        LinkedList<RaftLogItem> raftLogItemList = new LinkedList<RaftLogItem>();

        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURD.class);

        Long term = capture.getTerm();
        Long index = capture.getIndex();

        for (int i = 0; i <= peer.getRaftBase().getTerm() - term; i++) {
            capture.setIndex(index);
            for (int j = 0; j <= peer.getRaftBase().getIndex() - index; j++) {
                RaftLogItem logItem = couchdbCURD.querayByTermAndIndex(capture);
                if (null != logItem) {
                    log.debug("Now add:\n" +
                            logItem.printRaftBase() +
                            "\nto log list.");
                    raftLogItemList.add(logItem);
                    log.debug(logItem.printRaftBase() +
                            " added, raftLogItemList.size=" +
                            raftLogItemList.size());
                } else {
                    log.debug("log not found:\n" +
                            capture.toString());
                }
                capture.increaseIndex();
            }
            capture.increaseTerm();
        }

        log.debug("Log sync list assembled: " + raftLogItemList.toString());

        RPCInvocationHandler handler = new RPCInvocationHandler(peer.getNeighbourAdd(capture.getApplierId()));
        ReceiveSyncDataService receiveSyncDataService = ProxyFactory.getInstance(ReceiveSyncDataService.class, handler);

        SerializableRaftLogListWrapper logListWrapper = new SerializableRaftLogListWrapper();
        logListWrapper.setRaftLogItemList(raftLogItemList);
        log.debug("RaftLogItemList are wrapped and ready to be sent, size=" + logListWrapper.getRaftLogItemList().size());

        receiveSyncDataService.receiveData(logListWrapper);
    }
}
