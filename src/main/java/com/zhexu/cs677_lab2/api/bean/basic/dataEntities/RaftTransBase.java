package com.zhexu.cs677_lab2.api.bean.basic.dataEntities;

import java.io.Serializable;

import static com.zhexu.cs677_lab2.constants.Consts.RAFT_LOG_ID_SEPRATOR;
import static com.zhexu.cs677_lab2.constants.Consts.RAFT_LOG_PREFIX;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class RaftTransBase implements Serializable {
    private Long term = 0L;
    private Long index = 0L;

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void increaseIndex() {
        this.index += 1L;
    }

    public void increaseTerm() {
        this.term += 1L;
    }

    public void decreaseTerm() {
        this.term -= 1L;
    }

    public void decreaseIndex(){
        this.index -= 1L;
    }

    public void setTermAndIndex(RaftTransBase transBase) {
        this.term = transBase.getTerm();
        this.index = transBase.getIndex();
    }

    public Boolean isLeaderTermAndIndexUptoDate(RaftTransBase transBase) {
        return this.index.longValue() <= transBase.getIndex().longValue()
                && this.term.longValue() <= transBase.getTerm().longValue();
    }

    public Boolean isLeaderTermLargerIndexUptoDate(RaftTransBase transBase) {
        return this.index.longValue() <= transBase.getIndex().longValue()
                && this.term.longValue() < transBase.getTerm().longValue();
    }

    public Boolean isNeedToSyncLog(RaftTransBase transBase) {
        return this.index.longValue() < transBase.getIndex().longValue()
                || this.term.longValue() < transBase.getTerm().longValue();
    }

    public Boolean isTermOrIndexLarger(RaftTransBase transBase) {
        return this.index.longValue() > transBase.getIndex().longValue()
                || this.term.longValue() > transBase.getTerm().longValue();
    }

    public Boolean updateTermOnly(RaftTransBase transBase) {
        return this.index.longValue() == transBase.getIndex().longValue()
                && this.term.longValue() < transBase.getTerm().longValue();
    }

    public Boolean atSameTermAndIndex(RaftTransBase transBase) {
        return this.index.longValue() == transBase.getIndex().longValue()
                && this.term.longValue() == transBase.getTerm().longValue();
    }

    public Boolean termAndIndexGreaterThanZero() {
        return this.index.longValue() >= 0l && this.term.longValue() >= 0l;
    }

    public String generateDocumentId(){
        return RAFT_LOG_PREFIX + this.getTerm() + RAFT_LOG_ID_SEPRATOR + this.getIndex();
    }

    @Override
    public String toString() {
        return "RaftTransBase{" +
                "term=" + term +
                ", index=" + index +
                '}';
    }
}
