package com.weisong.common.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.weisong.common.javaconfig.CommonCombinedJavaConfig;
import com.weisong.common.messaging.consumer.MConsumer;
import com.weisong.common.messaging.consumer.MConsumerConfigSupport;
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
public class RpcTestJavaConfig {
    
    final static private String TEST_RPC_EXCHANGE = "test.rpc.exchange";
    final static private String TEST_RPC_QUEUE = "test.rpc.queue";
    final static private String TEST_RPC_PUBLISHER = "test.rpc.publisher";
    final static private String TEST_RPC_CONSUMER = "test.rpc.consumer";
    
    @Autowired @Qualifier("defaultMessageConverter") private MMessageConverter messageConverter;
    @Autowired @Qualifier(TEST_RPC_EXCHANGE) private MExchange rpcExchange;
    @Autowired @Qualifier(TEST_RPC_QUEUE) private MQueue rpcQueue;
    @Autowired private MPublisherConfigSupport publisherConfigSupport;
    @Autowired private MConsumerConfigSupport consumerConfigSupport;
    
    @Bean(name = TEST_RPC_EXCHANGE)
    public MExchange rpcExchange() {
        MExchange exchange = new MExchange();
        exchange.setName(TEST_RPC_EXCHANGE);
        exchange.setType("direct");
        exchange.setDurable(false);
        exchange.setAutoDelete(true);
        return exchange;
    }

    @Bean(name = TEST_RPC_QUEUE)
    public MQueue queue() {
        MQueue queue = new MQueue();
        queue.setName(TEST_RPC_QUEUE);
        queue.setDurable(false);
        queue.setExclusive(false);
        queue.setAutoDelete(true);
        return queue;
    }

    @Bean
    protected MPublisherConfigSupport rpcPublisherConfigSupport() {
        MPublisherConfigSupport configSupport = new MPublisherConfigSupport();
        configSupport.setExchange(rpcExchange);
        configSupport.setMessageConverter(messageConverter);
        configSupport.setMQueue(rpcQueue);
        return configSupport;
    }
    
    @Bean(name = TEST_RPC_PUBLISHER)
    public MPublisher rpcPublisher() {
        MPublisher publisher = new MPublisher();
        publisher.setConfigSupport(publisherConfigSupport);
        return publisher;
    }
    
    @Bean
    protected MConsumerConfigSupport rpcConsumerConfigSupport() {
        MConsumerConfigSupport configSupport = new MConsumerConfigSupport();
        configSupport.setExchange(rpcExchange);
        configSupport.setMQueue(rpcQueue);
        configSupport.setMessageConverter(messageConverter);
        return configSupport;
    }

    @Bean(name = TEST_RPC_CONSUMER)
    public MConsumer rpcConsumer() {
        MConsumer consumer = new MConsumer();
        consumer.setConfigSupport(consumerConfigSupport);
        return consumer;
    }
}
