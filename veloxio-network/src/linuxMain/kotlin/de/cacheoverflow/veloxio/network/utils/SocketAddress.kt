package de.cacheoverflow.veloxio.network.utils

import de.cacheoverflow.veloxio.network.EnumFamily
import de.cacheoverflow.veloxio.network.SocketAddress
import de.cacheoverflow.veloxio.network.address.IPv4SocketAddress
import de.cacheoverflow.veloxio.network.address.IPv6SocketAddress
import kotlinx.cinterop.*
import platform.linux.inet_ntop
import platform.linux.inet_pton
import platform.posix.*

fun EnumFamily.Companion.fromPosixFamily(family: Int): EnumFamily = when(family) {
    AF_INET -> EnumFamily.IPv4
    AF_INET6 -> EnumFamily.IPv6
    else -> throw IllegalArgumentException("Unknown family $family")
}

val EnumFamily.posixFamily: Int
    get() = when(this) {
        EnumFamily.IPv4 -> AF_INET
        EnumFamily.IPv6 -> AF_INET6
    }

val EnumFamily.posixAddressLength: Int
    get() = when(this) {
        EnumFamily.IPv4 -> INET_ADDRSTRLEN
        EnumFamily.IPv6 -> INET6_ADDRSTRLEN
    }

fun SocketAddress.Companion.fromSockaddrStorage(sockaddrStorage: sockaddr_storage): SocketAddress {
    when (val family = EnumFamily.fromPosixFamily(sockaddrStorage.ss_family.convert())) {
        EnumFamily.IPv4 -> {
            val sockaddr = sockaddrStorage.reinterpret<sockaddr_in>()
            val addressBuffer = ByteArray(family.posixAddressLength)
            addressBuffer.usePinned { pinned ->
                inet_ntop(sockaddrStorage.ss_family.toInt(), sockaddr.sin_addr.ptr, pinned.addressOf(0), addressBuffer.size.toUInt())
            }
            return IPv4SocketAddress(addressBuffer.toKString(), htonl(sockaddr.sin_port.toUInt()).toUShort())
        }
        EnumFamily.IPv6 -> {
            val sockaddr = sockaddrStorage.reinterpret<sockaddr_in6>()
            val addressBuffer = ByteArray(family.posixAddressLength)
            addressBuffer.usePinned { pinned ->
                inet_ntop(sockaddrStorage.ss_family.toInt(), sockaddr.sin6_addr.ptr, pinned.addressOf(0), addressBuffer.size.toUInt())
            }
            return IPv6SocketAddress(addressBuffer.toKString(), htonl(sockaddr.sin6_port.toUInt()).toUShort())
        }
    }
}

fun SocketAddress.getSockaddrSize(): Long = when(family) {
    EnumFamily.IPv4 -> sizeOf<sockaddr_in>()
    EnumFamily.IPv6 -> sizeOf<sockaddr_in6>()
}

fun NativePlacement.allocSockaddr(socketAddress: SocketAddress): CPointer<sockaddr> = when(socketAddress) {
    is IPv4SocketAddress -> alloc<sockaddr_in>().also { sockaddr ->
        sockaddr.sin_family = AF_INET.convert()
        inet_pton(socketAddress.family.posixFamily, socketAddress.address, sockaddr.sin_addr.ptr)
        sockaddr.sin_port = htons(socketAddress.port)
    }.reinterpret<sockaddr>().ptr
    is IPv6SocketAddress -> alloc<sockaddr_in6>().also { sockaddr ->
        sockaddr.sin6_family = AF_INET6.convert()
        inet_pton(socketAddress.family.posixFamily, socketAddress.address, sockaddr.sin6_addr.ptr)
        sockaddr.sin6_port = htons(socketAddress.port)
    }.reinterpret<sockaddr>().ptr
    else -> throw UnsupportedOperationException("Invalid socket address")
}
