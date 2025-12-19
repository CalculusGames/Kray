package kray.sprites

import kray.Positionable3D
import kray.Sizeable3D
import raylib.Model

/**
 * Represents a sprite in the Kray game engine.
 */
class Sprite3D(internal var raw: Model) : Sprite<Model>, Positionable3D, Sizeable3D {

	override var x: Int = 0
	override var y: Int = 0
	override var z: Int = 0
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
