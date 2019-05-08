package com.venus.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceDiscovery {
    private String zkServerAddress;
    private int sessionTimeout;
    private String zkRootPath;
    private List<String> list = new ArrayList<>();
    private CountDownLatch latch = new CountDownLatch(1);

    public ServiceDiscovery(String zkServerAddress, String path, int sessionTimeout){
        this.zkServerAddress = zkServerAddress;
        this.sessionTimeout = sessionTimeout;
        this.zkRootPath = path;
    }

    public String discover(){
        ZooKeeper zk = connect();
        if(zk != null){
            watcher(zk);
        }else {
            System.out.println("zookeeper connect fails");
        }

        String data = null;
        int size = list.size();
        if(size > 0){
            if(size == 1){
                data = list.get(0);
            }else {
                data = list.get(ThreadLocalRandom.current().nextInt(size));
            }
        }else {
            System.out.println("service discover is null");
        }

        return data;
    }

    private void watcher(final ZooKeeper zk){
        try {
            List<String> list = zk.getChildren(zkRootPath, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                        watcher(zk);
                    }
                }
            });

            List<String> dataList = new ArrayList<>();
            for(String node : list){
                byte[] bytes = zk.getData(zkRootPath +"/" +node, false, null);
                dataList.add(new String(bytes));
            }

            this.list = dataList;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ZooKeeper connect(){
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
}
