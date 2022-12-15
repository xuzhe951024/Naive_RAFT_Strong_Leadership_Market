package com.zhexu.cs677_lab2.business.rpcServer.service.impl;


import com.zhexu.cs677_lab2.api.bean.Person;
import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.business.rpcServer.service.HelloService;

/**
 * RPC服务实现类
 *
 */
public class HelloServiceImpl implements HelloService {

    private Person person = new Person("lisi");

    @Override
    public String sayHello(String name) {
        return "Say hello to " + name;
    }

    @Override
    public Person getPerson(String name) {
//        Person person = new Person();
//        person.setName(name);
//        person.setAge(22);
        Address address = new Address();
        address.setPort(1111);
        address.setDomain("localhost");
        person.setAddress(address);

        return person;
    }
}
