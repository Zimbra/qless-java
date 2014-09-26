package com.zimbra.qless;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.google.common.base.Objects;
import com.zimbra.qless.map.LuaStringArrayDeserializer;


public class WorkerJobs {
	
    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> jobs;
    
    @JsonProperty
    @JsonDeserialize(using=LuaStringArrayDeserializer.class)
    protected List<String> stalled;
    
    
    public List<String> getJobs() throws IOException {
    	return jobs;
    }
    
    public List<String> getStalled() throws IOException {
    	return stalled;
    }
    
    public String toString() {
        return Objects.toStringHelper(this)
                .add("jobs", jobs)
                .add("stalled", stalled)
                .toString();
    }
}
