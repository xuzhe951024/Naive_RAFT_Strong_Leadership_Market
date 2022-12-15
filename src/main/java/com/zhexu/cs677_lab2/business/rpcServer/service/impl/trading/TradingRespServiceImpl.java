package com.zhexu.cs677_lab2.business.rpcServer.service.impl.trading;

import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.rpcServer.service.trading.TradingRespService;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
public class TradingRespServiceImpl implements TradingRespService {
    Role role = SingletonFactory.getRole();

    /**
     * check if product and stock available
     * @param productId
     * @param stock
     * @return
     */
    @Override
    public Boolean checkIfProductAvailable(Integer productId, Integer stock) {
        if (!role.isSeller()){
            return Boolean.FALSE;
        }
        return role.getStockByProductId(productId) - stock >= 0;
    }

    /**
     * @param productId
     * @param number
     * @return
     */
    @Override
    public Integer consumeProduct(Integer productId, Integer number) {
        if(!role.isSeller()){
            return -1;
        }
        return role.consumeStockByProductId(productId, number);
    }
}
