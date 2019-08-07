package com.lazzy.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BInboundHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("BInboundHandler channelRegistered");
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("BInboundHandler receive message: {}", msg);
        ctx.writeAndFlush("你好" + msg);
        log.info("BInboundHandler write message: {}", "你好" + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("BInboundHandler channelReadComplete");
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("BInboundHandler exceptionCaught");
        ctx.writeAndFlush(cause.getMessage());
//        super.exceptionCaught(ctx, cause);
    }
}
