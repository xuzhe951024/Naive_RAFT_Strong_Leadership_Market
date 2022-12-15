package com.zhexu.cs677_lab2.business.logEventHandler.Impl;

import com.zhexu.cs677_lab2.api.bean.MarketTransaction;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.logEventHandler.Impl.abstracts.EventHandlerBase;
import com.zhexu.cs677_lab2.constants.ResponseCode;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

import static com.zhexu.cs677_lab2.constants.Consts.ENTER;
import static com.zhexu.cs677_lab2.constants.Consts.IMPORTANT_LOG_WRAPPER;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/10/22
 **/
@Log4j2
public class MarketTransactionHandlerImpl extends EventHandlerBase {
    private MarketTransaction transactionBean;
    private UUID logId;
    private UUID eventId;
    private Role peer = SingletonFactory.getRole();

    /**
     * @param eventBean
     * @param logId
     * @param eventId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean run(Object eventBean, UUID logId, UUID eventId) throws Exception {
        this.transactionBean = (MarketTransaction) eventBean;
        this.logId = logId;
        this.eventId = eventId;

        if (null == this.transactionBean) {
            log.error("Event bean can not be null!");
            return Boolean.FALSE;
        }

        if (!peer.isFollower()) {
            log.info("Peer: " +
                    peer.getSelfAddress().getDomain() +
                    "dose not legally apply the transaction!");
            return Boolean.FALSE;
        }

        log.info("Save:\n" +
                saveEventBeanToCouchDb(this.transactionBean, this.logId, this.eventId) +
                "\nto couchDb");

        if (!peer.isSyncLogInProgress() && transactionBean.getBuyer().equals(peer.getId())) {
            Long time = System.currentTimeMillis() - transactionBean.getLocalTimeStamp();
            SingletonFactory.setTransactionTime(SingletonFactory.getTransactionTime() + time);
            SingletonFactory.setTransactionNum(SingletonFactory.getTransactionNum() + 1);

            log.info(IMPORTANT_LOG_WRAPPER + ENTER +
                    "Transaction: " +
                    transactionBean.getTransactionId() +
                    " takes " +
                    time +
                    " ms" +
                    "\nAvarage transaction time: " +
                    SingletonFactory.getTransactionTime() / SingletonFactory.getTransactionNum() +
                    " ms\nTransaction details:" +
                    transactionBean.toString() +
                    IMPORTANT_LOG_WRAPPER);
        }

        if (peer.isSyncLogInProgress() && peer.isSeller() && transactionBean.isMainSeller(peer.getId())) {
            peer.getStock().put(transactionBean.getProduct(), transactionBean.getStock());
        }

        return Boolean.TRUE;
    }

    public MarketTransaction getTransactionBean() {
        return transactionBean;
    }

    public void setTransactionBean(MarketTransaction transactionBean) {
        this.transactionBean = transactionBean;
    }

    public UUID getLogId() {
        return logId;
    }

    public void setLogId(UUID logId) {
        this.logId = logId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

}
