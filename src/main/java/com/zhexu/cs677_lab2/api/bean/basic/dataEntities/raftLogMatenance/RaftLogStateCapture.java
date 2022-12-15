package com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;

import java.io.Serializable;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/9/22
 **/
public class RaftLogStateCapture extends RaftTransBase implements Serializable {
    private UUID applierId;
    private String logHashCode = "";

    public UUID getApplierId() {
        return applierId;
    }

    public void setApplierId(UUID applierId) {
        this.applierId = applierId;
    }

    public String getLogHashCode() {
        return logHashCode;
    }

    public void setLogHashCode(String logHashCode) {
        this.logHashCode = logHashCode;
    }
}
