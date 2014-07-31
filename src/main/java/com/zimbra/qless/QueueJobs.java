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
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;



public class QueueJobs {
    protected Client client;
    protected String queueName;
    protected ObjectMapper objectMapper = new ObjectMapper();

    /** Constructor */
    QueueJobs(String queueName, Client client) {
        this.queueName = queueName;
        this.client = client;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> jobs(String state, int start, int count) throws IOException {
        Object result = client.call("jobs", state, queueName, "" + start, "" + count);
        return (List<String>)result;
    }
    
    public List<String> depends() throws IOException {
        return depends(0);
    }
    
    public List<String> depends(int start) throws IOException {
        return depends(start, 25);
    }
    
    public List<String> depends(int start, int count) throws IOException {
        return jobs("depends", start, count);
    }
    
    public List<String> recurring() throws IOException {
        return recurring(0);
    }
    
    public List<String> recurring(int start) throws IOException {
        return recurring(start, 25);
    }
    
    public List<String> recurring(int start, int count) throws IOException {
        return jobs("recurring", start, count);
    }
    
    public List<String> running() throws IOException {
        return running(0);
    }
    
    public List<String> running(int start) throws IOException {
        return running(start, 25);
    }
    
    public List<String> running(int start, int count) throws IOException {
        return jobs("running", start, count);
    }
    
    public List<String> scheduled() throws IOException {
        return scheduled(0);
    }
    
    public List<String> scheduled(int start) throws IOException {
        return scheduled(start, 25);
    }
    
    public List<String> scheduled(int start, int count) throws IOException {
        return jobs("scheduled", start, count);
    }
    
    public List<String> stalled() throws IOException {
        return stalled(0);
    }
    
    public List<String> stalled(int start) throws IOException {
        return stalled(start, 25);
    }
    
    public List<String> stalled(int start, int count) throws IOException {
        return jobs("stalled", start, count);
    }
}
