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


public class RecurringJobIntegrationTest {
    final Logger LOGGER = LoggerFactory.getLogger(RecurringJobIntegrationTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new Client(jedisPool);
        jedis.flushDB();
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

//        it 'has all the expected attributes' do
//          queue.recur('Foo', { whiz: 'bang' }, 60, jid: 'jid', tags: ['foo'],
//                      retries: 3)
//          job = client.jobs['jid']
//          expected = {
//            :jid        => 'jid',
//            :data       => {'whiz' => 'bang'},
//            :tags       => ['foo'],
//            :count      => 0,
//            :backlog    => 0,
//            :retries    => 3,
//            :interval   => 60,
//            :priority   => 0,
//            :queue_name => 'foo',
//            :klass_name => 'Foo'
//          }
//          expected.each do |key, value|
//            expect(job.send(key)).to eq(value)
//          end
//        end
    @Test
    public void hasAllTheExpectedAttributes() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void canSetItsPriority() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.recur("Foo", null, 60, null);
        client.jobs(jid).priority(10);
        Assert.assertEquals(10, client.jobs(jid).priority());
    }

    @Test
    public void canSetItsRetries() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.retries(5);
        Assert.assertEquals(5, job.retries());
    }

    @Test
    public void canSetItsInterval() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.jobs(jid);
        job.interval(10);
        Assert.assertEquals(10, job.interval());
    }

//        it 'can set its data' do
//          queue.recur('Foo', {}, 60, jid: 'jid')
//          client.jobs['jid'].data = { 'foo' => 'bar' }
//          expect(client.jobs['jid'].data).to eq({ 'foo' => 'bar' })
//        end
    @Test
    public void canSetItsData() {
        Assert.fail("NIY"); // TODO
    }

//        it 'can set its klass' do
//          queue.recur('Foo', {}, 60, jid: 'jid')
//          client.jobs['jid'].klass = Job
//          expect(client.jobs['jid'].klass).to eq(Job)
//        end
    @Test
    public void canSetItsKlass() {
        Assert.fail("NIY"); // TODO
    }

    @Test
    public void canSetItsQueue() throws IOException {
        Queue queue = client.queues("foo");
        String jid = queue.recur("Foo", null, 60, null);
        client.jobs(jid).requeue("bar");
        Assert.assertEquals("bar", client.jobs(jid).queueName());
    }

//        it 'can set its backlog' do
//          queue.recur('Foo', {}, 60, jid: 'jid', backlog: 10)
//          client.jobs['jid'].backlog = 1
//          expect(client.jobs['jid'].backlog).to eq(1)
//        end
    @Test
    public void canSetItsBacklog() {
        Assert.fail("NIY"); // TODO
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

//        it 'can cancel itself' do
//          queue.recur('Foo', {}, 60, jid: 'jid')
//          client.jobs['jid'].cancel
//          expect(client.jobs['jid']).to_not be
//        end
    @Test
    public void canCancelItself() {
        Assert.fail("NIY"); // TODO
    }

//        it 'can set its tags' do
//          queue.recur('Foo', {}, 60, jid: 'jid')
//          client.jobs['jid'].tag('foo')
//          expect(client.jobs['jid'].tags).to eq(['foo'])
//          client.jobs['jid'].untag('foo')
//          expect(client.jobs['jid'].tags).to eq([])
//        end
    @Test
    public void canSetItsTags() {
        Assert.fail("NIY"); // TODO
    }

//        describe 'last spawned job access' do
//          it 'exposes the jid and job of the last spawned job' do
//            queue.recur('Foo', {}, 60, jid: 'jid')
    @Test
    public void exposesJidAndJobOfLastSpawnedJob() {
        Assert.fail("NIY"); // TODO
    }

//            Timecop.travel(Time.now + 121) do # give it enough time to spawn 2 jobs
//              last_spawned = queue.peek(2).max_by(&:initially_put_at)
//
//              job = client.jobs['jid']
//              expect(job.last_spawned_jid).to eq(last_spawned.jid)
//              expect(job.last_spawned_job).to eq(last_spawned)
//            end
//          end
//
//          it 'returns nil if no job has ever been spawned' do
//            queue.recur('Foo', {}, 60, jid: 'jid')
//            job = client.jobs['jid']
//
//            expect(job.last_spawned_jid).to be_nil
//            expect(job.last_spawned_job).to be_nil
//          end
    @Test
    public void lastSpawnedJobReturnsNilIfNoJobHasEverBeenSpawned() {
        Assert.fail("NIY"); // TODO
    }
}
