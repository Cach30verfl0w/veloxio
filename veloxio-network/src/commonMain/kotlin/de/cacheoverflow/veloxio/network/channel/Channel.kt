package de.cacheoverflow.veloxio.network.channel

import de.cacheoverflow.veloxio.network.annotations.InternalVeloxioAPI
import kotlinx.coroutines.flow.FlowCollector

/**
 * This interface represents the abstract API of a duplex channel that can handle both input and output. It allows the user to send data
 * to the peer or receive data from the peer.
 *
 * @param T The type of objects outgoing and incoming in this channel
 *
 * @author Cedric Hammes
 * @since  26/10/2024
 */
interface Channel<T> : ReadableChannel<T>, WritableChannel<T> {
    /**
     * This function creates a mapping channel. These mapping channels are used to construct a pipeline where the objects are being mapped
     * if incoming or outgoing down or up to a byte array.
     *
     * @param readClosure  The closure to map the incoming data into the new object
     * @param writeClosure The closure to map the outgoing object into the new data
     * @param R            The type of the data mapped for incoming data and being mapped into
     * @return             The channel with these closures applied for the mapping operations
     *
     * @author Cedric Hammes
     * @since  26/10/2024
     */
    fun <R> map(readClosure: (T) -> R, writeClosure: (R) -> T): Channel<R> = Mapped(this, readClosure, writeClosure)
    
    private class Mapped<A, B>(
        private val parent: Channel<A>,
        private val readClosure: (A) -> B,
        private val writeClosure: (B) -> A
    ): Channel<B> {
        @InternalVeloxioAPI
        override fun readAndSendToCollectors(): Int = parent.readAndSendToCollectors()
        override suspend fun collect(collector: FlowCollector<B>) = parent.collect { collector.emit(readClosure(it)) }
        override fun write(data: B) = parent.write(writeClosure(data))
        override fun close() = parent.close()
        override fun flush() = parent.flush()
    }
}
