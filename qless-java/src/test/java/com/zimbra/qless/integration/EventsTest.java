package com.zimbra.qless.integration;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.zimbra.qless.QlessClient;
import com.zimbra.qless.ClientEvents.QlessEventListener;
import com.zimbra.qless.Job;
import com.zimbra.qless.Queue;


public class EventsTest {
    final Logger LOGGER = LoggerFactory.getLogger(EventsTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    QlessClient client;
    Queue queue;
    Job untracked, tracked;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new QlessClient(jedisPool);
        jedis.flushDB();
        
        queue = client.queue("foo");
        untracked = client.getJob(queue.put("Foo", null, null));
        tracked = client.getJob(queue.put("Bar", null, null));
        tracked.track();
    }
    
    @Test
    public void canDetectCanceledEvents() throws Exception {
        final EventCapture eventCapture = new EventCapture();
        client.events().on("canceled").fire(eventCapture);
        tracked.cancel();
        untracked.cancel();
        Thread.sleep(100);
        Assert.assertEquals(1, eventCapture.jidsForEvent("canceled").size());
        Assert.assertEquals(tracked.getJid(), eventCapture.jidsForEvent("canceled").iterator().next());
    }
    
    @Test
    public void canDetectCompletionEvents() throws Exception {
        final EventCapture eventCapture = new EventCapture();
        client.events().on("completed").fire(eventCapture);
        for (Job job: queue.pop(10)) {
            job.isComplete();
        }
        Thread.sleep(100);
        Assert.assertEquals(1, eventCapture.jidsForEvent("completed").size());
        Assert.assertEquals(tracked.getJid(), eventCapture.jidsForEvent("completed").iterator().next());
    }

    @Test
    public void canDetectFailedEvents() throws Exception {
        final EventCapture eventCapture = new EventCapture();
        client.events().on("failed").fire(eventCapture);
        for (Job job: queue.pop(10)) {
            job.fail("foo", "bar");
        }
        Thread.sleep(100);
        Assert.assertEquals(1, eventCapture.jidsForEvent("failed").size());
        Assert.assertEquals(tracked.getJid(), eventCapture.jidsForEvent("failed").iterator().next());
    }

    @Test
    public void canDetectPopEvents() throws Exception {
        final EventCapture eventCapture = new EventCapture();
        client.events().on("popped").fire(eventCapture);
        queue.pop(10);
        Thread.sleep(100);
        Assert.assertEquals(1, eventCapture.jidsForEvent("popped").size());
        Assert.assertEquals(tracked.getJid(), eventCapture.jidsForEvent("popped").iterator().next());
    }

    @Test
    public void canDetectPutEvents() throws Exception {
        final EventCapture eventCapture = new EventCapture();
        client.events().on("put").fire(eventCapture);
        tracked.requeue("other");
        untracked.requeue("other");
        Thread.sleep(100);
        Assert.assertEquals(1, eventCapture.jidsForEvent("put").size());
        Assert.assertEquals(tracked.getJid(), eventCapture.jidsForEvent("put").iterator().next());
    }

    @Test
    public void canDetectStalledEvents() throws Exception {
        client.config().put("grace-period", 0);
        client.config().put("heartbeat", 0);
        final EventCapture eventCapture = new EventCapture();
        client.events().on("stalled").fire(eventCapture);
        queue.pop(2);
        queue.pop(2);
        Thread.sleep(100);
        Assert.assertEquals(1, eventCapture.jidsForEvent("stalled").size());
        Assert.assertEquals(tracked.getJid(), eventCapture.jidsForEvent("stalled").iterator().next());
    }
    
    
    class EventCapture implements QlessEventListener {
        Multimap<String, Object> eventsByChannel = ArrayListMultimap.create();
        
        public void fire(String channel, Object event) {
            LOGGER.debug("fire {}", event);
            eventsByChannel.put(channel, event);
        }
        
        public Collection<Object> jidsForEvent(String channel) {
            return eventsByChannel.get(channel);
        }
    };
    
}
