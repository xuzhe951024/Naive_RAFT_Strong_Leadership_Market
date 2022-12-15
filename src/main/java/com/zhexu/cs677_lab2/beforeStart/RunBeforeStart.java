package com.zhexu.cs677_lab2.beforeStart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.InitConfigForRole;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.api.scheduled.HeartBeatTimer;
import com.zhexu.cs677_lab2.business.raft.ElectionInitialzeService;
import com.zhexu.cs677_lab2.business.raft.Impl.ElectionInitialzeServiceImpl;
import com.zhexu.cs677_lab2.business.raft.Impl.SendPulseServiceImpl;
import com.zhexu.cs677_lab2.business.raft.SendPulseService;
import com.zhexu.cs677_lab2.business.rpcServer.RpcServer;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import static com.zhexu.cs677_lab2.constants.Consts.*;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/7/22
 **/
@Component
@Log4j2
@Order(value = 1)
public class RunBeforeStart implements CommandLineRunner {

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) {
        SingletonFactory.setRpcBufferSize(Integer.parseInt(args[TWO]));
        Role role = SingletonFactory.getRole();
        RpcServer rpcServer = new RpcServer();
        log.debug("Thread pool size: "+
                threadPoolTaskExecutor.getPoolSize() +
                " Thread pool maxSize: " +
                threadPoolTaskExecutor.getMaxPoolSize() +
                " Thread queue size: "+
                threadPoolTaskExecutor.getQueueSize());
        threadPoolTaskExecutor.submit(new Thread(() -> {
            rpcServer.openServer(role.getSelfAddress().getPort());
        }));
    }
}
