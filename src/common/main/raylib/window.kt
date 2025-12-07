@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.*
import kray.to
import kray.toVector3
import platform.posix.getenv
import platform.posix.usleep
import raylib.Canvas.end
import raylib.Canvas.start
import raylib.Window.lifecycle
import raylib.Window.open
import raylib.Window.shouldClose
import raylib.internal.*

/**
 * The window management object.
 */
object Window {

	/**
	 * The current window title.
	 */
	var currentTitle: String? = null
		internal set

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
	 *
	 * Note: macOS headless detection is not 100% reliable.
	 */
	val isHeadless: Boolean
		get() = _isHeadless || getenv("CI") != null || getenv("HEADLESS") != null

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
	 * The X position of the current monitor in pixels.
	 *
	 * This determines the monitor's position in a multi-monitor setup. For example,
	 * if a monitor is positioned to the left of the primary monitor, this value may be negative.
	 */
	val monitorX: Float
		get() = GetMonitorPosition(monitorId).useContents { x }

	/**
	 * The Y position of the current monitor in pixels.
	 *
	 * This determines the monitor's position in a multi-monitor setup. For example,
	 * if a monitor is positioned above the primary monitor, this value may be negative.
	 */
	val monitorY: Float
		get() = GetMonitorPosition(monitorId).useContents { y }

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
	 * The physical width of the current monitor in millimeters.
	 */
	val monitorPhysicalWidth: Int
		get() = GetMonitorPhysicalWidth(monitorId)

	/**
	 * The physical height of the current monitor in millimeters.
	 */
	val monitorPhysicalHeight: Int
		get() = GetMonitorPhysicalHeight(monitorId)

	/**
	 * The current refresh rate of the monitor in Hz.
	 */
	val monitorRefreshRate: Int
		get() = GetMonitorRefreshRate(monitorId)

	/**
	 * The name of the current monitor.
	 */
	val monitorName: String?
		get() = GetMonitorName(monitorId)?.toKString()

    /**
     * Initializes the window with the specified width, height, and title.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param title The title of the window.
	 * @param loop Optional lifecycle loop (see [lifecycle])
	 * @param targetFps The target FPS to set for the game (defaults to 60 when [loop] is provided)
     */
	fun open(
		width: Int,
		height: Int,
		title: String = "Raylib Window",
		loop: (Window.() -> Unit)? = null,
		targetFps: Int? = null
	) {
        InitWindow(width, height, title)
		currentTitle = title

		if (targetFps != null) {
			this.fps = targetFps
		}

		if (loop != null) {
			if (targetFps == null) this.fps = 60
			lifecycle(loop)
		}
    }

	/**
	 * Runs a game loop while [shouldClose] is false.
	 *
	 * This should serve as the main entrypoint to your game. The function will be called repeatedly until
	 * the game closes.
	 * @param loop The game loop to call until the window should be closed
	 */
	fun lifecycle(loop: Window.() -> Unit) {
		while (!shouldClose) {
			loop()
		}
	}

	/**
	 * The X position of the window on the screen.
	 */
	val windowX: Float
		get() = GetWindowPosition().useContents { x }

	/**
	 * The Y position of the window on the screen.
	 */
	val windowY: Float
		get() = GetWindowPosition().useContents { y }

	/**
	 * The current scale factor of the window on the X axis for HighDPI support.
	 */
	val windowScaleDPIX: Float
		get() = GetWindowScaleDPI().useContents { x }

	/**
	 * The current scale factor of the window on the Y axis for HighDPI support.
	 */
	val windowScaleDPIY: Float
		get() = GetWindowScaleDPI().useContents { y }

    /**
     * Closes the window.
     */
    fun close() {
		_close0()
    }

    /**
     * Indicates whether the window has been initialized successfully.
     */
    val ready: Boolean
        get() = IsWindowReady()

	/**
	 * Whether the window should close.
	 *
	 * This is determined by whether the 'X' button was clicked, the computer is shutting down,
	 * or other triggers that would cause the window to close.
	 */
	val shouldClose: Boolean
		get() = WindowShouldClose()

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

	/**
	 * The image stored in the system clipboard.
	 */
	val clipboardImage: Image
		get() = Image(GetClipboardImage())

	/**
	 * Enables waiting for events when canvas drawing is finished.
	 */
	fun enableEventWaiting() {
		EnableEventWaiting()
	}

	/**
	 * Disables waiting for events when canvas drawing is finished.
	 */
	fun disableEventWaiting() {
		DisableEventWaiting()
	}

	/**
	 * The number of seconds since [open] was called.
	 */
	val time: Double
		get() = GetTime()

	/**
	 * The target frames per second in the window.
	 *
	 * The getter will return the current FPS; setter will set the target FPS.
	 */
	var fps: Int
		get() = GetFPS()
		set(value) = SetTargetFPS(value)

	/**
	 * Delays execution for the specified number of milliseconds.
	 * @param ms The number of milliseconds to delay.
	 */
	fun delay(ms: Int) {
		usleep(ms.toUInt() * 1000u)
	}

	/**
	 * Gets the color of the pixel at the specified screen coordinates.
	 * @param x The X coordinate of the pixel.
	 * @param y The Y coordinate of the pixel.
	 * @return The color of the pixel at the specified coordinates.
	 */
	fun getColor(x: Int, y: Int): Color {
		val img = LoadImageFromScreen()
		val color = GetImageColor(img, x, y)
		UnloadImage(img)

		return Color(color)
	}

}

/**
 * The canvas management object.
 */
object Canvas {

	/**
	 * Whether the canvas is currently in a drawing state.
	 */
	var inDrawingState: Boolean = false
		private set

	/**
	 * Sets the background color of the canvas.
	 * @param color The color to set as the background.
	 */
	fun setBackgroundColor(color: Color) {
		ClearBackground(color.raw())
	}

	/**
	 * Begins the drawing process on the canvas.
	 *
	 * Note that [end] must be called after finishing drawing to finalize.
	 */
	fun start() {
		if (inDrawingState) return
		BeginDrawing()
		inDrawingState = true
	}

	/**
	 * Ends the drawing process on the canvas.
	 */
	fun end() {
		if (!inDrawingState) return
 		EndDrawing()
		inDrawingState = false
	}

	/**
	 * Draws on the canvas using the provided callback function.
	 *
	 * [start] and [end] are called automatically.
	 * @param callback The drawing operations to perform on the canvas.
	 */
	fun draw(callback: Canvas.() -> Unit) {
		if (!inDrawingState) start()
		this.callback()
		if (inDrawingState) end()
	}

}

/**
 * Represents a 2D Camera in raylib.
 * @property delta Displacement from the target position
 * @property target The camera's target and the origin of the rotation and zoom
 * @property rotation Camera rotation in degrees
 * @property zoom Camera zoom scale
 */
class Camera2D(
	var delta: Pair<Float, Float> = 0F to 0F,
	var target: Pair<Float, Float> = 0F to 0F,
	var rotation: Float = 0F,
	var zoom: Float = 1.0F
) {

	/**
	 * Creates a new 2D Camera.
	 * @param dx The X coordinate of the displacement from the target
	 * @param dy The Y coordinate of the displacement from the target
	 * @param targetX The X coordinate of the camera's target
	 * @param targetY The Y coordinate of the camera's target
	 * @param rotation Camera rotation in radians
	 * @param zoom Camera zoom scale
	 */
	constructor(
		dx: Float = 0F, dy: Float = 0F,
		targetX: Float = 0F, targetY: Float = 0F,
		rotation: Float = 0F, zoom: Float = 1F
	) : this(
		dx to dy,
		targetX to targetY,
		rotation, zoom
	)

	/**
	 * The X coordinate of the displacement from the target.
	 */
	var dx: Float
		get() = delta.first
		set(value) {
			delta = value to delta.second
		}

	/**
	 * The Y coordinate of the displacement from the target.
	 */
	var dy: Float
		get() = delta.second
		set(value) {
			delta = delta.first to value
		}

	/**
	 * The X coordinate of the camera's target.
	 */
	var targetX: Float
		get() = target.first
		set(value) {
			target = value to target.second
		}

	/**
	 * The Y coordinate of the camera's target.
	 */
	var targetY: Float
		get() = target.second
		set(value) {
			target = target.first to value
		}

	internal fun raw(): CValue<raylib.internal.Camera2D> = cValue {
		offset.x = dx
		offset.y = dy
		target.x = targetX
		target.y = targetY
		this.rotation = rotation
		this.zoom = zoom
	}

}

/**
 * Starts a 2D camera on the current window.
 * @param camera The camera to use
 */
fun Canvas.start2D(camera: Camera2D) {
	BeginMode2D(camera.raw())
}

/**
 * Ends the 2D camera on the current window.
 */
fun Canvas.end2D() {
	EndMode2D()
}

/**
 * Draws within a 2D camera context.
 * @param camera The camera to use
 * @param block The drawing operations to perform
 */
fun Canvas.camera2D(camera: Camera2D, block: Canvas.() -> Unit) {
	start2D(camera)
	this.block()
	end2D()
}

/**
 * Represents the projection of the current camera.
 */
enum class CameraProjection3D(internal val value: CameraProjection) {

	/**
	 * Perspective projection.
	 *
	 * Corresponds to a frustum projection where objects further away appear smaller.
	 */
	PERSPECTIVE(CAMERA_PERSPECTIVE),

	/**
	 * Orthographic projection
	 *
	 * Corresponds to a parallel projection where objects maintain the same size regardless of distance.
	 */
	ORTHOGRAPHIC(CAMERA_ORTHOGRAPHIC)

}

enum class CameraMode3D(internal val value: CameraMode) {
	/**
	 * Camera is controlled manually and [Camera3D.update] doesn't do anything
	 */
	CUSTOM(CAMERA_CUSTOM),
	/**
	 * Camera is freely movable in 3D space
	 */
	FREE(CAMERA_FREE),
	/**
	 * Camera orbits around the target with zoom supported
	 */
	ORBITAL(CAMERA_ORBITAL),
	/**
	 * Camera mimicks a first person view
	 */
	FIRST_PERSON(CAMERA_FIRST_PERSON),
	/**
	 * Camera mimicks a third person view
	 */
	THIRD_PERSON(CAMERA_THIRD_PERSON)
}

/**
 * Represents a 3D Camera representing what the user is seeing.
 * @property position The current position of the camera in 3D space.
 * @property target The coordinates of where the camera is looking.
 * @property rotation The rotational vector applied to the camera
 * @property fovy Camera FOV apperture in Y (degrees) in perspective, or near plane width in orthographic.
 * In perspective, when increased, it zooms out; when decreased, it zooms in.
 * In orthographic, when increased, it zooms in; when decreased, it zooms out.
 * @property projection The type of camera projection
 */
class Camera3D(
	var position: Triple<Float, Float, Float>,
	var target: Triple<Float, Float, Float>,
	var rotation: Triple<Float, Float, Float> = 0f to 0f to 0f,
	var fovy: Float = 0F,
	var projection: CameraProjection3D = CameraProjection3D.PERSPECTIVE
) {

	/**
	 * Creates a new Camera3D.
	 * @param x The X position of the camera
	 * @param y The Y position of the camera
	 * @param z The Z position of the camera
	 * @param targetX The X position of the camera's target
	 * @param targetY The Y position of the camera's target
	 * @param targetZ The Z position of the camera's target
	 * @param rotX The X rotational value of the camera in degress
	 * @param rotY The X rotational value of the camera in degrees
	 * @param rotZ The X rotational value of the camera in degrees
	 * @param fovy Camera FOV apperture in Y (degrees) in perspective, or near plane width in orthographic
	 * @param projection The type of camera projection
	 */
	constructor(
		x: Float = 0f, y: Float = 0f, z: Float = 0f,
		targetX: Float = 0f, targetY: Float = 0f, targetZ: Float = 0f,
		rotX: Float = 0f, rotY: Float = 0f, rotZ: Float = 0f,
		fovy: Float = 0f, projection: CameraProjection3D = CameraProjection3D.PERSPECTIVE
	) : this(
		x to y to z,
		targetX to targetY to targetZ,
		rotX to rotY to rotZ,
		fovy, projection
	)

	/**
	 * The X coordinate of the camera's position.
	 */
	var x: Float
		get() = position.first
		set(value) {
			position = value to position.second to position.third
		}

	/**
	 * The Y coordinate of the camera's position.
	 */
	var y: Float
		get() = position.second
		set(value) {
			position = position.first to value to position.third
		}

	/**
	 * The Z coordinate of the camera's position.
	 */
	var z: Float
		get() = position.third
		set(value) {
			position = position.first to position.second to value
		}

	/**
	 * The X coordinate of the camera's current target to look at.
	 */
	var targetX: Float
		get() = target.first
		set(value) {
			target = value to target.second to target.third
		}

	/**
	 * The Y coordinate of the camera's current target to look at.
	 */
	var targetY: Float
		get() = target.second
		set(value) {
			target = target.first to value to target.third
		}

	/**
	 * The Z coordinate of the camera's current target to look at.
	 */
	var targetZ: Float
		get() = target.third
		set(value) {
			target = target.first to target.second to value
		}

	/**
	 * The X rotational value of the camera.
	 */
	var rotX: Float
		get() = rotation.first
		set(value) {
			rotation = value to rotation.second to rotation.third
		}

	/**
	 * The Y rotational value of the camera.
	 */
	var rotY: Float
		get() = rotation.second
		set(value) {
			rotation = rotation.first to value to rotation.third
		}

	/**
	 * The Z rotational value of the camera.
	 */
	var rotZ: Float
		get() = rotation.third
		set(value) {
			rotation = rotation.first to rotation.second to value
		}

	@Suppress("DuplicatedCode")
	internal fun NativePlacement.raw(): CPointer<raylib.internal.Camera3D>
		= alloc<raylib.internal.Camera3D> {
			position.x = x
			position.y = y
			position.z = z
			target.x = targetX
			target.y = targetY
			target.z = targetZ
			up.x = rotX
			up.y = rotY
			up.z = rotZ
			this.fovy = this@Camera3D.fovy
			this.projection = this@Camera3D.projection.value.toInt()
		}.ptr

	@Suppress("DuplicatedCode")
	internal fun raw(): CValue<raylib.internal.Camera3D> = cValue {
		position.x = x
		position.y = y
		position.z = z
		target.x = targetX
		target.y = targetY
		target.z = targetZ
		up.x = rotX
		up.y = rotY
		up.z = rotZ
		this.fovy = this@Camera3D.fovy
		this.projection = this@Camera3D.projection.value.toInt()
	}

	/**
	 * Updates the camera's current mode.
	 * @param mode The new camera mode to set
	 */
	@Suppress("DuplicatedCode")
	fun update(mode: CameraMode3D): Unit = memScoped {
		val cameraPtr = raw()
		UpdateCamera(cameraPtr, mode.value.toInt())

		// read back values
		val camera = cameraPtr.pointed
		x = camera.position.x
		y = camera.position.y
		z = camera.position.z
		targetX = camera.target.x
		targetY = camera.target.y
		targetZ = camera.target.z
		rotX = camera.up.x
		rotY = camera.up.y
		rotZ = camera.up.z
	}

	/**
	 * Updates the camera's movement, rotation, and speed.
	 * @param dx The delta in X position.
	 * @param dy The delta in Y position.
	 * @param dz The delta in Z position.
	 * @param drotX The delta in X rotation in degrees.
	 * @param drotY The delta in Y rotation in degrees.
	 * @param drotZ The delta in Z rotation in degrees.
	 * @param zoom The new zoom value.
	 */
	fun update(
		dx: Float = 0F, dy: Float = 0F, dz: Float = 0F,
		drotX: Float = 0F, drotY: Float = 0F, drotZ: Float = 0F,
		zoom: Float = 1F
	): Unit = memScoped {
		val cameraPtr = raw()
		UpdateCameraPro(
			cameraPtr,
			(dx to dy to dz).toVector3(),
			(drotX to drotY to drotZ).toVector3(),
			zoom
		)
		// read back modified values
		x = cameraPtr.pointed.position.x
		y = cameraPtr.pointed.position.y
		z = cameraPtr.pointed.position.z
		targetX = cameraPtr.pointed.target.x
		targetY = cameraPtr.pointed.target.y
		targetZ = cameraPtr.pointed.target.z
		rotX = cameraPtr.pointed.up.x
		rotY = cameraPtr.pointed.up.y
		rotZ = cameraPtr.pointed.up.z
	}

	/**
	 * Updates the camera's movement, rotation, and speed.
	 * @param delta The delta for the camera position.
	 * @param deltaRot The delta for the camera rotation in degrees.
	 * @param zoom The new zoom value.
	 */
	fun update(
		delta: Triple<Float, Float, Float> = 0F to 0F to 0F,
		deltaRot: Triple<Float, Float, Float> = 0F to 0F to 0F,
		zoom: Float = 1F
	): Unit = memScoped {
		val cameraPtr = raw()
		UpdateCameraPro(
			cameraPtr,
			delta.toVector3(),
			deltaRot.toVector3(),
			zoom
		)
		// read back modified values
		x = cameraPtr.pointed.position.x
		y = cameraPtr.pointed.position.y
		z = cameraPtr.pointed.position.z
		targetX = cameraPtr.pointed.target.x
		targetY = cameraPtr.pointed.target.y
		targetZ = cameraPtr.pointed.target.z
		rotX = cameraPtr.pointed.up.x
		rotY = cameraPtr.pointed.up.y
		rotZ = cameraPtr.pointed.up.z
	}
}

/**
 * Starts a 3D camera on the current window.
 * @param camera The camera to use
 */
fun Canvas.start3D(camera: Camera3D) {
	BeginMode3D(camera.raw())
}

/**
 * Ends the 3D camera on the current window.
 */
fun Canvas.end3D() {
	EndMode3D()
}

/**
 * Draws within a 3D camera context.
 * @param camera The camera to use
 * @param block The drawing operations to perform
 */
fun Canvas.camera3D(camera: Camera3D, block: Canvas.() -> Unit) {
	start3D(camera)
	this.block()
	end3D()
}

// Expect

internal expect val _isHeadless: Boolean

internal expect fun _close0()
