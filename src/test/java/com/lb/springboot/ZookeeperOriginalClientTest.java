package com.lb.springboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * zk原生客户端测试
 */
@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZookeeperOriginalClientTest {

	@Value("${zookeeper.cluster.hosts}")
	private String hosts;

	@Value("${zookeeper.cluster.sessionTimeout}")
	private Integer sessionTimeout;

	private ZooKeeper zk;

	static class ZkOriginalWatcher implements Watcher {
		@Override
		public void process(WatchedEvent event) {
			log.info("watched event: {}",event);
			if (event.getState() == Event.KeeperState.SyncConnected){
				log.info("已连接");
			}
		}
	}

	@BeforeAll
	public void initZooKeeper()throws Exception{
		zk = new ZooKeeper(hosts,sessionTimeout,new ZkOriginalWatcher());
		log.info("init zk: {}",zk);
	}

	@Test
	public void testNode() throws Exception{
		String path = "/testnode/node2";
		// 是否存在
		Stat exists = zk.exists(path, true);
		log.info("path stat: {}",exists);

		// 创建节点
		String createRet = zk.create(path, "hello,zookeeper".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		log.info("createRet: {}",createRet);

		// 修改节点内容
		Stat stat = zk.setData(path, "world".getBytes(), -1);
		log.info("setData result: {}",stat);

		// 创建多个节点
		zk.create(path+"/snode1", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(path+"/snode2", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(path+"/snode3", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

		// 获取所有子节点
		List<String> children = zk.getChildren(path, true);
		log.info("children: {}",children);

		// 删除节点
		zk.delete(path+"/snode3",-1);

	}
}
