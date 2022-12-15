package com.zhexu.cs677_lab2.api.bean;

import com.zhexu.cs677_lab2.api.bean.basic.Product;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.zhexu.cs677_lab2.constants.Consts.TRANSACTION_ROLLING_BACK_PREFIX;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/10/22
 **/
public class MarketTransaction extends RaftTransBase implements Serializable {
    private UUID transactionId = UUID.randomUUID();
    private UUID buyer;
    private UUID seller;
    private Product product;
    private Integer number;
    private Boolean successful = Boolean.TRUE;
    private String remark;
    private Integer stock;
    private UUID eventId = UUID.randomUUID();
    private Long localTimeStamp = System.currentTimeMillis();

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getBuyer() {
        return buyer;
    }

    public void setBuyer(UUID buyer) {
        this.buyer = buyer;
    }

    public UUID getSeller() {
        return seller;
    }

    public void setSeller(UUID seller) {
        this.seller = seller;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if(StringUtils.isEmpty(this.remark)){
            this.remark = product.getProductName();
        }
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Boolean isMainSeller(UUID id){
        return null != this.seller && null != id && this.seller.equals(id);
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setRollBackTransaction(MarketTransaction rollBackTransaction){
        this.eventId = rollBackTransaction.getEventId();
        this.product = rollBackTransaction.getProduct();
        this.seller = rollBackTransaction.getSeller();
        this.buyer = rollBackTransaction.getBuyer();
        this.remark = TRANSACTION_ROLLING_BACK_PREFIX + rollBackTransaction.getTransactionId();
        this.number = -rollBackTransaction.getNumber();
        this.successful = Boolean.FALSE;
    }

    public Long getLocalTimeStamp() {
        return localTimeStamp;
    }

    @Override
    public String toString() {
        return "MarketTransaction{" +
                "transactionId=" + transactionId +
                ", buyer=" + buyer +
                ", seller=" + seller +
                ", product=" + product +
                ", number=" + number +
                ", successful=" + successful +
                ", remark='" + remark + '\'' +
                ", stock=" + stock +
                ", eventId=" + eventId +
                '}';
    }
}
