package com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft.basic;

import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import lombok.extern.log4j.Log4j2;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
@Log4j2
public class BasicImpl {
    protected PeerBase peer = SingletonFactory.getRole();
    protected BasicResponse checkIfLogSyncInProgress(){
        BasicResponse response = new BasicResponse();

        if(peer.isSyncLogInProgress()){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Log sync in progress");
            log.info("Log sync in progress, pause election voting.");
            return response;
        }

        return response;
    }
}
