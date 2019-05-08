package com.venus.zk;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class ServiceRegister {
    private String zkServerAddress;
    private String path;
    private int sessionTimeout;

    private final CountDownLatch latch = new CountDownLatch(1);

    public ServiceRegister(String zkServerAddress, String path, int sessionTimeout){
        this.zkServerAddress = zkServerAddress;
        this.path = path;
        this.sessionTimeout = sessionTimeout;
    }

    public void register(String data){
        ZooKeeper zk = connect();

        if(zk != null){
            create(zk, data, path);
        }else {
            System.out.println("service register zk is null");
        }

//        disConnect(zk);
    }

    public ZooKeeper connect(){
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(zkServerAddress, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }

    public void create(ZooKeeper zk, String data, String path){
        byte[] bytes = data.getBytes();

        try {
            //只有当节点不为空的时候才创建
            if(zk.getData(path, false, null) == null) {
                zk.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            System.out.println("zookeeper node creates fails");
        }
    }

    public void disConnect(ZooKeeper zk){
        try {
            zk.close();
        } catch (InterruptedException e) {
            System.out.println("zk disconnect fails");
        }
    }
}
