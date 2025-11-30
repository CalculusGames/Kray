package raylib

import platform.CoreGraphics.CGMainDisplayID

internal actual val _isHeadless: Boolean
	get() = CGMainDisplayID() == 0u
