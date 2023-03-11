package com.zjj.proxy_hub.service.impl;

import com.zjj.proxy_hub.middleware.ProxyPool;
import com.zjj.proxy_hub.model.ProxyIp;
import com.zjj.proxy_hub.service.SpiderService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
public class XsSpiderServiceImpl implements SpiderService {

    private static final String INDEX_URL = "https://www.xsdaili.cn/index.php?s=/index/index.html";
    private static final String URL = "https://www.xsdaili.cn/dayProxy/ip/";

    Pattern compile = Pattern.compile("[^0-9]");

    @Autowired
    @Qualifier("restTemplateUTF8")
    private RestTemplate restTemplate;

    @Autowired
    private ProxyPool proxyPool;

    @Override
    public int getMaxPage() {
        return 30;
    }

    @Override
    public void resolve() throws InterruptedException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "text/html,application/xhtml+xml,application/xml;");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(INDEX_URL, HttpMethod.GET, entity, String.class);
        String html = resp.getBody();
        if (resp.getStatusCode() != HttpStatusCode.valueOf(200) || html == null) {
            log.error("拉取错误 {}", html);
            return;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".title a");
        Set<Integer> pageSet = new HashSet<>();
        for (Element element : elements) {
            int pageId = Integer.parseInt(compile.matcher(element.attr("href")).replaceAll("").trim());
            pageSet.add(pageId);
        }
        for (int page : pageSet) {
            solve_single_page(page);
            TimeUnit.MILLISECONDS.sleep(getSleepTime());
        }
    }

    public boolean solve_single_page(int page) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "text/html,application/xhtml+xml,application/xml;");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(URL + page + ".html", HttpMethod.GET, entity, String.class);
        String html = resp.getBody();
        if (resp.getStatusCode() != HttpStatusCode.valueOf(200) || html == null) {
            log.error("拉取错误 {}", html);
            return false;
        }
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".cont");
        for (TextNode textNode : elements.textNodes()) {
            String trim = textNode.getWholeText().trim();
            if (trim.isBlank()) {
                continue;
            }
            String[] host = trim.split("@")[0].split(":");
            proxyPool.setProxy(ProxyIp.builder().host(host[0].trim()).port(Integer.parseInt(host[1].trim())).build());
        }
        log.info("拉取完xs代理第{}页", page);
        return true;
    }
}
