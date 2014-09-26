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
    protected ClientWorkers workers = new ClientWorkers(this);

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
    
    public Job getJob(String jid) throws IOException {
        return jobs.get(jid);
    }
    
    public String getWorkerName() {
        return hostname() + "-" + pid();
    }
    
    public ClientWorkers getWorkers() throws IOException {
        return workers;
    }
    
    protected String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {}
        return "localhost";
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
}
