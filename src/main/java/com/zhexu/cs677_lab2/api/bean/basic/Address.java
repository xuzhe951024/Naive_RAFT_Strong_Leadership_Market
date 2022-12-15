package com.zhexu.cs677_lab2.api.bean.basic;

import java.io.Serializable;

import static com.zhexu.cs677_lab2.constants.Consts.Colon;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class Address implements Serializable {
    private static final long serialVersionUID = 5542635716484888244L;
    private String domain;
    private Integer port;

    public Address(String domain, Integer port) {
        this.domain = domain;
        this.port = port;
    }

    public Address(){

    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return domain + Colon + port;
    }
}
