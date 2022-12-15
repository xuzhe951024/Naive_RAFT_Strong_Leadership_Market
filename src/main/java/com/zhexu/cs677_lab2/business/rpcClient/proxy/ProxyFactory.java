package com.zhexu.cs677_lab2.business.rpcClient.proxy;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyFactory {

//    private static InvocationHandler handler = (proxy, method, args) -> {
//        NetModel netModel = new NetModel();
//
//        Class<?>[] classes = proxy.getClass().getInterfaces();
//        String className = classes[0].getName();
//
//        netModel.setClassName(className);
//        netModel.setArgs(args);
//        netModel.setMethod(method.getName());
//        String[] types = null;
//
//        if (args != null) {
//            types = new String[args.length];
//            for (int i = 0; i < types.length; i++) {
//                types[i] = args[i].getClass().getName();
//            }
//        }
//        netModel.setTypes(types);
//
//        byte[] bytes = SerializeUtils.serialize(netModel);
//
//        return RpcClient.send(bytes);
//    };



    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> serviceClass, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[] {serviceClass}, handler);
    }
}
