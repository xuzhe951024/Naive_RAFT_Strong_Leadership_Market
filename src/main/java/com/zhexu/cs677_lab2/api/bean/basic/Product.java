package com.zhexu.cs677_lab2.api.bean.basic;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Objects;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/
public class Product implements Serializable {
    private Integer productId;
    private String productName;

    public Product(Integer productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public Product(){

    }

    public Product(String planString){
        String[] split = planString.split(":");
        this.productId = Integer.valueOf(split[0].trim());
        this.productName = split[1].trim();
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    @JsonValue
    public String toString() {
        return productId + ":" + productName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId.equals(product.productId) && productName.equals(product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName);
    }
}
