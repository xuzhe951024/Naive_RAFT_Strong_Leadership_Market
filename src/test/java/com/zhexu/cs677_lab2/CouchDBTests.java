package com.zhexu.cs677_lab2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.RaftEvents.election.RaftElection;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.config.basic.CouchDBInfo;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import lombok.extern.log4j.Log4j2;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@SpringBootTest
@Log4j2
class CouchDBTests {

    @Autowired
    private CouchDBInfo couchDBInfo;

    @Autowired
    CouchdbCURD couchdbCURD;


    @Test
    void test() throws Exception {
        new Thread(() -> {
            try {
                writeToDB();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private void writeToDB() throws Exception {

        UUID candidate = UUID.randomUUID();
        RaftElection election = new RaftElection();
        election.setTerm(0L);
        election.setIndex(0L);
        election.setNewLeader(candidate);
        election.setVoteMap((Map<UUID, Boolean>) new HashMap<>().put(UUID.randomUUID(), true));

        RaftLogItem logItem = new RaftLogItem();
        logItem.setTerm(0L);
        logItem.setIndex(1L);
        logItem.setEventClassName(RaftElection.class.getName());

        ObjectMapper objectMapper = new ObjectMapper();
        logItem.setEventJSONString(objectMapper.writeValueAsString(election));
        logItem.setLogId(UUID.randomUUID());

        assert(logItem.getLogId().equals(couchdbCURD.addRaftLog(logItem).getLogId()));
    }

}
