package com.zimbra.qless.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zimbra.qless.Client;
import com.zimbra.qless.QueueCounts;

@Service
public class QueueServiceImpl implements QueueService {
    final Logger LOGGER = LoggerFactory.getLogger(QueueServiceImpl.class);
    @Autowired Client qlessClient;
    
    public List<QueueCounts> listQueues() throws IOException {
        List<QueueCounts> result = qlessClient.queues().counts();
        LOGGER.debug("listQueues returns {}", result);
        return result;
    }
    
    public List<String> listQueueNames() throws IOException {
        List<QueueCounts> queueCounts = qlessClient.queues().counts();
        List<String> list = new ArrayList<String>();
        for (QueueCounts c: queueCounts) {
        	list.add(c.name);
        }
        LOGGER.debug("listQueueNames returns {}", list);
        return list;
    }
}
