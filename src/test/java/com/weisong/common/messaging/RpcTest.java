package com.weisong.common.messaging;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.weisong.common.messaging.MMessage.ContentType;
import com.weisong.common.messaging.admin.MMessagingConnectionFactory;
import com.weisong.common.messaging.consumer.MConsumer;
import com.weisong.common.messaging.publisher.MPublisher;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RpcTestJavaConfig.class })
public class RpcTest {

    static Logger logger = LoggerFactory.getLogger(RpcTest.class);

    @Autowired @Qualifier("test.rpc.exchange") private MExchange testRpcExchange;
    @Autowired @Qualifier("test.rpc.consumer") private MConsumer rpcServer;
    @Autowired @Qualifier("test.rpc.publisher") private MPublisher rpcClient;
    @Autowired @Qualifier("test.rpc.queue") private MQueue testRpcQueue;
	@Autowired private MMessagingConnectionFactory connFactory;
	
	@Before
	public void setUp() {
        rpcServer.getConfigSupport().setMessageListenerAdapterDelegate(new MMessageListener());
        rpcServer.start();
	}
	
	@Test
	public void testSyncCall() throws Exception {
		int n = 10;
		MMessage message = new MMessage(String.valueOf(n), ContentType.PlainText);
		message.setXMessageHeaderTagValue("RPCCall");
		message.setReplyTo("fib");
		message.setMessageId("RPCall-" + (int)(Math.random()*100));
        MMessage response = rpcClient.syncSend(message);
		validateResponse(n, response, message);
	}

	@Test
	public void testSyncCallWithTimeout() throws Exception {
		int n = 10;
		MMessage message = new MMessage(String.valueOf(n), ContentType.PlainText);
		message.setXMessageHeaderTagValue("timedout");
		message.setMessageId("RPCall-" + (int)(Math.random()*100));
		MMessage response = rpcClient.syncSend(message, 1000);
		validateResponse(n, response, message);
	}
	
	private void validateResponse(int n, MMessage response, MMessage request)
			throws UnsupportedEncodingException, Exception {
		//validation
		if(request.getXMessageHeaderTagValue().equals("timedout")) {
			Assert.assertNull(response);
			return;
		}
		Assert.assertNotNull("Response must not be null!", response);
		Object res = (Object)response.getMessageBody();
		Assert.assertNotNull(res);
		String result = "";
		if(res instanceof String) {
			result = (String)res;
		} else if(res instanceof byte[]) {
			result = new String((byte[])res, "utf-8");
		}
		logger.info("Received fib value: " + result);
		Assert.assertEquals(fib(n), Integer.valueOf(result).intValue());
	}

    public static int fib(int n) throws Exception {
        if (n < 2)
            return n;
        return fib(n - 1) + fib(n - 2);
    }

    private class MMessageListener extends MMessageListenerAdapter {
        @Override
        public MMessage handleMessage(MMessage message) throws Exception {
            logger.info("Sync call: " + message.getReplyTo());
            logger.info("xMessageHeaderTagValue: " + message.getXMessageHeaderTagValue());
            
            if(message.getXMessageHeaderTagValue() != null) {
                if(message.getXMessageHeaderTagValue().equals("timedout")) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        //
                    }
                }
            }
            int n = Integer.parseInt((String)message.getMessageBody());
            
            String response = String.valueOf(RpcTest.fib(n));
            logger.info(String.format("fib(%d) = %s", n, response));
            return new MMessage(response, message.getMessageProperties());
        }
    }
}
