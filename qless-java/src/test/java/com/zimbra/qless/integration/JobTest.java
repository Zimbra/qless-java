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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;

import com.zimbra.qless.Client;
import com.zimbra.qless.JSON;
import com.zimbra.qless.Job;
import com.zimbra.qless.Queue;


public class JobTest {
    final Logger LOGGER = LoggerFactory.getLogger(JobTest.class);
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
    
    @Test
    public void canSpecifyJidInPutAndKlassAsString() throws IOException {
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "a");
        String jid = queue.put("Qless::Job", null, opts);
        Assert.assertEquals("a",  jid);
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void hasExpectedProperties() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("whiz",  "bang");
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "jid");
        opts.put("tags", Arrays.asList("foo"));
        opts.put("retries", "3");
        String jid = queue.put("Foo", data, opts);
        Job job = client.jobs(jid);
        Assert.assertEquals("jid", job.jid());
        Assert.assertNotNull(job.data());
        Assert.assertTrue(job.data() instanceof Map);
        Assert.assertEquals("bang", ((Map)job.data()).get("whiz"));
        Assert.assertEquals("[\"foo\"]", JSON.stringify(job.tags()));
        Assert.assertEquals(0, job.priority());
        Assert.assertEquals(0, job.expiresAt());
        Assert.assertEquals("[]", JSON.stringify(job.dependents()));
        Assert.assertEquals("Foo", job.klassName());
        Assert.assertEquals("foo", job.queueName());
        Assert.assertEquals("", job.workerName());
        Assert.assertEquals(3, job.retriesLeft());
        Assert.assertEquals("[]", JSON.stringify(job.dependencies()));
        Assert.assertEquals(3, job.originalRetries());
    }

    @Test
    public void canSetItsOwnPriority() throws IOException {
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "jid");
        opts.put("priority", 0);
        @SuppressWarnings("unused")
        String jid = queue.put("Foo", null, opts);
        Assert.assertEquals(0, client.jobs("jid").priority());
        client.jobs("jid").priority(10);
        Assert.assertEquals(10, client.jobs("jid").priority());
    }

    @Test
    public void exposesItsQueueObject() throws IOException {
        String jid = queue.put("Foo", null, null);
        Job job = client.jobs(jid);
        Assert.assertEquals(job.queueName(), job.queue().name());
        Assert.assertTrue(job.queue() instanceof Queue);
    }
    
//        it 'exposes its klass' do
//          queue.put(Job, {}, jid: 'jid')
//          expect(client.jobs['jid'].klass).to eq(Job)
//        end
    @Test
    public void exposesItsKlass() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void exposesItsTtl() throws IOException {
        client.config().put("heartbeat", 10);
        queue.put("Foo", null, null);
        Job job = queue.pop();
        int ttl = job.ttl();
        Assert.assertTrue(ttl >= 9 && ttl <= 10);
    }

    @Test
    public void exposesItsCancelMethod() throws IOException {
        String jid = queue.put("Foo", null, null);
        client.jobs(jid).cancel();
        Assert.assertEquals(null, client.jobs(jid));
    }

    @Test
    public void canTagItself() throws IOException {
        String jid = queue.put("Foo", null, null);
        client.jobs(jid).tag("foo");
        Assert.assertEquals("foo", client.jobs(jid).tags().get(0));
    }

    @Test
    public void canUntagItself() throws IOException {
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "jid");
        opts.put("tags", Arrays.asList("foo"));
        String jid = queue.put("Foo", null, opts);
        client.jobs(jid).untag("foo");
        Assert.assertEquals(0, client.jobs(jid).tags().size());
    }

    @Test
    public void exposesDataAccess() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("whiz",  "bang");
        String jid = queue.put("Foo", data, null);
        Assert.assertEquals("bang", client.jobs(jid).data("whiz"));
    }

    @Test
    public void exposesDataAssignment() throws IOException {
        String jid = queue.put("Foo", null, null);
        Job job = client.jobs(jid);
        job.data("foo", "bar");
        Assert.assertEquals("bar", job.data("foo"));
    }

    @Test
    public void canMoveItself() throws IOException {
        String jid = queue.put("Foo", null, null);
        client.jobs(jid).requeue("bar");
        Assert.assertEquals("bar", client.jobs(jid).queue().name());
        Assert.assertEquals("bar", client.jobs(jid).queueName());
    }

    @Test
    public void failsWhenRequeingACancelledJob() throws IOException {
        String jid = queue.put("Foo", null, null);
        Job job = client.jobs(jid); 
        client.jobs(jid).cancel(); // Cancel a different instance that represents the same job
        try {
            job.requeue("bar");
            Assert.fail("Expected exception");
        } catch (JedisDataException e) {}
        Assert.assertEquals(null, client.jobs(jid));
    }

    @Test
    public void canCompleteItself() throws IOException {
        String jid = queue.put("Foo", null, null);
        queue.pop().complete();
        Assert.assertEquals("complete", client.jobs(jid).state());
    }

    @Test
    public void canAdvanceItselfToAnotherQueue() throws IOException {
        String jid = queue.put("Foo", null, null);
        queue.pop().complete("bar");
        Assert.assertEquals("waiting", client.jobs(jid).state());
    }

//        it 'can heartbeat itself' do
//          client.config['heartbeat'] = 10
//          queue.put('Foo', {}, jid: 'jid')
//          job = queue.pop
//          before = job.ttl
//          client.config['heartbeat'] = 20
//          job.heartbeat
//          expect(job.ttl).to be > before
//        end
    @Test
    public void canHeartbeatItself() {
        Assert.fail("NIY"); // TODO
    }

//        it 'raises an error if it fails to heartbeat' do
//          queue.put('Foo', {}, jid: 'jid')
//          expect { client.jobs['jid'].heartbeat }.to raise_error
//        end
    @Test
    public void raisesAnErrorIfItFailsToHeartbeat() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void knowsIfItIsTracked() throws IOException {
        String jid = queue.put("Foo", null, null);
        Assert.assertEquals(false, client.jobs(jid).tracked());
        client.jobs(jid).track();
        Assert.assertEquals(true, client.jobs(jid).tracked());
        client.jobs(jid).untrack();
        Assert.assertEquals(false, client.jobs(jid).tracked());
    }

    @Test
    public void canAddAndRemoveDependencies() throws IOException {
        String jidA = queue.put("Foo", null, null);
        String jidB = queue.put("Foo", null, null);
        Map<String,Object> opts = new HashMap<>();
        opts.put("depends", Arrays.asList(jidA));
        String jidC = queue.put("Foo", null, opts);
        Assert.assertNotNull(client.jobs(jidC).dependencies());
        Assert.assertEquals(1, client.jobs(jidC).dependencies().size());
        Assert.assertEquals(jidA, client.jobs(jidC).dependencies().get(0));
        client.jobs(jidC).depend(jidB);
        Assert.assertEquals(2, client.jobs(jidC).dependencies().size());
        Assert.assertTrue(client.jobs(jidC).dependencies().contains(jidA));
        Assert.assertTrue(client.jobs(jidC).dependencies().contains(jidB));
        client.jobs(jidC).undepend(jidA);
        Assert.assertEquals(1, client.jobs(jidC).dependencies().size());
        Assert.assertEquals(jidB, client.jobs(jidC).dependencies().get(0));
    }

//        it 'raises an error if retry fails' do
//          queue.put('Foo', {}, jid: 'jid')
//          expect { client.jobs['jid'].retry }.to raise_error
//        end
    @Test
    public void raisesAnErrorIfRetryFails() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void hasAReasonableToString() throws IOException {
        String jid = queue.put("Foo", null, null);
        String s = client.jobs(jid).toString();
        Assert.assertTrue(s.contains("Foo (" + jid + " / foo / waiting)"));
    }

//        it 'fails to process if it does not have the method' do
//          queue.put(NoPerformJob, {}, jid: 'jid')
//          queue.pop.perform
//          job = client.jobs['jid']
//          expect(job.state).to eq('failed')
//          expect(job.failure['group']).to eq('foo-method-missing')
//        end
    @Test
    public void failsToProcessIfItDoesNotHaveARunnableMethod() {
        Assert.fail("NIY"); // TODO
    }

//        it 'raises an error if it cannot find the class' do
//          queue.put('Foo::Whiz::Bang', {}, jid: 'jid')
//          queue.pop.perform
//          job = client.jobs['jid']
//          expect(job.state).to eq('failed')
//          expect(job.failure['group']).to eq('foo-NameError')
//        end
    @Test
    public void raisesAnErrorIfTheKlassIsNotFound() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void exposesFailingAJob() throws IOException {
        String jid = queue.put("Foo", null, null);
        queue.pop().fail("foo", "message");
        Assert.assertEquals("failed", client.jobs(jid).state());
        Assert.assertEquals("foo", client.jobs(jid).failure("group"));
        Assert.assertEquals("message", client.jobs(jid).failure("message"));
    }

//        it 'only invokes before_complete on an already-completed job' do
//          queue.put('Foo', {})
//          job = queue.pop
//          job.fail('foo', 'some message')
//
//          events = []
//          job.before_complete { events << :before }
//          job.after_complete  { events << :after  }
//
//          expect do
//            job.complete
//          end.to raise_error(Qless::Job::CantCompleteError, /failed/)
//
//          expect(events).to eq([:before])
//        end
    @Test
    public void invokesBeforeCompleteOnAnAlreadyCompletedJob() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void providesAccessToLog() throws IOException {
        String jid = queue.put("Foo", null, null);
        client.jobs(jid).log("hello");
        Map<String,Object> data = new HashMap<>();
        data.put("foo", "bar");
        client.jobs(jid).log("hello", data);
        List<Job.History> history = client.jobs(jid).history(); 
        Assert.assertNotNull(history);
        Assert.assertEquals(3, history.size());
        Assert.assertEquals("hello", history.get(1).what());
        Assert.assertEquals("bar", history.get(2).get("foo"));
    }

//        it 'returns the source recurring job from `spawned_from`' do
//          queue.recur('Foo', {}, 1, jid: 'recurring-jid', offset: -1)
//          recurring_job = client.jobs['recurring-jid']
//          expect(recurring_job).to be_a(RecurringJob)
//          expect(queue.pop.spawned_from).to eq(recurring_job)
//        end
    @Test
    public void returnsTheSourceRecurringJobFromSpawnedFrom() {
        Assert.fail("NIY"); // TODO
    }

//        it 'returns nil from `spawned_from` when it is not a recurring job' do
//          queue.put('Foo', {}, jid: 'jid')
//          expect(client.jobs['jid'].spawned_from).to be_nil
//        end
//      end
    @Test
    public void returnsNullFromSpawnedFromWhenItIsNotARecurringJob() throws IOException {
        String jid = queue.put("Foo", null, null);
        Assert.assertEquals(null, client.jobs(jid).spawnedFrom());
    }
}