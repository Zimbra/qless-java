package com.zimbra.qless.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zimbra.qless.Client;
import com.zimbra.qless.QlessException;
import com.zimbra.qless.Queue;
import com.zimbra.qless.worker.SerialWorker;

public class WorkerTest {

    final Logger LOGGER = LoggerFactory.getLogger(WorkerTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    Queue queue;

    @Before
    public void before() throws IOException {
	Jedis jedis = jedisPool.getResource();
	client = new Client(jedisPool);
	jedis.flushDB();
	Foo.runningHistory.clear();
    }

    @After
    public void after() throws IOException {
	jedisPool.getResource().flushDB();
	Foo.runningHistory.clear();
    }

    @Test
    public void serialWorkerBasicTest() throws IOException,
	    InterruptedException, QlessException {
	Queue queue = client.queue("test");

	queue.put("com.zimbra.qless.integration.Foo", null, null);

	final SerialWorker worker = new SerialWorker(
		Arrays.asList(new String[] { "test" }), client, null, 10);

	Thread signal = new Thread() {
	    public void run() {
		try {
		    Thread.sleep(5000);
		    worker.shutDown();
		} catch (InterruptedException v) {
		    System.out.println(v);
		}
	    }
	};

	signal.start();
	worker.run();

	String expectedHistory = "com.zimbra.qless.integration.Foo.test";
	Assert.assertEquals(expectedHistory,
		String.join("", Foo.runningHistory));
    }

    @Test
    public void queueRotationTest() throws IOException, InterruptedException,
	    QlessException {
	Queue queueA = client.queue("testA");
	Queue queueB = client.queue("testB");
	Queue queueC = client.queue("testC");

	queueA.put("com.zimbra.qless.integration.Foo", null, null);
	queueA.put("com.zimbra.qless.integration.Foo", null, null);
	queueB.put("com.zimbra.qless.integration.Foo", null, null);
	queueC.put("com.zimbra.qless.integration.Foo", null, null);
	queueC.put("com.zimbra.qless.integration.Foo", null, null);

	final SerialWorker worker = new SerialWorker(
		Arrays.asList(new String[] { "testA", "testB", "testC" }),
		client, null, 10);

	Thread signal = new Thread() {
	    public void run() {
		try {
		    Thread.sleep(5000);
		    worker.shutDown();
		} catch (InterruptedException v) {
		    System.out.println(v);
		}
	    }
	};

	signal.start();
	worker.run();

	String expectedHistory = "com.zimbra.qless.integration.Foo.testA"
		+ "com.zimbra.qless.integration.Foo.testB"
		+ "com.zimbra.qless.integration.Foo.testC"
		+ "com.zimbra.qless.integration.Foo.testA"
		+ "com.zimbra.qless.integration.Foo.testC";
	Assert.assertEquals(expectedHistory,
		String.join("", Foo.runningHistory));
    }
}
