package com.zhexu.cs677_lab2.api.repository;


import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public interface CouchdbCURD<T> {

    RaftLogItem addRaftLog(RaftLogItem logItem)throws Exception;

    RaftLogItem querayByTermAndIndex(RaftTransBase raftTransBase);

}
