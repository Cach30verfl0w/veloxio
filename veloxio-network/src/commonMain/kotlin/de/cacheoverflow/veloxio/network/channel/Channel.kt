package de.cacheoverflow.veloxio.network.channel

import de.cacheoverflow.veloxio.network.annotations.InternalVeloxioAPI
import kotlinx.coroutines.flow.FlowCollector

/**
 * This interface represents the abstract API of a duplex channel that can handle both input and output. It allows the user to send data
 * to the peer or receive data from the peer.
 *
 * @param I The incoming object type of the channel
 * @param O The outgoing object type of the channel
 *
 * @author Cedric Hammes
 * @since  26/10/2024
 */
interface Channel<I, O> : ReadableChannel<I>, WritableChannel<O> {
    fun <IR, OR> map(readClosure: (I) -> IR, writeClosure: (OR) -> O): Channel<IR, OR> = Mapped(this, readClosure, writeClosure)
    
    private class Mapped<IA, IB, OA, OB>(
        private val parent: Channel<IA, OA>,
        private val readClosure: (IA) -> IB,
        private val writeClosure: (OB) -> OA
    ): Channel<IB, OB> {
        @InternalVeloxioAPI
        override fun readAndSendToCollectors(): Int = parent.readAndSendToCollectors()
        override suspend fun collect(collector: FlowCollector<IB>) = parent.collect { collector.emit(readClosure(it)) }
        override fun write(data: OB) = parent.write(writeClosure(data))
        override fun close() = parent.close()
        override fun flush() = parent.flush()
    }
}
