package com.zhexu.cs677_lab2.business.rpcServer.service;


import com.zhexu.cs677_lab2.api.bean.Person;

// 公共服务接口类
public interface HelloService {
    String sayHello(String name);

    Person getPerson(String name);

//    Boolean lookUpReceiver();
}
