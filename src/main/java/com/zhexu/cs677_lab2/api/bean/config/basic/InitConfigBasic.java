package com.zhexu.cs677_lab2.api.bean.config.basic;



import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class InitConfigBasic {
    private String id;
    private Map<String, Address> neighbours;
    private  Address selfAdd;

    private Map<Integer, Product> products;

    public Map<String, Address> getNeighbours() {
        return neighbours;
    }

    public void putNeighbours(String id, Address address){
        this.neighbours.put(id, address);
    }

    public void setNeighbours(Map<String, Address> neighbours) {
        this.neighbours = neighbours;
    }

    public Address getSelfAdd() {
        return selfAdd;
    }

    public void setSelfAdd(Address selfAdd) {
        this.selfAdd = selfAdd;
    }

    public Map<Integer, Product> getProducts() {
        return products;
    }

    public void setProducts(Map<Integer, Product> products) {
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
