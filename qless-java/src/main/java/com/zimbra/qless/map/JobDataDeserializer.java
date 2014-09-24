package com.zimbra.qless.map;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;


public class JobDataDeserializer extends JsonDeserializer<Map<String,Object>> {
    
    @Override
    public Map<String,Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        if ("{}".equals(node.asText())) {
            return new HashMap<String,Object>();
        }
        JavaType type = new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        return new ObjectMapper().reader(type).readValue(node.asText());
    }
}
