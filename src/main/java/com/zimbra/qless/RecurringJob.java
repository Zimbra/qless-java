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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JacksonInject;


public class RecurringJob extends Job {
    @JsonProperty
    protected int interval;

    
    @JsonCreator
    RecurringJob(@JacksonInject("client") Client client) {
        super(client);
    }
    
    public int interval() {
        return interval;
    }
    
    public void interval(int interval) {
        this.interval = interval;
    }
    
    public void priority(int priority) throws IOException {
        client.call("recur.update", jid, "priority", Integer.toString(priority));
        this.priority = priority;
    }
}
