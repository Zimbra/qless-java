package com.zimbra.qless.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.zimbra.qless.Client;
import com.zimbra.qless.Job;
import com.zimbra.qless.QlessException;
import com.zimbra.qless.Queue;

@SuppressWarnings("rawtypes")
public class SerialWorker {
	final Logger LOGGER = LoggerFactory.getLogger(SerialWorker.class);

	private Client client;
	private List<Queue> queues = new ArrayList<>();
	private String workerName;
	private int intervalInSeconds;
	private String currentJid;
	private Iterator queueItor;
	private boolean shutDown = false;

	public SerialWorker(List<String> queueNameList, Client client,
			String workerName, int intervalInSeconds) throws QlessException {
		Preconditions.checkArgument(
				null != queueNameList && queueNameList.size() > 0,
				"empty queue list provided");
		Preconditions.checkArgument(null != client, "null client provided");

		this.client = client;
		this.intervalInSeconds = intervalInSeconds;

		if (Strings.isNullOrEmpty(workerName)) {
			this.setWorkerName(this.client.workerName());
		} else {
			this.setWorkerName(workerName + "-" + this.client.workerName());
		}

		for (String queueName : queueNameList) {
			try {
				this.queues.add(client.queues().get(queueName));
			} catch (IOException ex) {
				LOGGER.error("failed to get queue (%s)", queueName, ex);
				throw new QlessException("failed to get queue (" + queueName
						+ ")", ex);
			}
		}

		if (this.queues.size() == 0) {
			throw new QlessException("no queue found");
		} else {
			this.queueItor = this.queues.iterator();
		}
	}

	public void shutDown() {
		this.shutDown = true;
		LOGGER.debug("shutdown worker");
	}

	private Job getJob() throws IOException {
		if (null == this.queues || this.queues.size() == 0) {
			return null;
		}

		int count = 0;
		while (!this.shutDown) {
			if (!queueItor.hasNext()) {
				this.queueItor = this.queues.iterator();
			}

			Queue queue = (Queue) this.queueItor.next();
			count++;
			Job job = queue.pop();
			if (null != job) {
				return job;
			}

			if (count == this.queues.size()) {
				break;
			}
		}

		return null;
	}

	public void run() throws InterruptedException, IOException, QlessException {
		while (!this.shutDown) {
			Job job = this.getJob();
			if (null == job) {
				LOGGER.debug("empty job list and sleep "
						+ this.intervalInSeconds + " seconds");
				Thread.sleep(this.intervalInSeconds * 1000);
				continue;
			}

			this.setCurrentJid(job.getJid());
			this.LOGGER.debug("working on " + job.getKlassName() + ":"
					+ this.getCurrentJid());
			job.process();
		}
	}

	public String getCurrentJid() {
		return currentJid;
	}

	public void setCurrentJid(String currentJid) {
		this.currentJid = currentJid;
	}

	public String getWorkerName() {
		return workerName;
	}

	private void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
}
