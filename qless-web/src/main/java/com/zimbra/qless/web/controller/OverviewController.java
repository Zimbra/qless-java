package com.zimbra.qless.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zimbra.qless.QlessClient;
import com.zimbra.qless.JSON;
import com.zimbra.qless.Queue;
import com.zimbra.qless.WorkerJobs;
import com.zimbra.qless.web.viewmodel.Tab;

@Controller
public class OverviewController {
    final Logger LOGGER = LoggerFactory.getLogger(OverviewController.class);

    @Autowired
    private QlessClient qlessClient;

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

    @RequestMapping(value="/queues/{queue}")
    public String queue(@PathVariable("queue") String queue, Map<String, Object> map) throws Exception {
    	return queueTab(queue, "stats", map);
    }

    @RequestMapping(value="/queues/{queue}/{tab}")
    public String queueTab(@PathVariable("queue") String queue, @PathVariable("tab") String tab, Map<String, Object> map) throws Exception {
    	setDefaults(map);
    	Queue q = qlessClient.queue(queue);
    	Map<String,Object> stats = q.stats();
        map.put("queue", q);
        map.put("counts", q.counts());
        map.put("stats", stats);
        map.put("tab", tab);
        map.put("waitStatsHistogram", JSON.stringify(((Map)stats.get("wait")).get("histogram")));
        map.put("runStatsHistogram", JSON.stringify(((Map)stats.get("run")).get("histogram")));
        LOGGER.debug("Queue stats", q.stats());
        if ("waiting".equals(tab)) {
            map.put("jobs", q.peek(20));
        } else if (getTabs().contains(tab)) {
        	map.put("jobs", q.jobs().jobs(tab)); // TODO pagination
        }
        return "queue";
    }

    static void setDefaults(Map<String, Object> map) {
    	map.put("tabs", getTabs());
    	map.put("application_name", "Qless");
    }
    
    @RequestMapping("/workers")
    public String workers(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        map.put("workers", qlessClient.getWorkerCounts());
        return "workers";
    }

    @RequestMapping(value="/workers/{worker}/")
    public String worker(@PathVariable("worker") String worker, Map<String, Object> map) throws Exception {
    	setDefaults(map);
        map.put("name", worker);
        WorkerJobs workerJobs = qlessClient.getWorkerJobs(worker);
        map.put("workerJobs", workerJobs);
        map.put("running", qlessClient.getJobs(workerJobs.getJobs().toArray(new String[workerJobs.getJobs().size()])));
        map.put("stalled", qlessClient.getJobs(workerJobs.getStalled().toArray(new String[workerJobs.getStalled().size()])));
        return "worker";
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
