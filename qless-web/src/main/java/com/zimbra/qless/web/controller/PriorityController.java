package com.zimbra.qless.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zimbra.qless.Job;
import com.zimbra.qless.QlessClient;

@Controller
public class PriorityController {
    final Logger LOGGER = LoggerFactory.getLogger(PriorityController.class);
    @Autowired private QlessClient qlessClient;

    
    @RequestMapping(value="/priority", method=RequestMethod.POST, headers={"Content-Type=application/json"})
    @ResponseBody
    public Map<String,Object> priority(@RequestBody Map<String,Integer> body) {
        // Expects a JSON-encoded dictionary of jid => priority
    	Map<String,Object> response = new HashMap<String,Object>();
    	for (Map.Entry<String,Integer> entry: body.entrySet()) {
    		String jid = entry.getKey();
    		Integer priority = entry.getValue();
    		try {
	    		Job job = qlessClient.getJob(jid);
	    		job.priority(priority);
	    		response.put(jid, priority);
    		} catch (IOException e) {
    			response.put(jid, "failed");
    		}
    	}
    	return response;
    }
}
