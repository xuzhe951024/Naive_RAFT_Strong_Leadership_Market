package com.zhexu.cs677_lab2.business.logEventHandler;

import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/10/22
 **/
public interface EventHandler {
    public Boolean run(Object eventBean, UUID logId, UUID eventId) throws Exception;
}
