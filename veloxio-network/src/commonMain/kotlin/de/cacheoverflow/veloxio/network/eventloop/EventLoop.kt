package de.cacheoverflow.veloxio.network.eventloop

import de.cacheoverflow.veloxio.network.channel.SocketChannel

/**
 * This interface provides an abstract API for event loops in Veloxio. These event loops are used to tell the client when data to read is
 * available or a socket can be accepted.
 *
 * @author Cedric Hammes
 * @since  26/10/2024
 */
interface EventLoop : AutoCloseable {
    fun addToEventLoop(channel: SocketChannel<*, *>)
    fun removeFromEventLoop(channel: SocketChannel<*, *>)
}