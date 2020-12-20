package com.lb.springboot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * zkClient客户端测试
 */
@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZkClientTests {

	@Value("${zookeeper.cluster.hosts}")
	private String hosts;

	private ZkClient client;

	@BeforeAll
	public void initClient(){
		client = new ZkClient(hosts);
		log.info("ZkClient已完成初始化！！！");
	}

	@Data
	@AllArgsConstructor
	static class User implements Serializable {
		private String username;
		private Integer age;
	}

	@Test
	public void createTestZkClient(){
		// 创建一个持久节点，值支持object
		client.create("/zkclientnode2",20,CreateMode.PERSISTENT);

		// 创建一个持久节点，可以递归创建节点，但无法递归设置值
		client.createPersistent("/zkclientnode/users/lisi",true);
		client.createPersistent("/zkclientnode/address/beijing",true);

		// 创建一个持久节点，序列化
		client.createPersistent("/zkclientnode/users/zhangsan",new User("张三",18));
	}

	@Test
	public void modifyTest(){
		// 读取节点内容,不存在返回Null
		Object readDataBefore = client.readData("/zkclientnode2",true);
		log.info("readDataBefore value: {}",readDataBefore);

		// 修改节点内容
		client.writeData("/zkclientnode2","wirte data");
		Object readDataAfter = client.readData("/zkclientnode2",true);
		log.info("readDataAfter value: {}",readDataAfter);
	}

	@Test
	public void deleteTest(){
		// 普通删除
		client.delete("/zkclientnode2");
		// 递归删除
		client.deleteRecursive("/testnode");
//		client.create("/testnode",1,CreateMode.PERSISTENT);
	}

	@Test
	public void readTestZkClient(){
		List<String> children = client.getChildren("/zkclientnode/users");
		log.info("children: {}",children);

		children.forEach(nodeName ->{
			Object readData = client.readData("/zkclientnode/users/"+nodeName);
			log.info("batch read node: {}",readData);
		});
	}

	@Test
	public void watcherTest() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		client.subscribeDataChanges("/testnode", new IZkDataListener() {
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				log.info("===========数据已发生改变! dataPath: {},data: {}==========",dataPath,data);
				countDownLatch.countDown();
			}

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				log.info("===========数据已被删除! dataPath: {}==========",dataPath);
				countDownLatch.countDown();
			}
		});

		Object readData = client.readData("/testnode");
		log.info("origin data: {}",readData);

		client.writeData("/testnode","899");
		client.deleteRecursive("/testnode");
		countDownLatch.await();
	}

}
