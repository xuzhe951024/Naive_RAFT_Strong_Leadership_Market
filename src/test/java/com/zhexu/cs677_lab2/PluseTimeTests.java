package com.zhexu.cs677_lab2;

import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/

@SpringBootTest
@Log4j2
public class PluseTimeTests {
    @Test
    public void testPulse() throws InterruptedException {
        System.out.println(0L != 1L);
    }

}
