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
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import static com.zhexu.cs677_lab2.constants.Consts.SERIALIZATION_BUF_SIZE;


/**
 * Server
 *
 * Using serverSocket offered by JDK
 *
 * Server response result to client after receiving and processing of requests
 *
 */
@Log4j2
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
     *
     */
    public static void openServer(Integer port) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Service on!");

            while (true) {
                socket = serverSocket.accept();
//               log.info(socket.getInetAddress() + "-rpc_connected!");

                InputStream in = socket.getInputStream();
                byte[] buf = new byte[SingletonFactory.getRpcBufferSize()];
                in.read(buf);

                byte[] formatData = formatData(buf);

                OutputStream out = socket.getOutputStream();
                out.write(formatData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理接收到的数据, 通过反序列化得到传递的NetModel!
     *
     * 然后得到接口名, 方法名, 参数, 参数类型;
     *
     * 最后通过JDK反射, 调用实现类的方法, 并将结果序列化后, 返回byte数组
     *
     * @param bs
     * @return
     */
    private static byte[] formatData(byte[] bs) {
        try {
            // 收到的NetModel二进制反序列化为NetModel模型, 然后通过反射调用服务实现类的方法
            NetModel netModel = (NetModel) SerializeUtils.deserialize(bs);
            String className = netModel.getClassName();
            String[] types = netModel.getTypes();
            Object[] args = netModel.getArgs();

            /*
                1. 通过Map来做接口映射到实现类, 从map取出实现类方法

                Map<String, String> map = new HashMap<>();
                map.put("rpc.server.service.HelloService", "rpc.server.service.impl.HelloServiceImpl");
                Class<?> clazz = Class.forName(map.className);
             */

            /*
                2. 放在配置文件下, 读取配置文件读取
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
