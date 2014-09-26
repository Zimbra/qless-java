package com.zimbra.qless.integration;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zimbra.qless.QlessClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class ConfigTest {
    final Logger LOGGER = LoggerFactory.getLogger(ConfigTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    QlessClient client;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new QlessClient(jedisPool);
        jedis.flushDB();
    }
    
    @Test
    public void canSetGetAndEraseConfiguration() throws IOException {
        client.config().put("testing", "foo");
        Assert.assertEquals("foo", client.config().get("testing"));
        Assert.assertEquals("foo", client.config().all().get("testing"));
        client.config().clear("testing");
        Assert.assertEquals(null, client.config().get("testing"));
    }
    
    @Test
    public void canGetAllConfigurations() throws IOException {
        Map<String,Object> config = client.config().all();
        Assert.assertEquals(60     , config.get("heartbeat"));
        Assert.assertEquals("qless", config.get("application"));
        Assert.assertEquals(10     , config.get("grace-period"));
        Assert.assertEquals(604800 , config.get("jobs-history"));
        Assert.assertEquals(30     , config.get("stats-history"));
        Assert.assertEquals(7      , config.get("histogram-history"));
        Assert.assertEquals(50000  , config.get("jobs-history-count"));
    }
}
