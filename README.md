qless-java
==========

This is a Java port of the [qless](https://github.com/seomoz/qless) Ruby language binding for [qless-core](https://github.com/seomoz/qless-core).

Building
========

````
$ mvn install -Dmaven.test.skip=true
````

Monitoring
==========

To monitor Qless using the provided Java webapp:

````
$ cd qless-web
$ REDIS_URL=redis://localhost foreman start
````

To remote debug the webapp via port 8000:

````
$ cd qless-web
$ REDIS_URL=redis://localhost foreman start webdebug
````

Usage
=====

To create a job (and a queue if it doesn't yet exist):

````
JedisPool jedisPool = new JedisPool("localhost");
QlessClient qlessClient = new QlessClient(jedisPool);
Queue queue = qlessClient.queue("myqueue");
String jid = queue.put(MyJobClass.class.getName(), null, null);
````

To tag an existing job:

````
Job job = qless.getJob(jid);
job.tag("interesting");
````

See the unit tests for a comprehensive exploration of the available API.

About Qless
===========

`Qless` is a set of data structures and stored procedures for Redis that perform job tracking, created by [Moz](https://github.com/seomoz).

`Qless` was inspired by [resque](https://github.com/defunkt/resque#readme), but is next generation in many ways:

* It's like a job and worker library, but can push 1,000 ops per second.
* It's like a queue, but with random-access read/write, recall, and queue lane-changing.
* It's sort of like a developer library, but more like a meta-library. Since all the business logic is written in Lua that runs in Redis via atomic client/server invocations, the polyglot bindings are more like facades. This is the future.
* Its job chaining and meta tagging features allow it to behave like a distributed lock library. I call this immediate-mode.


Design Criteria
===============

This package is sponsored by Zimbra, and designed for [Zimbra Collaboration Server](http://www.zimbra.com/products/zimbra-collaboration/index.html) 9.0. This drives library dependency selection, which is:

* Jackson 1.9.2
* Jedis 2.5.x
* SLF4J

A concerted effort is made to keep out dependency injection frameworks like Spring or Guice. I expect you to be using one of them, but so far I can get by without the dependency.

Use of private functions and member vars is avoided, with CGLIB proxying in mind. This might come up if you're using Spring AOP or Spring Configuration.

Testing [seomoz/qless](https://github.com/seomoz/qless)
======================

I'm not a Ruby developer, and from my outside perspective the whole Ruby ecosystem has always looked incredibly broken. For years, certain gems would fail to install and build on my Mac, all because of a certain gem file called nokogiri. There are several dozen web pages dedicated to this nonsense, and none of it worked for me. I finally had enough, and flushed almost a half-day in order to find this obscure "gem":

````
sudo ARCHFLAGS=-Wno-error=unused-command-line-argument-hard-error-in-future gem install nokogiri -v '1.6.0' --verbose --no-ri --no-rdoc
````

On certain Mac's like mine, that gets the prereqs out of the way, and allows you to build and then test the original Qless:

````
bundle install
bundle exec rake spec
````
