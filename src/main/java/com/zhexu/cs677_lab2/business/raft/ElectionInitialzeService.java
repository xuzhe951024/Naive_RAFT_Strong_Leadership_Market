package com.zhexu.cs677_lab2.business.raft;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/4/22
 **/
public interface ElectionInitialzeService {
    void startElection() throws InterruptedException, JsonProcessingException;
}
