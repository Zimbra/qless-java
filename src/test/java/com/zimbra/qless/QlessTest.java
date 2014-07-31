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
        String workerName = client.workerName();
        String hostName = InetAddress.getLocalHost().getHostName();
        Assert.assertTrue(workerName.contains(hostName));
    }
    
    @Test
    public void workerNameIncludesPid() {
        Client client = new Client(null);
        String workerName = client.workerName();
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        Assert.assertTrue(workerName.contains(pid));
    }
}