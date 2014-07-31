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

import org.codehaus.jackson.map.InjectableValues;


public class ClientJobs {
    protected Client client;

    /** Constructor */
    ClientJobs(Client client) {
        this.client = client;
    }
    
    public Job get(String jid) throws IOException {
        Class<? extends Job> klass = Job.class;
        Object result = client.call("get", jid);
        if (result == null) {
            result = client.call("recur.get", jid);
            if (result == null) {
                return null;
            }
            klass = RecurringJob.class;
        }
        
        String json = result.toString();
        InjectableValues inject = new InjectableValues.Std().addValue("client", client);
        return JSON.parse(json, klass, inject);
    }
}
