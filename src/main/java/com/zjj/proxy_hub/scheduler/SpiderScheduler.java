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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SpiderScheduler implements ApplicationContextAware {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    private TaskScheduler taskScheduler;


    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 2 * 60 * 1000)
    public void ScheduleResolve() throws InterruptedException {
        if (running.get()) {
            return;
        }
        running.set(true);
        Map<String, SpiderService> beansOfType = applicationContext.getBeansOfType(SpiderService.class);
        CountDownLatch countDownLatch = new CountDownLatch(beansOfType.size());
        for (SpiderService spiderService : beansOfType.values()) {
            taskScheduler.schedule(() -> {
                spiderService.resolve();
                countDownLatch.countDown();
            }, Instant.now());
        }
        countDownLatch.await();
        running.set(false);
    }

}
