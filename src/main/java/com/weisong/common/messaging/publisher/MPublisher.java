package com.weisong.common.messaging.publisher;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import com.weisong.common.messaging.MMessage;
import com.weisong.common.messaging.MMessage.ContentType;
import com.weisong.common.messaging.MMessageConverter;
import com.weisong.common.messaging.MMessagePostProcessor;

public class MPublisher {

    private static Logger logger = LoggerFactory.getLogger(MPublisher.class);

    final static public long DEFAULT_RPC_TIMEOUT = 10000; // 10 sec
    final static private MMessageConverter defaultMessageConverter = new MMessageConverter();
    
    @Getter @Setter private MPublisherConfigSupport configSupport;
    @Getter @Setter private MessagePostProcessor messagePostProcessor = new MMessagePostProcessor();

    public void send(MMessage message) throws MPublisherException {
        send(getConfigSupport().getMQueue().getName(), message);
    }

    public void send(String routingKey, MMessage message) throws MPublisherException {
        try {
            Message msg = convert(message);
            configSupport.getRabbitTemplate().convertAndSend(routingKey, msg, messagePostProcessor);
        }
        catch (Exception ex) {
            throw new MPublisherException("Failed to send message", ex);
        }
    }

    public MMessage syncSend(MMessage message) throws MPublisherException {
        return syncSend(message, DEFAULT_RPC_TIMEOUT);
    }

    public MMessage syncSend(MMessage message, long replyTimeoutInMillisecond) throws MPublisherException {
        try {
            Message msg = convert(message);
            if (msg.getMessageProperties().getReplyTo() != null) {
                msg.getMessageProperties().setReplyTo(null);
            }
            getConfigSupport().getRabbitTemplate().setReplyTimeout(replyTimeoutInMillisecond);
            return (MMessage) getConfigSupport().getRabbitTemplate().convertSendAndReceive(msg);
        }
        catch (Exception ex) {
            logger.error("Failed to invoke callback", ex);
            throw new MPublisherException(ex);
        }
    }

    public MMessage syncSend(String routingKey, MMessage message) throws MPublisherException {
        return syncSend(routingKey, message, DEFAULT_RPC_TIMEOUT);
    }

    public MMessage syncSend(String routingKey, MMessage message, long replyTimeoutInMillisecond)
            throws MPublisherException {
        try {
            Message hmsg = convert(message);
            getConfigSupport().getRabbitTemplate().setReplyTimeout(replyTimeoutInMillisecond);
            return (MMessage) getConfigSupport().getRabbitTemplate().convertSendAndReceive(routingKey, hmsg,
                    messagePostProcessor);
        }
        catch (Exception ex) {
            logger.error("Failed to syncSend message ", ex);
            throw new MPublisherException(ex);
        }
    }

    private Message convert(MMessage mmsg) {
        Message msg = null;
        if (ContentType.PlainText.equals(mmsg.getContentType())) {
            msg = defaultMessageConverter.toMessage(mmsg, mmsg.getMessageProperties());
        }
        else {
            msg = getConfigSupport().getMessageConverter().toMessage(mmsg, mmsg.getMessageProperties());
        }
        return msg;
    }

}
