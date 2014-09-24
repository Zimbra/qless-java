package com.zimbra.qless;

import java.io.IOException;

import org.codehaus.jackson.map.InjectableValues;


public class ClientJobs {
    protected Client client;

    /** Constructor */
    ClientJobs(Client client) {
        this.client = client;
    }
    
    public Job get(String jid) throws IOException {
        Class<? extends Job> klass = Job.class;
        Object result = client.call("get", jid);
        if (result == null) {
            result = client.call("recur.get", jid);
            if (result == null) {
                return null;
            }
            klass = RecurringJob.class;
        }
        
        String json = result.toString();
        InjectableValues inject = new InjectableValues.Std().addValue("client", client);
        return JSON.parse(json, klass, inject);
    }
}
