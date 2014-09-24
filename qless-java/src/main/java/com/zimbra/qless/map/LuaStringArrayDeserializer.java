package com.zimbra.qless.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;


/**
 * Redis returns a proper JSON array when there's at least one element, but {} when there's none.
 * Jackson doesn't have a built-in feature for that type of curious behavior. 
 */
public class LuaStringArrayDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        if ("{}".equals(node.toString())) {
            return new ArrayList<String>();
        }
        JavaType type = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, String.class);
        return new ObjectMapper().reader(type).readValue(node.toString());
    }
}
