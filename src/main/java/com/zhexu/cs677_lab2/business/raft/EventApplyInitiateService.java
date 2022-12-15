package com.zhexu.cs677_lab2.business.raft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/4/22
 **/
public interface EventApplyInitiateService<T> {
    public void setLogItem(RaftLogItem logItem);

    public void broardCast() throws JsonProcessingException;
    public Boolean collectedEnougthResponse();
    public void commit() throws Exception;
    public void rollback(RollbackMethod rollbackMethod) throws JsonProcessingException;
    public void cleanMessageBroadCastMap ();
}
