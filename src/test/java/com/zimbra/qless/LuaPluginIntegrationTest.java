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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class LuaPluginIntegrationTest {
    final Logger LOGGER = LoggerFactory.getLogger(LuaPluginIntegrationTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Jedis jedis;
    
    @Before
    public void before() throws IOException {
        jedis = jedisPool.getResource();
        jedis.flushDB();
    }

    @After
    public void after() throws IOException {
        jedisPool.returnResource(jedis);
        jedis = null;
    }

//	  describe LuaPlugin, :integration do
//	    let(:script) do
//	      "-- some comments\n return Qless.config.get(ARGV[1]) * ARGV[2]"
//	    end
//	    let(:plugin) { LuaPlugin.new("my_plugin", redis, script) }
//	
//	    it 'supports Qless lua plugins' do
//	      client.config['heartbeat'] = 14
//	      expect(plugin.call('heartbeat', 3)).to eq(14 * 3)
//	    end
    @Test
    public void supportsQlessLuaPlugins() throws IOException {
        Assert.fail("NIY"); // TODO
    }
    
//	
//	    RSpec::Matchers.define :string_excluding do |snippet|
//	      match do |actual|
//	        !actual.include?(snippet)
//	      end
//	    end
//	
//	    it 'strips out comment lines before sending the script to redis' do
//	      redis.should_receive(:script)
//	           .with(:load, string_excluding("some comments"))
//	           .at_least(:once)
//	           .and_call_original
//	
//	      client.config["heartbeat"] = 16
//	      expect(plugin.call "heartbeat", 3).to eq(16 * 3)
//	    end
    @Test
    public void stripsOutCommentLinesBeforeSendingScripToRedis() throws IOException {
        Assert.fail("NIY"); // TODO
    }
	
//	    it 'does not load the script extra times' do
//	      redis.should_receive(:script)
//	           .with(:load, an_instance_of(String))
//	           .once
//	           .and_call_original
//	
//	      3.times do
//	        plugin = LuaPlugin.new('my_plugin', redis, script)
//	        plugin.call('heartbeat', 3)
//	      end
//	    end
    @Test
    public void doesNotLoadTheScriptExtraTimes() throws IOException {
        Assert.fail("NIY"); // TODO
    }
}