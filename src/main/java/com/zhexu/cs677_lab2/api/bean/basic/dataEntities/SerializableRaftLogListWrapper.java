package com.zhexu.cs677_lab2.api.bean.basic.dataEntities;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;

import java.io.Serializable;
import java.util.List;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
public class SerializableRaftLogListWrapper implements Serializable {
    private List<RaftLogItem> raftLogItemList;

    public List<RaftLogItem> getRaftLogItemList() {
        return raftLogItemList;
    }

    public void setRaftLogItemList(List<RaftLogItem> raftLogItemList) {
        this.raftLogItemList = raftLogItemList;
    }

    public Boolean isNullorEmpty(){
        return null == this.raftLogItemList || raftLogItemList.isEmpty();
    }
}
