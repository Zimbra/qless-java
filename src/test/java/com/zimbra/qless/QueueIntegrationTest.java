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
        Assert.fail("NIY"); // TODO
    }
    
//      it 'provides access to job counts' do
//        queue.put('Foo', {})
//        expect(queue.counts).to eq({
//          'depends'   => 0,
//          'name'      => 'foo',
//          'paused'    => false,
//          'recurring' => 0,
//          'scheduled' => 0,
//          'running'   => 0,
//          'stalled'   => 0,
//          'waiting'   => 1
//        })
//      end
    @Test
    public void providesAccessToJobCounts() throws IOException {
        Assert.fail("NIY"); // TODO
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

//      it 'exposes max concurrency' do
//        queue.max_concurrency = 5
//        expect(queue.max_concurrency).to eq(5)
//      end
    @Test
    public void exposesMaxConcurrency() throws IOException {
        Assert.fail("NIY"); // TODO
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
