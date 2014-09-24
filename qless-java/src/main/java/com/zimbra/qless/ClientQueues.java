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
        if ("{}".equals(result)) {
        	return new ArrayList<QueueCounts>();
        }
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, QueueCounts.class);
        return JSON.parse(result.toString(), javaType);
    }
    
    public Queue get(String name) {
        return new Queue(name, client);
    }
}
