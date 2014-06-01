package com.weisong.common.messaging;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.weisong.common.messaging.admin.MMessagingAdminConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MessagingTestJavaConfig.class })
public class MessagingAdminConfigTest {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired private MMessagingAdminConfig messagingAdminConfig;

    @Test
    public void testMessagingAdminConfigTest() {
        Assert.assertNotNull("Messaging Admin Config info is null.", messagingAdminConfig);
        logger.info("messagingAdminConfig = " + messagingAdminConfig);
    }

}
