package com.zhexu.cs677_lab2.business.logEventHandler;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;

import java.util.List;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/9/22
 **/
public interface LogEventHandlerService {
    public Boolean extractHandlerAndRun(List<RaftLogItem> raftLogItemList);
}
