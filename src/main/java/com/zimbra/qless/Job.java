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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JacksonInject;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;


public class Job {
    @JacksonInject
    @JsonIgnore
    protected transient Client client;
    
    @JsonIgnore
    protected transient Queue queue;
    
    @JsonSerialize(as=HashMap.class, include=JsonSerialize.Inclusion.NON_NULL)
    protected Map<String,Object> data;

    @JsonProperty protected String jid;
    @JsonProperty protected int priority;
    @JsonProperty protected String queueName;
    @JsonProperty protected String state = "running";
    @JsonProperty protected boolean tracked;
    
//    @JsonSerialize(as=ArrayList.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using=JSON.TagsDeserializer.class)
    @JsonProperty protected List<String> tags;
    
    
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
            Object retval = client.call("complete", jid, client.workerName(), queueName, new ObjectMapper().writeValueAsString(data));
        } else {
            // TODO
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
        Object retval = client.call("priority", jid, Integer.toString(priority));
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
}
