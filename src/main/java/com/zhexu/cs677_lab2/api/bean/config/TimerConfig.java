package com.zhexu.cs677_lab2.api.bean.config;

import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static com.zhexu.cs677_lab2.constants.Consts.*;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class TimerConfig {
    private static Properties properties = readProperties(TIME_CONFIG_FILE);

    private volatile static Long pulseTimeOut;
    private volatile static Long sendPulseTime;
    private volatile static Long electionWaitTime;
    private volatile static Long logBroadCastApplyWaitTime;
    private volatile static Boolean isReady = Boolean.FALSE;

    private static Properties readProperties(String confFile) {
        final Properties properties = new Properties();
        try {
            final ClassPathResource resource = new ClassPathResource(confFile);
            properties.load(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Long getPulseTimeOut() {
        return pulseTimeOut;
    }

    public static void setPulseTimeOut(Long pulseTimeOut) {
        TimerConfig.pulseTimeOut = pulseTimeOut;
    }

    public static Long getSendPulseTime() {
        return sendPulseTime;
    }

    public static void setSendPulseTime(Long sendPulseTime) {
        TimerConfig.sendPulseTime = sendPulseTime;
    }

    public static Long getElectionWaitTime() {
        return electionWaitTime;
    }

    public static void setElectionWaitTime(Long electionWaitTime) {
        TimerConfig.electionWaitTime = electionWaitTime;
    }

    public static Long getLogBroadCastApplyWaitTime() {
        return logBroadCastApplyWaitTime;
    }

    public static void setLogBroadCastApplyWaitTime(Long logBroadCastApplyWaitTime) {
        TimerConfig.logBroadCastApplyWaitTime = logBroadCastApplyWaitTime;
    }

    public static void initiateTime(Long networkLantency){

        Integer pulseBase = Integer.parseInt((String) properties.get(PULSE_TIMEOUT_BASE));
        Integer broadcastBase = Integer.parseInt((String) properties.get(BROADCAST_TIMEOUT_BASE));

        Random ra = new Random();

//        TimerConfig.sendPulseTime = Math.round(1000L * Math.log(ra.nextInt(pulseBase) + 2L));;
//        TimerConfig.pulseTimeOut = Math.round(1000L * Math.log(ra.nextInt(pulseBase) + pulseBase + 2L) + networkLantency);

        TimerConfig.sendPulseTime = (long)ra.nextInt(pulseBase);
        TimerConfig.pulseTimeOut = ra.nextInt(pulseBase) + pulseBase + networkLantency;

        TimerConfig.electionWaitTime = ra.nextInt(broadcastBase) + networkLantency * SingletonFactory.getRole().getNeighbourPeerMap().size();
        TimerConfig.logBroadCastApplyWaitTime = ra.nextInt(broadcastBase) + networkLantency;

        TimerConfig.isReady = Boolean.TRUE;

    }

    public static Boolean getIsReady() {
        return isReady;
    }

    public static void main(String[] args) {

    }

}
