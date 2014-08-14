/*
 * ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2014 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

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
