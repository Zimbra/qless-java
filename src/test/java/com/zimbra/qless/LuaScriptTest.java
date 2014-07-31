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
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class LuaScriptTest {
    
    @Test
    public void testScriptResource() throws IOException {
        byte[] scriptData = new LuaScript(null).scriptContents();
        Assert.assertNotNull(scriptData);
        Assert.assertTrue(scriptData.length > 40000);
    }
    
    @Test
    public void testEval() throws IOException {
        JedisPool jedisPool = new JedisPool("localhost");
        Jedis jedis = jedisPool.getResource();
        try  {
            jedis.flushDB();
        } finally {
            jedisPool.returnResource(jedis);;
        }
        List<String> keys = Arrays.asList();
        List<String> args = Arrays.asList("get", "23", "34");
        Object result = new LuaScript(jedisPool).call(keys, args);
    }
}
