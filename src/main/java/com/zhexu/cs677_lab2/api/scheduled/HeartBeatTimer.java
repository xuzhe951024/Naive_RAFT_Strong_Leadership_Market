package com.zhexu.cs677_lab2.api.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.business.raft.ElectionInitialzeService;
import com.zhexu.cs677_lab2.business.raft.SendPulseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

import static com.zhexu.cs677_lab2.constants.Consts.MILLISECOND;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@Component
@Log4j2
@Order(value = 2)
public class HeartBeatTimer implements CommandLineRunner {

    @Autowired
    SendPulseService sendPulseService;

//    @Qualifier("com.zhexu.cs677_lab2.business.rpcServer.service.impl.raft.ElectionServiceImpl")

    @Autowired
    ElectionInitialzeService electionInitialzeService;


    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        Long sendInterval = TimerConfig.getSendPulseTime();
        Long pulseTimeout = TimerConfig.getPulseTimeOut();

        Timer sendHeartPulseTimer = new Timer();
        TimerTask sendHeartPulseTask = new TimerTask() {
            @Override
            public void run() {
                PeerBase peer = SingletonFactory.getRole();
                if(peer.isLeader()){

                    sendPulseService.sendPulseToFollowers();
                }
            }
        };

        Timer heartPulseTimeoutTimer = new Timer();
        TimerTask heartPulseTimeoutTask = new TimerTask() {
            @Override
            public void run() {
                PeerBase peer = SingletonFactory.getRole();

                if (peer.isSyncLogInProgress()){
                    log.info("Log sync in progress, pause checking heart pulse time out.");
                    SingletonFactory.setCurrentTime();
                    return;
                }

                if (!peer.isFollower()){
                    return;
                }

                if (pulseTimeout <= System.currentTimeMillis() - SingletonFactory.getCurrentTime()){
                    log.info("follower heart pulse time out: " + TimerConfig.getPulseTimeOut() + MILLISECOND);
                    peer.setPulseTimeout(Boolean.TRUE);
                    try {
                        electionInitialzeService.startElection();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        };


        sendHeartPulseTimer.schedule(sendHeartPulseTask, sendInterval, sendInterval);
        heartPulseTimeoutTimer.schedule(heartPulseTimeoutTask, pulseTimeout, pulseTimeout);
    }
}
