package com.weisong.common.messaging;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;

import com.weisong.common.messaging.MMessage.ContentType;

public class MMessageListenerAdapter extends MessageListenerAdapter implements MMessageHandler {
	public MMessageListenerAdapter() {
	}

	@Override
	public MMessage handleMessage(MMessage message) throws Exception {
		return null;
	}

	protected Object extractMessage(Message message) throws Exception {
		ContentType type = ContentType.lookup(message.getMessageProperties().getContentType());
		MessageConverter converter;
		if(ContentType.PlainText.equals(type)) {
			converter = new MMessageConverter();
		} else {
			converter = getMessageConverter();
		}
		if (converter != null) {
			return converter.fromMessage(message);
		}
		return message;
	}

}
