package com.zhexu.cs677_lab2.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/30/22
 **/
public class LoggerFormator extends Formatter {
    @Override
    public String format(LogRecord arg0) {
        StringBuilder builder = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date now = new Date();
        String dateStr = sdf.format(now);

        builder.append(dateStr);
        builder.append(" - ");

        builder.append(arg0.getLevel()).append(" - ");

        builder.append(arg0.getSourceMethodName()).append(" - ");

        builder.append(arg0.getMessage());

        builder.append("\r\n");

        return builder.toString();
    }

    @Override
    public String getHead(Handler h) {
        return "Log of Transactions\r\n";
    }

    @Override
    public String getTail(Handler h) {
        return "End of log\r\n";
    }
}
