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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;



public class ClientQueues {
    protected Client client;

    /** Constructor */
    ClientQueues(Client client) {
        this.client = client;
    }
    
    public List<QueueCounts> counts() throws IOException {
        Object result = client.call("queues");
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, QueueCounts.class);
        return JSON.parse(result.toString(), javaType);
    }
    
    public Queue get(String name) {
        return new Queue(name, client);
    }
}
