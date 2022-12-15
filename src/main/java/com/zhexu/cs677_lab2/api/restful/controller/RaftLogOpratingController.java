package com.zhexu.cs677_lab2.api.restful.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftElection;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
@RestController

@Log4j2
@RequestMapping("/raftlog")
public class RaftLogOpratingController {
    Role role = SingletonFactory.getRole();

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("/add")
    @DependsOn("CouchDbCURD")
    public BasicResponse newRaftLog() throws Exception {
        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURD.class);
        BasicResponse response = new BasicResponse();

        if(!role.isLeader()){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Not a leader!");
            log.error("Not a leader!");
            return response;
        }

        if(null == couchdbCURD){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("couchdbCURD is null!");
            log.error("couchdbCURD is null!");
            return response;
        }

        role.getRaftBase().increaseIndex();
        RaftElection election = new RaftElection();
        election.setElected(Boolean.TRUE);
        election.setTermAndIndex(role.getRaftBase());
        election.setNewLeader(role.getId());
        election.setVoteMap(new HashMap<>(){{
            put(role.getId(), Boolean.TRUE);
            role.getNeighbourPeerMap().forEach((k, v) -> {
                put(k, Boolean.TRUE);
            });
        }});

        RaftLogItem logItem = new RaftLogItem();
        logItem.setTermAndIndex(election);
        logItem.setEventJSONString(objectMapper.writeValueAsString(election));
        logItem.setEventClassName(RaftElection.class.getName());

        response.setMessage(couchdbCURD.addRaftLog(logItem).toString());

        return response;
    }
}
