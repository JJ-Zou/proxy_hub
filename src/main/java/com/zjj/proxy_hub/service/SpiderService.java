package com.zjj.proxy_hub.service;

import java.util.concurrent.TimeUnit;

public interface SpiderService {


    default int getMaxPage() {
        return 10;
    }

    default long getSleepTime() {
        return 400L;
    }

    default void resolve() {
        int pageIndex = 1;
        while (pageIndex <= getMaxPage()) {
            if (!solve_single_page(pageIndex++)) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    boolean solve_single_page(int page);

}
