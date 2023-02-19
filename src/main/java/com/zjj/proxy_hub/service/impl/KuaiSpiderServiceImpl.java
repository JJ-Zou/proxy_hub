package com.zjj.proxy_hub.service.impl;

import com.zjj.proxy_hub.middleware.ProxyPool;
import com.zjj.proxy_hub.model.ProxyIp;
import com.zjj.proxy_hub.service.SpiderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KuaiSpiderServiceImpl implements SpiderService {

    private static final String URL = "https://www.kuaidaili.com/free/inha/";

    @Autowired
    private RestTemplate six6RestTemplate;

    @Autowired
    private ProxyPool proxyPool;

    @SneakyThrows
    @Scheduled(initialDelay = 2000, fixedDelay = 5 * 60 * 1000)
    public void kuaiResolve() {
        int pageIndex = 1;
        while (true) {
            log.info("快代理第{}页", pageIndex);
            if (!solve_single_page(pageIndex++)) {
                return;
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public boolean solve_single_page(int page) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "text/html,application/xhtml+xml,application/xml;");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> resp = six6RestTemplate.exchange(URL + page, HttpMethod.GET, entity, String.class);
        String html = resp.getBody();
        if (resp.getStatusCode() != HttpStatusCode.valueOf(200) || html == null) {
            System.out.println(html);
            return false;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.select("#list > table > tbody tr");
        for (Element element : elements) {
            String host = "localhost";
            int port = 0;
            for (Element child : element.children()) {
                String value = child.attr("data-title");
                if("IP".equals(value)){
                    host = child.text();
                    continue;
                }
                if("PORT".equals(value)){
                    port = Integer.parseInt(child.text());
                    break;
                }
            }
            proxyPool.setProxy(ProxyIp.builder().host(host).port(port).build());
        }
        return true;
    }

}
