package com.zhexu.cs677_lab2.api.restful.controller;

import com.zhexu.cs677_lab2.api.bean.MarketTransaction;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.trading.TradingLaunchService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/13/22
 **/
@RestController

@Log4j2
@RequestMapping("/market")
public class MarketTransactionController {

    Role role = SingletonFactory.getRole();

    @RequestMapping("/add")
    public BasicResponse newMarketTransaction() {
        BasicResponse response = new BasicResponse();

        if (!role.isFollower()
                || !role.isBuyer()
                || role.isSyncLogInProgress()
                || null ==  role.getLeaderID()
                || null == role.getNeighbourPeerMap().get(role.getLeaderID())){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("System not ready");

            if (!role.isFollower()){
                log.debug("is not follower");
            }else if (!role.isBuyer()){
                log.debug("is not buyer");
            }else if (role.isSyncLogInProgress()){
                log.debug("is in sync log process");
            } else if (null == role.getLeaderID()) {
                log.debug("no leader available");
            } else if (null == role.getNeighbourPeerMap().get(role.getLeaderID())) {
                log.debug("can not find leader add");
            }

            return response;
        }

        RPCInvocationHandler handler = new RPCInvocationHandler(role.getNeighbourAdd(role.getLeaderID()));
        TradingLaunchService tradingLaunchService = ProxyFactory.getInstance(TradingLaunchService.class, handler);

        Random random = new Random();
        MarketTransaction transaction = new MarketTransaction();
        transaction.setBuyer(role.getId());
        transaction.setNumber(random.nextInt(SingletonFactory.getMaxStock()));
        transaction.setProduct(role.getProductMap().get(random.nextInt(role.getProductMapSize())));

        log.info("Start to send purchase request to leader: " +
                role.getLeaderID() +
                "@" +
                role.getNeighbourAdd(role.getLeaderID()).getDomain());

        response = tradingLaunchService.launchTradingTransaction(transaction);

        return response;
    }

}
