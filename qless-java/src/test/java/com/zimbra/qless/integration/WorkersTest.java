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

import com.zimbra.qless.Client;
import com.zimbra.qless.Queue;
import com.zimbra.qless.WorkerCounts;


public class WorkersTest {
    final Logger LOGGER = LoggerFactory.getLogger(WorkersTest.class);
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
    
//    it 'provides access to worker stats' do
//        # Put the job, there should be no workers
//        queue.put('Foo', {}, jid: 'jid')
//        expect(client.workers.counts).to eq({})
//
//        # Pop a job and we have some information
//        queue.pop
//        expect(client.workers.counts).to eq([{
//          'name'    => queue.worker_name,
//          'jobs'    => 1,
//          'stalled' => 0
//        }])
//        expect(client.workers[queue.worker_name]).to eq({
//          'jobs'    => ['jid'],
//          'stalled' => {}
//        })
//      end
    @Test
    public void providesAccessToWorkerListStats() throws IOException {
    	// Put the job, there should be no workers
        queue.put("Foo", null, null);
        Assert.assertEquals(0, client.getWorkers().getCounts().size());
        // Pop a job and we have some information
        queue.pop();
        List<WorkerCounts> workerCounts = client.getWorkers().getCounts();
        Assert.assertEquals(1, workerCounts.size());
        Assert.assertEquals(queue.getWorkerName(), workerCounts.get(0).getName());
        Assert.assertEquals(1, workerCounts.get(0).getJobs());
        Assert.assertEquals(0, workerCounts.get(0).getStalled());
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
