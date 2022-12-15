package com.zhexu.cs677_lab2.api.bean.config.basic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/

@Component
public class CouchDBInfo {

    @Value(value = "${couchdb.host}")
    private String host;

    @Value(value = "${couchdb.port}")
    private String port;

    @Value(value = "${couchdb.database}")
    private String database;

    @Value(value = "${couchdb.username}")
    private String username;

    @Value(value = "${couchdb.password}")
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
