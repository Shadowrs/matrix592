package com.rs.network

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.TimeUnit

object DummyChannelFuture : ChannelFuture {

    override fun cancel(p0: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCancelled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isDone(): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(): Void {
        TODO("Not yet implemented")
    }

    override fun get(timeout: Long, unit: TimeUnit): Void {
        TODO("Not yet implemented")
    }

    override fun isSuccess(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCancellable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cause(): Throwable {
        TODO("Not yet implemented")
    }

    override fun addListener(p0: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun addListeners(vararg p0: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun removeListener(p0: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun removeListeners(vararg p0: GenericFutureListener<out Future<in Void>>?): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun sync(): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun syncUninterruptibly(): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun await(): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun await(p0: Long, p1: TimeUnit?): Boolean {
        TODO("Not yet implemented")
    }

    override fun await(p0: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun awaitUninterruptibly(): ChannelFuture {
        TODO("Not yet implemented")
    }

    override fun awaitUninterruptibly(p0: Long, p1: TimeUnit?): Boolean {
        TODO("Not yet implemented")
    }

    override fun awaitUninterruptibly(p0: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun getNow(): Void {
        TODO("Not yet implemented")
    }

    override fun channel(): Channel {
        TODO("Not yet implemented")
    }

    override fun isVoid(): Boolean {
        TODO("Not yet implemented")
    }

}