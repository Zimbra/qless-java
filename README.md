qless-java
==========

This is a Java port of the [qless](https://github.com/seomoz/qless) Ruby language binding for [qless-core](https://github.com/seomoz/qless-core), which is a set of data structures and stored procedures for Redis that perform job tracking.

`Qless` was inspired by [resque](https://github.com/defunkt/resque#readme), but is next generation in many ways:

* It's like a job and worker library, but can push 1,000 ops per second.
* It's like a queue, but with random-access read/write, recall, and queue lane-changing.
* It's sort of like a developer library, but more like a meta-library. Since all the business logic is written in Lua that runs in Redis via atomic client/server invocations, the polyglot bindings are more like facades. This is the future.
* Its job chaining and meta tagging features allow it to behave like a distributed lock library. I call this immediate-mode.

Philosophy
==========

My original motivation was to get the blocked threads in a distributed system to show up for Ops in one of these web consoles that come with qless, celery, resque, or RabbitMQ. That's the killer feature I'm chasing, which prevents me from warming up to other distributed lock frameworks like [Redisson](https://github.com/mrniko/redisson).

The use of a job tracking and pipelining library for distributed locking and blocker publishing might sound a bit unusual, but so far it seems like a decent fit.

Loading multi-purpose "firmware" like `qless-core` into Redis on demand feels lightweight and agile, especially relative to a Java-based alternative like [ZooKeeper](http://zookeeper.apache.org), whose benefits seem to hardly justify the recommended 1 GB footprint.

Design Criteria
===============

This package is sponsored by Zimbra, and designed for [Zimbra Collaboration Server](http://www.zimbra.com/products/zimbra-collaboration/index.html) 9.0. This drives library dependency selection, which is:

* Jackson 1.9.2
* Jedis 2.5.x
* SLF4J

A concerted effort is made to keep out dependency injection frameworks like Spring or Guice. I expect you to be using one of them, but so far I can get by without the dependency.

Use of private functions and member vars is avoided, with CGLIB proxying in mind. This might come up if you're using Spring AOP or Spring Configuration.

Monitoring [seomoz/qless](https://github.com/seomoz/qless)
=========================

The `qless` Ruby gem includes a `Qless::Server` Sinatra-based web app for monitoring current and recent job activity. The web UI can be run on any server that has access to Redis.

**Install qless**

````
$ sudo gem install qless
````

**Configure Rack**

Create this `config.ru` file:

````
require 'rack'
require 'Qless/server'
client = Qless::Client.new(:host => "localhost", :port => 6379)

builder = Rack::Builder.new do
  map('/') { run Qless::Server.new(client) }
end

Rack::Handler::WEBrick.run builder, :Port => 9292
````

**Launch your web container**

````
$ rackup
````

**Browse the qless web UI**

You can now point your browser at `http://localhost:9292`, and begin monitoring the qless data structures.

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
