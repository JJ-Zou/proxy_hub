package com.zjj.proxy_hub.middleware;

import com.zjj.proxy_hub.model.ProxyIp;
import com.zjj.proxy_hub.scheduler.SpiderScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class ProxyPool {

    private final Set<ProxyIp> proxyPool = ConcurrentHashMap.newKeySet();

    @Autowired
    private SpiderScheduler spiderScheduler;

    public int size() {
        return proxyPool.size();
    }

    public List<ProxyIp> listAll() {
        return new ArrayList<>(proxyPool);
    }

    public synchronized ProxyIp popProxy() {
        if (proxyPool.isEmpty()) {
            try {
                spiderScheduler.ScheduleResolve();
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
            if (proxyPool.isEmpty()) {
                return null;
            }
        }
        ProxyIp ip = proxyPool.iterator().next();
        proxyPool.remove(ip);
        return ip;
    }

    public void setProxy(ProxyIp ip) {
        proxyPool.add(ip);
    }

    public boolean removeProxy(ProxyIp ip) {
        return proxyPool.remove(ip);
    }

}
