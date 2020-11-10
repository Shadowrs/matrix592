package com.rs.network

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.util.Attribute
import io.netty.util.AttributeKey
import java.net.InetSocketAddress
import java.net.SocketAddress

object DummyChannel : Channel {

    val DUMMY_SOCKET = InetSocketAddress(0)

    override fun <T : Any?> attr(p0: AttributeKey<T>?): Attribute<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> hasAttr(p0: AttributeKey<T>?): Boolean {
        TODO("Not yet implemented")
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

    override fun read(): Channel {
        TODO("Not yet implemented")
    }

    override fun write(p0: Any?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun write(p0: Any?, p1: ChannelPromise?): ChannelFuture {
        return DummyChannelFuture
    }

    override fun flush(): Channel {
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

    override fun compareTo(other: Channel?): Int {
        return 1
    }

    override fun id(): ChannelId {
        TODO("Not yet implemented")
    }

    override fun eventLoop(): EventLoop {
        TODO("Not yet implemented")
    }

    override fun parent(): Channel {
        TODO("Not yet implemented")
    }

    override fun config(): ChannelConfig {
        TODO("Not yet implemented")
    }

    override fun isOpen(): Boolean {
        return true
    }

    override fun isRegistered(): Boolean {
        return true
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun metadata(): ChannelMetadata {
        TODO("Not yet implemented")
    }

    override fun localAddress(): SocketAddress {
        return DUMMY_SOCKET
    }

    override fun remoteAddress(): SocketAddress {
        return DUMMY_SOCKET
    }

    override fun closeFuture(): ChannelFuture {
        return DummyChannelFuture
    }

    override fun isWritable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun bytesBeforeUnwritable(): Long {
        TODO("Not yet implemented")
    }

    override fun bytesBeforeWritable(): Long {
        TODO("Not yet implemented")
    }

    override fun unsafe(): Channel.Unsafe {
        TODO("Not yet implemented")
    }

    override fun pipeline(): ChannelPipeline {
        TODO("Not yet implemented")
    }

    override fun alloc(): ByteBufAllocator {
        TODO("Not yet implemented")
    }
}