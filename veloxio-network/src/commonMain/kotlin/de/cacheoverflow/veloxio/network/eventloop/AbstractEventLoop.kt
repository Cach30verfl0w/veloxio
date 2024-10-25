package de.cacheoverflow.veloxio.network.eventloop

import de.cacheoverflow.veloxio.network.address.ServerSocket
import de.cacheoverflow.veloxio.network.Socket
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.*

abstract class AbstractEventLoop(coroutineScope: CoroutineScope, override val logger: KLogger) : EventLoop {
    private val eventLoopJob: Job = coroutineScope.launch(CoroutineName("EventLoopJob") + CoroutineExceptionHandler { _, throwable ->
        logger.error(throwable) { "Exception caught while running event loop job" }
    }) {
        while (true) {
            // Wait for events and handle them
            for (event in waitForEvents()) {
                when (event.type) {
                    Event.EnumType.INCOMING_SOCKET -> {
                        val socket = (event.socket as ServerSocket).accept(this@AbstractEventLoop)
                        logger.info { "Accepted socket connection from ${socket.remoteAddress()}" }
                    }
                    
                    Event.EnumType.INCOMING_DATA -> {
                        logger.info { "Incoming data" }
                    }
                }
            }
        }
    }
    
    /**
     * This function await for new events and returns a list of them. This function is blocking.
     *
     * @return The list of events polled
     *
     * @author Cedric Hammes
     * @since  24/10/2024
     */
    abstract suspend fun waitForEvents(): List<Event>
    
    override fun close() {
        eventLoopJob.cancel(CancellationException("EventLoop closed"))
    }
    
    /**
     * @author Cedric Hammes
     * @since  24/10/2024
     */
    data class Event(val socket: Socket, val type: EnumType) {
        enum class EnumType {
            INCOMING_DATA,
            INCOMING_SOCKET
        }
    }
}
