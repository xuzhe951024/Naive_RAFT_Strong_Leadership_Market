package com.zhexu.cs677_lab2.api.bean.config;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@Plugin(name = "thread", category = ThreadLookup.CATEGORY)
public class ThreadLookup implements StrLookup {

    @Override
    public String lookup(String key) {
        return Thread.currentThread().getName();
    }
    @Override
    public String lookup(LogEvent event, String key) {
        return null == event.getThreadName() ? Thread.currentThread().getName()
                : event.getThreadName();
    }
}
