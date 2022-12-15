package com.zhexu.cs677_lab2.api.bean.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.zhexu.cs677_lab2.api.bean.basic.Product;
import com.zhexu.cs677_lab2.api.bean.config.basic.InitConfigBasic;
import com.zhexu.cs677_lab2.utils.ProductDeSerializer;

import java.util.Map;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class InitConfigForRole extends InitConfigBasic {

    private Boolean isBuyer = Boolean.FALSE;
    private Boolean isSeller = Boolean.FALSE;


    @JsonProperty("stock")
    @JsonDeserialize(keyUsing = ProductDeSerializer.class)
    private Map<Product, Integer> stock;

    private Integer maxStock;


    public Map<Product, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<Product, Integer> stock) {
        this.stock = stock;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }

    public Boolean isBuyer() {
        return isBuyer;
    }

    public void setBuyer(Boolean buyer) {
        isBuyer = buyer;
    }

    public Boolean isSeller() {
        return isSeller;
    }

    public void setSeller(Boolean seller) {
        isSeller = seller;
    }
}
