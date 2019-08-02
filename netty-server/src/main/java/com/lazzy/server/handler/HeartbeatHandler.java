package com.lazzy.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatHandler extends CustomHeartbeatHandler {

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        log.info("client({}) reader timeout, close it", ctx.channel().remoteAddress());
        ctx.close();
    }
}
