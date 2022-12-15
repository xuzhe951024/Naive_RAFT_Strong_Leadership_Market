package com.zhexu.cs677_lab2.business.rpcServer.service.trading;

import com.zhexu.cs677_lab2.api.bean.MarketTransaction;
import com.zhexu.cs677_lab2.api.bean.basic.BasicResponse;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
public interface TradingLaunchService {
    BasicResponse launchTradingTransaction(MarketTransaction transaction);
}
