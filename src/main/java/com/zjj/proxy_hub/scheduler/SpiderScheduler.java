package com.zjj.proxy_hub.scheduler;

import com.zjj.proxy_hub.service.SpiderService;
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
    private TaskScheduler taskScheduler;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Scheduled(initialDelay = 2000, fixedRate = 5 * 60 * 1000)
    public void ScheduleResolve() throws InterruptedException {
        Map<String, SpiderService> beansOfType = applicationContext.getBeansOfType(SpiderService.class);
        for (SpiderService spiderService : beansOfType.values()) {
            taskScheduler.schedule(() -> {
                try {
                    spiderService.resolve();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, Instant.now());
        }
    }

}
