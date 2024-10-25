package de.cacheoverflow.veloxio.network.utils

import de.cacheoverflow.veloxio.network.exceptions.LinuxException

internal fun checkFd(value: Int, errorFactory: (String, Throwable) -> Exception = ::RuntimeException, errorClosure: () -> String): Int =
    if (value < 0) throw errorFactory("${errorClosure()} ($value)", LinuxException()) else value

internal fun checkCall(value: Int, errorFactory: (String, Throwable) -> Exception = ::RuntimeException, errorClosure: () -> String): Int =
    if (value < 0) throw errorFactory("${errorClosure()} ($value)", LinuxException()) else value
