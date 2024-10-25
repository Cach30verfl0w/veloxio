package de.cacheoverflow.veloxio.network

import de.cacheoverflow.veloxio.network.eventloop.EventLoop
import de.cacheoverflow.veloxio.network.utils.checkCall
import de.cacheoverflow.veloxio.network.utils.fromSockaddrStorage
import kotlinx.cinterop.*
import platform.posix.getpeername
import platform.posix.sockaddr_storage

actual class SocketDescriptor(val fileDescriptor: Int)

class LinuxSocket(override val eventLoop: EventLoop, fileDescriptor: Int) : Socket {
    override val descriptor: SocketDescriptor = SocketDescriptor(fileDescriptor)
    
    init {
        eventLoop.registerSocket(this)
    }
    
    override fun remoteAddress(): SocketAddress = memScoped {
        val sockaddr = alloc<sockaddr_storage>()
        val size = alloc(sizeOf<sockaddr_storage>().toUInt())
        checkCall(getpeername(descriptor.fileDescriptor, sockaddr.ptr.reinterpret(), size.ptr)) { "Unable to get peer's address" }
        return SocketAddress.fromSockaddrStorage(sockaddr)
    }
    
    override fun close() {
        eventLoop.unregisterSocket(this)
        platform.posix.close(descriptor.fileDescriptor)
    }
}
