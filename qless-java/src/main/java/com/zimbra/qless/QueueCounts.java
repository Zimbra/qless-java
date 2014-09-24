package com.zimbra.qless;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Objects;


public class QueueCounts {
    public final String name;
    public final boolean paused;
    public final int running;
    public final int waiting;
    public final int recurring;
    public final int depends;
    public final int stalled;
    public final int scheduled;
    
    @JsonCreator
    public QueueCounts(
        @JsonProperty("name")
        String name,
        
        @JsonProperty("paused")
        boolean paused,
        
        @JsonProperty("running")
        int running,
        
        @JsonProperty("waiting")
        int waiting,
        
        @JsonProperty("recurring")
        int recurring,
        
        @JsonProperty("depends")
        int depends,
        
        @JsonProperty("stalled")
        int stalled,
        
        @JsonProperty("scheduled")
        int scheduled)
    {
        this.name = name;
        this.paused = paused;
        this.running = running;
        this.waiting = waiting;
        this.recurring = recurring;
        this.depends = depends;
        this.stalled = stalled;
        this.scheduled = scheduled;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean getPaused() {
    	return paused;
    }
    
    public int getRunning() {
    	return running;
    }
    
    public int getWaiting() {
    	return waiting;
    }
    
    public int getRecurring() {
    	return recurring;
    }
    
    public int getDepends() {
    	return depends;
    }
    
    public int getStalled() {
    	return stalled;
    }
    
    public int getScheduled() {
    	return scheduled;
    }
    
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("paused", paused)
                .add("running", running)
                .add("waiting", waiting)
                .add("recurring", recurring)
                .add("depends", depends)
                .add("stalled", stalled)
                .add("scheduled", scheduled)
                .toString();
    }
}
