package com.zimbra.qless.worker;

import java.io.IOException;
import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import redis.clients.jedis.JedisPool;
import com.zimbra.qless.Client;

public class QlessJavaWorker {
    @Option(name = "-host", usage = "The url to connect to Redis")
    public String host = "localhost";

    @Option(name = "-name", usage = "The hostname to identify your worker as")
    public String name = null;

    @Option(name = "-interval", usage = "The pulling interval")
    public int inverval = 60;

    @Option(name = "-queue", required = true, handler = StringArrayOptionHandler.class, usage = "The queues to pull work from")
    public String[] queues = null;

    public static void main(String[] args) {
	QlessJavaWorker worker = new QlessJavaWorker();
	CmdLineParser parser = new CmdLineParser(worker);

	try {
	    parser.parseArgument(args);

	    JedisPool jedisPool = new JedisPool(worker.host);
	    Client client = new Client(jedisPool);

	    new SerialWorker(Arrays.asList(worker.queues), client, worker.name,
		    worker.inverval).run();
	} catch (CmdLineException | InterruptedException | IOException e) {
	    System.err.println(e.getMessage());
	    parser.printUsage(System.err);
	}
    }
}
