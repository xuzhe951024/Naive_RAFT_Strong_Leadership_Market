package com.zhexu.cs677_lab2.business.rpcServer.service.impl.trading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.MarketTransaction;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.business.raft.EventApplyInitiateService;
import com.zhexu.cs677_lab2.business.raft.Impl.EventApplyInitiateServiceImpl;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab2.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab2.business.rpcServer.service.trading.TradingLaunchService;
import com.zhexu.cs677_lab2.business.rpcServer.service.trading.TradingRespService;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;

import static com.zhexu.cs677_lab2.constants.Consts.MARKET_LOG_PREFIX;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
@Log4j2
public class TradingLaunchServiceImpl implements TradingLaunchService {
    Role role = SingletonFactory.getRole();

    /**
     * @param transaction
     * @return
     */
    @Override
    public BasicResponse launchTradingTransaction(MarketTransaction transaction) {
        BasicResponse response = new BasicResponse();
        log.info(MARKET_LOG_PREFIX + "Market transaction request reveived:" + transaction.toString());

        if (!role.isLeader()) {
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Transaction could only handle by leader:\npeer:" +
                    role.getSelfAddress().getDomain() +
                    "is not a leader!");
            log.error(MARKET_LOG_PREFIX + "Self is not a leader, can not handle transaction requests");
            return response;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
        threadPoolTaskExecutor.submit(new Thread(() -> {
            try {
                startTrading(transaction);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        return response;
    }

    private void startTrading(MarketTransaction transaction) throws Exception {

        log.info(MARKET_LOG_PREFIX + "Starting find seller");

        List<Address> stockAvailableList = new LinkedList<>(){{
            role.getNeighbourPeerMap().forEach((k, v) -> {
                RPCInvocationHandler handler = new RPCInvocationHandler(v);
                TradingRespService tradingRespService = ProxyFactory.getInstance(TradingRespService.class, handler);
                if (tradingRespService.checkIfProductAvailable(transaction.getProduct().getProductId(),
                        transaction.getNumber())){
                    add(v);
                }
            });
        }};

        if (stockAvailableList.isEmpty()) {
            log.info(MARKET_LOG_PREFIX +
                    "No product:" +
                    transaction.getProduct().getProductName() +
                    "stock: " +
                    transaction.getStock() +
                    "available!");
            transaction.setSuccessful(Boolean.FALSE);
            transaction.setRemark("Stock insufficient!");
        }

        log.info(MARKET_LOG_PREFIX + "Seller List: " + stockAvailableList.toString());

        ObjectMapper objectMapper = new ObjectMapper();

        for (Address sellerAdd : stockAvailableList) {
            RPCInvocationHandler handler = new RPCInvocationHandler(sellerAdd);
            TradingRespService tradingRespService = ProxyFactory.getInstance(TradingRespService.class, handler);
            Integer stock = tradingRespService.consumeProduct(transaction.getProduct().getProductId(), transaction.getNumber());
            if (stock.intValue() < 0) {
                continue;
            }

            role.getRaftBase().increaseIndex();
            transaction.setTermAndIndex(role.getRaftBase());
            transaction.setSeller(role.getId());
            transaction.setStock(stock);
            EventApplyInitiateService eventApplyInitiateService = new EventApplyInitiateServiceImpl();

            RaftLogItem logItem = new RaftLogItem();

            logItem.setEventClassName(transaction.getClass().getName());
            logItem.setEventJSONString(objectMapper.writeValueAsString(transaction));
            logItem.setTermAndIndex(transaction);
            logItem.setEventId(transaction.getEventId());

            eventApplyInitiateService.setLogItem(logItem);
            eventApplyInitiateService.broardCast();

            Thread.sleep(TimerConfig.getLogBroadCastApplyWaitTime());

            if (!eventApplyInitiateService.collectedEnougthResponse()) {
                log.info(MARKET_LOG_PREFIX +
                        "Did not collect enough broadcast responses for transaction:" +
                        transaction.getTransactionId() +
                        "rolling back now.");

                MarketTransaction rollbackTransaction = new MarketTransaction();

                role.getRaftBase().increaseIndex();

                rollbackTransaction.setRollBackTransaction(transaction);
                rollbackTransaction.setTermAndIndex(role.getRaftBase());

                Integer rollBackStock = tradingRespService.consumeProduct(transaction.getProduct().getProductId(),
                        -transaction.getNumber());
                rollbackTransaction.setStock(rollBackStock);

                eventApplyInitiateService.rollback(() -> {
                    EventApplyInitiateService rollBackApply = new EventApplyInitiateServiceImpl();

                    RaftLogItem rollBackLogItem = new RaftLogItem();

                    rollBackLogItem.setEventClassName(rollbackTransaction.getClass().getName());
                    rollBackLogItem.setEventJSONString(objectMapper.writeValueAsString(rollbackTransaction));
                    rollBackLogItem.setTermAndIndex(rollbackTransaction);
                    rollBackLogItem.setEventId(transaction.getEventId());

                    rollBackApply.setLogItem(rollBackLogItem);
                    rollBackApply.broardCast();
                    rollBackApply.cleanMessageBroadCastMap();
                });
            }

            eventApplyInitiateService.commit();
            log.info(MARKET_LOG_PREFIX +
                    "Transaction: " +
                    transaction.toString() +
                    "\nhas been committed");

            break;

        }

    }
}
