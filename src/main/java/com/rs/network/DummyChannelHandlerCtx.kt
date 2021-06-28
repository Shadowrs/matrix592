package com.rs.network

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.util.Attribute
import io.netty.util.AttributeKey
import io.netty.util.concurrent.EventExecutor
import java.net.SocketAddress

object DummyChannelHandlerCtx : ChannelHandlerContext {
    override fun <T : Any?> attr(p0: AttributeKey<T>?): Attribute<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> hasAttr(p0: AttributeKey<T>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun fireChannelRegistered(): ChannelHandlerContext {
        return this
    }

    override fun fireChannelUnregistered(): ChannelHandlerContext {
        return this
    }

    override fun fireChannelActive(): ChannelHandlerContext {
        return this
    }

    override fun fireChannelInactive(): ChannelHandlerContext {
        return this
    }

    override fun fireExceptionCaught(p0: Throwable?): ChannelHandlerContext {
        return this
    }

    override fun fireUserEventTriggered(p0: Any?): ChannelHandlerContext {
        return this
    }

    override fun fireChannelRead(p0: Any?): ChannelHandlerContext {
        return this
    }

    override fun fireChannelReadComplete(): ChannelHandlerContext {
        return this
    }

    override fun fireChannelWritabilityChanged(): ChannelHandlerContext {
        return this
    }

    override fun bind(p0: SocketAddress?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun bind(p0: SocketAddress?, p1: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun connect(p0: SocketAddress?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun connect(p0: SocketAddress?, p1: SocketAddress?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun connect(p0: SocketAddress?, p1: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun connect(p0: SocketAddress?, p1: SocketAddress?, p2: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun disconnect(): ChannelFuture {
        return DummyChannelFuture
    }

    override fun disconnect(p0: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun close(): ChannelFuture {
        return DummyChannelFuture
    }

    override fun close(p0: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun deregister(): ChannelFuture {
        return DummyChannelFuture
    }

    override fun deregister(p0: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun read(): ChannelHandlerContext {
        TODO("Not yet implemented")
    }

    override fun write(p0: Any?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun write(p0: Any?, p1: ChannelPromise?): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun flush(): ChannelHandlerContext {
        TODO("Not yet implemented")
    }

    override fun writeAndFlush(p0: Any?, p1: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun writeAndFlush(p0: Any?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun newPromise(): ChannelPromise {
        TODO("Not yet implemented")
    }

    override fun newProgressivePromise(): ChannelProgressivePromise {
        TODO("Not yet implemented")
    }

    override fun newSucceededFuture(): ChannelFuture {
        return DummyChannelFuture
    }

    override fun newFailedFuture(p0: Throwable?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun voidPromise(): ChannelPromise {
        TODO("Not yet implemented")
    }

    override fun channel(): Channel {
        TODO("Not yet implemented")
    }

    override fun executor(): EventExecutor {
        TODO("Not yet implemented")
    }

    override fun name(): String {
        TODO("Not yet implemented")
    }

    override fun handler(): ChannelHandler {
        TODO("Not yet implemented")
    }

    override fun isRemoved(): Boolean {
        TODO("Not yet implemented")
    }

    override fun pipeline(): ChannelPipeline {
        TODO("Not yet implemented")
    }

    override fun alloc(): ByteBufAllocator {
        TODO("Not yet implemented")
    }
}