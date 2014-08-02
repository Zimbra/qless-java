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
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientConfig {
    final Logger LOGGER = LoggerFactory.getLogger(ClientConfig.class);
    Client client;

    public ClientConfig(Client client) {
        this.client = client;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String,Object> all() throws IOException {
        Object result = client.call("config.get");
        JavaType javaType = new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        return (Map<String,Object>)JSON.parse(result.toString(), javaType);
    }
    
    public String get(String key) throws IOException {
        return (String)client.call("config.get", key);
    }
    
    public void put(String key, String value) throws IOException {
        client.call("config.set", key, value);
    }
    
    public void clear(String key) throws IOException {
        client.call("config.unset", key);
    }
}
