package br.jus.tredf.oracle.monitoramento.zookeeper;

import org.apache.zookeeper.KeeperException;

public interface IZKManager {
	public void create(String path, byte[] data) throws KeeperException, InterruptedException;
	public String getZNodeData(String path, boolean watchFlag) throws Exception;
	public void update(String path, byte[] data) throws KeeperException, InterruptedException;
}
