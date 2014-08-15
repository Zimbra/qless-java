package com.zimbra.qless.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zimbra.qless.web.service.QueueService;
import com.zimbra.qless.web.viewmodel.Tab;

@Controller
public class OverviewController {
    final Logger LOGGER = LoggerFactory.getLogger(OverviewController.class);

    @Autowired
    private QueueService queueService;

    @RequestMapping("/")
    public String listQueues(Map<String, Object> map) throws IOException {
    	List<Tab> tabs = new ArrayList<Tab>();
    	tabs.add(new Tab("Queues"   , "/queues"));
    	tabs.add(new Tab("Workers"  , "/workers"));
    	tabs.add(new Tab("Track"    , "/track"));
    	tabs.add(new Tab("Failed"   , "/failed"));
    	tabs.add(new Tab("Completed", "/completed"));
    	tabs.add(new Tab("Config"   , "/config"));
    	tabs.add(new Tab("About"    , "/about"));
    	map.put("tabs", tabs);
    	map.put("application_name", "Zimbra Qless Web");
    	
        map.put("queues", queueService.listQueues());
        return "overview";
    }
}
