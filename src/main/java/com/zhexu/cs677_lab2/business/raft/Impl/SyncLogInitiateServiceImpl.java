package com.zhexu.cs677_lab2.business.raft.Impl;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogStateCapture;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.business.raft.SyncLogInitiateService;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.SyncLogService;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import static com.zhexu.cs677_lab2.constants.Consts.ENTER;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/9/22
 **/
@Log4j2
public class SyncLogInitiateServiceImpl implements SyncLogInitiateService {
    PeerBase peer = SingletonFactory.getRole();

    /**
     * @return
     */
    @Override
    public void startToSyncLog() {
        log.info("Start log sync service");
        peer.startSyncLog();

        RaftLogStateCapture capture = new RaftLogStateCapture();
        capture.setApplierId(peer.getId());
        capture.setTermAndIndex(peer.getRaftBase());

        RPCInvocationHandler handler = new RPCInvocationHandler(peer.getNeighbourAdd(peer.getLeaderID()));
        SyncLogService syncLogService = ProxyFactory.getInstance(SyncLogService.class, handler);

        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURD.class);

        log.info("Now trying to find the baseline of unaligned log:");

        Long term = capture.getTerm();
        Long index = capture.getIndex();

        for (int i = 0; i < term; i++) {
            capture.setIndex(peer.getRaftBase().getIndex());
            for (int j = 0; j < index; j++) {
                log.debug("Trying capture: " + capture.toString());

                RaftLogItem logItem = couchdbCURD.querayByTermAndIndex(capture);

                if (null == logItem) {
                    log.debug("capture: " +
                            capture.toString() +
                            "not exist in local database!");
                    continue;
                }

                capture.setLogHashCode(logItem.getJsonStringHashCode());

                if (syncLogService.foundLogTermAndIndex(capture)) {
                    log.info("Send log data sync request to leader: " +
                            peer.getNeighbourPeerMap().get(peer.getLeaderID()).getDomain() + ENTER +
                            syncLogService.getSyncData(capture));
                    return;
                }
                log.debug("Capture:\n" +
                        capture.toString() +
                        "\nnot found!");
                capture.decreaseIndex();
            }
            capture.decreaseTerm();
        }

        log.info("Fail to sync log!");
        peer.finishedSyncLog();
        return;

    }
}
