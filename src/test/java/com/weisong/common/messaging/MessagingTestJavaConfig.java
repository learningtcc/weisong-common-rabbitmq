package com.weisong.common.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.weisong.common.javaconfig.CommonCombinedJavaConfig;
import com.weisong.common.messaging.javaconfig.MessagingAdminJavaConfig;
import com.weisong.common.messaging.javaconfig.MessagingJavaConfig;
import com.weisong.common.messaging.publisher.MPublisher;
import com.weisong.common.messaging.publisher.MPublisherConfigSupport;

@Configuration
@Import({
    CommonCombinedJavaConfig.class
  , MessagingJavaConfig.class
  , MessagingAdminJavaConfig.class
})
public class MessagingTestJavaConfig {

    @Autowired @Qualifier("toDeviceExchange") private MExchange exchange;
    @Autowired @Qualifier("defaultMessageConverter") private MMessageConverter messageConverter;
    @Autowired private MPublisherConfigSupport configSupport;
    
    @Bean
    protected MPublisherConfigSupport publisherConfigSupport() {
        MPublisherConfigSupport configSupport = new MPublisherConfigSupport();
        configSupport.setExchange(exchange);
        configSupport.setMessageConverter(messageConverter);
        return configSupport;
    }

    @Bean
    public MPublisher publisher() {
        MPublisher publisher = new MPublisher();
        publisher.setConfigSupport(configSupport);
        return publisher;
    }

    @Bean(name = "test.rpc.queue")
    public MQueue queue() {
        MQueue queue = new MQueue();
        queue.setName("test.rpc.queue");
        queue.setDurable(false);
        queue.setExclusive(false);
        queue.setAutoDelete(true);
        return queue;
    }
}
