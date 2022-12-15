package com.zhexu.cs677_lab2.api.bean.basic;

import com.zhexu.cs677_lab2.constants.ResponseCode;

import java.io.Serializable;

import static com.zhexu.cs677_lab2.constants.ResponseCode.STATUS_ACCEPTED;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/
public class BasicResponse implements Serializable {
    private Integer status;
    private String discription;
    private String message;

    public BasicResponse(Integer status, String discription) {
        this.status = status;
        this.discription = discription;
        this.message = this.discription;
    }

    public BasicResponse(){
        this.status = ResponseCode.STATUS_ACCEPTED;
        this.discription = ResponseCode.GET_DESCRIPTIONS.get(ResponseCode.STATUS_ACCEPTED);
        this.message = this.discription;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
        this.discription = ResponseCode.GET_DESCRIPTIONS.get(status);
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean accepted(){
        return this.status.intValue() == STATUS_ACCEPTED.intValue();
    }

    @Override
    public String toString() {
        return "BasicResponse{" +
                "status=" + status +
                ", discription='" + discription + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
