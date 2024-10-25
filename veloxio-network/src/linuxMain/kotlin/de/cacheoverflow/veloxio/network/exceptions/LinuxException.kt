package de.cacheoverflow.veloxio.network.exceptions

import kotlinx.cinterop.toKString
import platform.posix.errno
import platform.posix.strerror

class LinuxException : RuntimeException(errno.let { if (it == 0) "No errors captured" else requireNotNull(strerror(it)).toKString() })
