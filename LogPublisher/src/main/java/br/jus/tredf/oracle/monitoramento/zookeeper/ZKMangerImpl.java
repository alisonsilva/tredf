package br.jus.tredf.oracle.monitoramento.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZKMangerImpl implements IZKManager {

	private static ZooKeeper zkeeper;
	private static ZKConnection zkConnection;
	
	public ZKMangerImpl(String host) throws Exception {
		initialize(host);
	}
	
	private void initialize(String host) throws Exception {
    zkConnection = new ZKConnection();
    zkeeper = zkConnection.connect(host);		
	}
	
	public void closeConnection() throws Exception {
		zkConnection.close();
	}
	
	@Override
	public void create(String path, byte[] data) throws KeeperException, InterruptedException {
		zkeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	@Override
	public String getZNodeData(String path, boolean watchFlag) throws Exception {
		byte[] b = null;
		b = zkeeper.getData(path, null, null);		
		return new String(b, "UTF-8");
	}

	@Override
	public void update(String path, byte[] data) throws KeeperException, InterruptedException {
		int version = zkeeper.exists(path, true).getVersion();
    zkeeper.setData(path, data, version);
	}

	
}
