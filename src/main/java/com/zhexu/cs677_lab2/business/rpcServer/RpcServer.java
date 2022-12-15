package com.zhexu.cs677_lab2.business.rpcServer;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/


import com.zhexu.cs677_lab2.api.bean.basic.NetModel;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.utils.SerializeUtils;
import com.zhexu.cs677_lab2.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import static com.zhexu.cs677_lab2.constants.Consts.IMPORTANT_LOG_WRAPPER;
import static com.zhexu.cs677_lab2.constants.Consts.SERIALIZATION_BUF_SIZE;


/**
 * Server
 * <p>
 * Using serverSocket offered by JDK
 * <p>
 * Server response result to client after receiving and processing of requests
 */
@Log4j2
@Component
public class RpcServer {
    /**
     * Profiles
     */
    private static Properties properties;

    /**
     * Read configurations
     */
    static {
        properties = new Properties();
        InputStream in = null;
        try {
            in = RpcServer.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
//        openServer(9999);
    }

    /**
     * Launch Server
     */

    public void openServer(Integer port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Service on!");
            ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);

            while (true) {
                Socket socketHandler = serverSocket.accept();
                log.info(socketHandler.getInetAddress() + "-rpc_connected!");
                if (null == threadPoolTaskExecutor){
                    log.warn(IMPORTANT_LOG_WRAPPER);
                    log.warn("threadPoolTaskExecutor equals null!");
                    log.warn(IMPORTANT_LOG_WRAPPER);
                }
                log.debug("Thread pool activated thread number: " + threadPoolTaskExecutor.getActiveCount());
                threadPoolTaskExecutor.submit(new Thread(() -> {
                    try {
                        serviceHandler(socketHandler);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void serviceHandler(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        byte[] buf = new byte[SingletonFactory.getRpcBufferSize()];
        in.read(buf);

        byte[] formatData = formatData(buf);

        OutputStream out = socket.getOutputStream();
        out.write(formatData);
    }

    /**
     * DeSerialize to NetModel
     * <p>
     * Get class name, method name and args
     * <p>
     * Reflect to get instance, then invoke and return byte array
     *
     * @param bs
     * @return
     */
    private byte[] formatData(byte[] bs) {
        try {
            // The received NetModel binary is deserialized to a NetModel model, and then the methods of the service implementation class are called via reflection
            NetModel netModel = (NetModel) SerializeUtils.deserialize(bs);

            log.debug("Received netModel: " + netModel.toString());

            String className = netModel.getClassName();
            String[] types = netModel.getTypes();
            Object[] args = netModel.getArgs();

            /*
                1. Map the interface to the implementation class via Map, and retrieve the implementation class methods from map

                Map<String, String> map = new HashMap<>();
                map.put("rpc.server.service.HelloService", "rpc.server.service.impl.HelloServiceImpl");
                Class<?> clazz = Class.forName(map.className);
             */

            /*
                2. Put under configuration file, read configuration file read
             */
            Class<?> clazz = Class.forName(getPropertyValue(className));
            Class<?>[] typeClazzs = null;

            if (types != null) {
                typeClazzs = new Class[types.length];
                for (int i = 0; i < types.length; i++) {
                    typeClazzs[i] = Class.forName(types[i]);
                }
            }

            Method method = clazz.getMethod(netModel.getMethod(), typeClazzs);
            Object object = method.invoke(clazz.newInstance(), args);

            byte[] bytes = SerializeUtils.serialize(object);
            return bytes;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Fail to format data");
    }

    private static String getPropertyValue(String key) {
        return properties.getProperty(key);
    }

}
