package com.weisong.common.messaging.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.weisong.common.messaging.MExchange;
import com.weisong.common.messaging.MMessageConverter;
import com.weisong.common.messaging.MQueue;

@Configuration
public class MessagingJavaConfig {
    
    final static private String PREFIX = "mediation";
    
    final static public String EXCHANGE_TO_DEVICE = PREFIX + ".to-device";
    final static public String EXCHANGE_FROM_DEVICE = PREFIX + ".from-device";
    final static public String QUEUE_FROM_DEVICE = PREFIX + ".from-device-queue";
    
    @Bean
    public MMessageConverter defaultMessageConverter() {
        return new MMessageConverter();
    }
    
    @Bean
    public MExchange toDeviceExchange() {
        return createExchange(EXCHANGE_TO_DEVICE);
    }
    
    @Bean
    public MExchange fromDeviceExchange() {
        return createExchange(EXCHANGE_FROM_DEVICE);
    }
    
    @Bean
    public MQueue fromDeviceEventQueue() {
        return createQueue(QUEUE_FROM_DEVICE);
    }
    
    private MExchange createExchange(String name) {
        MExchange ex = new MExchange();
        ex.setName(name);
        return ex;
    }
    
    private MQueue createQueue(String name) {
        MQueue queue = new MQueue();
        queue.setName(name);
        return queue;
    }
    
}
