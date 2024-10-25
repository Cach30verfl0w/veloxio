package de.cacheoverflow.veloxio.network.eventloop

import de.cacheoverflow.veloxio.network.Socket
import io.github.oshai.kotlinlogging.KLogger

/**
 * This interface is the abstract event loop API for recording socket events like input or output, socket accept etc. It allows the library
 * to provide non-blocking I/O for sockets. The developer needs an event loop for the creation of server or client sockets.
 *
 * @author Cedric Hammes
 * @since  25/10/2024
 */
interface EventLoop : AutoCloseable {
    val logger: KLogger
    
    /**
     * This function is used to register a socket into the event loop job of this event loop. This is used to manage the events of the
     * socket with this loop.
     *
     * @param socket The socket to register into the event loop
     *
     * @author Cedric Hammes
     * @since  25/10/2024
     */
    fun registerSocket(socket: Socket)
    
    /**
     * This function is used to unregister a socket from the event loop job of this event loop. This is used to remove the management of
     * the socket's event from this event loop.
     *
     * @param socket The socket to unregister from the event loop
     *
     * @author Cedric Hammes
     * @since  25/10/2024
     */
    fun unregisterSocket(socket: Socket)
}