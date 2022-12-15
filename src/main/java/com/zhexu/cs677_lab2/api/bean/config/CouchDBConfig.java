package com.zhexu.cs677_lab2.api.bean.config;

import com.zhexu.cs677_lab2.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab2.api.bean.config.basic.CouchDBInfo;
import com.zhexu.cs677_lab2.api.repository.CouchdbCURD;
import com.zhexu.cs677_lab2.api.repository.impl.CouchdbCURDImpl;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.log4j.Log4j2;

import static com.zhexu.cs677_lab2.constants.Consts.ONE_MILLION;
import static com.zhexu.cs677_lab2.constants.Consts.TEN_THOUSAND;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@Configuration
@Log4j2
public class CouchDBConfig {

    @Autowired
    CouchDBInfo couchDBInfo;

    @Bean(name = "CouchDbCURD")
    public CouchdbCURD couchDbConnector() throws Exception {
        HttpClient httpClient = new StdHttpClient.Builder().url(couchDBInfo.getHost() + ":" + couchDBInfo.getPort())
                .username(couchDBInfo.getUsername()).connectionTimeout(TEN_THOUSAND).socketTimeout(ONE_MILLION)
                .password(couchDBInfo.getPassword()).build();
        CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector couchDbConnector = new StdCouchDbConnector(couchDBInfo.getDatabase() +
                "_" +
                SingletonFactory.getRole().getSelfAddress().getDomain().replace(".", "_"),
                couchDbInstance);
        couchDbConnector.createDatabaseIfNotExists();

        CouchdbCURD couchdbCURD = new CouchdbCURDImpl(RaftLogItem.class, couchDbConnector, true);
        log.info("CouchDb Connector injection successed!");
        return couchdbCURD;
    }

}
