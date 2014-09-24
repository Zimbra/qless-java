package com.zimbra.qless.integration;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zimbra.qless.Client;
import com.zimbra.qless.Queue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class QueueTest {
    final Logger LOGGER = LoggerFactory.getLogger(QueueTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    Queue queue;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.flushDB();
        } finally {
            jedisPool.returnResource(jedis);
        }
        client = new Client(jedisPool);
        queue = client.queue("foo");
    }
    
    @Test
    public void providesAccessToJobsInDifferentStates() throws IOException {
        queue.put("Foo", null, null);
        final String[] STATES = {"depends", "running", "stalled", "scheduled", "recurring"};
        for (String state: STATES) {
            Assert.assertEquals(Collections.EMPTY_LIST, queue.jobs().jobs(state));
        }
    }
    
    @Test
    public void providesAccessToJobCounts() throws IOException {
        queue.put("Foo", null, null);
        Map<String,Object> counts = queue.counts();
        Assert.assertEquals(0    , counts.get("depends"));
        Assert.assertEquals("foo", counts.get("name"));
        Assert.assertEquals(false, counts.get("paused"));
        Assert.assertEquals(0    , counts.get("recurring"));
        Assert.assertEquals(0    , counts.get("scheduled"));
        Assert.assertEquals(0    , counts.get("running"));
        Assert.assertEquals(0    , counts.get("stalled"));
        Assert.assertEquals(1    , counts.get("waiting"));
    }

    @Test
    public void providesAccessToHeartbeatConfiguration() throws IOException {
        int original = queue.heartbeat();
        queue.heartbeat(10);
        Assert.assertTrue(queue.heartbeat() != original);
    }

    @Test
    public void providesListOfJobsWhenUsingMultiPop() throws IOException {
        queue.put("Foo", null, null);
        queue.put("Foo", null, null);
        Assert.assertEquals(2, queue.peek(10).size());
    }

    @Test
    public void exposesQueuePeeking() throws IOException {
        String jid = queue.put("Foo", null, null);
        Assert.assertEquals(jid, queue.peek().getJid());
    }

    @Test
    public void providesAnArrayOfJobsWhenUsingMultiPeek() throws IOException {
        queue.put("Foo", null, null);
        queue.put("Foo", null, null);
        Assert.assertEquals(2, queue.peek(10).size());
    }

    @Test
    public void exposesQueueStatistics() throws IOException {
    	Object stats = queue.stats();
        Assert.assertTrue(stats != null);
    }

    @Test
    public void exposesQueueLength() throws IOException {
        Assert.assertEquals(0, queue.length());
        queue.put("Foo", null, null);
        Assert.assertEquals(1, queue.length());
    }

    @Test
    public void canPauseAndUnpauseItself() throws IOException {
        Assert.assertEquals(false, queue.paused());
        queue.pause();
        Assert.assertEquals(true, queue.paused());
        queue.unpause();
        Assert.assertEquals(false, queue.paused());
    }

    @Test
    public void canOptionallyStopAllRunningJobsWhenPausing() throws IOException {
        LOGGER.debug("This is specific to Ruby");
    }

    @Test
    public void exposesMaxConcurrency() throws IOException {
        queue.maxConcurrency(5);
        Assert.assertEquals(5, queue.maxConcurrency());
    }

    @Test
    public void getsNullForPoppingAnEmptyQueue() throws IOException {
        Assert.assertEquals(null, queue.pop());
    }

    @Test
    public void getsNullForPeekingAnEmptyQueue() throws IOException {
        Assert.assertEquals(null, queue.peek());
    }
}
