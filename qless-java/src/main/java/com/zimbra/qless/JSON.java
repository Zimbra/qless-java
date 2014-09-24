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
