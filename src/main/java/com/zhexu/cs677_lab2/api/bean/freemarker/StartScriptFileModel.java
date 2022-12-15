package com.zhexu.cs677_lab2.api.bean.freemarker;

import java.io.Serializable;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 11/17/22
 **/
public class StartScriptFileModel implements Serializable {
    private String jsonFilePath;
    private Integer lookupStartDelay;
    private Integer testNum;
    private Integer rpcBuffSize;

    public StartScriptFileModel(String jsonFilePath, Integer lookupStartDelay, Integer testNum, Integer rpcBuffSize) {
        this.jsonFilePath = jsonFilePath;
        this.lookupStartDelay = lookupStartDelay;
        this.testNum = testNum;
        this.rpcBuffSize = rpcBuffSize;
    }

    public String getJsonFilePath() {
        return jsonFilePath;
    }

    public void setJsonFilePath(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public Integer getLookupStartDelay() {
        return lookupStartDelay;
    }

    public void setLookupStartDelay(Integer lookupStartDelay) {
        this.lookupStartDelay = lookupStartDelay;
    }

    public Integer getTestNum() {
        return testNum;
    }

    public void setTestNum(Integer testNum) {
        this.testNum = testNum;
    }

    public Integer getRpcBuffSize() {
        return rpcBuffSize;
    }

    public void setRpcBuffSize(Integer rpcBuffSize) {
        this.rpcBuffSize = rpcBuffSize;
    }

    @Override
    public String toString() {
        return "StartScriptFileModel{" +
                ", jsonFilePath='" + jsonFilePath + '\'' +
                ", lookupStartDelay=" + lookupStartDelay +
                ", testNum=" + testNum +
                ", rpcBuffSize=" + rpcBuffSize +
                '}';
    }
}
