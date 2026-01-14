@file:OptIn(ExperimentalUuidApi::class)

package kray.sprites

import kray.physics.Hitbox3D
import kray.Positionable3D
import kray.Sizeable3D
import raylib.Matrix4
import raylib.Model
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a sprite in the Kray game engine.
 */
class Sprite3D(internal var raw: Model) : Sprite<Model>, Positionable3D, Sizeable3D {

	override val id: Uuid = Uuid.random()

	override var x: Float = 0F
	override var y: Float = 0F
	override var z: Float = 0F
	override val width: Int
		get() = raw.boundingBox.width.toInt()
	override val height: Int
		get() = raw.boundingBox.height.toInt()
	override val depth: Int
		get() = raw.boundingBox.depth.toInt()

	override var isDrawn: Boolean = false
		internal set
	override var static: Boolean = false
	override var collideable: Boolean = true
	override var transform: Matrix4
		get() = raw.transform
		set(value) {
			raw.transform = value
			hitbox = Hitbox3D.transformedRectangle(
				0.0f, 0.0f, 0.0f, width.toFloat(), height.toFloat(), depth.toFloat(), value
			)
		}

	/**
	 * The z-index of the sprite. Higher values are drawn on top of lower values.
	 */
	var zIndex: Int = 0

	override var currentCostumeIndex: Int = 0
		internal set

	/**
	 * An immutable list of all costumes (models) associated with this sprite.
	 */
	override val costumes = mutableListOf(raw)

	override fun changeTo(index: Int) {
		if (index < 0 || index >= costumes.size)
			throw IndexOutOfBoundsException("Index $index out of bounds for costumes of size ${costumes.size}")

		raw = costumes[index]
		currentCostumeIndex = index
	}

	override fun switchTo(costume: Model) {
		val index = costumes.indexOf(costume)
		if (index == -1) {
			throw IllegalArgumentException("The provided image is not a costume of this sprite.")
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

	override fun setCurrentCostume(costume: Model) {
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

	override var hitbox: Hitbox3D = Hitbox3D.rectangle(0.0f, 0.0f, 0.0f, width.toFloat(), height.toFloat(), depth.toFloat())
	override fun isLeftX(x: Float): Boolean = hitbox.isLeftX(x - this.x)
	override fun isRightX(x: Float): Boolean = hitbox.isRightX(x - this.y)
	override fun isAbove(y: Float): Boolean = hitbox.isAbove(y - this.z)
	override fun isBelow(y: Float): Boolean = hitbox.isBelow(y - this.y)
	override fun isLeftZ(z: Float): Boolean = hitbox.isLeftZ(z - this.z)
	override fun isRightZ(z: Float): Boolean = hitbox.isRightZ(z - this.z)

	override fun equals(other: Any?): Boolean {
		if (other == null) return false
		if (this === other) return true
		if (other !is Sprite3D) return false

		return other.id == id
	}

	override fun hashCode(): Int = id.hashCode()

	companion object {
		/**
		 * Creates a [Sprite3D] from the given [resolver] function that provides an [Model].
		 * @param resolver The function that provides the [Model].
		 * @return A new [Sprite3D] instance.
		 */
		fun from(resolver: () -> Model): Sprite3D {
			return Sprite3D(resolver())
		}
	}
}
