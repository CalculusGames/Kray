@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.*
import platform.windows.*
import raylib.internal.ClosePlatform
import raylib.internal.LOG_INFO
import raylib.internal.TraceLog
import raylib.internal.rlglClose

internal actual val _isHeadless: Boolean
    get() = GetSystemMetrics(SM_CMONITORS) == 0

// for whatever dumb reason, the author of raylib decided to name it 'CloseWindow',
// which conflicts with the Win32 API function of the same name.
// thus we recreate the function here to avoid naming conflicts (very hacky!)
internal actual fun _close0() {
	if (Window.currentTitle == null) return

	_closed0 = true
	rlglClose()
	ClosePlatform()
	TraceLog(LOG_INFO.toInt(), "Window closed successfully")
}

internal actual var _closed0: Boolean = false
