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
    private RestTemplate restTemplateGb2312;

    @Autowired
    private ProxyPool proxyPool;

    @Override
    public long getSleepTime() {
        return 1000;
    }

    public boolean solve_single_page(int page) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "text/html,application/xhtml+xml,application/xml;");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplateGb2312.exchange(URL + page, HttpMethod.GET, entity, String.class);
        String html = resp.getBody();
        if (resp.getStatusCode() != HttpStatusCode.valueOf(200) || html == null) {
            log.error("拉取错误 {}", html);
            return false;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.select("#list > table > tbody tr");
        for (Element element : elements) {
            String host = null;
            int port = -1;
            for (Element child : element.children()) {
                String value = child.attr("data-title");
                if ("IP".equals(value)) {
                    host = child.text().trim();
                    continue;
                }
                if ("PORT".equals(value)) {
                    port = Integer.parseInt(child.text().trim());
                    break;
                }
            }
            if (host == null || port == -1) {
                log.warn("快代理解析ip或端口错误, skip.");
                continue;
            }
            proxyPool.setProxy(ProxyIp.builder().host(host).port(port).build());
        }
        log.info("拉取完快代理第{}页", page);
        return true;
    }

}
