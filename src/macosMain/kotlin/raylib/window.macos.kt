@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGMainDisplayID
import platform.CoreGraphics.CGSessionCopyCurrentDictionary
import platform.CoreGraphics.kCGNullDirectDisplay
import raylib.internal.CloseWindow

internal actual val _isHeadless: Boolean
	get() = CGMainDisplayID() == kCGNullDirectDisplay || CGSessionCopyCurrentDictionary() == null

internal actual fun _close0() {
	CloseWindow()
}
