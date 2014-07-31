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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;



public class Queue {
    protected Client client;
    protected String name;
    protected ObjectMapper objectMapper = new ObjectMapper();

    /** Constructor */
    Queue(String name, Client client) {
        this.name = name;
        this.client = client;
    }
    
    public Map<String,Object> counts() throws IOException {
        Object result = client.call("queues", name);
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>(){};
        return objectMapper.readValue(result.toString(), typeRef);
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
        return null; // TODO
    }
    
    public String put(String klass, Object data, Map<String,Object> opts) throws IOException {
        return (String)client.call("put", workerName(), name,
                OptsHelper.get(opts, "jid", client.generateJid()),
                klass,
                data == null ? "{}": objectMapper.writeValueAsString(data),
                OptsHelper.get(opts, "delay", "0"),
                OptsHelper.get(opts, "priority", "0"),
//                "tags", "{}", // TODO
                OptsHelper.get(opts, "retries", "5"));
//                "depends", "[]"); // TODO
    }
    

    /**
     * Make a recurring job in this queue
     * Options include:
     * => priority (int)
     * => tags (array of strings)
     * => retries (int)
     * => offset (int)
     */
//    def recur(klass, data, interval, opts = {})
//      opts = job_options(klass, data, opts)
//      @client.call(
//        'recur',
//        @name,
//        (opts[:jid] || Qless.generate_jid),
//        klass.is_a?(String) ? klass : klass.name,
//        JSON.generate(data),
//        'interval', interval, opts.fetch(:offset, 0),
//        'priority', opts.fetch(:priority, 0),
//        'tags', JSON.generate(opts.fetch(:tags, [])),
//        'retries', opts.fetch(:retries, 5),
//        'backlog', opts.fetch(:backlog, 0)
//      )
//    end
    public String recur(String klass, Object data, int interval, Map<String,Object> opts) throws IOException {
        return (String)client.call("recur", name,
                OptsHelper.get(opts, "jid", client.generateJid()),
                klass,
                objectMapper.writeValueAsString(data),
                "interval", "" + interval, OptsHelper.get(opts, "offset", "0"),
                OptsHelper.get(opts, "priority", "0"),
                "tags", "[]", // TODO
                OptsHelper.get(opts, "retries", "5"),
                OptsHelper.get(opts, "backlog", "0"));
    }
    
    String workerName() {
        return client.workerName();
    }
}