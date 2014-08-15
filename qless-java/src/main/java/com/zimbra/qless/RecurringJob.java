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
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JacksonInject;


public class RecurringJob extends Job {
    @JsonProperty
    protected int backlog;

    @JsonProperty
    protected int count;

    @JsonProperty
    protected int interval;

    @JsonProperty
    protected int retries;

    
    @JsonCreator
    RecurringJob(@JacksonInject("client") Client client) {
        super(client);
    }
    
    public int backlog() {
        return backlog;
    }
    
    public void backlog(int backlog) throws IOException {
        client.call("recur.update", jid, "backlog", Integer.toString(backlog));
        this.backlog = backlog;
    }
    
    public void cancel() throws IOException {
        client.call("unrecur", jid);
    }
    
    public int count() {
        return count;
    }
    
    public void data(Map<String,Object> data) throws IOException {
        client.call("recur.update", jid, "data", JSON.stringify(data));
        this.data = data;
    }
    
    public void data(String key, Object value) throws IOException {
        data.put(key,  value);
        client.call("recur.update", jid, "data", JSON.stringify(data));
    }
    
    public int interval() {
        return interval;
    }
    
    public void interval(int interval) {
        this.interval = interval;
    }
    
    public void klass(String klass) throws IOException {
        client.call("recur.update", jid, "klass", klass);
        this.klassName = klass;
    }
    
    public String lastSpawnedJid() {
        if (neverSpawned()) {
            return null;
        }
        return jid + "-" + count;
    }
    
    public Job lastSpawnedJob() throws IOException {
        if (neverSpawned()) {
            return null;
        }
        return client.jobs(lastSpawnedJid());
    }
    
    public void move(String queue) throws IOException {
        client.call("recur.update", jid, "queue", queue);
        this.queueName = queue;
        this.queue = null;
    }
    
    public boolean neverSpawned() {
        return count == 0;
    }
    
    public void priority(int priority) throws IOException {
        client.call("recur.update", jid, "priority", Integer.toString(priority));
        this.priority = priority;
    }
    
    public void requeue(String queue) throws IOException {
        move(queue);
    }
    
    public int retries() {
        return retries;
    }
    
    public void retries(int retries) throws IOException {
        client.call("recur.update", jid, "retries", Integer.toString(retries));
        this.retries = retries;
    }
    
    public void tag(String... tags) throws IOException {
        List<String> args = new ArrayList<String>();
        args.add(jid);
        for (String tag: tags) {
            args.add(tag);
        }
        client.call("recur.tag", args);
    }
    
    public void untag(String... tags) throws IOException {
        List<String> args = new ArrayList<String>();
        args.add(jid);
        for (String tag: tags) {
            args.add(tag);
        }
        client.call("recur.untag", args);
    }
}