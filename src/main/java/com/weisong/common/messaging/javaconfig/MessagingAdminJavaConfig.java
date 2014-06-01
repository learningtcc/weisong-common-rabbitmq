package com.weisong.common.messaging.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.weisong.common.messaging.admin.MMessagingAdminConfig;
import com.weisong.common.messaging.admin.MMessagingConnectionFactory;

@Configuration
public class MessagingAdminJavaConfig {
    
    @Bean
    public MMessagingAdminConfig messagingAdminConfig() throws Exception {
        return new MMessagingAdminConfig();
    }
    
    @Bean
    public MMessagingConnectionFactory messagingConnectionFactory() {
        return new MMessagingConnectionFactory();
    }

}
