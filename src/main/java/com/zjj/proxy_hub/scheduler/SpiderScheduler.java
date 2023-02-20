package com.zjj.proxy_hub.scheduler;

import com.zjj.proxy_hub.middleware.ProxyPool;
import com.zjj.proxy_hub.service.SpiderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class SpiderScheduler implements ApplicationContextAware {

    @Autowired
    private ProxyPool proxyPool;

    @Autowired
    private TaskScheduler taskScheduler;


    private ApplicationContext applicationContext;

    @SneakyThrows
    @Scheduled(initialDelay = 2000, fixedDelay = 5 * 60 * 1000)
    public void ScheduleResolve() {
        if (proxyPool.size() > 0) {
            return;
        }
        Map<String, SpiderService> beansOfType = applicationContext.getBeansOfType(SpiderService.class);
        for (SpiderService spiderService : beansOfType.values()) {
            taskScheduler.schedule(spiderService::resolve, Instant.now());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
