package com.lazzy.server.handler;

import com.lazzy.server.manager.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cooper.q.zeng
 */
@Slf4j
public class ClientManagerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        int index = ChannelManager.addChannel(channel);
        log.info("第{}个Client接入", index);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        int index = ChannelManager.removeChannel(channel);
        log.info("第{}个Client断开连接", index);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("AInboundHandler channelReadComplete");
        ctx.fireChannelReadComplete();
    }

}
