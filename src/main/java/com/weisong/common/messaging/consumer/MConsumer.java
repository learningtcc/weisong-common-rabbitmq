package com.weisong.common.messaging.consumer;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class MConsumer {

    private static Logger logger = LoggerFactory.getLogger(MConsumer.class);

    @Getter @Setter private MConsumerConfigSupport configSupport;
    @Getter @Setter private int concurrentConsumers = 1;
    @Getter private boolean running = false;

    public void addRoutingKey(String routingKey) {
        validate();
        getConfigSupport().addRoutingKey(routingKey);
    }

    public void removeRoutingKey(String routingKey) {
        validate();
        getConfigSupport().removeRoutingKey(routingKey);
    }

    public void start() {
        validate();
        if(isRunning()) {
            return;
        }
        try {
            getConfigSupport().afterPropertiesSet();
            getConfigSupport().messageListenerContainer().setConcurrentConsumers(concurrentConsumers);
            getConfigSupport().messageListenerContainer().start();
            running = true;
            logger.info("Started Consumer: " + getConfigSupport().toString());
        }
        catch (Exception ex) {
            logger.error("Failed to start Consumer:", ex);
            throw new RuntimeException(ex);
        }
    }

    public void stop() {
        validate();
        if(isRunning() == false) {
            return;
        }
        try {
            getConfigSupport().messageListenerContainer().stop();
            running = false;
            logger.info("Stoped Consumer: " + getConfigSupport().toString());
        }
        catch (Exception ex) {
            logger.error("Failed to stop Consumer:", ex);
            throw new RuntimeException(ex);
        }
    }

    private void validate() {
        Assert.notNull(getConfigSupport(), "Consumer configuration not set");
    }
}
