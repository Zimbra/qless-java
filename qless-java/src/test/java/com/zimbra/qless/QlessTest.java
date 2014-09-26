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
        QlessClient client = new QlessClient(null);
        String jid = client.generateJid();
        Assert.assertTrue(jid.matches("\\A[a-f0-9]{32}\\z"));
    }
    
    @Test
    public void workerNameIncludesHostname() throws IOException {
        QlessClient client = new QlessClient(null);
        String workerName = client.getWorkerName();
        String hostName = InetAddress.getLocalHost().getHostName();
        Assert.assertTrue(workerName.contains(hostName));
    }
    
    @Test
    public void workerNameIncludesPid() {
        QlessClient client = new QlessClient(null);
        String workerName = client.getWorkerName();
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        Assert.assertTrue(workerName.contains(pid));
    }
}