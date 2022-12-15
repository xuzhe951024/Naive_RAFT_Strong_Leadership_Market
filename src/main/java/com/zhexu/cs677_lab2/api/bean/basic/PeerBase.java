package com.zhexu.cs677_lab2.api.bean.basic;


import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.zhexu.cs677_lab2.constants.Consts.ONE;
import static com.zhexu.cs677_lab2.constants.RaftConsts.*;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/25/22
 **/
@Log4j2
public class PeerBase implements Serializable {
    private UUID Id;
    private Map<UUID, Address> neighbourPeerMap;
    private RaftTransBase raftBase;
    private UUID leaderID;
    private Address selfAddress;
    private Long measureTimes = 1L;
    private Long averageResponseTime = 0L;
    private Map<Long, Long> electedMap;
    private Boolean isPulseTimeout = Boolean.FALSE;
    private Map<UUID, Boolean> voteCollector;
    private String raftRole;
    private Map<UUID, Integer> messageBroadCastMap;
    private Boolean syncLogInProgress = Boolean.FALSE;



    public void updateMeanTime(Long responseTime) {
        this.averageResponseTime = (this.averageResponseTime * this.measureTimes + responseTime) / (this.measureTimes + ONE);
        this.measureTimes += ONE;
    }


    public PeerBase(UUID id, Map<UUID, Address> neighbourPeerMap, Address selfAddress) {
        Id = id;
        this.neighbourPeerMap = neighbourPeerMap;
        this.selfAddress = selfAddress;
        this.reSetElectedMap();
        this.messageBroadCastMap = new HashMap<>();
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }


    public Address getSelfAddress() {
        return selfAddress;
    }

    public void setSelfAddress(Address selfAddress) {
        this.selfAddress = selfAddress;
    }

    public Long getMeasureTimes() {
        return measureTimes;
    }

    public void setMeasureTimes(Long measureTimes) {
        this.measureTimes = measureTimes;
    }

    public Long getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Long averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public RaftTransBase getRaftBase() {
        return raftBase;
    }

    public void setRaftBase(RaftTransBase raftBase) {
        this.raftBase = raftBase;
    }

    public UUID getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(UUID leaderID) {
        this.leaderID = leaderID;
    }

    public Map<UUID, Address> getNeighbourPeerMap() {
        return neighbourPeerMap;
    }

    public Address getNeighbourAdd(UUID id) {
        return this.neighbourPeerMap.get(id);
    }

    public void setNeighbourPeerMap(Map<UUID, Address> neighbourPeerList) {
        this.neighbourPeerMap = neighbourPeerList;
    }

    public Boolean isLeader() {
        return this.Id.equals(this.leaderID) && this.raftRole.equals(RAFT_ROLE_LEADER);
    }

    public Boolean isFollower() {
        return this.raftRole.equals(RAFT_ROLE_FOLLOWER);
    }

    public Boolean isCandidate() {
        return this.raftRole.equals(RAFT_ROLE_CANDIDATE);
    }

    public void becomeLeader() {
        this.raftRole = RAFT_ROLE_LEADER;
        this.leaderID = this.Id;
    }

    public void becomeCandidate() {
        this.raftRole = RAFT_ROLE_CANDIDATE;
    }

    public void becomeFollwer() {
        this.raftRole = RAFT_ROLE_FOLLOWER;
        SingletonFactory.setCurrentTime();
    }

    public Map<Long, Long> getElectedMap() {
        return electedMap;
    }

    public Boolean checkTermIfElectingAvailable(Long term, Long index) {
        if (!this.electedMap.containsKey(term) ||
                this.electedMap.get(term) < index) {
            return true;
        }
        return false;
    }

    public void setElectedMap(Map<Long, Long> electedMap) {
        this.electedMap = electedMap;
    }

    public void putElectedMap(Long term, Long index) {
        this.electedMap.put(term, index);
    }

    public void reSetElectedMap() {
        this.electedMap = new HashMap<>();
    }

    public void unlockInitElection(Long term, Long index){
        log.debug("trying to use term: " +
                term +
                " index: " +
                index +
                " to unlock" +
                (this.electedMap.containsKey(term) ? this.electedMap.get(term):" null "));
        if (this.electedMap.containsKey(term) && this.electedMap.get(term).longValue() == index.longValue()){
            this.electedMap.remove(term);
            log.debug("unlock successful!");
        }
        log.debug("unlock fail because of index is null/unmatch");
    }

    public Boolean allowInitElection(Long term){
        return !this.electedMap.containsKey(term);
    }

    public Boolean isPulseTimeout() {
        return isPulseTimeout;
    }

    public void setPulseTimeout(Boolean pulseTimeout) {
        isPulseTimeout = pulseTimeout;
    }

    public Boolean getPulseTimeout() {
        return isPulseTimeout;
    }

    public Map<UUID, Boolean> getVoteCollector() {
        return voteCollector;
    }

    public void setVoteCollector(UUID id, Boolean AgreeToVote) {
        if (null == this.voteCollector) {
            this.voteCollector = new HashMap<>();
        }
        this.voteCollector.put(id, AgreeToVote);
    }

    public void reSetVoteCollector() {
        this.voteCollector = new HashMap<>();
    }


    public Boolean isAllowedInitElection(Long term){
        return this.electedMap.containsKey(term);
    }

    public void putMessageBroadCast(UUID id){
        if (this.messageBroadCastMap.containsKey(id)){
            this.messageBroadCastMap.put(id, this.messageBroadCastMap.get(id) + 1);
        } else {
            this.messageBroadCastMap.put(id, ONE);
        }
    }

    public void removeMessageBroadCast(UUID id){
        if(this.messageBroadCastMap.containsKey(id)){
            this.messageBroadCastMap.remove(id);
        }
    }

    public Integer getMessageBroadCast(UUID id){
        return this.messageBroadCastMap.get(id);
    }

    public Boolean isMessageBroadCastContains(UUID id){
        return this.messageBroadCastMap.containsKey(id);
    }

    public String getRaftRole() {
        return raftRole;
    }

    public Boolean isSyncLogInProgress() {
        return syncLogInProgress;
    }

    public void startSyncLog() {
        this.syncLogInProgress = Boolean.TRUE;
    }

    public void finishedSyncLog(){
        this.syncLogInProgress = Boolean.FALSE;
    }

    @Override
    public String toString() {
        return "PeerBase{" +
                "Id=" + Id +
                ", neighbourPeerMap=" + neighbourPeerMap +
                ", raftBase=" + raftBase +
                ", leaderID=" + leaderID +
                ", selfAddress=" + selfAddress +
                ", measureTimes=" + measureTimes +
                ", averageResponseTime=" + averageResponseTime +
                ", electedMap=" + electedMap +
                ", isPulseTimeout=" + isPulseTimeout +
                ", voteCollector=" + voteCollector +
                ", raftRole='" + raftRole + '\'' +
                ", messageBroadCastMap=" + messageBroadCastMap +
                '}';
    }
}
