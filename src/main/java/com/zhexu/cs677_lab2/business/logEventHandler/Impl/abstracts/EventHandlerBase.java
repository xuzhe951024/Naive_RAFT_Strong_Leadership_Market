package com.zhexu.cs677_lab2.business.logEventHandler.Impl.abstracts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.MarketTransaction;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.business.logEventHandler.EventHandler;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;

import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
public abstract class EventHandlerBase<T> implements EventHandler {
    protected RaftLogItem saveEventBeanToCouchDb(T eventBean, UUID logId, UUID eventId) throws Exception {
        CouchdbCURD couchdbCURD = SpringContextUtils.getBean(CouchdbCURD.class);
        ObjectMapper objectMapper = new ObjectMapper();
        RaftLogItem raftLogItem = new RaftLogItem();
        raftLogItem.setLogId(logId);
        raftLogItem.setEventId(eventId);
        raftLogItem.setEventClassName(eventBean.getClass().getName());
        raftLogItem.setEventJSONString(objectMapper.writeValueAsString(eventBean));
        raftLogItem.setTermAndIndex((RaftTransBase) eventBean);
        return couchdbCURD.addRaftLog(raftLogItem);
    }
}
