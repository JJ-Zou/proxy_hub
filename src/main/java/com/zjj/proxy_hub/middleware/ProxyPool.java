package com.zjj.proxy_hub.middleware;

import com.zjj.proxy_hub.model.ProxyIp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProxyPool {

    private final Set<ProxyIp> proxyPool = ConcurrentHashMap.newKeySet();

    public int size() {
        return proxyPool.size();
    }

    public List<ProxyIp> listAll() {
        return new ArrayList<>(proxyPool);
    }

    public synchronized ProxyIp popProxy() {
        if (proxyPool.isEmpty()) {
            return null;
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
