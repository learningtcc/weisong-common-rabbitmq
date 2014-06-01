package com.weisong.common.messaging;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.amqp.core.Queue;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class MQueue {
	
	private String name;
	private boolean durable = false;
	private boolean exclusive = false;
	private boolean autoDelete = false;

	public static Queue convert(MQueue queue) {
		if(queue == null) return null;
		Queue amqpQ = null;
		amqpQ = new Queue(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete());
		return amqpQ;
	}

    public static MQueue createAnonymous() {
        return createQueue(null);
    }

    public static MQueue create(String name) {
        return createQueue(name);
    }

    private static MQueue createQueue(String name) {
        MQueue q = new MQueue();
        q.setName(name == null ? UUID.randomUUID().toString() : name);
        q.setDurable(false);
        q.setExclusive(false);
        q.setAutoDelete(true);
        return q;
    }
}
