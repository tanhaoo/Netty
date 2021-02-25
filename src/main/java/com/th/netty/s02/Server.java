package com.th.netty.s02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author TanHaooo
 * @date 2021/2/24 21:20
 */
public class Server {

    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void serverStart() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);//2个线程
        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//除非指定的是BIOServer不然都是异步的
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println(ch);
                            ChannelPipeline pl = ch.pipeline();
                            pl.addLast(new ServerChildHandler());
                        }
                    })
                    .bind(8888)
                    .sync();//这个同步是看bind有没有成功，成功了才往下执行
            System.out.println("server started!");
            ServerFrame.INSTANCE.updateServerMsg("server started!");
            f.channel().closeFuture().sync();//close()->ChannelFuture  如果没有调close(),sync就会使他一直在这等着
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

class ServerChildHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
    }

    //channelRead 是workerGroup调的
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String s = new String(bytes);
            if (s.equals("_bye_")) {
                ServerFrame.INSTANCE.updateServerMsg(ctx.channel() + "客户端退出");
                Server.clients.remove(ctx.channel());
                ctx.close();
            } else {
                ServerFrame.INSTANCE.updateClientMsg(s);
                Server.clients.writeAndFlush(msg);
            }
        } finally {
            //if (buf != null) ReferenceCountUtil.release(buf); 与writeAndFlush冲突
            // System.out.println(buf.refCnt());//知道有多少人引用了他
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Server.clients.remove(ctx.channel());
        ctx.close();
        //当close后client端的 f.channel().closeFuture().sync();就会执行
    }
}
