package com.weisong.common.messaging.publisher;

import com.weisong.common.messaging.MMessagingException;


@SuppressWarnings("serial")
public class MPublisherException extends MMessagingException {

	public MPublisherException(String message) {
		super(message);
	}

	public MPublisherException(Throwable cause) {
		super(cause);
	}

	public MPublisherException(String message, Throwable cause) {
		super(message, cause);
	}

}
