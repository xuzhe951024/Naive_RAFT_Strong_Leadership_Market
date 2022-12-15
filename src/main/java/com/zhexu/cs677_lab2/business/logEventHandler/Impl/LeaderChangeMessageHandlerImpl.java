package com.zhexu.cs677_lab2.business.logEventHandler.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftElection;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.logEventHandler.Impl.abstracts.EventHandlerBase;
import lombok.extern.log4j.Log4j2;

import javax.print.attribute.standard.Sides;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
@Log4j2
public class LeaderChangeMessageHandlerImpl extends EventHandlerBase {

    /**
     * @param eventBean
     * @param logId
     * @param eventId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean run(Object eventBean, UUID logId, UUID eventId) throws Exception {

        PeerBase peer = SingletonFactory.getRole();

        RaftElection election = (RaftElection) eventBean;
        peer.setLeaderID(election.getNewLeader());
        peer.getRaftBase().setTermAndIndex(election);

        RaftLogItem raftLogItem = saveEventBeanToCouchDb(eventBean, logId, eventId);
        if (null != raftLogItem){
            log.info("Save:\n" +
                    raftLogItem +
                    "\n to couchDb");
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
