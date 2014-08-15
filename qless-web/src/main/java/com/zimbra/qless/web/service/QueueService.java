package com.zimbra.qless.web.service;


import java.io.IOException;
import java.util.List;

import com.zimbra.qless.QueueCounts;

public interface QueueService {
    
    public List<QueueCounts> listQueues() throws IOException;
}
