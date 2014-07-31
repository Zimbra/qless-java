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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JacksonInject;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.zimbra.qless.map.JobDataDeserializer;
import com.zimbra.qless.map.LuaStringArrayDeserializer;


public class Job {
    @JsonIgnore @JacksonInject
    protected transient Client client;
    
    @JsonIgnore
    protected transient Queue queue;
    
    @JsonProperty
    @JsonDeserialize(using=JobDataDeserializer.class)
    protected Map<String,Object> data;
    
    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> tags;

    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> dependents;

    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> dependencies;

    @JsonProperty
    protected String jid;
    
    @JsonProperty(value="expires")
    protected int expiresAt;
    
    @JsonProperty
    protected List<History> history;

    @JsonProperty(value="klass")
    protected String klassName;
    
    @JsonProperty
    protected int priority;
    
    @JsonProperty(value="queue")
    protected String queueName;
    
    @JsonProperty(value="retries")
    protected int originalRetries;
    
    @JsonProperty(value="remaining")
    protected int retriesLeft;
    
    @JsonProperty
    protected String state = "running";
    
    @JsonProperty
    protected boolean tracked;
    
    @JsonProperty(value="worker")
    protected String workerName;
    
    
    @JsonCreator
    Job(@JacksonInject("client") Client client) {
        this.client = client;
    }
    
    public void complete() throws IOException {
        complete(null);
    }
    
    public void complete(String nextQueue) throws IOException {
        complete(nextQueue, null);
    }
    
    public void complete(String nextQueue, Map<String,Object> opts) throws IOException {
        // TODO note_state_change :complete
        if (nextQueue == null) {
            @SuppressWarnings("unused")
            Object retval = client.call("complete", jid, client.workerName(), queueName, JSON.stringify(data));
        } else {
            @SuppressWarnings("unused")
            
            Object retval = client.call("complete", jid, client.workerName(), queueName, JSON.stringify(data),
                    "next", nextQueue,
                    "delay", OptsHelper.get(opts, "delay", "0"),
                    "depends", JSON.stringify(OptsHelper.getList(opts, "depends"))
                    );
        }
    }
    
    public Map<String,Object> data() {
        return data;
    }
    
    public Object data(String key) {
        return data.get(key);
    }
    
    public Object data(String key, Object value) {
        return data.put(key, value);
    }
    
    public String jid() {
        return jid;
    }
    
    public int priority() {
        return priority;
    }
    
    public void priority(int priority) throws IOException {
        client.call("priority", jid, Integer.toString(priority));
        this.priority = priority;
    }
    
    public Queue queue() {
        if (this.queue == null) {
            this.queue = new Queue(queueName, client);
        }
        return this.queue;
    }
    
    public String queueName() {
        return queueName;
    }
    
//    public String requeue(String queue, Map<String,Object> opts) throws IOException {
//        // TODO note_state_change :requeue
//        return (String)qlessClient.call("requeue", qlessClient.workerName(), queue, jid, klassName,
//                new ObjectMapper().writeValueAsString(data),
//                "delay", OptsHelper.get(opts, "delay", "0"),
//                "priority", OptsHelper.get(opts, "priority", Integer.toString(priority)),
////                "tags", "{}", // TODO
//                "retries", OptsHelper.get(opts, "retries", "5"));
////                "depends", "[]"); // TODO
//    }
    
    public String state() {
        return state;
    }
    
    public void tag(String... tags) throws IOException {
        List<String> args = new ArrayList<String>();
        for (String arg: Arrays.asList("add", jid)) {
            args.add(arg);
        }
        for (String tag: tags) {
            args.add(tag);
        }
        client.call("tag", args);
    }
    
    public void track() throws IOException {
        client.call("track", "track", jid);
        tracked = true;
    }
    
    public boolean tracked() {
        return tracked;
    }
    
    public void untag(String... tags) throws IOException {
        List<String> args = new ArrayList<String>();
        for (String arg: Arrays.asList("remove", jid)) {
            args.add(arg);
        }
        for (String tag: tags) {
            args.add(tag);
        }
        client.call("tag", args);
    }
    
    public void untrack() throws IOException {
        client.call("track", "untrack", jid);
        tracked = false;
    }
    
    public List<String> tags() {
        return tags;
    }
    
    public String toString() {
        return new StringBuilder()
            .append(getClass().getName())
            .append(' ').append(klassName)
            .append(" (") 
            .append(jid).append(" / ").append(queueName()).append(" / ").append(state)
            .append(')')
            .toString();
    }
    
    
    public static class History {
        @JsonProperty
        public int when;
        
        @JsonProperty(value="q")
        public String queueName;
        
        @JsonProperty
        public String what;
    }
}
