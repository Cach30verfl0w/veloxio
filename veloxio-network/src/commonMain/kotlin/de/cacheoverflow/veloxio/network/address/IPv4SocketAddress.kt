package de.cacheoverflow.veloxio.network.address

import de.cacheoverflow.veloxio.network.EnumFamily
import de.cacheoverflow.veloxio.network.SocketAddress

class IPv4SocketAddress(val address: String, val port: UShort) : SocketAddress {
    override val family: EnumFamily = EnumFamily.IPv4
    override fun toString(): String = "$address:$port"
}
