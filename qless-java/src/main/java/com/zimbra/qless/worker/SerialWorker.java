package com.zimbra.qless.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zimbra.qless.Client;
import com.zimbra.qless.Job;
import com.zimbra.qless.QlessException;
import com.zimbra.qless.Queue;

@SuppressWarnings("rawtypes")
public class SerialWorker {
	final Logger LOGGER = LoggerFactory.getLogger(SerialWorker.class);
	
	private List<Queue> queues = new ArrayList<>();
	
	private int intervalInSeconds;
	private String currentJid;
	private Iterator queueItor;
	
	public SerialWorker(List<String> queueNameList, Client client, int intervalInSeconds)
	{
		this.intervalInSeconds = intervalInSeconds;
		for (String queueName : queueNameList)
		{
			try {
				this.queues.add(client.queues().get(queueName));
			} catch (IOException ex) {
				LOGGER.error("failed to get queue (%s)", queueName, ex);
				throw new QlessException("failed to get queue (" + queueName + ")", ex);				
			}
		}
		
		if (this.queues.size() == 0) 
			throw new QlessException("no queue found");
		else 
			queueItor = this.queues.iterator();
	}
	
	private Job getJob() throws IOException
	{
		if (null == this.queues || this.queues.size() == 0 || null == this.queueItor) {
			return null;
		}
		
		Iterator oldItor = this.queueItor;
		while (true) {
			Queue q = (Queue)this.queueItor;
			Job job = q.pop();
			if (null != job) {
				queueItor = (Iterator) queueItor.next();
				return job;
			}
			
			if (queueItor.hasNext()) {
				queueItor = (Iterator) queueItor.next();
				if (this.queueItor == oldItor) {
					break;
				}
			} else {
				queueItor = this.queues.iterator();
			}
		}
		
		return null;
	}
	
	public void run() throws InterruptedException, IOException
	{
		while (true)
		{
			Job job = this.getJob();
			if (null == job) {
				Thread.sleep(this.intervalInSeconds * 1000);
				continue;
			}
			
			this.setCurrentJid(job.getJid());
			this.LOGGER.debug("working on %s %s", job.getJid(), job.getKlassName());
			job.process();
		}
	}

	public String getCurrentJid() {
		return currentJid;
	}

	public void setCurrentJid(String currentJid) {
		this.currentJid = currentJid;
	}
}
