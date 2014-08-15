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

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;




public class JSON {
    
    public static <T> T parse(String json, Class<T> klass) throws IOException {
        return parse(json, klass, new InjectableValues.Std());
    }
    
    public static <T> T parse(String json, JavaType javaType) throws IOException {
        return parse(json, javaType, new InjectableValues.Std());
    }
    
    public static <T> T parse(String json, Class<T> klass, InjectableValues injectables) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.reader(klass).withInjectableValues(injectables).readValue(json);
    }
    
    public static <T> T parse(String json, JavaType javaType, InjectableValues injectables) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.reader(javaType).withInjectableValues(injectables).readValue(json);
    }
    
    public static String stringify(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);        
    }
}
