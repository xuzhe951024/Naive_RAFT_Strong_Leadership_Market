package com.zhexu.cs677_lab2.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class ResponseCode {
    public static final Integer STATUS_SUCCESS = 200;
    public static final String DESCRIPTION_SUCCESS = "OK";

    public static final Integer STATUS_INTERNAL_FAIL = 500;
    public static final String DESCRIPTION_INTERNAL_FAIL = "Internal Failed!";

    public static final Integer STATUS_ACCEPTED = 202;
    public static final String DESCRIPTION_ACCEPTED = "Request accepted";

    public static final Integer STATUS_NOT_IMPLEMENTED = 501;
    public static final String DESCRIPTION_NOT_IMPLEMENTED = "Not Implemented";

    public static final Integer STATUS_FORBIDDEN = 403;
    public static final String DESCRIPTION_FORBIDDEN = "Forbidden";

    public static final Integer STATUS_TIME_OUT = 408;
    public static final String DESCRIPTION_TIME_OUT = "Request Time-out";

    public static final Map<Integer, String> GET_DESCRIPTIONS = new HashMap(){
        {
            put(STATUS_SUCCESS, DESCRIPTION_SUCCESS);
            put(STATUS_INTERNAL_FAIL, DESCRIPTION_INTERNAL_FAIL);
            put(STATUS_ACCEPTED, DESCRIPTION_ACCEPTED);
            put(STATUS_NOT_IMPLEMENTED, DESCRIPTION_NOT_IMPLEMENTED);
            put(STATUS_FORBIDDEN, DESCRIPTION_FORBIDDEN);
            put(STATUS_TIME_OUT, DESCRIPTION_TIME_OUT);
        }
    };
}
