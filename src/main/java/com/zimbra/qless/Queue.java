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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;



public class Queue {
    protected Client client;
    protected String name;

    /** Constructor */
    Queue(String name, Client client) {
        this.name = name;
        this.client = client;
    }
    
    public Map<String,Object> counts() throws IOException {
        Object result = client.call("queues", name);
        JavaType javaType = new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        return JSON.parse(result.toString(), javaType);
    }
    
    public QueueJobs jobs() {
        return new QueueJobs(name, client);
    }
    
    public String name() {
        return name;
    }
    
    public Job pop() throws IOException {
        return pop(1).get(0);
    }
    
    public List<Job> pop(int count) throws IOException {
        Object result = client.call("pop", name, client.workerName(), Integer.toString(count));
        InjectableValues injectables = new InjectableValues.Std().addValue("client", client);
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Job.class);
        List<Job> jobs = JSON.parse(result.toString(), javaType, injectables);
        return jobs;
    }
    
    public String put(String klass, Object data, Map<String,Object> opts) throws IOException {
        return (String)client.call("put", workerName(), name,
                OptsHelper.get(opts, "jid", client.generateJid()),
                klass,
                data == null ? "{}": JSON.stringify(data),
                OptsHelper.get(opts, "delay", "0"),
                "priority", OptsHelper.get(opts, "priority", "0"),
                "tags", JSON.stringify(OptsHelper.getList(opts, "tags")),
                "retries", OptsHelper.get(opts, "retries", "5"),
                "depends", JSON.stringify(OptsHelper.getList(opts, "depends")));
    }
    
    /**
     * Make a recurring job in this queue
     * Options include:
     * => priority (int)
     * => tags (array of strings)
     * => retries (int)
     * => offset (int)
     */
    public String recur(String klass, Object data, int interval, Map<String,Object> opts) throws IOException {
        return (String)client.call("recur", name,
                OptsHelper.get(opts, "jid", client.generateJid()),
                klass,
                data == null ? "{}" : JSON.stringify(data),
                "interval", Integer.toString(interval), OptsHelper.get(opts, "offset", "0"),
                "priority", OptsHelper.get(opts, "priority", "0"),
                "tags", JSON.stringify(OptsHelper.getList(opts, "tags")),
                "retries", OptsHelper.get(opts, "retries", "5"),
                "backlog", OptsHelper.get(opts, "backlog", "0"));
    }
    
    String workerName() {
        return client.workerName();
    }
}
