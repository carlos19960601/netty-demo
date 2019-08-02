package com.lazzy.server;

import com.lazzy.server.handler.ClientManagerHandler;
import com.lazzy.server.handler.HeartbeatHandler;
import com.lazzy.server.handler.LogInHandler;
import com.lazzy.server.handler.ReplyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

/**
 * @author cooper.q.zeng
 */
@Slf4j
public class ServerLauncher {


    public static void main(String[] args) {

        int port = 8000;
        int nWorkers = 10;
        ThreadFactory bossFactory = new DefaultThreadFactory("netty.acceptor.boss");
        ThreadFactory workerFactory = new DefaultThreadFactory("netty.acceptor.worker");


        /**
         * 一般高性能的场景下,使用的堆外内存，也就是直接内存，使用堆外内存的好处就是减少内存的拷贝，和上下文的切换，缺点是
         * 堆外内存处理的不好容易发生堆外内存OOM
         * 当然也要看当前的JVM是否只是使用堆外内存，换而言之就是是否能够获取到Unsafe对象#PlatformDependent.directBufferPreferred()
         */
        ByteBufAllocator allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());

        ServerBootstrap bootstrap = new ServerBootstrap();

        EventLoopGroup boss = new NioEventLoopGroup(1, bossFactory);
        EventLoopGroup worker = new NioEventLoopGroup(nWorkers, workerFactory);
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .childOption(ChannelOption.ALLOCATOR, allocator)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ChannelPipeline pipeline = sc.pipeline();
                        pipeline.addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new IdleStateHandler(10, 0, 0))
                                .addLast(new LogInHandler())
                                .addLast(new HeartbeatHandler())
                                .addLast(new ReplyHandler())
                                .addLast(new ClientManagerHandler());

                    }
                });

        ChannelFuture channelFuture = bootstrap.bind(port).syncUninterruptibly();

//        while (true) {
//            Vector<Channel> channels = ChannelManager.getChannels();
//            if (!channels.isEmpty()) {
//                for (Channel channel : channels) {
//                    channel.writeAndFlush("推送消息");
//                }
//            } else {
//                log.info("没有客户端在线，无法推送消息");
//            }
//            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
//        }
    }

}
