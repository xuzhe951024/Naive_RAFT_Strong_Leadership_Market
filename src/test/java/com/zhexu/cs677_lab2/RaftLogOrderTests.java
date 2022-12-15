package com.zhexu.cs677_lab2;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
@Log4j2
public class RaftLogOrderTests {

    public static void main(String[] args) {
        List<RaftLogItem> logItemList = generateRaftLogList(10);
        log.info("before order:");
        logItemList.forEach((e) -> {
            log.info(e.getIndex());
        });

        Collections.sort(logItemList, logItemList.get(0));
        log.info("after order:");
        logItemList.forEach((e) -> {
            log.info(e.getIndex());
        });
    }

    private static List<RaftLogItem> generateRaftLogList(int number) {
        Random random = new Random();
        List raftList = new LinkedList();
        for (int i = 0; i < number; i++) {
            RaftLogItem raftLogItem = new RaftLogItem();
            raftLogItem.setIndex((long) random.nextInt(number));
            raftList.add(raftLogItem);
        }
        return raftList;
    }

}
