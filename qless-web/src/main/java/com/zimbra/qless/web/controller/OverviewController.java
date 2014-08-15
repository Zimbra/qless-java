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

import com.zimbra.qless.Client;
import com.zimbra.qless.web.viewmodel.Tab;

@Controller
public class OverviewController {
    final Logger LOGGER = LoggerFactory.getLogger(OverviewController.class);

    @Autowired
    private Client qlessClient;

    @RequestMapping("/")
    public String overview(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        map.put("queues", qlessClient.queues().counts());
        return "overview";
    }

    @RequestMapping("/about")
    public String about(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        return "about";
    }
    
    @RequestMapping("/config")
    public String config(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        map.put("options", qlessClient.config().all());
        return "config";
    }
    
    @RequestMapping("/queues")
    public String queues(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        map.put("queues", qlessClient.queues().counts());
        return "queues";
    }

    static void setDefaults(Map<String, Object> map) {
    	map.put("tabs", getTabs());
    	map.put("application_name", "Qless Web");
    }
    
    static List<Tab> getTabs() {
    	List<Tab> tabs = new ArrayList<Tab>();
    	tabs.add(new Tab("Queues"   , "/queues"));
    	tabs.add(new Tab("Workers"  , "/workers"));
    	tabs.add(new Tab("Track"    , "/track"));
    	tabs.add(new Tab("Failed"   , "/failed"));
    	tabs.add(new Tab("Completed", "/completed"));
    	tabs.add(new Tab("Config"   , "/config"));
    	tabs.add(new Tab("About"    , "/about"));
    	return tabs;
    }
}
