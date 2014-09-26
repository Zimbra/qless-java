package com.zimbra.qless.integration;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zimbra.qless.QlessClient;
import com.zimbra.qless.Queue;
import com.zimbra.qless.WorkerCounts;
import com.zimbra.qless.WorkerJobs;


public class WorkersTest {
    final Logger LOGGER = LoggerFactory.getLogger(WorkersTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    QlessClient client;
    Queue queue;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.flushDB();
        } finally {
            jedisPool.returnResource(jedis);
        }
        client = new QlessClient(jedisPool);
        queue = client.queue("foo");
    }
    
    @Test
    public void providesAccessToWorkerListStats() throws IOException {
    	// Put the job, there should be no workers
        queue.put("Foo", null, null);
        Assert.assertEquals(0, client.getWorkerCounts().size());
        // Pop a job and we have some information
        queue.pop();
        List<WorkerCounts> workerCounts = client.getWorkerCounts();
        Assert.assertEquals(1, workerCounts.size());
        Assert.assertEquals(queue.getWorkerName(), workerCounts.get(0).getName());
        Assert.assertEquals(1, workerCounts.get(0).getJobs());
        Assert.assertEquals(0, workerCounts.get(0).getStalled());
    }

    @Test
    public void providesAccessToWorkerJobs() throws IOException {
    	// Put the job, there should be no workers
        String jid = queue.put("Foo", null, null);
        Assert.assertEquals(0, client.getWorkerCounts().size());
        // Pop a job and we have some information
        queue.pop();
        WorkerJobs workerJobs = client.getWorkerJobs(queue.getWorkerName());
        Assert.assertEquals(1, workerJobs.getJobs().size());
        Assert.assertEquals(jid, workerJobs.getJobs().get(0));
        Assert.assertEquals(0, workerJobs.getStalled().size());
    }
    
//      it 'can deregister workers' do
//        # Ensure there's a worker listed
//        queue.put('Foo', {}, jid: 'jid')
//        queue.pop
//
//        # Deregister and make sure it goes away
//        client.deregister_workers(queue.worker_name)
//        expect(client.workers.counts).to eq({})
//      end
//    end
    @Test
    public void canDeregisterWorkers() throws IOException {
    	throw new UnsupportedOperationException("NIY"); // TODO
    }    
}
