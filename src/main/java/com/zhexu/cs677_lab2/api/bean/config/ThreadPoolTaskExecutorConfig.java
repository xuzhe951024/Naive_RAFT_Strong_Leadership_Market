package com.zhexu.cs677_lab2.api.bean.config;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public class ThreadPoolTaskExecutorConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor poolExecutor = new ThreadPoolTaskExecutor();
        poolExecutor.setCorePoolSize(5);
        poolExecutor.setMaxPoolSize(15);
        poolExecutor.setQueueCapacity(100);
        poolExecutor.setKeepAliveSeconds(300);
        poolExecutor.setRejectedExecutionHandler((RejectedExecutionHandler) new ThreadPoolExecutor.CallerRunsPolicy());
        poolExecutor.setThreadNamePrefix("my-task-pool-");

        return poolExecutor;
    }

}
