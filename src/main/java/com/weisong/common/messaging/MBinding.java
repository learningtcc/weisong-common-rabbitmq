package com.weisong.common.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

import com.weisong.common.messaging.admin.AbstractMMessagingConfigSupport;

public class MBinding {
    
	private MExchange exchange;
	private MQueue queue;
	private String routingKey;
	
	public MBinding(MExchange exchange, MQueue queue, String routingKey) {
		this.exchange = exchange;
		this.queue = queue;
		this.routingKey = routingKey;
	}
	
	public Binding getBinding(AbstractMMessagingConfigSupport configSupport) {
		return BindingBuilder
		        .bind(MQueue.convert(queue))
				.to(MExchange.convert(exchange))
				.with(routingKey).noargs();
	}
}
