package com.weisong.common.messaging;

public interface MMessageHandler {
	MMessage handleMessage(MMessage hiveMessage) throws Exception;
}
