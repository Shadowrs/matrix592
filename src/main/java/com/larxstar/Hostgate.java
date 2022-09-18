package com.larxstar;

import com.rs.game.World;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Hostgate {
    public static Bootstrap bootstrap;
    public static Channel clientChannel;
    private static final int IDENTITY = 14;

    public static void reconnect() {
        log.info("attempting to reconnect to hostgate ...");

        ChannelFuture sync = bootstrap.connect();
        // sync blocks until outcome
        sync.addListener(l -> { // attach listener before calling sync
            if (l.cause() != null) {
                log.info("connection outcome", l.cause());
                // dont need to reconnect here, it keeps trying
            }
        });
    }

    public static void connectHostgateBlockingStartup() {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline channelPipeline = ch.pipeline();
                        channelPipeline.addLast(new StringDecoder());
                        channelPipeline.addLast(new StringEncoder());
                        channelPipeline.addLast(new MyHandler());
                        channelPipeline.addLast(new HostgateLink.DefaultOutboundAdapter());
                        channelPipeline.addLast("writeTimeoutHandler", new WriteTimeoutHandler(30));
                    }
                });
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.remoteAddress(HostgateLink.HOSTGATE_IP, HostgateLink.HOSTGATE_PORT);
        try {
            System.out.println("connecting to hostgate...");
            bootstrap.connect().sync();
        } catch (Exception e) {
            log.error("hostgate no connect on game server start", e); // crash server if cant connect
            System.exit(0);
        }
    }

    public static void ping(int pcount) {
        if (clientChannel != null && clientChannel.isActive()) {
            clientChannel.writeAndFlush("opcode:002 "+IDENTITY+"::"+ pcount);
        }
    }

    @Slf4j
    public static class MyHandler extends HostgateLink.DefaultAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof String) {
                System.out.println("received: "+ msg);
                String str = (String)msg;
                if (str.equalsIgnoreCase("boss says hi")) { // handshake response
                    System.out.println("handshake delivered.");
                    ctx.channel().close();
                } else if (str.equalsIgnoreCase("please die")) {
                    System.out.println("kill request recieved.");
                    ctx.channel().close();
                    // should really queue this to game thread but fuck it LUL
                    World.safeShutdown(false, 10);
                }
            } else {
                log.error("unknown msg received: "+ msg);
            }
            super.channelRead(ctx, msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);

            clientChannel = ctx.channel(); //newer channel
            clientChannel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                log.info("[closeFuture] hostgate disconnected");
            });
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            //log.info("reg {}", ctx);
            log.info("sent handshake");
            ctx.writeAndFlush("opcode:002 "+IDENTITY+"::0::startup");
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            log.info("cya hostgate {}", ip);

            //if (connected) {
                ctx.channel().eventLoop().schedule(() -> {
                    //log.info("Attempting to reconnect...");
                    reconnect();

                }, 2, TimeUnit.SECONDS);
            //}
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            if (!cause.getStackTrace()[0].getMethodName().equals("read0") && !cause.getMessage().equals("Connection reset")) {
                log.error("exceptionCaught expected:", cause);
            } else {
                log.error("exceptionCaught: {}", cause.getMessage());
            }

            //super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }
}
