package com.lb.springboot.zk.config;

import com.lb.springboot.zk.ZkSimpleWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 功能描述:ZkClusterConfig <br/>
 *
 * @author yunnasheng
 * @date: 2020-12-19 20:28<br/>
 * @since JDK 1.8
 */
@Slf4j
@Configuration
public class ZkClusterConfig {

    @Value("${zookeeper.cluster.hosts}")
    private String hosts;
    @Value("${zookeeper.cluster.sessionTimeout}")
    private Integer sessionTimeout;

    public ZooKeeper zooKeeper() {
        log.info("=================== hosts: {} ,sessionTimeout: {}",hosts,sessionTimeout);

        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(hosts,sessionTimeout,new ZkSimpleWatcher());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("=================== zooKeeper 客户端初始化完毕！");
        return zooKeeper;
    }
}
