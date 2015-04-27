package com.zimbra.qless;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;


public class QlessClient {
    final Logger LOGGER = LoggerFactory.getLogger(QlessClient.class);
    protected Pool<Jedis> jedisPool;
    protected LuaScript luaScript;
    protected QlessClientConfig config = new QlessClientConfig(this);
    protected ClientEvents events;
    protected ClientQueues queues = new ClientQueues(this);

    public QlessClient(Pool<Jedis> jedisPool) {
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
    
    public QlessClientConfig config() {
        return config;
    }
    
    public ClientEvents events() {
        return events;
    }
    
    String generateJid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    public Job getJob(String jid) throws IOException {
        Class<? extends Job> klass = Job.class;
        Object result = call("get", jid);
        if (result == null) {
            result = call("recur.get", jid);
            if (result == null) {
                return null;
            }
            klass = RecurringJob.class;
        }
        
        String json = result.toString();
        InjectableValues inject = new InjectableValues.Std().addValue("client", this);
        return JSON.parse(json, klass, inject);
    }
    
    public List<Job> getJobs(String... jids) throws IOException {
    	if (jids.length == 0) {
    		return new ArrayList<Job>();
    	}
        Object result = call("multiget", jids);
        if ("{}".equals(result.toString())) {
        	return new ArrayList<Job>();
        }
        InjectableValues inject = new InjectableValues.Std().addValue("client", this);
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Job.class);
        return JSON.parse(result.toString(), javaType, inject);
    }
    
    public List<String> getJobsComplete() throws IOException {
    	return getJobsComplete(0, 25);
    }
    
    public List<String> getJobsComplete(int offset, int count) throws IOException {
        Object result = call("jobs", "complete", Integer.toString(offset), Integer.toString(count));
        if ("{}".equals(result.toString())) {
        	return new ArrayList<String>();
        }
        return (ArrayList<String>)result;
    }
    
	public List<WorkerCounts> getWorkerCounts() throws IOException {
        Object result = call("workers");
        if ("{}".equals(result)) {
        	return new ArrayList<WorkerCounts>();
        }
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, WorkerCounts.class);
        return JSON.parse(result.toString(), javaType);
    }
	
    public WorkerJobs getWorkerJobs(String workerName) throws IOException {
        Object result = call("workers", workerName);
        return JSON.parse(result.toString(), WorkerJobs.class);
    }
    
    public String getWorkerName() {
        return hostname() + "-" + pid();
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
