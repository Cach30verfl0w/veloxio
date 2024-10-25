package de.cacheoverflow.veloxio.network

import de.cacheoverflow.veloxio.network.eventloop.EventLoop

expect class SocketDescriptor

interface Socket : AutoCloseable {
    val eventLoop: EventLoop
    val descriptor: SocketDescriptor
    fun remoteAddress(): SocketAddress
}
