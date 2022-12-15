package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.SerializableRaftLogListWrapper;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.logEventHandler.Impl.LogEventHandlerServiceImpl;
import com.zhexu.cs677_lab2.business.logEventHandler.LogEventHandlerService;
import com.zhexu.cs677_lab2.business.rpcServer.service.raft.ReceiveSyncDataService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/9/22
 **/
@Log4j2
public class ReceiveSyncDataServiceImpl implements ReceiveSyncDataService {
    /**
     * @param raftLogItemList
     * @return
     */
    @Override
    public BasicResponse receiveData(SerializableRaftLogListWrapper raftLogItemList) {
        BasicResponse response = new BasicResponse();

        if (raftLogItemList.isNullorEmpty()){
            log.error("Log list can not be null or empty!");
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Log list can not be null or empty!");
            SingletonFactory.getRole().finishedSyncLog();
            return response;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
        threadPoolTaskExecutor.submit(new Thread(() -> applySyncDataLocally(raftLogItemList.getRaftLogItemList())));

        return response;
    }

    private void applySyncDataLocally(List<RaftLogItem> raftLogItemList){
        LogEventHandlerService logEventHandlerService = new LogEventHandlerServiceImpl();

        if (!logEventHandlerService.extractHandlerAndRun(raftLogItemList)){
            log.error("Log applying failed!");
        }
    }
}
