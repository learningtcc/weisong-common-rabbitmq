package com.weisong.common.messaging.consumer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import com.weisong.common.messaging.MExchange;
import com.weisong.common.messaging.MQueue;
import com.weisong.common.messaging.admin.AbstractMMessagingConfigSupport;

public class MConsumerConfigSupport extends AbstractMMessagingConfigSupport {
    
	@Setter private List<String> routingKeys;
	@Getter @Setter private Object messageListenerAdapterDelegate;
	@Setter private SimpleMessageListenerContainer simpleMessageListenerContainer;

	@Getter @Setter private AcknowledgeMode ackMode = AcknowledgeMode.AUTO;
	
	private boolean initReady = false;

	@Override
	protected void configureRabbitTemplate(RabbitTemplate template) {
		declareAll();
	}
	
	/**
     * declare all exchange and queue and bindings
     */
	public void declareAll() {
	    try {
            if(exchange != null) {
                Exchange amqpExchange = MExchange.convert(exchange);
                logger.debug("Declare exchange: " + exchange.toString());
                try {
                    getRabbitAdmin().declareExchange(amqpExchange);
                }
                catch (Exception e) {
                    logger.error("Fail to declare rabbitmq exchange " + amqpExchange.getName(),e);
                }
            }
            
            if(mQueue != null) {
                logger.debug("Declare queue: " + mQueue.toString());
                try {
                    getRabbitAdmin().declareQueue(MQueue.convert(mQueue));
                }
                catch (Exception e) {
                    logger.error("Fail to declare rabbitmq queue " + mQueue.getName(),e);
                }
            }
            
            if(routingKeys!=null) {
                for(String key:routingKeys) {
                    declareBinding(key);
                }
            } else {
                declareBinding(null);
            }
            initReady = true;
        }
        catch (Exception e) {
            logger.error("Fail to declare rabbitmq exchange and queue and bindings",e);
        }
	}
	
	synchronized public void addRoutingKey(String routingKey) {
		if(routingKeys == null) {
			routingKeys = new ArrayList<String>();
		}
		if(routingKey != null && !routingKeys.contains(routingKey))
			this.routingKeys.add(routingKey);

		if(initReady)
			declareBinding(routingKey);
	}

	synchronized public void removeRoutingKey(String routingKey) {
		if(routingKeys == null || routingKey == null) {
			return;
		}
		if(initReady) {
			unBinding(routingKey);
			this.routingKeys.remove(routingKey);
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
		logger.info("Bind builder: " + mQueue.getName() + " to " + ex.getName() + " with " + routingKey);
		getRabbitAdmin().declareBinding(binding);
	}

	private void unBinding(String routingKey) {
		Binding binding;
		if(routingKey == null) {
			return;
		}
		binding = BindingBuilder.bind(MQueue.convert(mQueue))
			.to(MExchange.convert(exchange)).with(routingKey).noargs();
		getRabbitAdmin().removeBinding(binding);
		logger.debug("remove binding: " + routingKey);
	}

	public SimpleMessageListenerContainer messageListenerContainer() {
		if(simpleMessageListenerContainer != null)
			return simpleMessageListenerContainer;
		simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
		simpleMessageListenerContainer.setQueues(MQueue.convert(this.mQueue));
		simpleMessageListenerContainer.setAcknowledgeMode(ackMode);
		simpleMessageListenerContainer.setMessageListener(getMessageListenerAdapter());
		simpleMessageListenerContainer.setConcurrentConsumers(1);
		return simpleMessageListenerContainer;
	}
	
	public MessageListenerAdapter getMessageListenerAdapter() {
		MessageListenerAdapter adapter = (MessageListenerAdapter)getMessageListenerAdapterDelegate();
		adapter.setMessageConverter(getMessageConverter());
		return new MessageListenerAdapter(adapter, getMessageConverter());		
	}
}
