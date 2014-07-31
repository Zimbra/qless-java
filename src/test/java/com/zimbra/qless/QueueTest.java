/*
 * ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2014 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.qless;

import java.io.IOException;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class QueueTest {
    final Logger LOGGER = LoggerFactory.getLogger(QueueTest.class);
    JedisPool jedisPool = new JedisPool("localhost");
    Client client;
    
    @Before
    public void before() throws IOException {
        Jedis jedis = jedisPool.getResource();
        client = new Client(jedisPool);
        try {
            jedis.flushDB();
        } finally {
            jedisPool.returnResource(jedis);
        }
    }
    
//    ['name', :name].each do |name|
//    it "can query length when initialized with nem = #{name.inspect}" do
//      q = Queue.new(name, client)
//      q.length.should eq(0)
//    end
//  end

//  [:to_s, :inspect].each do |meth|
//    it "returns a human-readable string from ##{meth}" do
//      q = Queue.new('queue-name', client)
//      string = q.send(meth)
//      string.should have_at_most(100).characters
//      string.should include('queue-name')
//    end
//  end

    @Test
    public void specifyAJidInPutAndRecur() throws IOException {
        String s = "Failed specifying a jid in put or recur";
        Queue queue = client.queues("foo");
        
        HashMap<String,Object> data = new HashMap<>();
        data.put("foo", "bar");
        HashMap<String,Object> opts = new HashMap<>();
        opts.put("jid", "howdy");
        String jid = queue.put("SomeJobClass", data, opts);
        Assert.assertEquals(s, "howdy", jid);
        
        data = new HashMap<>();
        data.put("foo", "bar");
        opts = new HashMap<>();
        opts.put("jid", "howdy");
        jid = queue.recur("SomeJobClass", data, 5, opts);
        Assert.assertEquals(s, "howdy", jid);
    }
    
//  shared_examples_for 'job options' do
//    let(:q) { Queue.new('q', client) }
//
//    let(:klass1) do
//      Class.new do
//        def self.default_job_options(data)
//          { jid: "jid-#{data[:arg]}", priority: 100 }
//        end
//      end
//    end
//
//    let(:klass2) { Class.new }
//
//    it "uses options provided by the klass's .default_job_options method" do
//      jid = enqueue(q, klass1, { arg: 'foo' })
//      job = client.jobs[jid]
//      job.jid.should eq('jid-foo')
//      job.priority.should eq(100)
//    end
//
//    it 'overrides the default options with the passed options' do
//      jid = enqueue(q, klass1, { arg: 'foo' }, priority: 15)
//      job = client.jobs[jid]
//      job.priority.should eq(15)
//    end
//    @Test
//    public void overrideDefaultOptionsWithPassedOptions() {
//        String q = client
//        String jid = enqueue()
//    }

//    it 'works fine when the klass does not define .default_job_options' do
//      jid = enqueue(q, klass2, { arg: 'foo' }, priority: 15)
//      job = client.jobs[jid]
//      job.priority.should eq(15)
//    end
//  end
//
//  describe '#put' do
//    def enqueue(q, klass, data, opts = {})
//      q.put(klass, data, opts)
//    end
//
//    include_examples 'job options'
//
//    it "uses the class's name properly (not #to_s)" do
//      q = Queue.new('q', client)
//      jid = enqueue(q, SomeJobClassWithDifferentToS, {})
//      job = client.jobs[jid]
//      job.klass_name.should eq('SomeJobClassWithDifferentToS')
//    end
//  end
//
//  describe '#recur' do
//    def enqueue(q, klass, data, opts = {})
//      q.recur(klass, data, 10, opts)
//    end
//
//    include_examples 'job options'
//  end
//
//  describe "equality" do
//    it 'is considered equal when the qless client and name are equal' do
//      q1 = Qless::Queue.new('foo', client)
//      q2 = Qless::Queue.new('foo', client)
//
//      expect(q1 == q2).to eq(true)
//      expect(q2 == q1).to eq(true)
//      expect(q1.eql? q2).to eq(true)
//      expect(q2.eql? q1).to eq(true)
//
//      expect(q1.hash).to eq(q2.hash)
//    end
//
//    it 'is considered equal when the qless client is the same and the names only differ in symbol vs string' do
//      q1 = Qless::Queue.new('foo', client)
//      q2 = Qless::Queue.new(:foo, client)
//
//      expect(q1 == q2).to eq(true)
//      expect(q2 == q1).to eq(true)
//      expect(q1.eql? q2).to eq(true)
//      expect(q2.eql? q1).to eq(true)
//
//      expect(q1.hash).to eq(q2.hash)
//    end
//
//    it 'is not considered equal when the name differs' do
//      q1 = Qless::Queue.new('foo', client)
//      q2 = Qless::Queue.new('food', client)
//
//      expect(q1 == q2).to eq(false)
//      expect(q2 == q1).to eq(false)
//      expect(q1.eql? q2).to eq(false)
//      expect(q2.eql? q1).to eq(false)
//
//      expect(q1.hash).not_to eq(q2.hash)
//    end
//
//    it 'is not considered equal when the client differs' do
//      q1 = Qless::Queue.new('foo', client)
//      q2 = Qless::Queue.new('foo', double)
//
//      expect(q1 == q2).to eq(false)
//      expect(q2 == q1).to eq(false)
//      expect(q1.eql? q2).to eq(false)
//      expect(q2.eql? q1).to eq(false)
//
//      expect(q1.hash).not_to eq(q2.hash)
//    end
//
//    it 'is not considered equal to other types of objects' do
//      q1 = Qless::Queue.new('foo', client)
//      q2 = Class.new(Qless::Queue).new('foo', client)
//
//      expect(q1 == q2).to eq(false)
//      expect(q1.eql? q2).to eq(false)
//      expect(q1.hash).not_to eq(q2.hash)
//    end
//  end
}
