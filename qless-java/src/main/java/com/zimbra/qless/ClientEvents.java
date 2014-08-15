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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class ClientEvents implements AutoCloseable {
    static final String[] CHANNELS = {"canceled", "completed", "failed", "popped", "put", "stalled", "track", "untrack"};
    final Logger LOGGER = LoggerFactory.getLogger(ClientEvents.class);
    protected Client client;
    protected JedisPool jedisPool;
    protected Jedis jedis;
    protected Listener listener = new Listener();
    protected ListenerThread listenerThread;
    protected Multimap<String, QlessEventListener> listenersByChannel = ArrayListMultimap.create(); 

    /** Constructor */
    ClientEvents(Client client, JedisPool jedisPool) {
        this.client = client;
        this.jedisPool = jedisPool;
        jedis = jedisPool.getResource();
        listenerThread = new ListenerThread(this);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    
    public void close() throws IOException {
        listenerThread.cleanup();
    }
    
    public Builder on(String... events) {
        return new Builder(events);
    }
    
    
    public class Builder {
        String[] channels;
        
        Builder(String... channels) {
            this.channels = channels;
        }
        
        public void fire(QlessEventListener listener) {
            for (String channel: channels) {
                listenersByChannel.put(channel, listener);
            }
        }
    }
    
    
    class Listener extends JedisPubSub {
        public void onMessage(String channel, String message) {
            LOGGER.debug("onMessage channel={} message={}", channel, message);
            channel = channel.substring(3); // skip "ql:"
            
            // Fire event on all the registered listeners
            Collection<QlessEventListener> listeners = listenersByChannel.get(channel);
            for (QlessEventListener listener: listeners) {
                try {
                    listener.fire(channel, message);
                } catch (Exception e) {
                    LOGGER.error("Error handling qless event {}", message, e);
                }
            }
        }

        public void onPMessage(String pattern, String channel, String message) {
            LOGGER.debug("onPMessage pattern={} channel={} message={}", channel, message);
        }

        public void onSubscribe(String channel, int subscribedChannels) {
            LOGGER.debug("onSubscribe channel={} subscribedChannels={}", channel, subscribedChannels);
        }
        
        public void onUnsubscribe(String channel, int subscribedChannels) {
            LOGGER.debug("onUnsubscribe channel={} subscribedChannels={}", channel, subscribedChannels);
        }

        public void onPUnsubscribe(String pattern, int subscribedChannels) {
            LOGGER.debug("onPUnsubscribe pattern={} subscribedChannels={}", pattern, subscribedChannels);
        }

        public void onPSubscribe(String pattern, int subscribedChannels) {
            LOGGER.debug("onPSubscribe pattern={} subscribedChannels={}", pattern, subscribedChannels);
        }
    }
    
    
    class ListenerThread extends Thread {
        final Logger LOGGER = LoggerFactory.getLogger(ListenerThread.class);
        protected ClientEvents clientEvents;
        
        ListenerThread(ClientEvents clientEvents) {
            this.clientEvents = clientEvents;
        }
        
        public void run() {
            LOGGER.debug("Run loop starting");
            List<String> channelList = new ArrayList<>();
            for (String channel: CHANNELS) {
                channelList.add("ql:" + channel);
            }
            String[] channels = new String[channelList.size()];
            channelList.toArray(channels);
            jedis.subscribe(listener, channels);
            LOGGER.debug("Run loop ending");
            cleanup();
        }
        
        void cleanup() {
            clientEvents.jedisPool.returnResource(clientEvents.jedis);
        }
    }
    
    
    public interface QlessEventListener {
        public void fire(String channel, Object event) throws Exception;
    }
}
