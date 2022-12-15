package com.zhexu.cs677_lab2.utils;

import com.zhexu.cs677_lab2.api.bean.basic.Address;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.zhexu.cs677_lab2.constants.Consts.*;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/6/22
 **/
@Log4j2
public class NetworkLatencyDetector {
    private Long averageLatency = 0l;

    public Long getAverageLatency() {
        return averageLatency;
    }

    public Boolean isReachable(Address address) throws Exception {
        BufferedReader br = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(COMMAND_PING + address.getDomain());
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "GB2312");
            br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < LATENCY_TEST_TIME; i++) {
                line = br.readLine();
                log.info(line);
                String[] words = line.split(PING_LATENCY_SPLITER);
                if (words.length != TWO) {
                    log.info(address.getDomain() +
                            " is not reachable");
                    return Boolean.FALSE;
                }
                this.averageLatency += Long.parseLong(words[1].split(" ")[0].split("\\.")[0]);
            }

            this.averageLatency /= LATENCY_TEST_TIME;
            log.info("Average latency to " +
                    address.getDomain() +
                    ": " +
                    this.averageLatency +
                    " ms");
        } catch (Exception e) {
            throw new Exception();
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return Boolean.TRUE;
    }

    public static void main(String[] args) throws Exception {
        Address address = new Address();
        address.setDomain("127.0.0.1");
        address.setPort(80);
        System.out.println(new NetworkLatencyDetector().isReachable(address));
    }
}
