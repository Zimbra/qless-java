package com.zimbra.qless.integration;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zimbra.qless.Client;
import com.zimbra.qless.ClientQueues;
import com.zimbra.qless.Queue;
import com.zimbra.qless.QueueCounts;


public class QueuesTest {
    final Logger LOGGER = LoggerFactory.getLogger(QueuesTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.flushDB();
        } finally {
            jedisPool.returnResource(jedis);
        }
        client = new Client(jedisPool);
    }
    
    @Test
    public void providesAccessToQueues() throws IOException {
        client.queue("A").put("Foo", null, null);
        ClientQueues queues = client.queues();
        Assert.assertEquals("A", queues.get("A").getName());
    }
    
    @Test
    public void providesAccessToQueueCounts() throws IOException {
        @SuppressWarnings("unused")
        Queue queueA = client.queue("A");
        Queue queueB = client.queue("B");
        Queue queueC = client.queue("C");
        queueB.put("Foo", null, null);
        queueC.put("Bar", null, null);
        queueC.put("Baz", null, null);
        ClientQueues queues = client.queues();
        
        List<QueueCounts> counts = queues.counts();
        Assert.assertEquals(2, counts.size());
        Assert.assertEquals(1, counts.get(0).waiting);
    }
}
