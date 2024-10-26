package de.cacheoverflow.veloxio.network.channel

import de.cacheoverflow.veloxio.network.Address

/**
 * This interface provides an abstract API for socket channels. These channels are directly associated with sockets created by the user of
 * the library.
 *
 * @param I The incoming data type of the channel
 * @param O The outgoing data type of the channel
 *
 * @author Cedric Hammes
 * @since  27/10/2024
 */
interface SocketChannel<I, O> : DataChannel<I, O> {
    val descriptor: ChannelDescriptor
    fun bind(address: Address)
    fun connect(address: Address)
    fun isServerChannel(): Boolean
}
