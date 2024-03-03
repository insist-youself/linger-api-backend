package com.yupi.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "linger.gateway")
@Data
public class GatewayConfig {

    private String host;

}
