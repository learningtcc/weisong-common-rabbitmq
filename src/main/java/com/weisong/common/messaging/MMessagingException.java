package com.weisong.common.messaging;

@SuppressWarnings("serial")
public class MMessagingException extends Exception {
    
	public MMessagingException(String message) {
		super(message);
	}

	public MMessagingException(Throwable cause) {
		super(cause);
	}

	public MMessagingException(String message, Throwable cause) {
		super(message, cause);
	}

}
