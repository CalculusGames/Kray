package kray.sprites

/**
 * Represents a sprite in the Kray game engine. Either 2D or 3D.
 * @param T The type of the sprite (e.g., Image for 2D sprites).
 */
interface Sprite<T> {

	/**
	 * Whether the sprite has been drawn to the canvas this frame.
	 */
	val isDrawn: Boolean

	/**
	 * Whether the sprite is affected by physics. False means it is not.
	 */
	var static: Boolean

	/**
	 * Whether the sprite can collide with other sprites. False means it cannot.
	 */
	var collideable: Boolean

	/**
	 * The index of the current costume being used by this sprite.
	 */
	val currentCostumeIndex: Int

	/**
	 * A list of all costumes associated with this sprite.
	 */
	val costumes: MutableList<T>

	/**
	 * The current costume that the sprite is wearing.
	 */
	val currentCostume: T
		get() = costumes[currentCostumeIndex]

	/**
	 * Adds a costume to this sprite.
	 * @param costume The image to add as a costume.
	 */
	fun addCostume(costume: T) {
		costumes.add(costume)
	}

	/**
	 * Removes the specified costume from this sprite.
	 * @param costume The image to remove.
	 */
	fun removeCostume(costume: T) {
		costumes.remove(costume)
	}

	/**
	 * Removes the costume at the specified index from this sprite.
	 * @param index The index of the costume to remove.
	 */
	fun removeCostumeAt(index: Int) {
		costumes.removeAt(index)
	}

	/**
	 * Sets the costumes for this sprite.
	 * @param costumes The costumes to set.
	 */
	fun setCostumes(vararg list: T) {
		costumes.clear()
		costumes.addAll(list)
	}

	/**
	 * Sets the costumes for this sprite.
	 * @param list The costumes to set.
	 */
	fun setCostumes(list: Iterable<T>) {
		costumes.clear()
		costumes.addAll(list)
	}

	/**
	 * Sets the costume at the specified index for this sprite.
	 * @param index The index of the costume to set.
	 * @param costume The image to set as the costume.
	 */
	fun setCostume(index: Int, costume: T) {
		if (index < 0 || index >= costumes.size)
			throw IndexOutOfBoundsException("Index $index out of bounds for costumes of size ${costumes.size}")

		costumes[index] = costume
	}

	/**
	 * Changes the current costume of this sprite to the one at the specified index.
	 * @param index The index of the costume to change to.
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	fun changeTo(index: Int)

	/**
	 * Changes the current costume of this sprite to the specified image.
	 * The costume must already be part of this sprite's costumes.
	 * @param costume The image to change to.
	 * @throws IllegalArgumentException if the provided image is not a costume of this sprite.
	 */
	fun switchTo(costume: T)

	/**
	 * Changes the current costume of this sprite to the next one in the list.
	 * Loops back to the first costume if currently at the last one.
	 */
	fun nextCostume()

	/**
	 * Changes the current costume of this sprite to the previous one in the list.
	 * Loops back to the last costume if currently at the first one.
	 */
	fun previousCostume()

	/**
	 * Sets the current costume of this sprite to the specified image.
	 * If the image is not already a costume of this sprite, it is added.
	 * @param costume The image to set as the current costume.
	 */
	fun setCurrentCostume(costume: T)

}
