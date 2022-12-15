package com.zhexu.cs677_lab2.api.bean.basic.factories;


import com.zhexu.cs677_lab2.api.bean.Role;
import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.Product;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.config.InitConfigForRole;
import com.zhexu.cs677_lab2.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab2.utils.NetworkLatencyDetector;

import java.util.*;
import java.util.logging.FileHandler;

import static com.zhexu.cs677_lab2.constants.Consts.ONE_THOUSAND;
import static com.zhexu.cs677_lab2.constants.Consts.SERIALIZATION_BUF_SIZE;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class SingletonFactory {
    private volatile static Role role;
    private volatile static Map<String, FileHandler> fileHandlerMap;
    private volatile static List<Product> productList;
    private volatile static long currentTime;
    private static int maxStock;
    private static InitConfigForRole initConfigForRole = null;
    private volatile static long networkLatency = 1L;
    private static Integer rpcBufferSize = SERIALIZATION_BUF_SIZE;
    private volatile static int transactionNum = 0;
    private volatile static long transactionTime = 0L;

    private volatile static int testNum;


    public static Long getNetworkLatency() {
        return networkLatency;
    }

    public static void setNetworkLatency(Long networkLatency) {
        SingletonFactory.networkLatency = networkLatency;
    }

    private SingletonFactory() {
    }

    public static void setInitConfigForRole(InitConfigForRole config) {
        initConfigForRole = config;
    }

    public static void setProductList(List<Product> products){
        productList = new ArrayList<>(products);
    }

    public static void setMaxStock(Integer number){
        maxStock = number;
    }


    public static Integer getMaxStock(){
        return maxStock;
    }


    public static List<Product> getProductList(){
        return productList;
    }

    public static String getSelfDomain(){
        return getRole().getSelfAddress().getDomain();
    }

    public static Role getRole() {
        if (null == role) {
            synchronized (Role.class) {
                Map<UUID, Address> neighbouMap = new HashMap<>();
                initConfigForRole.getNeighbours().forEach((k, v) -> {
                    NetworkLatencyDetector networkLatencyDetector = new NetworkLatencyDetector();
                    try {
                        if (!networkLatencyDetector.isReachable(v)){
                            return;
                        }
                        SingletonFactory.networkLatency = networkLatencyDetector.getAverageLatency();
                        neighbouMap.put(UUID.fromString(k), v);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                SingletonFactory.networkLatency /= neighbouMap.size();

                if (null == role) {
                    Map<Integer, Product> products = new HashMap<>();
                    Map<Product, Integer> stocks = new HashMap<>();

                    if (initConfigForRole.isBuyer()){
                        products = initConfigForRole.getProducts();
                    }

                    if (initConfigForRole.isSeller()){
                        stocks = initConfigForRole.getStock();
                    }

                    role = new Role(UUID.fromString(initConfigForRole.getId()),
                            neighbouMap,
                            initConfigForRole.getSelfAdd(),
                            products,
                            stocks);
                    RaftTransBase raftTransBase = new RaftTransBase();
                    raftTransBase.setIndex(0l);
                    raftTransBase.setTerm(0l);
                    role.setRaftBase(raftTransBase);
                    role.becomeFollwer();
                    if (initConfigForRole.isBuyer()){
                        role.becomeBuyer();
                    }
                    if (initConfigForRole.isSeller()){
                        role.becomeSeller();
                    }
                }
                role.setLeaderID(null);
                TimerConfig.initiateTime(SingletonFactory.networkLatency);
                SingletonFactory.currentTime = System.currentTimeMillis();
            }
        }
        return role;
    }


    public static Long getCurrentTime() {
        return currentTime;
    }

    public static void setCurrentTime() {
        SingletonFactory.currentTime = System.currentTimeMillis();
    }

    public static Boolean notReady(){
        return null == SingletonFactory.initConfigForRole && !TimerConfig.getIsReady();
    }

    public static Integer getRpcBufferSize() {
        return rpcBufferSize;
    }

    public static void setRpcBufferSize(Integer rpcBufferSize) {
        SingletonFactory.rpcBufferSize = rpcBufferSize;
    }

    public static int getTransactionNum() {
        return transactionNum;
    }

    public static void setTransactionNum(int transactionNum) {
        SingletonFactory.transactionNum = transactionNum;
    }

    public static long getTransactionTime() {
        return transactionTime;
    }

    public static void setTransactionTime(long transactionTime) {
        SingletonFactory.transactionTime = transactionTime;
    }

    public static int getTestNum() {
        return testNum;
    }

    public static void setTestNum(int testNum) {
        SingletonFactory.testNum = testNum;
    }
}
