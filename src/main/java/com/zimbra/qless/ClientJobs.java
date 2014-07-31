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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.ObjectMapper;


public class ClientJobs {
    protected Client client;
    protected ObjectMapper objectMapper = new ObjectMapper();

    /** Constructor */
    ClientJobs(Client client) {
        this.client = client;
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public Job get(String jid) throws IOException {
        Class<?> klass = Job.class;
        Object result = client.call("get", jid);
        if (result == null) {
            result = client.call("recur.get", jid);
            if (result == null) {
                return null;
            }
            klass = RecurringJob.class;
        }
        
        // 1st pass - instantiate a Job from returned keys
        InjectableValues inject = new InjectableValues.Std().addValue("client", client);
        Job job = objectMapper.reader(klass).withInjectableValues(inject).readValue(result.toString());
        
        // 2nd pass - instantiate data member
        JsonNode jsonNode = objectMapper.readTree(result.toString());
        String jsonData = jsonNode.path("data").asText();
        job.data = objectMapper.reader(HashMap.class).readValue(jsonData);
        return job;
    }
}
