@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.getenv

internal actual val _isHeadless: Boolean
    get() = getenv("DISPLAY") == null || getenv("WAYLAND_DISPLAY") == null
