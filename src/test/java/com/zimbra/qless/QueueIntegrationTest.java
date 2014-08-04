/*
 * ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2014 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.qless;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class QueueIntegrationTest {
    final Logger LOGGER = LoggerFactory.getLogger(QueueIntegrationTest.class);
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
        queue = client.queues("foo");
    }
    
//    it 'provides access to jobs in different states' do
//        queue.put('Foo', {})
//        [:depends, :running, :stalled, :scheduled, :recurring].each do |cmd|
//          expect(queue.jobs.send(cmd)).to eq([])
//        end
//      end
    @Test
    public void providesAccessToJobsInDifferentStates() throws IOException {
        queue.put("Foo", null, null);
        final String[] STATES = {"depends", "running", "stalled", "scheduled", "recurring"};
        for (String state: STATES) {
//            Assert.assertEquals(0, queue.jobs()
        }
        Assert.fail("NIY"); // TODO
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

//      it 'provides access to the heartbeat configuration' do
//        original = queue.heartbeat
//        queue.heartbeat = 10
//        expect(queue.heartbeat).to_not eq(original)
//      end
    @Test
    public void providesAccessToHeartbeatConfiguration() throws IOException {
        Assert.fail("NIY"); // TODO
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
        Assert.assertEquals(jid, queue.peek().jid());
    }

    @Test
    public void providesAnArrayOfJobsWhenUsingMultiPeek() throws IOException {
        queue.put("Foo", null, null);
        queue.put("Foo", null, null);
        Assert.assertEquals(2, queue.peek(10).size());
    }

//      it 'exposes queue statistics' do
//        expect(queue.stats).to be
//      end
    @Test
    public void exposesQueueStatistics() throws IOException {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void exposesQueueLength() throws IOException {
        Assert.assertEquals(0, queue.length());
        queue.put("Foo", null, null);
        Assert.assertEquals(1, queue.length());
    }

//      it 'can pause and unpause itself' do
//        expect(queue.paused?).to be(false)
//        queue.pause
//        expect(queue.paused?).to be(true)
//        queue.unpause
//        expect(queue.paused?).to be(false)
//      end
    @Test
    public void canPauseAndUnpauseItself() throws IOException {
        Assert.fail("NIY"); // TODO
    }

//      it 'can optionally stop all running jobs when pausing' do
//        pending('this is specific to ruby')
//      end
    @Test
    public void canOptionallyStopAllRunningJobsWhenPausing() throws IOException {
        Assert.fail("NIY"); // TODO
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
