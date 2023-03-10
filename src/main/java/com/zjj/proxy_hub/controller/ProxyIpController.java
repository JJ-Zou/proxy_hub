package com.zjj.proxy_hub.controller;

import com.zjj.proxy_hub.middleware.ProxyPool;
import com.zjj.proxy_hub.model.ProxyIp;
import com.zjj.proxy_hub.scheduler.SpiderScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/proxy")
public class ProxyIpController {

    @Autowired
    private ProxyPool proxyPool;

    @Autowired
    private SpiderScheduler spiderScheduler;

    @PostMapping
    public ResponseEntity<ProxyIp> popProxyIp() {
        ProxyIp proxyIp = proxyPool.popProxy();
        if (proxyIp == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(proxyIp, HttpStatus.OK);
    }

    @DeleteMapping
    public Boolean delProxyIp(@RequestBody ProxyIp ip) {
        return proxyPool.removeProxy(ip);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProxyIp>> listAllProxyIp() {
        return new ResponseEntity<>(proxyPool.listAll(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countProxyIp() {
        return new ResponseEntity<>(proxyPool.size(), HttpStatus.OK);
    }

    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        spiderScheduler.ScheduleResolve();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
