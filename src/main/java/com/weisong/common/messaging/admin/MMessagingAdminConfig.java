package com.weisong.common.messaging.admin;

import lombok.Getter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ToString
@ManagedResource
public class MMessagingAdminConfig implements InitializingBean {
    
    private static Logger logger = LoggerFactory.getLogger(MMessagingAdminConfig.class);
    
    private String serverUsed;
    private String[] serverAddresses;
    
    @Getter @Value("${rabbit.hosts:localhost}") private String messagingServerAddresses;
    @Value("${rabbit.channel-cache-size:1}") private int channelCacheSize;
    @Value("${rabbit.port:5672}") private int port;
    @Value("${rabbit.username:guest}") private String username;
    @Value("${rabbit.password:guest}") private String password;
    @Value("${rabbit.vhost:/}") private String vhost;

    @ManagedAttribute(description = "return server addresses")
    public String[] getServerAddresses() {
        return serverAddresses;
    }

    @ManagedAttribute(description = "return channel cache size")
    public int getChannelCacheSize() {
        return channelCacheSize;
    }

    @ManagedAttribute(description = "set channel cache size")
    public void setChannelCacheSize(int channelCacheSize) {
        this.channelCacheSize = channelCacheSize;
        logger.debug("set channel cache size from {} to {} ", this.channelCacheSize, channelCacheSize);
    }

    @ManagedAttribute(description = "return port")
    public int getPort() {
        return port;
    }

    @ManagedAttribute(description = "set port")
    public void setPort(int port) {
        this.port = port;
        logger.debug("set port from {} to {} ", this.port, port);
    }

    @ManagedAttribute(description = "return username")
    public String getUsername() {
        return username;
    }

    @ManagedAttribute(description = "set username")
    public void setUsername(String username) {
        this.username = username;
    }

    @ManagedAttribute(description = "return password")
    public String getPassword() {
        return password;
    }

    @ManagedAttribute(description = "set password")
    public void setPassword(String password) {
        this.password = password;
    }

    @ManagedAttribute(description = "return vhost")
    public String getVhost() {
        return vhost;
    }

    @ManagedAttribute(description = "set vhost")
    public void setVhost(String vhost) {
        this.vhost = vhost;
        logger.debug("set vhost from {} to {} ", this.vhost, vhost);
    }

    @ManagedAttribute(description = "return random server")
    public String getServerUsed() {
        if (serverUsed == null) {
            if (serverAddresses.length == 1) {
                serverUsed = serverAddresses[0];
            }
            else {
                int idx = ((int) (Math.random() * 10) % serverAddresses.length);
                serverUsed = serverAddresses[idx];
            }
            logger.info("server used: {} ", this.serverUsed);
        }
        return serverUsed;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serverAddresses = messagingServerAddresses.split(",");
        logger.info("set server address string {} ", messagingServerAddresses);
    }

}
