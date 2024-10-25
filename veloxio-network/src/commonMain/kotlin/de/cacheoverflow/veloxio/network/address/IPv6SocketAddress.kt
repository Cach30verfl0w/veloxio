package de.cacheoverflow.veloxio.network.address

import de.cacheoverflow.veloxio.network.EnumFamily
import de.cacheoverflow.veloxio.network.SocketAddress

class IPv6SocketAddress(val address: String, val port: UShort) : SocketAddress {
    override val family: EnumFamily = EnumFamily.IPv6
    override fun toString(): String = "$address:$port"
}
