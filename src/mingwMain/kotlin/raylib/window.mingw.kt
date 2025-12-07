@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.*
import platform.windows.*
import raylib.internal.rlglClose

internal actual val _isHeadless: Boolean
    get() = GetSystemMetrics(SM_CMONITORS) == 0

// for whatever dumb reason, the author of raylib decided to name it 'CloseWindow',
// which conflicts with the Win32 API function of the same name.
// thus we recreate the function here to avoid naming conflicts (very hacky!)
internal actual fun _close0() {
	if (Window.currentTitle == null) return

	rlglClose()

	// try and destroy the window, otherwise leave it up to the OS
	var hwnd = FindWindowA(null, Window.currentTitle)
	if (hwnd == null) {
		// try if the window is in foreground and belongs to this process
		val fg = GetForegroundWindow()
		if (fg != null) {
			memScoped {
				val pid = alloc<UIntVar>()
				GetWindowThreadProcessId(fg, pid.ptr)
				if (pid.value == GetCurrentProcessId()) {
					hwnd = fg
				}
			}
		}
	}

	// give up and leave it to the OS
	if (hwnd == null) return

	val hdc = GetDC(hwnd)
	val hglrc = wglGetCurrentContext()

	if (hglrc != null) {
		wglMakeCurrent(null, null)
		wglDeleteContext(hglrc)
	}

	if (hdc != null) {
		ReleaseDC(hwnd, hdc)
	}

	DestroyWindow(hwnd)
}
