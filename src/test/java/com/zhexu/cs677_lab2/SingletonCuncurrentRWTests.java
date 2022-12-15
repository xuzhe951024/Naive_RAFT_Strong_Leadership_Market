package com.zhexu.cs677_lab2;

import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.Product;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.InitConfigForRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/3/22
 **/
@SpringBootTest
@Log4j2
public class SingletonCuncurrentRWTests {
    public void runBefore() {
        InitConfigForRole config = new InitConfigForRole();
        UUID uuidForPeer = UUID.randomUUID();
        config.setNeighbours(new HashMap<String, Address>() {{
            put(uuidForPeer.toString(), new Address());
        }});
        config.setSelfAdd(new Address());
        config.setProducts(new HashMap<>() {{
            put(0, new Product());
        }});
        config.setStock(new HashMap<>() {{
            put(new Product(), 1);
        }});

        SingletonFactory.setInitConfigForRole(config);
    }

    @Test
    public void testCuncurrentRW() throws InterruptedException {
        runBefore();

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        PeerBase peer = SingletonFactory.getRole();

        new Thread(() -> {
            log.info("thread-1: changing leader to uuid1");
            peer.setLeaderID(uuid1);
            try {
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                log.info("thread-2: changing leader to uuid2");

                peer.setLeaderID(uuid2);
                log.info("thread-2: leaderId equals uuid2:" + uuid2.equals(peer.getLeaderID()));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        Thread.sleep(5000);


        log.info("leaderId equals uuid1:" + uuid1.equals(peer.getLeaderID()));
        log.info("leaderId equals uuid2:" + uuid2.equals(peer.getLeaderID()));
    }


}
