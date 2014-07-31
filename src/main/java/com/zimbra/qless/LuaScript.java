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
import java.text.DecimalFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

import com.google.common.io.Resources;

public class LuaScript {
    final Logger LOGGER = LoggerFactory.getLogger(LuaScript.class);
    public static final String SCRIPT = "qless.lua";
    JedisPool jedisPool;
    protected byte[] scriptContents, sha1;

    public LuaScript(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public Object call(List<String> keys, List<String> args) throws IOException {
        Jedis jedis = jedisPool.getResource();
        try {
            Object result = jedis.evalsha(SafeEncoder.encode(sha1(jedis)), keys, args);
            return result;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }
    
    byte[] scriptContents() throws IOException {
        if (scriptContents == null) {
            scriptContents = Resources.toByteArray(Resources.getResource(SCRIPT));
        }
        return scriptContents;
    }
    
    synchronized byte[] sha1(Jedis jedis) throws IOException {
        if (sha1 == null) {
            byte[] script = scriptContents();
            sha1 = jedis.scriptLoad(script);
            LOGGER.info("{} ({} bytes) uploaded to redis, sha1={}", SCRIPT, new DecimalFormat("#,##0.#").format(script.length), SafeEncoder.encode(sha1));
        }
        return sha1;
    }
}
