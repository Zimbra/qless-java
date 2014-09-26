package com.zimbra.qless;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QlessClientConfig {
    final Logger LOGGER = LoggerFactory.getLogger(QlessClientConfig.class);
    QlessClient client;

    public QlessClientConfig(QlessClient client) {
        this.client = client;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String,Object> all() throws IOException {
        Object result = client.call("config.get");
        JavaType javaType = new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        return (Map<String,Object>)JSON.parse(result.toString(), javaType);
    }
    
    public Object get(String key) throws IOException {
        return client.call("config.get", key);
    }
    
    public void put(String key, Object value) throws IOException {
        client.call("config.set", key, value.toString());
    }
    
    public void clear(String key) throws IOException {
        client.call("config.unset", key);
    }
}
