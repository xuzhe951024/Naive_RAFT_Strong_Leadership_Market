package com.zhexu.cs677_lab2.api.bean;



import com.zhexu.cs677_lab2.api.bean.basic.Address;

import java.io.Serializable;
import java.util.Map;

/**
 * 普通公共Bean
 */
public class Person implements Serializable {

    private static final long serialVersionUID = 5542635716484888244L;

    private String name;
    private Address address;

    private Integer age=30;
    private Map<String, Long> test;

    public Person(String name) {
        this.name = name;
    }

    public Person(){}

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
