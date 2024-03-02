package com.yp.aigcsdk.config;

import com.yp.aigcsdk.client.XfAiClient;
import com.yp.aigcsdk.model.enums.XfVersionEnum;
import com.yp.aigcsdk.service.AiService;
import com.yp.aigcsdk.service.impl.XfAiServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yp
 * @date: 2024/3/2
 */
@Configuration
@ConfigurationProperties("xf")
@Data
@ComponentScan
public class XfAiConfig {

    private String appid;

    private String apiSecret;

    private String apiKey;

    private XfVersionEnum xfVersion;

    @Bean
    public XfAiClient xfAiClient() {
        return new XfAiClient(appid, apiSecret, apiKey, xfVersion);
    }

    @Bean
    public AiService aiService() {
        XfAiServiceImpl aiService = new XfAiServiceImpl();
        aiService.setXfAiClient(new XfAiClient(appid, apiSecret, apiKey, xfVersion));
        return aiService;
    }
}
