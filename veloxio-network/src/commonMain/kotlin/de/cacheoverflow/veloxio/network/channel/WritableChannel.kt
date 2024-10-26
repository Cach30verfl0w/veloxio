package de.cacheoverflow.veloxio.network.channel

/**
 * This interface provides an abstract API for handling outgoing data. This outgoing data can be mapped into new types for data
 * processing/serialization.
 *
 * @param T The type of objects outgoing through this channel
 *
 * @author Cedric Hammes
 * @since  26/10/2024
 */
interface WritableChannel<T> : AutoCloseable {
    /**
     * This function writes the specified data into the channel/temporary buffer of the channel. This data is a byte array or being mapped
     * down to byte array.
     *
     * @param data The data to write to the channel
     *
     * @author Cedric Hammes
     * @since  26/10/2024
     */
    fun write(data: T)
    
    /**
     * This function flushes the data of the buffer and writes the data of the buffer into the network channel to send the data to the
     * connection's peer.
     *
     * @author Cedric Hammes
     * @since  26/10/2024
     */
    fun flush()
    
    /**
     * This function creates a new channel mapping the output of this channel to the parent channel. It allows devs to create a pipeline
     * serializing data.
     *
     * @param closure The closure for the mapped from T to R
     * @param R       The type of the channel created
     * @return        The channel with a type mapped to R
     *
     * @author Cedric Hammes
     * @since  26/10/2024
     */
    fun <R> map(closure: (R) -> T): WritableChannel<R> = Mapped(this, closure)

    class Mapped<I, R>(
        private val parent: WritableChannel<R>,
        private val closure: (I) -> R
    ): WritableChannel<I> {
        override fun write(data: I) = parent.write(closure(data))
        override fun flush() = parent.flush()
        override fun close() = parent.close()
    }
}
