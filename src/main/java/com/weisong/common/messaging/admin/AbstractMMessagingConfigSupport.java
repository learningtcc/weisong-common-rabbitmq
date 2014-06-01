package com.weisong.common.messaging.admin;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.weisong.common.messaging.MExchange;
import com.weisong.common.messaging.MMessageConverter;
import com.weisong.common.messaging.MQueue;

@Component
public abstract class AbstractMMessagingConfigSupport implements InitializingBean {
    
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private RabbitAdmin rabbitAdmin = null;
	
	@Autowired @Setter private MMessagingConnectionFactory messagingConnectionFactory;
	
	@Getter @Setter protected MExchange exchange;
	@Getter @Setter protected MQueue mQueue;
	@Setter private MessageConverter messageConverter;
	@Getter private RabbitTemplate rabbitTemplate = null;

    protected abstract void configureRabbitTemplate(RabbitTemplate template);
    
	public MessageConverter getMessageConverter() {
		if(messageConverter == null) {
			messageConverter = new MMessageConverter();
		}
		return messageConverter;
	}

	public ConnectionFactory connectionFactory() {
		return messagingConnectionFactory.getConnectionFactory();
	}

	public RabbitAdmin getRabbitAdmin() {
	    if(rabbitAdmin == null) {
	        rabbitAdmin = new RabbitAdmin(connectionFactory());
	    }
		return rabbitAdmin ;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(rabbitTemplate == null) {
			rabbitTemplate = new RabbitTemplate(connectionFactory());
			rabbitTemplate.setMessageConverter(getMessageConverter());
		}
		configureRabbitTemplate(rabbitTemplate);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if(exchange != null) {
			buffer.append(exchange.toString()).append("|");
		}
		if(mQueue != null) {
			buffer.append(mQueue.toString());
		}
		return buffer.toString();
	}
}
