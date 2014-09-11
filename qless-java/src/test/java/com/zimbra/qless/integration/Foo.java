package com.zimbra.qless.integration;

import com.zimbra.qless.Job;

public class Foo
{
	public static String test(final Job job)
	{
		return job.getKlassName() + "." + job.getQueueName();
	}
}