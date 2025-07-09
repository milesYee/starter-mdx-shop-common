package com.mdx.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfigurationMdxCommon {
    @Bean
    public RocketMQEnhanceConfig mqEnhanceConfig() {
        return new RocketMQEnhanceConfig();
    }

    @Bean
    public RocketMqTemplate mqTemplate() {
        return new RocketMqTemplate();
    }


}
