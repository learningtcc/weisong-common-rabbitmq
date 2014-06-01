package com.weisong.common.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class MExchange {

	private String name;
	private String type = "direct";
	private boolean durable = true;
	private boolean autoDelete = false;

	public static Exchange convert(MExchange ex) {		
		if(ex == null) return null;
		
		Exchange reg = null;
		if(ex.getType().equalsIgnoreCase("direct")) {
			reg = new DirectExchange(ex.getName(), ex.isDurable(), ex.isAutoDelete());
		} else if(ex.getType().equalsIgnoreCase("fanout")) {
			reg = new FanoutExchange(ex.getName(), ex.isDurable(), ex.isAutoDelete());
		} else if(ex.getType().equalsIgnoreCase("topic")) {
			reg = new TopicExchange(ex.getName(), ex.isDurable(), ex.isAutoDelete());
		}
		return reg;
	}	

}
