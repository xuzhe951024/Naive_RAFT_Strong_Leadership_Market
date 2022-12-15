package com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import org.springframework.stereotype.Repository;


import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@Repository
public class RaftLogItem extends RaftTransBase implements Serializable, Comparator<RaftLogItem> {
    @JsonProperty(value = "_id")
    private String id;

    @JsonProperty(value = "_rev")
    private String revision;
    private UUID logId = UUID.randomUUID();
    private String eventClassName;
    private String eventJSONString;

    private String JsonStringHashCode;

    private UUID eventId = UUID.randomUUID();
    private String localTimeStamp = String.valueOf(System.currentTimeMillis());

    public UUID getLogId() {
        return logId;
    }

    public void setLogId(UUID logId) {
        this.logId = logId;
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public void setEventClassName(String eventClassName) {
        this.eventClassName = eventClassName;
    }

    public String getEventJSONString() {
        return eventJSONString;
    }

    public void setEventJSONString(String eventJSONString) {
        this.eventJSONString = eventJSONString;
        this.JsonStringHashCode = String.valueOf(eventJSONString.hashCode());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void setDocumentId() {
        this.id = super.generateDocumentId();
    }

    public String getJsonStringHashCode() {
        return JsonStringHashCode;
    }

    public void setJsonStringHashCode(String jsonStringHashCode) {
        this.JsonStringHashCode = jsonStringHashCode;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String printRaftBase(){
        return super.toString();
    }

    /**
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return
     */
    @Override
    public int compare(RaftLogItem o1, RaftLogItem o2) {
        return o1.getIndex().longValue() > o2.getIndex().longValue() ? 1 : -1;
    }

    @Override
    public String toString() {
        return "RaftLogItem{" +
                "Document id='" + id + '\'' +
                ", logId=" + logId +
                ", eventClassName='" + eventClassName + '\'' +
                ", eventJSONString='" + eventJSONString + '\'' +
                ", JsonStringHashCode='" + JsonStringHashCode + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}
