package com.zhexu.cs677_lab2.business.raft.Impl;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftPulse;
import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.raft.SendPulseService;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.RaftPulseService;
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
public class SendPulseServiceImpl implements SendPulseService {
    @Override
    public void sendPulseToFollowers() {
        PeerBase peer = SingletonFactory.getRole();
        if (!peer.isLeader()){
            log.info("Stop sending pulse, peer: "+
                    peer.getSelfAddress().getDomain()+
                    " is not a leader!");
            return;
        }

        RaftPulse raftPulse = new RaftPulse();
        raftPulse.setLeaderID(peer.getId());
        raftPulse.setIndex(peer.getRaftBase().getIndex());
        raftPulse.setTerm(peer.getRaftBase().getTerm());

        for (Address follower: peer.getNeighbourPeerMap().values()){
            Thread sendPulseToFollowers = new Thread(() -> sendPulse(follower, raftPulse));
            sendPulseToFollowers.start();
        }

    }

    private void sendPulse(Address follower, RaftPulse raftPulse) {
        RPCInvocationHandler handler = new RPCInvocationHandler(follower);
        RaftPulseService raftPulseService = ProxyFactory.getInstance(RaftPulseService.class, handler);
        log.info("Sending heartbeat to: " + follower.getDomain());
        log.info(raftPulseService.receiveLeaderPulse(raftPulse));
    }
}
