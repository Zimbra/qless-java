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

package com.zimbra.qless.integration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zimbra.qless.Client;
import com.zimbra.qless.JSON;
import com.zimbra.qless.Queue;
import com.zimbra.qless.RecurringJob;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RecurringJobTest {
    final Logger LOGGER = LoggerFactory.getLogger(RecurringJobTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    Queue queue;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new Client(jedisPool);
        jedis.flushDB();
        queue = client.queue("foo");
    }
    
//      describe RecurringJob, :integration do
//        let(:queue) { client.queues['foo'] }
//
//        it 'can take either a class or string' do
//          queue.recur('Qless::Job', {}, 5, jid: 'a').should eq('a')
//          queue.recur(Job, {}, 5, jid: 'b').should eq('b')
//        end
    @Test
    public void canTakeEitherAClassOrString() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void hasExpectedProperties() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("whiz",  "bang");
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "jid");
        opts.put("tags", Arrays.asList("foo"));
        opts.put("retries", "3");
        String jid = queue.recur("Foo", data, 60, opts);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        Assert.assertEquals("jid", job.jid());
        Assert.assertNotNull(job.data());
        Assert.assertTrue(job.data() instanceof Map);
        Assert.assertEquals("bang", ((Map<String,Object>)job.data()).get("whiz"));
        Assert.assertEquals("[\"foo\"]", JSON.stringify(job.tags()));
        Assert.assertEquals(0, job.count());
        Assert.assertEquals(0, job.backlog());
        Assert.assertEquals(3, job.retries());
        Assert.assertEquals(60, job.interval());
        Assert.assertEquals(0, job.priority());
        Assert.assertEquals("foo", job.queueName());
        Assert.assertEquals("Foo", job.klassName());
    }

    @Test
    public void canSetItsPriority() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        client.jobs(jid).priority(10);
        Assert.assertEquals(10, client.jobs(jid).priority());
    }

    @Test
    public void canSetItsRetries() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.retries(5);
        Assert.assertEquals(5, job.retries());
    }

    @Test
    public void canSetItsInterval() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.interval(10);
        Assert.assertEquals(10, job.interval());
    }

    @Test
    public void canSetItsData() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.data("foo", "bar");
        Assert.assertEquals("bar", job.data("foo"));
    }

    @Test
    public void canSetItsKlass() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.klass("Foo");
        Assert.assertEquals("Foo", job.klassName());
        Assert.assertEquals("Foo", client.jobs(jid).klassName());
    }

    @Test
    public void canSetItsQueue() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.requeue("bar");
        Assert.assertEquals("bar", job.queueName());
        Assert.assertEquals("bar", client.jobs(jid).queueName());
    }

    @Test
    public void canSetItsBacklog() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.backlog(1);
        Assert.assertEquals(1, job.backlog());
        Assert.assertEquals(1, ((RecurringJob)client.jobs(jid)).backlog());
    }

//        it 'exposes when the next job will run' do
//          pending('This is implemented only in the python client')
//          queue.recur('Foo', {}, 60, jid: 'jid')
//          nxt = client.jobs['jid'].next
//          queue.pop
//          expect(client.jobs['jid'].next - nxt - 60).to be < 1
//        end
    @Test
    public void exposesWhenTheNextJobWillRun() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void canCancelItself() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        client.jobs(jid).cancel();
        Assert.assertEquals(null, client.jobs(jid));
    }

    @Test
    public void canSetItsTags() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        client.jobs(jid).tag("foo");
        Assert.assertEquals("foo", client.jobs(jid).tags().get(0));
        client.jobs(jid).untag("foo");
        Assert.assertEquals(0, client.jobs(jid).tags().size());
        Assert.assertEquals("[]", JSON.stringify(client.jobs(jid).tags()));
    }

//        describe 'last spawned job access' do
//          it 'exposes the jid and job of the last spawned job' do
//            queue.recur('Foo', {}, 60, jid: 'jid')
//            Timecop.travel(Time.now + 121) do # give it enough time to spawn 2 jobs
//              last_spawned = queue.peek(2).max_by(&:initially_put_at)
//
//              job = client.jobs['jid']
//              expect(job.last_spawned_jid).to eq(last_spawned.jid)
//              expect(job.last_spawned_job).to eq(last_spawned)
//            end
//          end
    @Test
    public void exposesJidAndJobOfLastSpawnedJob() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void lastSpawnedJobReturnsNullIfNoJobHasEverBeenSpawned() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        Assert.assertEquals(null, ((RecurringJob)client.jobs(jid)).lastSpawnedJid());
        Assert.assertEquals(null, ((RecurringJob)client.jobs(jid)).lastSpawnedJob());
    }
}
