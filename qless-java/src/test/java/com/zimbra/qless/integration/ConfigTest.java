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
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zimbra.qless.Client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class ConfigTest {
    final Logger LOGGER = LoggerFactory.getLogger(ConfigTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new Client(jedisPool);
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
