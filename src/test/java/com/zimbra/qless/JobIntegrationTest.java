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


public class JobIntegrationTest {
    final Logger LOGGER = LoggerFactory.getLogger(JobIntegrationTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new Client(jedisPool);
        jedis.flushDB();
    }
    
    @Test
    public void canSpecifyJidInPutAndKlassAsString() throws IOException {
        Queue queue = client.queues("foo");
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "a");
        String jid = queue.put("Qless::Job", null, opts);
        Assert.assertEquals("a",  jid);
    }
    
//        it 'has all the attributes we would expect' do
//          queue.put('Foo', { whiz: 'bang' }, jid: 'jid', tags: ['foo'], retries: 3)
//          job = client.jobs['jid']
//          expected = {
//            jid: 'jid',
//            data: { 'whiz' => 'bang' },
//            tags: ['foo'],
//            priority: 0,
//            expires_at: 0,
//            dependents: [],
//            klass_name: 'Foo',
//            queue_name: 'foo',
//            worker_name: '',
//            retries_left: 3,
//            dependencies: [],
//            original_retries: 3,
//          }
//          expected.each do |key, value|
//            expect(job.send(key)).to eq(value)
//          end
//        end
    @Test
    public void hasExpectedAttributes() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void canSetItsOwnPriority() throws IOException {
        Queue queue = client.queues("foo");
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
        Queue queue = client.queues("foo");
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

//        it 'exposes its ttl' do
//          client.config['heartbeat'] = 10
//          queue.put(Job, {}, jid: 'jid')
//          job = queue.pop
//          expect(9...10).to include(job.ttl)
//        end
    @Test
    public void exposesItsTtl() {
        Assert.fail("NIY"); // TODO
    }

//        it 'exposes its cancel method' do
//          queue.put('Foo', {}, jid: 'jid')
//          client.jobs['jid'].cancel
//          expect(client.jobs['jid']).to_not be
//        end
    @Test
    public void exposesItsCancelMethod() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void canTagItself() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.put("Foo", null, null);
        client.jobs(jid).tag("foo");
        Assert.assertEquals("foo", client.jobs(jid).tags().get(0));
    }

    @Test
    public void canUntagItself() throws IOException {
        Queue queue = client.queues("foo");
        Map<String, Object> opts = new HashMap<>();
        opts.put("jid", "jid");
        opts.put("tags", Arrays.asList("foo"));
        String jid = queue.put("Foo", null, opts);
        client.jobs(jid).untag("foo");
        Assert.assertEquals(0, client.jobs(jid).tags().size());
    }

    @Test
    public void exposesDataAccess() throws IOException {
        Queue queue = client.queues("foo");
        Map<String, Object> data = new HashMap<>();
        data.put("whiz",  "bang");
        String jid = queue.put("Foo", data, null);
        Assert.assertEquals("bang", client.jobs(jid).data("whiz"));
    }

    @Test
    public void exposesDataAssignment() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.put("Foo", null, null);
        Job job = client.jobs(jid);
        job.data("foo", "bar");
        Assert.assertEquals("bar", job.data("foo"));
    }

    @Test
    public void canMoveItself() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.put("Foo", null, null);
        client.jobs(jid).requeue("bar");
        Assert.assertEquals("bar", client.jobs(jid).queue().name());
        Assert.assertEquals("bar", client.jobs(jid).queueName());
    }

//        it 'fails when requeing a cancelled job' do
//          queue.put('Foo', {}, jid: 'the-jid')
//          job = client.jobs['the-jid']
//          client.jobs['the-jid'].cancel # cancel a different instance that represents the same job
//
//          expect {
//            job.requeue('bar')
//          }.to raise_error(/job the-jid does not exist/i)
//
//          expect(client.jobs['jid']).to be_nil
//        end
    @Test
    public void failsWhenRequeingACancelledJob() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void canCompleteItself() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.put("Foo", null, null);
        queue.pop().complete();
        Assert.assertEquals("complete", client.jobs(jid).state());
    }

    @Test
    public void canAdvanceItselfToAnotherQueue() throws IOException {
        Queue queue = client.queues("foo");
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
        Queue queue = client.queues("foo");
        String jid = queue.put("Foo", null, null);
        Assert.assertEquals(false, client.jobs(jid).tracked());
        client.jobs(jid).track();
        Assert.assertEquals(true, client.jobs(jid).tracked());
        client.jobs(jid).untrack();
        Assert.assertEquals(false, client.jobs(jid).tracked());
    }

    @Test
    public void canAddAndRemoveDependencies() throws IOException {
        Queue queue = client.queues("foo");
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
        Queue queue = client.queues("foo");
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
        Queue queue = client.queues("foo");
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
        Queue queue = client.queues("foo");
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
    public void returnsNullFromSpawnedFromWhenItIsNotARecurringJob() {
        Assert.fail("NIY"); // TODO
    }
}
