package com.zhexu.cs677_lab2.beforeStart.trading;

import com.zhexu.cs677_lab2.api.bean.MarketTransaction;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.trading.TradingLaunchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.zhexu.cs677_lab2.constants.Consts.*;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/13/22
 **/
@Component
@Log4j2
@Order(value = 3)
public class LaunchMarket implements CommandLineRunner {

    Role role = SingletonFactory.getRole();

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        Long sleepBeforeStart = Long.parseLong(args[ZERO]);
        SingletonFactory.setTestNum(Integer.parseInt(args[ONE]));
        Integer maxStock = Integer.parseInt(args[THREE]);

        SingletonFactory.setMaxStock(maxStock);

//        Thread.sleep(sleepBeforeStart);
//
//        for (int i = 0; i < numberOfTests; i++) {
//
//            Thread.sleep(10);
//
//        }

        int counter = 0;

        Timer tradingTimer = new Timer();
        TimerTask sendHeartPulseTask = new TimerTask() {
            @Override
            public void run() {

                if (SingletonFactory.getTransactionNum() >= SingletonFactory.getTestNum()){
                    log.info(IMPORTANT_LOG_WRAPPER);
                    log.info("finished !");
                    log.info(IMPORTANT_LOG_WRAPPER);
                    cancel();
                }
                startBusiness();

            }
        };

        tradingTimer.schedule(sendHeartPulseTask, sleepBeforeStart, 1000);
    }

    private Boolean startBusiness(){
        if (!role.isFollower()
                || !role.isBuyer()
                || role.isSyncLogInProgress()
                || null ==  role.getLeaderID()
                || null == role.getNeighbourPeerMap().get(role.getLeaderID())){
            return Boolean.FALSE;
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

        log.info(tradingLaunchService.launchTradingTransaction(transaction));
        return Boolean.TRUE;
    }

}
