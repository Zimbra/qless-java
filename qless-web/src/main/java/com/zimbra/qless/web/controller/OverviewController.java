package com.zimbra.qless.web.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zimbra.qless.web.service.QueueService;

@Controller
public class OverviewController {
    final Logger LOGGER = LoggerFactory.getLogger(OverviewController.class);

    @Autowired
    private QueueService queueService;

    @RequestMapping("/")
    public String listQueues(Map<String, Object> map) throws IOException {
        LOGGER.debug("** listQueues");
        map.put("queueList", queueService.listQueues());
        LOGGER.debug("** listQueues", queueService.listQueues());
        return "overview";
    }
}
