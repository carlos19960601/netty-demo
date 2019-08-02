package com.lazzy.client;

import com.lazzy.client.handler.CustomHeartbeatHandler;
import com.lazzy.client.handler.EchoClientHandler;
import com.lazzy.client.handler.HeartbeatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ClientLauncher {

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;
    private int port = 8000;


    public void start() {
        try {
            bootstrap = new Bootstrap();

            EventLoopGroup worker = new NioEventLoopGroup();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(new IdleStateHandler(0, 0, 7));
                            pipeline.addLast(new HeartbeatHandler(ClientLauncher.this));
                            pipeline.addLast(new EchoClientHandler());
                        }
                    });
            doConnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture channelFuture = bootstrap.connect("localhost", port);

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel = future.channel();
                    log.info("Connect to server successfully!");
                } else {
                    log.info("Failed to connect to server, try connect after 10s");
                    future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }

    public void sendData() {
        String[] names = {"张三", "李四", "王麻"};

        int count = 5;
        Random random = new Random();
        while (count > 0) {
            int index = random.nextInt(3);
            channel.writeAndFlush(names[index]);
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            count--;
        }
    }

    public static void main(String[] args) {
        ClientLauncher launcher = new ClientLauncher();
        launcher.start();
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        launcher.sendData();
    }

}
