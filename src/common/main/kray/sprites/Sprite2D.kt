@file:OptIn(ExperimentalUuidApi::class)

package kray.sprites

import kray.physics.Hitbox2D
import kray.Positionable2D
import kray.Sizeable2D
import raylib.Image
import raylib.Texture2D
import raylib.load
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a sprite in the Kray game engine.
 */
class Sprite2D(internal var raw: Texture2D) : Sprite<Texture2D>, Positionable2D, Sizeable2D {

	override val id: Uuid = Uuid.random()

	override var x: Float = 0F
	override var y: Float = 0F
	override val width: Int
		get() = raw.width
	override val height: Int
		get() = raw.height
	override var rotation: Float = 0F
	override var scale: Float = 1F

	override var isDrawn: Boolean = false
		internal set
	override var static: Boolean = false
	override var collideable: Boolean = true

	/**
	 * The z-index of the sprite. Higher values are drawn on top of lower values.
	 */
	var zIndex: Int = 0

	override var currentCostumeIndex: Int = 0
		internal set

	/**
	 * An immutable list of all costumes (textures) associated with this sprite.
	 */
	override val costumes = mutableListOf(raw)

	override fun resize(newWidth: Int, newHeight: Int): Sprite2D {
		if (newWidth <= 0 || newHeight <= 0)
			throw IllegalArgumentException("Width and height must be positive integers.")

		val image = Image.load(raw)
		raw = Texture2D.load(image.resize(newWidth, newHeight))
		return this
	}

	override fun changeTo(index: Int) {
		if (index < 0 || index >= costumes.size)
			throw IndexOutOfBoundsException("Index $index out of bounds for costumes of size ${costumes.size}")

		raw = costumes[index]
		currentCostumeIndex = index
	}

	override fun switchTo(costume: Texture2D) {
		val index = costumes.indexOf(costume)
		if (index == -1) {
			throw IllegalArgumentException("The provided texture is not a costume of this sprite.")
		}

		raw = costume
		currentCostumeIndex = index
	}

	override fun nextCostume() {
		if (costumes.isEmpty()) return

		val nextIndex = (currentCostumeIndex + 1) % costumes.size
		raw = costumes[nextIndex]
		currentCostumeIndex = nextIndex
	}

	override fun previousCostume() {
		if (costumes.isEmpty()) return

		val prevIndex = if (currentCostumeIndex - 1 < 0)
			costumes.size - 1
		else
			(currentCostumeIndex - 1) % costumes.size

		raw = costumes[prevIndex]
		currentCostumeIndex = prevIndex
	}

	override fun setCurrentCostume(costume: Texture2D) {
		val index = costumes.indexOf(costume)
		if (index == -1) {
			addCostume(costume)
			raw = costume
			currentCostumeIndex = costumes.size - 1
		} else {
			raw = costume
			currentCostumeIndex = index
		}
	}

	override var hitbox: Hitbox2D = Hitbox2D.rectangle(0.0f, 0.0f, width.toFloat(), height.toFloat())

	override fun spin(degrees: Float) {
		super.spin(degrees)
		hitbox = Hitbox2D.rotatedRectangle(0.0f, 0.0f, width.toFloat(), height.toFloat(), rotation)
	}

	override fun isLeft(x: Float): Boolean = hitbox.isLeft(x - this.x)
	override fun isRight(x: Float): Boolean = hitbox.isRight(x - this.x)
	override fun isAbove(y: Float): Boolean = hitbox.isAbove(y - this.y)
	override fun isBelow(y: Float): Boolean = hitbox.isBelow(y - this.y)

	override fun equals(other: Any?): Boolean {
		if (other == null) return false
		if (this === other) return true
		if (other !is Sprite2D) return false

		return other.id == id
	}

	override fun hashCode(): Int = id.hashCode()

	companion object {
		/**
		 * Creates a [Sprite2D] from the given [resolver] function that provides an [Texture2D].
		 * @param resolver The function that provides the [Texture2D].
		 * @return A new [Sprite2D] instance.
		 */
		fun from(resolver: () -> Texture2D): Sprite2D {
			return Sprite2D(resolver())
		}

		/**
		 * Creates a [Sprite2D] from the given [texture].
		 * @param texture The [Texture2D] to create the sprite from.
		 * @return A new [Sprite2D] instance.
		 */
		fun from(texture: Texture2D): Sprite2D {
			return Sprite2D(texture)
		}

		/**
		 * Creates a [Sprite2D] from the given [image].
		 * @param image The [Image] to create the sprite from.
		 * @return A new [Sprite2D] instance.
		 */
		fun from(image: Image): Sprite2D {
			val texture = Texture2D.load(image)
			return Sprite2D(texture)
		}
	}
}
