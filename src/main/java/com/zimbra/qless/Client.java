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
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class Client {
    final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    protected JedisPool jedisPool;
    protected LuaScript luaScript;
    protected ClientConfig config = new ClientConfig(this);
    protected ClientEvents events;
    protected ClientJobs jobs = new ClientJobs(this);
    protected ClientQueues queues = new ClientQueues(this);

    public Client(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.luaScript = new LuaScript(jedisPool);
        events = new ClientEvents(this, jedisPool);
    }
    
    Object call(String command, String... args) throws IOException {
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> argsList = new ArrayList<String>();
            argsList.add(command);
            argsList.add(now());
            for (String arg: args) {
                argsList.add(arg);
            }
            List<String> keysList = new ArrayList<String>();
            if (LOGGER.isDebugEnabled()) {LOGGER.debug("{}", argsList);}
            return luaScript.call(keysList, argsList);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }
    
    Object call(String command, List<String> args) throws IOException {
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> argsList = new ArrayList<String>();
            argsList.add(command);
            argsList.add(now());
            for (String arg: args) {
                argsList.add(arg);
            }
            List<String> keysList = new ArrayList<String>();
            if (LOGGER.isDebugEnabled()) {LOGGER.debug("{}", argsList);}
            return luaScript.call(keysList, argsList);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }
    
    public ClientConfig config() {
        return config;
    }
    
    public ClientEvents events() {
        return events;
    }
    
    String generateJid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    protected String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {}
        return "localhost";
    }
    
    public Job jobs(String jid) throws IOException {
        return jobs.get(jid);
    }
    
    protected String now() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
    
    protected String pid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }
    
    public Queue queue(String name) {
        return new Queue(name, this);
    }
    
    public ClientQueues queues() throws IOException {
        return queues;
    }
    
    public String workerName() {
        return hostname() + "-" + pid();
    }
}
