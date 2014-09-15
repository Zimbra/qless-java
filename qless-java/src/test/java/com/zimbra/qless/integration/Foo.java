package com.zimbra.qless.integration;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.qless.Job;

public class Foo {
    public static List<String> runningHistory = new ArrayList<String>();

    public static String test(final Job job) {
	String result = job.getKlassName() + "." + job.getQueueName();
	Foo.runningHistory.add(result);

	return result;
    }

    public static void testA(final Job job) {
	Foo.runningHistory.add(job.getKlassName() + "." + job.getQueueName());
	System.out.println(job.getKlassName() + "." + job.getQueueName());
    }

    public static void testB(final Job job) {
	Foo.runningHistory.add(job.getKlassName() + "." + job.getQueueName());
	System.out.println(job.getKlassName() + "." + job.getQueueName());
    }

    public static void testC(final Job job) {
	Foo.runningHistory.add(job.getKlassName() + "." + job.getQueueName());
	System.out.println(job.getKlassName() + "." + job.getQueueName());
    }
}
