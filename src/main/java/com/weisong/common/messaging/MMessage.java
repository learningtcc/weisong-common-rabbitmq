package com.weisong.common.messaging;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.util.Assert;

@Getter @Setter
public class MMessage {
	public static final String X_MESSAGE_HEADER_TAG_KEY = "x-message-header-tag-key";
	public static final String X_MESSAGE_HEADER_TIMEOUT_KEY = "x-message-header-timeout-key";

	private String messageId;
	private byte[] correlationId;
	private MessageDeliveryMode deliveryMode = MessageDeliveryMode.NON_PERSISTENT;
	private String replyTo;
	private ContentType contentType = ContentType.PlainText;
	private String receivedExchange;
	private String receivedRoutingKey;
	private Object messageBody;
	private String xMessageHeaderTagValue;
	private Long xMessageHeaderSyncTimeoutValue;
	

	public MMessage(Object messageBody, MessageProperties prop) {
		copyMessageProperties(prop);
		setMessageBody(messageBody);
	}

	public MMessage(Object messageBody, ContentType contentType) {
		setContentType(contentType);
		setMessageBody(messageBody);
	}
	
	public void setCorrelationId(String correlationId) {
		try {
			Assert.notNull(correlationId, "Correlation ID can not be null");
			this.correlationId = correlationId.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
	}

	public void setMessageBody(Object messageBody)  {
		this.messageBody = messageBody;
		if(correlationId == null) {
			setCorrelationId(UUID.randomUUID().toString());
			setMessageId(UUID.randomUUID().toString());
		}
	}

	public MessageProperties getMessageProperties()
	{
		MessageProperties prop = new MessageProperties();
		if(this.correlationId != null) prop.setCorrelationId(this.correlationId);
		if(this.contentType != null) prop.setContentType(this.contentType.toString());
		if(this.deliveryMode != null) prop.setDeliveryMode(this.deliveryMode);
		if(this.messageId != null) prop.setMessageId(this.messageId);
		if(this.replyTo != null) prop.setReplyTo(this.replyTo);
		if(this.receivedExchange != null) prop.setReceivedExchange(this.receivedExchange);
		if(this.receivedRoutingKey != null) prop.setReceivedRoutingKey(this.receivedRoutingKey);
		if(this.xMessageHeaderTagValue != null) {
			prop.setHeader(X_MESSAGE_HEADER_TAG_KEY, this.xMessageHeaderTagValue);
		}
		if(this.xMessageHeaderSyncTimeoutValue != null) {
			prop.setHeader(X_MESSAGE_HEADER_TIMEOUT_KEY, this.xMessageHeaderSyncTimeoutValue);
		}
		return prop;
	}
	
	private void copyMessageProperties(MessageProperties prop) {
		if(prop == null)
			return;
		this.messageId = prop.getMessageId();
		this.correlationId = prop.getCorrelationId();
		this.contentType = ContentType.lookup(prop.getContentType());
		this.messageId = prop.getMessageId();
		this.receivedExchange = prop.getReceivedExchange();
		this.receivedRoutingKey = prop.getReceivedRoutingKey();
		this.replyTo = prop.getReplyTo();
		Map<String, Object> headers = prop.getHeaders();
		if(headers != null) {
			this.xMessageHeaderTagValue = (String)headers.get(X_MESSAGE_HEADER_TAG_KEY);
			if(headers.get(X_MESSAGE_HEADER_TIMEOUT_KEY) != null) {
				Long timeout=(Long)headers.get(X_MESSAGE_HEADER_TIMEOUT_KEY);
				this.xMessageHeaderSyncTimeoutValue=timeout;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("messageId=").append(messageId).append(",")
		   .append("replyTo=").append(replyTo).append(",")
		   .append("contentType=").append(contentType).append(",")
		   .append("xMessageHeaderTagValue=").append(xMessageHeaderTagValue).append(",")
		   .append("messageBody=").append(messageBody).append(",")
		   .append("correlationId=").append(new String(correlationId));
		return buf.toString();
	}
	
	public enum ContentType {
		Binary,
		PlainText,
		SerializedObject,
		Json,
		Xml;
		
		private static HashMap<ContentType, String> contentString = 
				new HashMap<ContentType, String>(5);

		static {
			contentString.put(Binary, "application/octet-stream");
			contentString.put(PlainText, "text/plain");
			contentString.put(SerializedObject, "application/x-java-serialized-object");
			contentString.put(Json, "application/json");
			contentString.put(Xml, "application/xml");
		}
		
		public String toString() {
			return contentString.get(this);
		}
		
		public static ContentType lookup(String contentType) {
			for(Map.Entry<ContentType, String> en:contentString.entrySet()) {
				if(en.getValue().equalsIgnoreCase(contentType))
					return en.getKey();
			}
			return null;
		}
		
	}

//	public class ContentTypeDeserializer extends JsonDeserializer<ContentType> {
//	    @Override
//	    public ContentType deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
//	        return ContentType.lookup(parser.getText());
//	    }
//	}

}
