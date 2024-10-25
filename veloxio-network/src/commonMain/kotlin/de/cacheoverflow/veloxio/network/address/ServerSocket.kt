package de.cacheoverflow.veloxio.network.address

import de.cacheoverflow.veloxio.network.Socket
import de.cacheoverflow.veloxio.network.eventloop.EventLoop

interface ServerSocket : Socket {
    fun accept(eventLoop: EventLoop): Socket
}
