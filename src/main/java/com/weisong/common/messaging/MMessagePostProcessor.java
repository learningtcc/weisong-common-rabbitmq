package com.weisong.common.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class MMessagePostProcessor implements MessagePostProcessor {

	@Override
	public Message postProcessMessage(Message message) throws AmqpException {
		return message;
	}

}
