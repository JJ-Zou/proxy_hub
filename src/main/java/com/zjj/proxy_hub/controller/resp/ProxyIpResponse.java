package com.zjj.proxy_hub.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProxyIpResponse {
    private String host;

    private int port;

    private int lastVerifyTime;

}
