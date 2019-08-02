package com.lazzy.server.manager;

import io.netty.channel.Channel;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelManager {

    private static Vector<Channel> channels = new Vector<>();

    private static AtomicInteger channelCount = new AtomicInteger(0);

    public static int addChannel(Channel channel) {
        channels.add(channel);
        return channelCount.incrementAndGet();
    }

    public static int removeChannel(Channel channel) {
        channels.remove(channel);
        return channelCount.getAndDecrement();
    }

    public static Vector<Channel> getChannels() {
        return channels;
    }


}
