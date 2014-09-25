package com.zimbra.qless;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QlessTest {
    final Logger LOGGER = LoggerFactory.getLogger(QlessTest.class);

    @Test
    public void generatesUuidSuitableForUseAsAJid() {
        Client client = new Client(null);
        String jid = client.generateJid();
        Assert.assertTrue(jid.matches("\\A[a-f0-9]{32}\\z"));
    }
    
    @Test
    public void workerNameIncludesHostname() throws IOException {
        Client client = new Client(null);
        String workerName = client.getWorkerName();
        String hostName = InetAddress.getLocalHost().getHostName();
        Assert.assertTrue(workerName.contains(hostName));
    }
    
    @Test
    public void workerNameIncludesPid() {
        Client client = new Client(null);
        String workerName = client.getWorkerName();
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        Assert.assertTrue(workerName.contains(pid));
    }
}