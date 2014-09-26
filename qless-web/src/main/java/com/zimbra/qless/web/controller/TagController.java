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
public class TagController {
    final Logger LOGGER = LoggerFactory.getLogger(TagController.class);
    @Autowired private QlessClient qlessClient;

    
    @RequestMapping(value="/tag", method=RequestMethod.POST, headers={"Content-Type=application/json"})
    @ResponseBody
    public Map<String,Object> tag(@RequestBody Map<String, String[]> body) {
        // Expects a JSON-encoded dictionary of jid => [tag, tag, tag]
    	Map<String,Object> response = new HashMap<String,Object>();
    	for (Map.Entry<String,String[]> entry: body.entrySet()) {
    		String jid = entry.getKey();
    		String[] tags = entry.getValue();
    		try {
	    		Job job = qlessClient.getJob(jid);
	    		job.tag(tags);
	    		response.put(jid, tags);
    		} catch (IOException e) {
    			response.put(jid, "failed");
    		}
    	}
    	return response;
    }
    
    
    @RequestMapping(value="/untag", method=RequestMethod.POST, headers={"Content-Type=application/json"})
    @ResponseBody
    public Map<String,Object> untag(@RequestBody Map<String, String[]> body) throws Exception {
        // Expects a JSON-encoded dictionary of jid => [tag, tag, tag]
    	Map<String,Object> response = new HashMap<String,Object>();
    	for (Map.Entry<String,String[]> entry: body.entrySet()) {
    		String jid = entry.getKey();
    		String[] tags = entry.getValue();
    		try {
	    		Job job = qlessClient.getJob(jid);
	    		job.untag(tags);
	    		response.put(jid, tags);
    		} catch (IOException e) {
    			response.put(jid, "failed");
    		}
    	}
    	return response;
    }
}
