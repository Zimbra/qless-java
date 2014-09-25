package com.zimbra.qless;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;



public class ClientWorkers {
    protected Client client;

    /** Constructor */
    ClientWorkers(Client client) {
        this.client = client;
    }
    
	public List<WorkerCounts> getCounts() throws IOException {
        Object result = client.call("workers");
        if ("{}".equals(result)) {
        	return new ArrayList<WorkerCounts>();
        }
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, WorkerCounts.class);
        return JSON.parse(result.toString(), javaType);
    }
    
//    public Queue get(String name) {
//        return new Queue(name, client);
//    }
}
