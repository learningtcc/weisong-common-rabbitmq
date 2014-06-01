package com.weisong.common.messaging;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.weisong.common.messaging.MMessage.ContentType;
import com.weisong.common.messaging.admin.MMessagingConnectionFactory;
import com.weisong.common.messaging.consumer.MConsumer;
import com.weisong.common.messaging.consumer.MConsumerConfigSupport;
import com.weisong.common.messaging.publisher.MPublisher;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MessagingTestJavaConfig.class })
public class PublisherTest {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired private MPublisher publisher;
    @Autowired private MMessagingConnectionFactory messagingConnectionFactory;
    @Autowired @Qualifier("toDeviceExchange") private MExchange toDeviceExchange;
    
    private int routingKeyCount = 5;
    private String[] routingKeys;
	
    private MyMessageListener messageListener = new MyMessageListener();
    
    @Before
	public void setUp() {
        // Create routing keys
        Random random = new Random();
        routingKeys = new String[routingKeyCount];
        for(int i = 0; i < routingKeyCount; i++) {
            routingKeys[i] = String.valueOf(100000 + random.nextInt(10000));
        }
        
		// Create consumer
		createConsumer();
	}
	
    @Test
    public void testSendMessage() throws Exception {
        Assert.assertNotNull(publisher);
        publisher.getConfigSupport().setMessageConverter(new MMessageConverterJson());

        for (String routingKey : routingKeys) {
            TestUser user = new TestUser(String.valueOf(new Random().nextInt(10000)), "James", "Bind");
            MMessage message = new MMessage(user, ContentType.Json);
            message.setMessageId(routingKey);
            logger.info("Publish message: " + message.toString());
            publisher.send(routingKey, message);
        }
        
        // Wait for messages to be delivered
        Thread.sleep(500);
        
        Assert.assertEquals(messageListener.receivedMessageCount, routingKeys.length);
    }

	private void createConsumer() {
		MQueue mQueue = new MQueue();
		mQueue.setAutoDelete(true);
		mQueue.setName("mediation.to.device.queue");
		mQueue.setExclusive(false);
		mQueue.setDurable(false);
		
		MConsumerConfigSupport configSupport = new MConsumerConfigSupport();
		configSupport.setMQueue(mQueue);
		configSupport.setMessagingConnectionFactory(messagingConnectionFactory);
		configSupport.setExchange(toDeviceExchange);
		configSupport.setMessageConverter(new MMessageConverterJson());
		configSupport.setMessageListenerAdapterDelegate(messageListener);
		configSupport.setAckMode(AcknowledgeMode.AUTO);
		
        MConsumer consumer = new MConsumer();
		consumer.setConfigSupport(configSupport);
		for(String routingKey : routingKeys) {
            consumer.addRoutingKey(routingKey);
		}
		consumer.start();
    }

    private class MyMessageListener extends MMessageListenerAdapter {
        private int receivedMessageCount = 0;
        @Override
        public MMessage handleMessage(MMessage hiveMessage) throws Exception {
            TestUser userReceived = (TestUser)hiveMessage.getMessageBody();
            logger.info("Received object:" + userReceived);
            Assert.assertEquals("Object not same: ", userReceived.getLastname(), userReceived.getLastname());
            ++receivedMessageCount;
            return null;
        }
        
    }
}
