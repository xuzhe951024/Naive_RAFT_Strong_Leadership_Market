package com.zhexu.cs677_lab2;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@SpringBootTest
@Log4j2
public class LogTests {

    @Test
    void test(){
        Thread loggerA =  new Thread(() -> loggerA());

        Thread loggerB = new Thread(() -> loggerB());

        loggerA.start();
        loggerB.start();
    }

    void loggerA(){
        log.info("info");
        log.debug("debug");
        log.error("error");
    }

    void loggerB(){
        log.info("info");
        log.debug("debug");
        log.error("error");
    }
}
