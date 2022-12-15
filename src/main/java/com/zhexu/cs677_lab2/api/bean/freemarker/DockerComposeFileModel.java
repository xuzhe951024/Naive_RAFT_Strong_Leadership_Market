package com.zhexu.cs677_lab2.api.bean.freemarker;

import java.io.Serializable;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/30/22
 **/
public class DockerComposeFileModel implements Serializable {
    private String serviceName;
    private String workingDir;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
