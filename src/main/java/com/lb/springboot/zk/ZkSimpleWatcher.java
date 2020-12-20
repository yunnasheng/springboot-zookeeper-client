package com.lb.springboot.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 功能描述:ZkSimpleWatcher <br/>
 *
 * @author yunnasheng
 * @date: 2020-12-19 20:37<br/>
 * @since JDK 1.8
 */
@Slf4j
public class ZkSimpleWatcher implements Watcher {
    @Override
    public void process(WatchedEvent event) {

        log.info("watched event: {}",event);
        if (event.getState() == Event.KeeperState.SyncConnected){
            log.info("已连接");
        }
    }
}
