package com.zhexu.cs677_lab2.constants;


/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class Consts {
    public static final Long SERIAL_VERSION_UID = 3152168260407524091L;
    public static final Integer ONE = 1;
    public static final Integer ZERO = 0;
    public static final Integer TWO  = 2;
    public static final Integer THREE = 3;
    public static final Integer THIRTY = 30;
    public static final Integer SIXTY = 60;
    public static final Integer LATENCY_TEST_TIME = 5;
    public static final Integer ONE_MILLION = 1000000;
    public static final Integer TEN_THOUSAND = 10000;
    public static final Integer ONE_THOUSAND = 1000;
    public static final Integer ONE_HUNDRED = 100;
    public static final Integer HEART_PULSE_INTERVAL = 100;
    public static final Integer SERIALIZATION_BUF_SIZE = 10240;
    public static final String TRANSACTION_ROLLING_BACK_PREFIX = "Rolling back of transaction";
    public static final String MARKET_LOG_PREFIX = "[MARKET]:";
    public static final String RAFT_LOG_ID_SEPRATOR = "_";
    public static final String RAFT_LOG_PREFIX = "raftLogItem_";
    public static final String COMMAND_PING = "ping ";
    public static final String PING_LATENCY_SPLITER = "time=";
    public static final String TIME_CONFIG_FILE = "times_config.properties";
    public static final String LOG_EVENT_HANDLER_METHODS_MAP_CONFIG = "handler_map.properties";
    public static final String DOMAIN_PREFIX = "cs677.lab2.peer";
    public static final String INIT_ENV_JSON_FILENAME = "INIT_JSON_FILE_NAME";
    public static final String DOMAIN_SUFIX = ".example.com";
    public static final String MILLISECOND = "ms";
    public static final String SERVER_PORT_ARG = "--server.port=";
    public static final String IMPORTANT_LOG_WRAPPER = "\n****************************************************************************************\n";

    public static final String SLASH = "/";
    public static final String Colon = ":";

//    logger name constants
    public static final String LOGGER_MAIN = "LOGGER_MAIN";
    public static final String LOGGER_LOOKUP_SERVICE = "LOGGER_LOOKUP_SERVICE";
    public static final String LOGGER_LOOKUP_SENDER = "LOGGER_LOOKUP_SENDER";
    public static final String LOGGER_REPLY_SERVICE = "LOGGER_REPLY_SERVICE";
    public static final String LOGGER_MESURE_RESULT = "LOGGER_MESURE_RESULT";
    public static final String LOGGER_TRADE = "LOGGER_TRADE";
    public static final String LOG_FILE_DIR_PREFIX = "logs_";
    public static final String LOG_FILE_SUFIX = ".log";
    public static final String LOG_DIR_BASE = "Logs/";

//    role name constants
    public static final String ROLE_BUYER = "BUYER";
    public static final String ROLE_SELLER = "SELLER";

//    init profiles constants
    public static final String JSON_PROFILE_DIR_BASE = "JSON_FILES";
    public static final String JSON_INIT_FILE_NAME = "init.json";
    public static final String HOSTS_FILE = "host";
    public static final String ENTER = "\n";
    public static final String SPACE = " ";
    public static final String ARGS_FILE = "args";
    public static final String LOCALHOST_IP = "127.0.0.1";
    public static final String RUN_BASH_FILE = "start.sh";
    public static final String RUN_CMD = "java -jar app.jar";
    public static final String EXPORT_ENV_PREFIX = "export " + INIT_ENV_JSON_FILENAME + "=";
    public static final String CURRENT_DIR = "./";
    public static final String FREE_MARKER_TEMPLATE_DIR = "templates";
    public static final String FREE_MARKER_TEMPLATE_FILE_NAME = "DockerFile.ftl";
    public static final String START_SCRIPT_TEMPLATE_FILE_NAME = "startScript.ftl";
    public static final String MODEL_LIST = "modelList";
    public static final String START_SCRIPT = "startScript";
    public static final String DOCKER_COMPOSE_FILE = "docker-compose.yml";
    public static final String PULSE_TIMEOUT_BASE = "pulseTimeOutBase";
    public static final String BROADCAST_TIMEOUT_BASE = "broadcastTimoutBase";


}
