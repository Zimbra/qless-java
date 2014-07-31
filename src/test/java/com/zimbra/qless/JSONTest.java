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

import java.io.IOException;
import java.nio.charset.Charset;

import org.codehaus.jackson.map.InjectableValues;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;
import com.zimbra.qless.Client;
import com.zimbra.qless.JSON;
import com.zimbra.qless.Job;


public class JSONTest {
    
    @Test
    public void testParseGetResponse1() throws IOException {
        String json = Resources.toString(Resources.getResource(JSONTest.class, "getResponse1.json"), Charset.defaultCharset());
        InjectableValues injectables = new InjectableValues.Std().addValue("client", EasyMock.createNiceMock(Client.class));
        Job job = JSON.parse(json, Job.class, injectables);
        
        Assert.assertEquals("77687e485b0442c6b043c52620bac347", job.jid);
        Assert.assertEquals(5, job.originalRetries);
        Assert.assertNotNull(job.data); 
        Assert.assertEquals(true, job.data.isEmpty());
        //TODO:failure
        Assert.assertEquals(0, job.expiresAt);
        Assert.assertEquals(5, job.retriesLeft);
        //TODO:spawned_from_jid
        Assert.assertNotNull(job.dependencies); 
        Assert.assertEquals(0, job.dependencies.size()); 
        Assert.assertEquals("foo", job.klassName);
        Assert.assertEquals(false, job.tracked);
        Assert.assertNotNull(job.tags); 
        Assert.assertEquals(0, job.tags.size()); 
        Assert.assertEquals("Foo", job.queueName);
        Assert.assertEquals("waiting", job.state);
        Assert.assertNotNull(job.history); 
        Assert.assertEquals(1, job.history.size()); 
        Assert.assertEquals(1406824938, job.history.get(0).when);
        Assert.assertEquals("Foo", job.history.get(0).queueName);
        Assert.assertEquals("put", job.history.get(0).what);
        Assert.assertNotNull(job.dependents); 
        Assert.assertEquals(0, job.dependents.size()); 
        Assert.assertEquals(0, job.priority);
        Assert.assertEquals("", job.workerName);
    }
}
