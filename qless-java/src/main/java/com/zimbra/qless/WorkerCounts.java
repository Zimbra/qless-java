package com.zimbra.qless;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Objects;


public class WorkerCounts {
    public final String name;
    public final int jobs;
    public final int stalled;
    
    @JsonCreator
    public WorkerCounts(
        @JsonProperty("name")
        String name,
        
        @JsonProperty("jobs")
        int jobs,
        
        @JsonProperty("stalled")
        int stalled)
    {
        this.name = name;
        this.jobs = jobs;
        this.stalled = stalled;
    }
    
    public int getJobs() {
    	return jobs;
    }
    
    public String getName() {
        return name;
    }
    
    public int getStalled() {
    	return stalled;
    }
    
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("jobs", jobs)
                .add("stalled", stalled)
                .toString();
    }
}
