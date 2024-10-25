import de.cacheoverflow.veloxio.network.LinuxServerSocket
import de.cacheoverflow.veloxio.network.address.IPv4SocketAddress
import de.cacheoverflow.veloxio.network.eventloop.EpollEventLoop
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import platform.posix.AF_INET
import platform.posix.SOCK_STREAM
import platform.posix.sleep
import platform.posix.socket
import kotlin.test.Test

class Tests {
    
    @Test
    fun test() {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val eventLoop = EpollEventLoop(coroutineScope, KotlinLogging.logger("EventLoop"))
        LinuxServerSocket(eventLoop, IPv4SocketAddress("127.0.0.1", 1337U), socket(AF_INET, SOCK_STREAM, 0)).use {
            sleep(10U)
        }
        coroutineScope.cancel(CancellationException("Server stopped"))
    }
    
}
