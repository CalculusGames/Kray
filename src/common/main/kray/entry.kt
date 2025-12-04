package kray

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import raylib.Window
import raylib.Canvas

/**
 * The primary entrypoint of a Kray application
 */
suspend fun Kray(
	width: Int = 800,
	height: Int = 600,
	title: String = "Kray App",
	entrypoint: suspend Kray.() -> Unit
) {
	Window.open(width, height, title)
	entrypoint(Kray)
}

/**
 * The Kray game engine.
 */
object Kray {
	val window = Window
	val canvas = Canvas

	/**
	 * The provided lifecycle loop for the application.
	 */
	var loop: (suspend Kray.() -> Unit)? = null
		private set

	/**
	 * Whether the engine is currently stopped.
	 */
	var stopped = false
		private set

	/**
	 * Sets the game loop.
	 */
	suspend fun loop(loop: suspend Kray.() -> Unit) {
		if (this.loop != null) throw IllegalStateException("Already looping")
		this.loop = loop

		coroutineScope {
			launch {
				while (!Window.shouldClose && !stopped) {
					loop()
				}
			}
		}
	}

	/**
	 * Stops the current lifecycle [loop], ending the game.
	 */
	fun stopLoop() {
		stopped = true
	}
}
