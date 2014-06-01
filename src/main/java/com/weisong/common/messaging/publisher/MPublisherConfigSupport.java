package com.weisong.common.messaging.publisher;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.weisong.common.messaging.MExchange;
import com.weisong.common.messaging.MQueue;
import com.weisong.common.messaging.admin.AbstractMMessagingConfigSupport;

@Component
public class MPublisherConfigSupport extends AbstractMMessagingConfigSupport {
	
	@Override
	protected void configureRabbitTemplate(RabbitTemplate template) {
		declareAll();
	}
	
	/**
	 * declare all exchange and queue and bindings
	 */
	public void declareAll() {
	    try {
	        RabbitTemplate template = getRabbitTemplate();
            if(exchange != null) {
                Exchange amqpExchange = MExchange.convert(exchange);
                try {
                    getRabbitAdmin().declareExchange(amqpExchange);
                }
                catch (Exception e) {
                    logger.error("Fail to declare rabbitmq exchange " + amqpExchange.getName(),e);
                }
                template.setExchange(amqpExchange.getName());
            }
            if(mQueue != null) {
                Queue queue = MQueue.convert(mQueue);
                try {
                    getRabbitAdmin().declareQueue(queue);
                }
                catch (Exception e) {
                    logger.error("Fail to declare rabbitmq exchange " + queue.getName(),e);
                }
                template.setQueue(queue.getName());
                template.setRoutingKey(queue.getName());
            }
            declareBinding(null);
        }
        catch (Exception e) {
            logger.error("Fail to declare rabbitmq exchange and queue",e);
        }
	}
	private void declareBinding(String routingKey) {
        Binding binding;
        
        if(mQueue == null) {
            return;
        }
        
        if(routingKey == null) {
            routingKey = mQueue.getName();
        }
        Exchange ex = null;
        if(exchange != null) {
            ex = MExchange.convert(exchange);
        } else {
            ex = new DirectExchange("", false, false);
        }
        binding = BindingBuilder.bind(MQueue.convert(mQueue))
            .to(ex).with(routingKey).noargs();
        logger.info("Bind builder: " + mQueue.getName() + " to " + exchange.getName() + " with " + routingKey);
        getRabbitAdmin().declareBinding(binding);
    }
}
