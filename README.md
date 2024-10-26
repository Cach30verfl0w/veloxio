# Veloxio - Socket Framework
A framework, inspired by Netty, for developing fast networking applications in Kotlin Multiplatform applications. It allows to send data with UDP and TCP or use application-level protocols like HTTP. This complete framework is a experiment and should not be used for applications running in production environments.

## ToDo
This section of the readme contains a todo of features planned for Veloxio. These features are not ensured to be implemented in the future but if I don't, contributions are always welcome.
- [ ] Implementation of basic sockets on Linux with input and output channel
- [ ] Implementation of basic server sockets on Linux with input and output channel
- [ ] Support for event loops with Epoll or io_uring on Linux/Unix systems
- [ ] Support for event loops with Kqueue on BSD/macOS systems
- [ ] Support for alternative event loops like basic NIO loops
- [ ] Support for JVM (over Netty or NIO), windows and darwin targets