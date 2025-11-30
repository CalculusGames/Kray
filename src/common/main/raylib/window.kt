@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import raylib.internal.*

/**
 * The window management object.
 */
object Window {

	/**
	 * The width of the current screen.
	 */
	val screenWidth: Int
		get() = GetScreenWidth()

	/**
	 * The height of the current screen.
	 */
	val screenHeight: Int
		get() = GetScreenHeight()

	/**
	 * The width of the render area.
	 */
	val renderWidth: Int
		get() = GetRenderWidth()

	/**
	 * The height of the render area.
	 */
	val renderHeight: Int
		get() = GetRenderHeight()

	/**
	 * Indicates whether the system is running in headless mode (no monitors connected).
	 */
	val isHeadless: Boolean
		get() = _isHeadless

	/**
	 * The number of monitors connected to the system.
	 */
	val monitorCount: Int
		get() = GetMonitorCount()

	/**
	 * The current monitor ID.
	 */
	val monitorId: Int
		get() = GetCurrentMonitor()

	/**
	 * The width of the current monitor.
	 */
	val monitorWidth: Int
		get() = GetMonitorWidth(monitorId)

	/**
	 * The height of the current monitor.
	 */
	val monitorHeight: Int
		get() = GetMonitorHeight(monitorId)

    /**
     * Initializes the window with the specified width, height, and title.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param title The title of the window.
     */
    fun open(width: Int, height: Int, title: String = "Raylib Window") {
        InitWindow(width, height, title)
    }

    /**
     * Closes the window.
     */
    fun close() {
        CloseWindow()
    }

    /**
     * Indicates whether the window has been initialized successfully.
     */
    val ready: Boolean
        get() = IsWindowReady()

    /**
     * Indicates whether the window is hidden from view.
     */
    val hidden: Boolean
        get() = IsWindowHidden()

    /**
     * Indicates whether the window is currently minimized.
     */
    val minimized: Boolean
        get() = IsWindowMinimized()

    /**
     * Indicates whether the window is currently maximized.
     */
    val maximized: Boolean
        get() = IsWindowMaximized()

    /**
     * Indicates whether the window is currently focused.
     */
    val focused: Boolean
        get() = IsWindowFocused()

    /**
     * Indicates whether the window has been resized in the last frame.
     */
    val wasResized: Boolean
        get() = IsWindowResized()

    /**
     * The various windows state flags available.
     */
    enum class State(internal val value: UInt) {
        /**
         * Set to try enabling V-Sync on GPU
         */
        VSYNC_HINT(0x00000040u),
        /**
         * Set to run program in fullscreen
         */
        FULLSCREEN_MODE(0x00000002u),
        /**
         * Set to allow resizable window
         */
        WINDOW_RESIZABLE(0x00000004u),
        /**
         * Set to disable window decoration (frame and buttons)
         */
        WINDOW_UNDECORATED(0x00000008u),
        /**
         * Set to hide window
         */
        WINDOW_HIDDEN(0x00000080u),
        /**
         * Set to minimize window (iconify)
         */
        WINDOW_MINIMIZED(0x00000200u),
        /**
         * Set to maximize window (expanded to monitor)
         */
        WINDOW_MAXIMIZED(0x00000400u),
        /**
         * Set the window to be non-focused
         */
        WINDOW_UNFOCUSED(0x00000800u),
        /**
         * Set the window to be always on top
         */
        WINDOW_TOPMOST(0x00001000u),
        /**
         * Set to allow windows running while minimized
         */
        WINDOW_ALWAYS_RUN(0x00000100u),
        /**
         * Set to allow transparent framebuffer
         */
        WINDOW_TRANSPARENT(0x00000010u),
        /**
         * Set to support HighDPI
         */
        WINDOW_HIGHDPI(0x00002000u),
        /**
         * Set to support mouse passthrough, only supported when WINDOW_UNDECORATED
         */
        WINDOW_MOUSE_PASSTHROUGH(0x00004000u),
        /**
         * Set to run program in borderless windowed mode
         */
        BORDERLESS_WINDOWED_MODE(0x00008000u),
        /**
         * Set to try enabling MSAA 4X
         */
        MSAA_4X_HINT(0x00000020u),
        /**
         * Set to try enabling interlaced video format (for V3D)
         */
        INTERLACED_HINT(0x00010000u)

        ;

        internal companion object {
            fun from(values: UInt): Set<State> {
                return entries.filter { (it.value and values) != 0u }.toSet()
            }
        }
    }

    /**
     * Determines if a specific window flag is enabled.
     * @param flag The window state flag to check.
     * @return True if the flag is enabled, false otherwise.
     */
    fun isFlagEnabled(flag: State): Boolean {
        return IsWindowState(flag.value)
    }

    /**
     * Sets specific window state flags.
     * @param flags The set of window state flags to enable.
     */
    fun setFlags(flags: Set<State>) {
        val combined = flags.fold(0u) { acc, state -> acc or state.value }
        SetWindowState(combined)
    }

    /**
     * The state of fullscreen on the window.
     */
    var fullscreen: Boolean
        get() = IsWindowFullscreen()
        set(value) {
            val state = IsWindowFullscreen()
            if (value != state) ToggleFullscreen()
        }

    /**
     * Toggles the fullscreen state of the window.
     */
    fun toggleFullscreen() {
        ToggleFullscreen()
    }

    /**
     * Toggles the borderless windowed mode of the window.
     */
    fun toggleBorderlessWindowed() {
        ToggleBorderlessWindowed()
    }

    /**
     * Minimizes the window.
     */
    fun minimize() {
        MinimizeWindow()
    }

    /**
     * Maximizes the window.
     */
    fun maximize() {
        MaximizeWindow()
    }

    /**
     * Restores the window to its normal state that isn't minimized or maximized.
     */
    fun restore() {
        RestoreWindow()
    }

    /**
     * The system clipboard text.
     */
    var clipboardText: String?
        get() = GetClipboardText()?.toKString()
        set(value) = SetClipboardText(value)

}

/**
 * The current cursor state.
 */
object Cursor {

    /**
     * Whether the cursor is enabled on the screen.
     */
    var isCursorEnabled: Boolean
        get() = IsCursorOnScreen()
        set(value) {
            if (value) EnableCursor() else DisableCursor()
        }

    /**
     * Whether the cursor is hidden.
     */
    var isCursorHidden: Boolean
        get() = IsCursorHidden()
        set(value) {
            if (value) HideCursor() else ShowCursor()
        }

}

// Expect

internal expect val _isHeadless: Boolean
