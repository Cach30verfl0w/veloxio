package de.cacheoverflow.veloxio.network.channel

import de.cacheoverflow.veloxio.network.annotations.InternalVeloxioAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * This interface provides an abstract API for handling incoming data. This incoming data can be mapped into new types for data
 * processing/serialization.
 *
 * @param T The type of objects incoming through this channel
 *
 * @author Cedric Hammes
 * @since  26/10/2024
 */
interface ReadableChannel<T> : Flow<T>, AutoCloseable {
    /**
     * This function directly reads the native channel for data and sends the read data to the collectors attached to the channel's flow to
     * distribute the data to the channel's collectors.
     *
     * @return The count of bytes read by this function
     *
     * @author Cedric Hammes
     * @since  26/10/2024
     */
    @InternalVeloxioAPI
    fun readAndSendToCollectors(): Int
    
    /**
     * This function creates a new channel mapping the input of the readable channel to this channel. It allows devs to create a pipeline
     * serializing data.
     *
     * @param closure The closure for the mapped from T to R
     * @param R       The type of the channel created
     * @return        The channel with a type mapped to R
     *
     * @author Cedric Hammes
     * @since  26/10/2024
     */
    fun <R> map(closure: (T) -> R): ReadableChannel<R> = Mapped(this, closure)
    
    class Mapped<I, R>(
        private val parent: ReadableChannel<I>,
        private val closure: (I) -> R
    ): ReadableChannel<R> {
        @InternalVeloxioAPI
        override fun readAndSendToCollectors(): Int = parent.readAndSendToCollectors()
        override suspend fun collect(collector: FlowCollector<R>) = parent.collect { collector.emit(closure(it)) }
        override fun close() = parent.close()
    }
}
