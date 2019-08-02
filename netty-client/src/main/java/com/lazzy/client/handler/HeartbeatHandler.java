package com.lazzy.client.handler;

import com.lazzy.client.ClientLauncher;
import io.netty.channel.ChannelHandlerContext;

public class HeartbeatHandler extends CustomHeartbeatHandler {


    private ClientLauncher launcher;

    public HeartbeatHandler(ClientLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        launcher.doConnect();
    }
}
