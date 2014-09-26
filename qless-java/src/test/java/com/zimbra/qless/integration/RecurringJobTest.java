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
        RecurringJob job = (RecurringJob)client.getJob(jid);
        Assert.assertEquals("jid", job.getJid());
        Assert.assertNotNull(job.getData());
        Assert.assertTrue(job.getData() instanceof Map);
        Assert.assertEquals("bang", ((Map<String,Object>)job.getData()).get("whiz"));
        Assert.assertEquals("[\"foo\"]", JSON.stringify(job.getTags()));
        Assert.assertEquals(0, job.count());
        Assert.assertEquals(0, job.backlog());
        Assert.assertEquals(3, job.retries());
        Assert.assertEquals(60, job.interval());
        Assert.assertEquals(0, job.getPriority());
        Assert.assertEquals("foo", job.getQueueName());
        Assert.assertEquals("Foo", job.getKlassName());
    }

    @Test
    public void canSetItsPriority() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        client.getJob(jid).priority(10);
        Assert.assertEquals(10, client.getJob(jid).getPriority());
    }

    @Test
    public void canSetItsRetries() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.getJob(jid);
        job.retries(5);
        Assert.assertEquals(5, job.retries());
    }

    @Test
    public void canSetItsInterval() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.getJob(jid);
        job.interval(10);
        Assert.assertEquals(10, job.interval());
    }

    @Test
    public void canSetItsData() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.getJob(jid);
        job.data("foo", "bar");
        Assert.assertEquals("bar", job.data("foo"));
    }

    @Test
    public void canSetItsKlass() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.getJob(jid);
        job.klass("Foo");
        Assert.assertEquals("Foo", job.getKlassName());
        Assert.assertEquals("Foo", client.getJob(jid).getKlassName());
    }

    @Test
    public void canSetItsQueue() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.getJob(jid);
        job.requeue("bar");
        Assert.assertEquals("bar", job.getQueueName());
        Assert.assertEquals("bar", client.getJob(jid).getQueueName());
    }

    @Test
    public void canSetItsBacklog() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        RecurringJob job = (RecurringJob)client.getJob(jid);
        job.backlog(1);
        Assert.assertEquals(1, job.backlog());
        Assert.assertEquals(1, ((RecurringJob)client.getJob(jid)).backlog());
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
        client.getJob(jid).cancel();
        Assert.assertEquals(null, client.getJob(jid));
    }

    @Test
    public void canSetItsTags() throws IOException {
        String jid = queue.recur("Foo", null, 60, null);
        client.getJob(jid).tag("foo");
        Assert.assertEquals("foo", client.getJob(jid).getTags().get(0));
        client.getJob(jid).untag("foo");
        Assert.assertEquals(0, client.getJob(jid).getTags().size());
        Assert.assertEquals("[]", JSON.stringify(client.getJob(jid).getTags()));
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
        Assert.assertEquals(null, ((RecurringJob)client.getJob(jid)).lastSpawnedJid());
        Assert.assertEquals(null, ((RecurringJob)client.getJob(jid)).lastSpawnedJob());
    }
}
