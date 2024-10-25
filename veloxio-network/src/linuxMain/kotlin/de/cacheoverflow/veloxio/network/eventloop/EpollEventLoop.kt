package de.cacheoverflow.veloxio.network.eventloop

import de.cacheoverflow.veloxio.network.address.ServerSocket
import de.cacheoverflow.veloxio.network.Socket
import de.cacheoverflow.veloxio.network.exceptions.EpollException
import de.cacheoverflow.veloxio.network.utils.checkCall
import de.cacheoverflow.veloxio.network.utils.checkFd
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import platform.linux.*

class EpollEventLoop(coroutineScope: CoroutineScope, logger: KLogger, maxEventCount: Int = 50) : AbstractEventLoop(coroutineScope, logger) {
    private val registeredSockets: MutableMap<Int, Socket> = mutableMapOf()
    private val epollDescriptor: Int = checkFd(epoll_create1(EPOLL_CLOEXEC), ::EpollException) { "Unable to create epoll descriptor" }
    private val epollEventQueue: CArrayPointer<epoll_event> = nativeHeap.allocArray<epoll_event>(maxEventCount)
    
    override suspend fun waitForEvents(): List<Event> =
        checkCall(epoll_wait(epollDescriptor, epollEventQueue, 10, -1), ::EpollException) { "Unable to wait for events" }.let { count ->
            val polledEvents = mutableListOf<Event>()
            for (i in 0..<count) {
                val polledEvent = epollEventQueue[i]
                val socket = requireNotNull(registeredSockets[polledEvent.data.fd]) { "Unable to get socket from polled event" }
                if (socket is ServerSocket) {
                    polledEvents.add(Event(socket, Event.EnumType.INCOMING_SOCKET))
                    continue
                }
                
                // Handle incoming data event
                if (polledEvent.events and EPOLLIN.convert() == 1U)
                    polledEvents.add(Event(socket, Event.EnumType.INCOMING_DATA))
            }
            polledEvents
        }
    
    override fun registerSocket(socket: Socket): Unit = memScoped {
        val epollEvent = alloc<epoll_event>()
        epollEvent.data.fd = socket.descriptor.fileDescriptor
        epollEvent.events = (EPOLLIN or EPOLLOUT or EPOLLET.convert()).convert()
        checkCall(
            epoll_ctl(epollDescriptor, EPOLL_CTL_ADD, epollEvent.data.fd, epollEvent.ptr),
            ::EpollException
        ) { "Unable to add socket to epoll" }
        registeredSockets[socket.descriptor.fileDescriptor] = socket
    }
    
    override fun unregisterSocket(socket: Socket) {
        checkCall(
            epoll_ctl(epollDescriptor, EPOLL_CTL_DEL, socket.descriptor.fileDescriptor, null),
            ::EpollException
        ) { "Unable to add socket to epoll" }
        registeredSockets.remove(socket.descriptor.fileDescriptor)
    }
    
    override fun close() {
        nativeHeap.free(epollEventQueue)
        platform.posix.close(epollDescriptor)
    }
}
