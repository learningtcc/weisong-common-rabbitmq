package com.weisong.common.messaging.consumer;

import com.weisong.common.messaging.MMessagingException;

@SuppressWarnings("serial")
public class MConsumerException extends MMessagingException {
	
	public MConsumerException(String message) {
		super(message);
	}

	public MConsumerException(Throwable cause) {
		super(cause);
	}

	public MConsumerException(String message, Throwable cause) {
		super(message, cause);
	}

}
