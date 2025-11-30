package raylib

import platform.windows.GetSystemMetrics
import platform.windows.SM_CMONITORS

internal actual val _isHeadless: Boolean
    get() = GetSystemMetrics(SM_CMONITORS) == 0
