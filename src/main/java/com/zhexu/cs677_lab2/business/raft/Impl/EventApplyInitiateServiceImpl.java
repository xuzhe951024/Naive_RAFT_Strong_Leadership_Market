package com.zhexu.cs677_lab2.business.raft.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.raft.EventApplyInitiateService;
import com.zhexu.cs677_lab2.business.raft.RollbackMethod;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.EventApplyService;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;

import static com.zhexu.cs677_lab2.constants.Consts.ENTER;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/5/22
 **/
@Log4j2
public class EventApplyInitiateServiceImpl implements EventApplyInitiateService {


    private RaftLogItem logItem = new RaftLogItem();

    private PeerBase peer = SingletonFactory.getRole();


    /**
     * @param logItem
     */
    @Override
    public void setLogItem(RaftLogItem logItem) {
        this.logItem = logItem;
    }

    /**
     * broadcast message to all follwers
     */
    @Override
    public void broardCast() throws JsonProcessingException {
        if (!peer.isLeader()) {
            log.error("Broadcast ERR: Peer: " + ENTER +
                    peer.getSelfAddress().getDomain() + ENTER +
                    "is a " + peer.getRaftRole() +
                    "instead of a leader!");
            return;
        }
        if (null == this.logItem) {
            log.info("Event message can not be null !");
            return;
        }

        peer.getNeighbourPeerMap().forEach((k, v) -> {
            RPCInvocationHandler handler = new RPCInvocationHandler(v);
            EventApplyService eventApplyService = ProxyFactory.getInstance(EventApplyService.class, handler);

            try {
                log.info("Broadcasting to each peers:" +
                        eventApplyService.applyNewMassage(this.logItem));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * collecte more than N/2 + 1 followers confirm
     *
     * @return Boolen, if enougth number of followers confirmed
     */
    @Override
    public Boolean collectedEnougthResponse() {
        if (!peer.isLeader()) {
            log.info("Broadcast ERR: Peer: " +
                    peer.getSelfAddress().getDomain() +
                    " is not a leader!");
            return Boolean.FALSE;
        }

        log.debug("Broad cast collection:\n" +
                peer.getMessageBroadCast(logItem.getLogId()));

        if (peer.isMessageBroadCastContains(this.logItem.getLogId()) &&
                peer.getMessageBroadCast(this.logItem.getLogId()) >= peer.getNeighbourPeerMap().size() / 2){
            peer.removeMessageBroadCast(logItem.getLogId());
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * apply the message
     */
    @Override
    public void commit() throws Exception {
        if (!peer.isLeader()) {
            log.info("Broadcast ERR: Peer: " +
                    peer.getSelfAddress().getDomain() +
                    "is not a leader!");
            return;
        }

        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURD.class);

        log.info("Log has been applied and saved in couchdb at:" +
                couchdbCURD.addRaftLog(this.logItem));

        peer.removeMessageBroadCast(this.logItem.getLogId());

        peer.unlockInitElection(logItem.getTerm() - 1, logItem.getIndex() - 1);
    }

    /**
     * clean messageBroadCastMap
     */
    @Override
    public void rollback(RollbackMethod rollbackMethod) throws JsonProcessingException {
        peer.removeMessageBroadCast(logItem.getLogId());
        rollbackMethod.rollBack();
    }
    @Override
    public void cleanMessageBroadCastMap () {
        peer.removeMessageBroadCast(this.logItem.getLogId());
    }
}

