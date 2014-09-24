package com.zimbra.qless.integration;

import java.io.IOException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zimbra.qless.LuaScript;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class LuaScriptTest {
    final Logger LOGGER = LoggerFactory.getLogger(LuaScriptTest.class);
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

    @Test
    public void doesNotMakeAnyRedisRequestsUponInitialization() {
        // creation phase
        JedisPool jedisPool = EasyMock.createMock(JedisPool.class);
        // recording phase
        EasyMock.replay(jedisPool);
        // test
        new LuaScript(jedisPool);
        EasyMock.verify(jedisPool);
    }
    
//	    it 'can issue commands without reloading the script' do
//	      # Create a LuaScript object, and ensure the script is loaded
//	      redis.script(:load, script.send(:script_contents))
//	      redis.should_not_receive(:script)
//	      expect {
//	        script.call([], ['config.set', 12345, 'key', 3])
//	      }.to change { redis.keys.size }.by(1)
//	    end
    @Test
    public void canIssueCommandsWithoutReloadingTheScript() throws IOException {
        Assert.fail("NIY"); // TODO
//        // setup precondition by talking to redis directly
//        LuaScript luaScript = new LuaScript(jedisPool);
//        jedis.scriptLoad(luaScript.scriptContents());
//        // creation phase
//        Jedis jedis = EasyMock.createMock(Jedis.class);
//        JedisPool jedisPool = EasyMock.createMock(JedisPool.class);
//        EasyMock.expect(jedisPool.getResource()).andReturn(jedis);
//        luaScript = new LuaScript(jedisPool);
//        // recording phase
//        jedisPool.getResource();
//        EasyMock.replay(jedis, jedisPool, luaScript);
//        // test
//        List<String> keys = Arrays.asList();
//        List<String> args = Arrays.asList("config.set", "12345", "key", "3");
//        luaScript.call(keys, args);
//        EasyMock.verify(jedis, jedisPool);
    }
    
//	    it 'loads the script as needed if the command fails' do
//	      # Ensure redis has no scripts loaded, and then invoke the command
//	      redis.script(:flush)
//	      redis.should_receive(:script).and_call_original
//	      expect {
//	        script.call([], ['config.set', 12345, 'key', 3])
//	      }.to change { redis.keys.size }.by(1)
//	    end
	
//	    it 're-raises non user_script errors' do
//	      FooError = Class.new(Redis::CommandError)
//	      script.stub(:_call) do
//	        raise FooError.new
//	      end
//	      expect {
//	        script.call([], ['foo'])
//	      }.to raise_error(FooError)
//	    end
    @Test
    public void raisesNonUserScriptErrors() throws IOException {
        Assert.fail("NIY"); // TODO
    }
}