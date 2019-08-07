package com.lazzy.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CustomHeartbeatHandler extends SimpleChannelInboundHandler<String> {

    private int heartbeatCount = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
        if ("ping".equalsIgnoreCase(message)) {
            sendPongMsg(context);
        } else if ("pong".equalsIgnoreCase(message)) {
            log.info("get pong msg from {}", context.channel().remoteAddress());
        } else {
            context.fireChannelRead(message);
        }
    }

    protected void sendPingMsg(ChannelHandlerContext context) {
        context.writeAndFlush("ping");
        heartbeatCount++;
        log.info("sent ping msg to {}, count: {}", context.channel().remoteAddress(), heartbeatCount);
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        context.channel().writeAndFlush("pong");
        heartbeatCount++;
//        log.info("sent pong msg to {}, count: {}", context.channel().remoteAddress(), heartbeatCount);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }


    protected void handleReaderIdle(ChannelHandlerContext ctx) {

    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {

    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
    }
}