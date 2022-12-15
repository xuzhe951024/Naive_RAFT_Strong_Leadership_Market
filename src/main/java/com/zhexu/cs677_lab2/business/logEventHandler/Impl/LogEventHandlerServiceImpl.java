package com.zhexu.cs677_lab2.business.logEventHandler.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.business.logEventHandler.EventHandler;
import com.zhexu.cs677_lab2.business.logEventHandler.LogEventHandlerService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.zhexu.cs677_lab2.constants.Consts.*;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/10/22
 **/
@Log4j2
public class LogEventHandlerServiceImpl implements LogEventHandlerService {
    private PeerBase peer = SingletonFactory.getRole();
    private List<RaftLogItem> raftLogItemList;
    private static Properties properties = readProperties(LOG_EVENT_HANDLER_METHODS_MAP_CONFIG);
    private static Properties readProperties(String confFile) {
        final Properties properties = new Properties();
        try {
            final ClassPathResource resource = new ClassPathResource(confFile);
            properties.load(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
    /**
     * order the log list by index value
     */

    private void orderLogsByIndex() {
        if (null == this.raftLogItemList || this.raftLogItemList.isEmpty()){
            log.error("Log list can not be null or empty!");
            return;
        }
        Collections.sort(raftLogItemList, raftLogItemList.get(ZERO));
    }

    /**
     * extract hanler from event bean and apply the run method
     * @return the boolean if successfully finishing the process
     */
    @Override
    public Boolean extractHandlerAndRun(List<RaftLogItem> raftLogItemList) {
        this.raftLogItemList = raftLogItemList;

        if (null == this.raftLogItemList || this.raftLogItemList.isEmpty()){
            log.error("Log list can not be null or empty!");
            peer.finishedSyncLog();
            return Boolean.FALSE;
        }

        orderLogsByIndex();

        ObjectMapper objectMapper = new ObjectMapper();

        for (RaftLogItem e : this.raftLogItemList) {
            try {
                log.debug("logItem: " + e.toString());
                log.debug("Now extract handler:\n" +
                        (String) properties.get(e.getEventClassName()) +
                        "\nClass:\n" +
                        Class.forName((String) properties.get(e.getEventClassName())).getName());
                EventHandler handler = (EventHandler) Class.forName((String) properties.get(e.getEventClassName())).newInstance();
                if (!handler.run(objectMapper.readValue(e.getEventJSONString(), Class.forName(e.getEventClassName())),
                        e.getLogId(),
                        e.getEventId())){
                    return Boolean.FALSE;
                }
                peer.getRaftBase().setTermAndIndex(e);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                peer.finishedSyncLog();
            }
        }

        peer.finishedSyncLog();
        return Boolean.TRUE;
    }

}
