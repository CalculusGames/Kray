package kray

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kray.sprites.Sprite2D
import kray.sprites.drawSprite
import kray.sprites.drawnSprites
import raylib.Window
import raylib.Canvas
import raylib.GamePad
import raylib.Key
import raylib.Keyboard
import raylib.Mouse

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
	val mouse = Mouse
	val keyboard = Keyboard
	val gamepad = GamePad

	/**
	 * The provided lifecycle loop for the application.
	 */
	var loop: (suspend CoroutineScope.() -> Unit)? = null
		private set

	/**
	 * Whether the engine is currently stopped.
	 */
	var stopped = false
		private set

	/**
	 * Sets the game loop.
	 */
	suspend fun loop(logic: suspend CoroutineScope.() -> Unit) {
		if (this.loop != null) throw IllegalStateException("Already looping")
		this.loop = logic

		coroutineScope {
			while (!Window.shouldClose && !stopped) {
				logic()

				// draw registered sprites
				drawnSprites.forEach { sprite ->
					if (sprite is Sprite2D && sprite.isDrawn) {
						canvas.drawSprite(sprite, sprite.x, sprite.y)
					}
				}
			}
		}
	}

	/**
	 * Runs the given [logic] on the IO dispatcher.
	 * @param logic The logic to run.
	 * @return A [suspend] [Unit].
	 */
	suspend fun io(logic: suspend CoroutineScope.() -> Unit) {
		withContext(Dispatchers.IO) {
			logic()
		}
	}

	/**
	 * Stops the current lifecycle [loop], ending the game.
	 */
	fun stopLoop() {
		stopped = true
	}

	// Extensions

	/// Positionable2D

	/**
	 * Aligns the object to the left side of the screen.
	 * @param margin The margin from the left side of the screen.
	 */
	fun Positionable2D.alignHorizontalLeft(margin: Float = 0F) {
		this.x = margin
	}

	/**
	 * Aligns the object to the left side of the target object.
	 * @param target The target object to align to.
	 * @param margin The margin from the target object.
	 */
	fun Positionable2D.alignHorizontalLeft(target: Positionable2D, margin: Float = 0F) {
		this.x = target.x + margin
	}

	/**
	 * Aligns the object to the top side of the screen.
	 * @param margin The margin from the top side of the screen.
	 */
	fun Positionable2D.alignHorizontalRight(margin: Float = 0F) {
		this.x = window.screenWidth - 1 - margin
	}

	/**
	 * Aligns the object to the right side of the target object.
	 * @param target The target object to align to.
	 * @param margin The margin from the target object.
	 */
	fun Positionable2D.alignHorizontalRight(target: Positionable2D, margin: Float = 0F) {
		this.x = target.x + margin
	}

	/**
	 * Aligns the object to the top side of the screen.
	 * @param margin The margin from the top side of the screen.
	 */
	fun Positionable2D.alignVerticalTop(margin: Float = 0F) {
		this.y = 0F
	}

	/**
	 * Aligns the object to the top side of the target object.
	 * @param target The target object to align to.
	 * @param margin The margin from the target object.
	 */
	fun Positionable2D.alignVerticalTop(target: Positionable2D, margin: Float = 0F) {
		this.y = target.y + margin
	}

	/**
	 * Aligns the object to the bottom side of the screen.
	 * @param margin The margin from the bottom side of the screen.
	 */
	fun Positionable2D.alignVerticalBottom(margin: Float = 0F) {
		this.y = window.screenHeight - 1 - margin
	}

	/**
	 * Aligns the object to the bottom side of the target object.
	 * @param target The target object to align to.
	 * @param margin The margin from the target object.
	 */
	fun Positionable2D.alignVerticalBottom(target: Positionable2D, margin: Float = 0F) {
		this.y = target.y + margin
	}

	/// Positionable2D & Sizeable2D

	/**
	 * Aligns the object to the center of the screen horizontally.
	 */
	fun <T> T.alignHorizontalCenter() where T : Positionable2D, T : Sizeable2D {
		this.x = window.screenWidth / 2F - this.width / 2F
	}

	/**
	 * Aligns the object to the center of the target object horizontally.
	 * @param target The target object to align to.
	 */
	fun <T> T.alignHorizontalCenter(target: T) where T : Positionable2D, T : Sizeable2D {
		this.x = target.x + target.width / 2F - this.width / 2F
	}

	/**
	 * Aligns the object to the center of the screen vertically.
	 */
	fun <T> T.alignVerticalCenter() where T : Positionable2D, T : Sizeable2D {
		this.y = window.screenHeight / 2F - this.height / 2F
	}

	/**
	 * Aligns the object to the center of the target object vertically.
	 * @param target The target object to align to.
	 */
	fun <T> T.alignVerticalCenter(target: T) where T : Positionable2D, T : Sizeable2D {
		this.y = target.y + target.height / 2F - this.height / 2F
	}

	/**
	 * Aligns the object to the center of the screen both horizontally and vertically.
	 */
	fun <T> T.alignCenter() where T : Positionable2D, T : Sizeable2D {
		alignHorizontalCenter()
		alignVerticalCenter()
	}

	/**
	 * Aligns the object to the center of the target object both horizontally and vertically.
	 * @param target The target object to align to.
	 */
	fun <T> T.alignCenter(target: T) where T : Positionable2D, T : Sizeable2D {
		alignHorizontalCenter(target)
		alignVerticalCenter(target)
	}

	/// Sprite2D

	/**
	 * Checks if the sprite is currently being hovered over by the mouse.
	 * @return True if the mouse is over the sprite, false otherwise.
	 */
	val Sprite2D.isMouseOver: Boolean
		get() {
			val mouseX = mouse.mouseX.toFloat()
			val mouseY = mouse.mouseY.toFloat()

			return mouseX >= x && mouseX <= x + width &&
				   mouseY >= y && mouseY <= y + height
		}

	/**
	 * Checks if the sprite is currently being clicked by the mouse.
	 * @return True if the mouse is over the sprite and the left mouse button is pressed, false otherwise.
	 */
	val Sprite2D.isLeftDown: Boolean
		get() {
			val pressed = mouse.isPressed(Mouse.Button.LEFT)
			return isMouseOver && pressed
		}

	/**
	 * Checks if the sprite is currently being right-clicked by the mouse.
	 * @return True if the mouse is over the sprite and the right mouse button is pressed, false otherwise.
	 */
	val Sprite2D.isRightDown: Boolean
		get() {
			val pressed = mouse.isPressed(Mouse.Button.RIGHT)
			return isMouseOver && pressed
		}

	/**
	 * Checks if the sprite is currently being middle-clicked by the mouse.
	 * @return True if the mouse is over the sprite and the middle mouse button is pressed
	 */
	val Sprite2D.isMiddleDown: Boolean
		get() {
			val pressed = mouse.isPressed(Mouse.Button.MIDDLE)
			return isMouseOver && pressed
		}

	/**
	 * Checks if any mouse button is currently being pressed on the sprite.
	 * @return True if the mouse is over the sprite and any mouse button is pressed, false otherwise.
	 */
	val Sprite2D.isDown: Boolean
		get() = isLeftDown || isRightDown || isMiddleDown

	/**
	 * Performs the given [action] if the sprite is being clicked by any mouse button.
	 * @param action The action to perform.
	 */
	suspend fun Sprite2D.onDown(action: suspend Kray.() -> Unit) {
		if (isDown) {
			action()
		}
	}

	/**
	 * Checks if the sprite is currently being clicked by any mouse button.
	 * @return True if the mouse is over the sprite and any mouse button is pressed, false otherwise.
	 */
	val Sprite2D.isClicked: Boolean
		get() {
			val clicked = mouse.isPressed(Mouse.Button.LEFT) ||
					  mouse.isPressed(Mouse.Button.RIGHT) ||
					  mouse.isPressed(Mouse.Button.MIDDLE)
			return isMouseOver && clicked
		}

	/**
	 * Performs the given [action] if the sprite is being clicked by any mouse button.
	 * @param action The action to perform.
	 */
	suspend fun Sprite2D.onClick(action: suspend Kray.() -> Unit) {
		if (isClicked) {
			action()
		}
	}

	/**
	 * Moves the sprite left when the given [key] is pressed.
	 * @param key The key to check.
	 * @param speed The speed to move the sprite.
	 */
	fun Sprite2D.moveRightKey(key: Key, speed: Double = 1.0) {
		if (keyboard.isDown(key)) {
			this.x += (window.frameTime * speed).toFloat()
		}
	}

	/**
	 * Moves the sprite left when the given [key] is pressed.
	 * @param key The key to check.
	 * @param speed The speed to move the sprite.
	 */
	fun Sprite2D.moveLeftKey(key: Key, speed: Double = 1.0) {
		if (keyboard.isDown(key)) {
			this.x -= (window.frameTime * speed).toFloat()
		}
	}

	/**
	 * Moves the sprite up when the given [key] is pressed.
	 * @param key The key to check.
	 * @param speed The speed to move the sprite.
	 */
	fun Sprite2D.moveUpKey(key: Key, speed: Double = 1.0) {
		if (keyboard.isDown(key)) {
			this.y -= (window.frameTime * speed).toFloat()
		}
	}

	/**
	 * Moves the sprite down when the given [key] is pressed.
	 * @param key The key to check.
	 * @param speed The speed to move the sprite.
	 */
	fun Sprite2D.moveDownKey(key: Key, speed: Double = 1.0) {
		if (keyboard.isDown(key)) {
			this.y += (window.frameTime * speed).toFloat()
		}
	}

	/**
	 * A set of all 2D sprites that are currently colliding with this sprite.
	 */
	val Sprite2D.collisions: Set<Sprite2D>
		get() {
			val collisions = mutableSetOf<Sprite2D>()
			for (sprite in drawnSprites) {
				if (sprite is Sprite2D && sprite != this) {
					if (this.x < sprite.x + sprite.width &&
						this.x + this.width > sprite.x &&
						this.y < sprite.y + sprite.height &&
						this.y + this.height > sprite.y
					) {
						collisions.add(sprite)
					}
				}
			}

			return collisions
		}
}
