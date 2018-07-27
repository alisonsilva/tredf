package br.jus.tredf.oracle.monitoramento.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ZKConnection {
	private ZooKeeper zoo;
	private CountDownLatch connectionLatch = new CountDownLatch(1);

	public ZooKeeper connect(String host) throws IOException, InterruptedException {
		zoo = new ZooKeeper(host, 2000, new Watcher() {

			@Override
			public void process(WatchedEvent we) {
				if(we.getState() == KeeperState.SyncConnected) {
					connectionLatch.countDown();
				}

			}
		});
		connectionLatch.await();
		return zoo;
	}
	
	public void close() throws InterruptedException {
		zoo.close();
	}
}
