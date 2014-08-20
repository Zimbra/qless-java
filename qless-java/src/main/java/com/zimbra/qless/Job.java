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
    protected List<String> dependents;

    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> dependencies;
    
    @JsonProperty
    protected Map<String,Object> failure;
    
    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> tags;

    @JsonProperty
    protected String jid;
    
    @JsonProperty(value="expires")
    protected long expiresAt;
    
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
    
    @JsonProperty(value="spawned_from_jid")
    protected String spawnedFromJid;
    
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
    
    public void cancel() throws IOException {
        client.call("cancel", jid);
    }
    
    public void complete() throws IOException {
        complete(null, null);
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
            client.call("complete", jid, client.workerName(), queueName, JSON.stringify(data),
                    "next", nextQueue,
                    "delay", OptsHelper.get(opts, "delay", "0"),
                    "depends", JSON.stringify(OptsHelper.getList(opts, "depends"))
                    );
        }
    }
    
    public Object data(String key) {
        return data.get(key);
    }
    
    public void data(String key, Object value) throws IOException {
        data.put(key, value);
    }
    
    public void depend(String... jids) throws IOException {
        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList(jid, "on"));
        for (String jid: jids) {
            args.add(jid);
        }
        String[] array = new String[args.size()];
        args.toArray(array);
        client.call("depends", array);
    }
    
    public void fail(String group, String message) throws IOException {
        client.call("fail", jid, client.workerName(), group, message, JSON.stringify(data));
    }
    
    public Object failure(String group) {
        return this.failure.get(group);
    }
    
    public Map<String,Object> getData() {
        return data;
    }
    
    public List<String> getDependencies() {
        return dependencies;
    }
    
    public List<String> getDependents() {
        return dependents;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public Map<String,Object> getFailure() {
        return failure;
    }
    
    public List<History> getHistory() {
        return history;
    }
    
    public String getJid() {
        return jid;
    }
    
    public String getKlassName() {
        return klassName;
    }
    
    public int getOriginalRetries() {
        return originalRetries;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public Queue getQueue() {
        if (this.queue == null) {
            this.queue = new Queue(queueName, client);
        }
        return this.queue;
    }
    
    public String getQueueName() {
        return queueName;
    }
    
    public int getRetriesLeft() {
        return retriesLeft;
    }
    
    public String getSpawnedFrom() {
        return spawnedFromJid;
    }
    
    public String getState() {
        return state;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public boolean getTracked() {
        return tracked;
    }
    
    public long getTtl() {
        return expiresAt - System.currentTimeMillis() / 1000;
    }
    
    public String getWorkerName() {
        return workerName;
    }
    
    public void heartbeat() throws IOException {
    	expiresAt = (Long)client.call("heartbeat", jid, workerName, JSON.stringify(data));
    }
    
    public void isComplete() throws IOException {
        complete(null);
    }
    
    public boolean isRecurring() {
    	return false;
    }
    
    public void log(String message) throws IOException {
        client.call("log",  jid, message);
    }
    
    public void log(String message, Map<String,Object> data) throws IOException {
        client.call("log",  jid, message, JSON.stringify(data));
    }
    
    public void priority(int priority) throws IOException {
        client.call("priority", jid, Integer.toString(priority));
        this.priority = priority;
    }
    
    public void requeue(String queue) throws IOException {
        requeue(queue, null);
    }
    
    public void requeue(String queue, Map<String,Object> opts) throws IOException {
        client.call("requeue", client.workerName(), queue, jid, klassName,
                JSON.stringify(data),
                OptsHelper.get(opts, "delay", "0"),
                "priority", OptsHelper.get(opts, "priority", Integer.toString(priority)),
                "tags", JSON.stringify(OptsHelper.getList(opts, "tags", tags)),
                "retries", OptsHelper.get(opts, "retries", "5"),
                "depends", JSON.stringify(OptsHelper.getList(opts, "depends", getDependencies())));
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
    
    public String toString() {
        return new StringBuilder()
            .append(getClass().getName())
            .append(' ').append(klassName)
            .append(" (") 
            .append(jid).append(" / ").append(getQueueName()).append(" / ").append(state)
            .append(')')
            .toString();
    }
    
    public void undepend(String... jids) throws IOException {
        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList(jid, "off"));
        for (String jid: jids) {
            args.add(jid);
        }
        String[] array = new String[args.size()];
        args.toArray(array);
        client.call("depends", array);
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
    
    
    @SuppressWarnings("serial")
    public static class History extends HashMap<String,Object> {
        public Integer when() {
            Object value = get("when");
            if (value != null) {
                return new Integer(value.toString());
            }
            return null;
        }
        
        public String queueName() {
            return (String)get("q");
        }
        
        public Object what() {
            return get("what");
        }
    }
}
