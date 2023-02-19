package com.zjj.proxy_hub.controller;

import com.zjj.proxy_hub.controller.resp.ProxyIpResponse;
import com.zjj.proxy_hub.middleware.ProxyPool;
import com.zjj.proxy_hub.model.ProxyIp;
import com.zjj.proxy_hub.scheduler.SpiderScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(value = "/proxy")
public class ProxyIpController {

    @Autowired
    private ProxyPool proxyPool;

    @GetMapping
    public ProxyIp getProxyIp() {
        return proxyPool.popProxy();
    }

    @DeleteMapping()
    public Boolean delProxyIp(@RequestBody ProxyIp ip) {
        return proxyPool.removeProxy(ip);
    }


    @GetMapping("/all")
    public List<ProxyIp> listAllProxyIp() {
        return proxyPool.listAll();
    }


    @GetMapping("/count")
    public Integer countProxyIp() {
        return proxyPool.size();
    }

}
