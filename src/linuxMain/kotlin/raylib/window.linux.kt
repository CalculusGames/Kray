@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.getenv
import raylib.internal.CloseWindow

internal actual val _isHeadless: Boolean
    get() = getenv("DISPLAY") == null || getenv("WAYLAND_DISPLAY") == null

internal actual fun _close0() {
	_closed0 = true
	CloseWindow()
}
internal actual var _closed0: Boolean = false
