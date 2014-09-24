package com.zimbra.qless;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;


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
        Assert.assertNotNull(job.failure); 
        Assert.assertEquals(true, job.failure.isEmpty()); 
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
        Assert.assertNotNull(job.history.get(0).when()); 
        Assert.assertEquals(1406824938L, job.history.get(0).when().longValue());
        Assert.assertEquals("Foo", job.history.get(0).queueName());
        Assert.assertEquals("put", job.history.get(0).what());
        Assert.assertNotNull(job.dependents); 
        Assert.assertEquals(0, job.dependents.size()); 
        Assert.assertEquals(0, job.priority);
        Assert.assertEquals("", job.workerName);
    }
    
    @Test
    public void testParsePopResponse1() throws IOException {
        String json = Resources.toString(Resources.getResource(JSONTest.class, "popResponse1.json"), Charset.defaultCharset());
        InjectableValues injectables = new InjectableValues.Std().addValue("client", EasyMock.createNiceMock(Client.class));
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Job.class);
        List<Job> jobs = JSON.parse(json, javaType, injectables);
        Assert.assertNotNull(jobs);
        Assert.assertEquals(1, jobs.size());
        Assert.assertEquals("58ee8f40ce4e4e65b66332b915cddaaa", jobs.get(0).jid);
        Assert.assertEquals(1406831012, jobs.get(0).expiresAt);
    }
}
