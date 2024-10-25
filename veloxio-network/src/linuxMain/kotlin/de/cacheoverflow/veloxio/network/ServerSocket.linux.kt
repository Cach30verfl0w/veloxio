package de.cacheoverflow.veloxio.network

import de.cacheoverflow.veloxio.network.address.ServerSocket
import de.cacheoverflow.veloxio.network.eventloop.EventLoop
import de.cacheoverflow.veloxio.network.utils.allocSockaddr
import de.cacheoverflow.veloxio.network.utils.checkCall
import de.cacheoverflow.veloxio.network.utils.checkFd
import de.cacheoverflow.veloxio.network.utils.getSockaddrSize
import kotlinx.cinterop.memScoped
import platform.posix.bind
import platform.posix.listen

class LinuxServerSocket(
    override val eventLoop: EventLoop,
    socketAddress: SocketAddress,
    fileDescriptor: Int,
    backlog: Int = 100
) : ServerSocket {
    override val descriptor: SocketDescriptor = SocketDescriptor(fileDescriptor)
    override fun remoteAddress(): SocketAddress = throw UnsupportedOperationException()
    
    init {
        memScoped {
            val sockaddr = allocSockaddr(socketAddress)
            val sockaddrLength = socketAddress.getSockaddrSize().toUInt()
            checkCall(bind(fileDescriptor, sockaddr, sockaddrLength)) { "Unable to bind socket" }
            checkCall(listen(fileDescriptor, backlog)) { "Unable to listen socket" }
        }
        eventLoop.registerSocket(this)
    }
    
    override fun accept(eventLoop: EventLoop): Socket {
        return LinuxSocket(eventLoop, checkFd(platform.posix.accept(descriptor.fileDescriptor, null, null)) { "Unable to accept socket" })
    }
    
    override fun close() {
        eventLoop.unregisterSocket(this)
        platform.posix.close(descriptor.fileDescriptor)
    }
}
