package com.weisong.common.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

public class MMessageConverter extends SimpleMessageConverter {
    public MMessageConverter() {
        super();
    }

    @Override
    public MMessage fromMessage(Message message) throws MessageConversionException {
        Object obj = super.fromMessage(message);
        MMessage hiveMsg = new MMessage(obj, message.getMessageProperties());
        return hiveMsg;
    }

    @Override
    protected Message createMessage(Object objectToConvert, MessageProperties messageProperties)
            throws MessageConversionException {
        if (objectToConvert instanceof MMessage) {
            MMessage msg = (MMessage) objectToConvert;
            objectToConvert = msg.getMessageBody();
            messageProperties = msg.getMessageProperties();
        }
        return super.createMessage(objectToConvert, messageProperties);

    }

}
