package com.zhexu.cs677_lab2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.InitConfigForRole;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;

import static com.zhexu.cs677_lab2.constants.Consts.*;
import static com.zhexu.cs677_lab2.constants.Consts.IMPORTANT_LOG_WRAPPER;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@SpringBootApplication
@EnableScheduling
@Log4j2

public class Cs677Lab2Application {

    public static void main(String[] args) {

        SpringApplication.run(Cs677Lab2Application.class, args);

    }

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(new File(System.getenv(INIT_ENV_JSON_FILENAME)));
        InitConfigForRole initConfigForRole = objectMapper.readValue(jsonNode.toString(), InitConfigForRole.class);
        SingletonFactory.setInitConfigForRole(initConfigForRole);
        Role role = SingletonFactory.getRole();
        log.info("Initiating from file: " + role.toString());
        log.info(IMPORTANT_LOG_WRAPPER);
        log.debug("Times:" +
                "\npulse timeout interval: " + TimerConfig.getPulseTimeOut() +
                "\nsend pulse interval: " + TimerConfig.getSendPulseTime() +
                "\nelection timeout interval: " + TimerConfig.getElectionWaitTime() +
                "\nlog broadcast apply timeout interval" + TimerConfig.getLogBroadCastApplyWaitTime());

        log.debug("Stock info: " + SingletonFactory.getRole().getStock().toString());
        log.debug("Product list info: " + SingletonFactory.getRole().getProductMap().toString());
        log.info(IMPORTANT_LOG_WRAPPER);
    }

}
