package com.zjj.proxy_hub.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public interface SpiderService {


    default int getMaxPage() {
        return 10;
    }

    default long getSleepTime() {
        return 400L;
    }

    default DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }

    default long getLastVerifyTimeStamp(String date, DateTimeFormatter formatter) {
        return LocalDateTime.parse(date, formatter).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    default void resolve() throws InterruptedException {
        int pageIndex = 1;
        while (pageIndex <= getMaxPage()) {
            if (!solve_single_page(pageIndex++)) {
                return;
            }
            TimeUnit.MILLISECONDS.sleep(getSleepTime());
        }
    }

    boolean solve_single_page(int page);

}
