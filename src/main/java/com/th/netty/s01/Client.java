//package com.th.netty.s01;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.util.ReferenceCountUtil;
//
//import java.io.IOException;
//
///**
// * @author TanHaooo
// * @date 2021/1/29 20:35
// */
//public class Client {
//    public static void main(String[] args) throws Exception {
//
//        //线程池
//        EventLoopGroup group = new NioEventLoopGroup();
//        Bootstrap b = new Bootstrap();
//        try {
//            ChannelFuture f = b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new ClientChannelInitializer())
//                    .connect("localhost", 8888)
//                    .addListener(new ChannelFutureListener() {
//                        @Override
//                        public void operationComplete(ChannelFuture future) {
//                            if (!future.isSuccess())
//                                System.out.println("not connected");
//                            else
//                                System.out.println("connected");
//                        }
//                    })
//                    .sync();//因为Netty里都是异步的，所以sync在这里的作用是等它结束，不让他继续往下执行，必须等他结束
//            System.out.println("...");
//            f.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully();
//        }
//    }
//}
//
//class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
//    @Override
//    protected void initChannel(SocketChannel ch) throws Exception {
//        System.out.println(ch);
//        ch.pipeline().addLast(new ClientHandler());
//    }
//}
//
//class ClientHandler extends ChannelInboundHandlerAdapter {
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        //Channel第一次连上可用，写出一个字符串 Direct Memory  直接访问系统内存，不通过JVM虚拟机，带来了垃圾回收问题
//        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
//        ctx.writeAndFlush(buf);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = null;
//        try {
//            buf = (ByteBuf) msg;
//            byte[] bytes = new byte[buf.readableBytes()];
//            buf.getBytes(buf.readerIndex(), bytes);
//            System.out.println(new String(bytes));
//        } finally {
//            if (buf != null) ReferenceCountUtil.release(buf);
//            // System.out.println(buf.refCnt());//知道有多少人引用了他
//        }
//    }
//}
