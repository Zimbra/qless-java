package com.zimbra.qless.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zimbra.qless.JSON;
import com.zimbra.qless.Job;
import com.zimbra.qless.QlessClient;
import com.zimbra.qless.Queue;
import com.zimbra.qless.WorkerJobs;
import com.zimbra.qless.web.viewmodel.Tab;

@Controller
public class MainController {
    final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private QlessClient qlessClient;

    @RequestMapping("/")
    public String overview(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        map.put("queues", qlessClient.queues().counts());
        map.put("workers", qlessClient.getWorkerCounts());
        return "overview";
    }

    @RequestMapping("/about")
    public String about(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        return "about";
    }
    
    @RequestMapping("/completed")
    public String completed(Map<String, Object> map) throws IOException {
    	setDefaults(map);
    	List<String> jids = qlessClient.getJobsComplete();
        map.put("jobs", qlessClient.getJobs(jids.toArray(new String[jids.size()])));
        return "completed";
    }

    @RequestMapping("/config")
    public String config(Map<String, Object> map) throws IOException {
    	setDefaults(map);
        map.put("options", qlessClient.config().all());
        return "config";
    }
    
    @RequestMapping(value="/jobs/{jid}")
    public String job(@PathVariable("jid") String jid, Map<String, Object> map) throws Exception {
    	setDefaults(map);
    	Job job = qlessClient.getJob(jid);
    	map.put("jid", jid);
    	map.put("job", job);
        return "job";
    }

    @RequestMapping(value="/move", method=RequestMethod.POST, headers={"Content-Type=application/json"})
    @ResponseBody
    public Map<String,String> move(@RequestBody Map<String,String> body, HttpServletResponse res) {
        // Expects a JSON-encoded hash of id: jid and queue: queue_name
		String jid = body.get("id");
		String queueName = body.get("queue");
		try {
    		Job job = qlessClient.getJob(jid);
    		if (job == null) {
    			res.setStatus(404);
    			return null;
    		}
    		job.requeue(queueName);
		} catch (IOException e) {
			res.setStatus(500);
		}
       	return body;
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
        map.put("queues", qlessClient.queues().counts());
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
