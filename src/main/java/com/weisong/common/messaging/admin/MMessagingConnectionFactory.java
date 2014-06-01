package com.weisong.common.messaging.admin;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MMessagingConnectionFactory {
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired private MMessagingAdminConfig config;
    
    private ConnectionFactory connectionFactory = null;
    
    public ConnectionFactory getConnectionFactory() {
        if(connectionFactory == null ) {
            Assert.notNull(config, "MessagingAdminConfig object can not be null");
            CachingConnectionFactory factory = new CachingConnectionFactory(config.getServerUsed());
            factory.setUsername(config.getUsername());
            factory.setPassword(config.getPassword());
            factory.setPort(config.getPort());
            factory.setVirtualHost(config.getVhost());
            factory.setChannelCacheSize(config.getChannelCacheSize());
            logger.info("RabbitMQ connection factory created: " + config.getServerUsed());
            connectionFactory = factory;
        }
        return connectionFactory;
    }
    
    @PreDestroy
    public void destroy() throws Exception {
        if(connectionFactory != null) {
            CachingConnectionFactory cacheConnectionFactory = (CachingConnectionFactory)connectionFactory;
            cacheConnectionFactory.destroy();
            logger.info("RabbitMQ connection factory destroyed");
        }
    }
}