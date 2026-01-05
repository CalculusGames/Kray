package kray

/**
 * Represents an object that has a position in 2D space.
 */
interface Positionable2D : Positionable {

	/**
	 * Moves the object to the specified [newX] and [newY] coordinates in place.
	 * @param newX The new x-coordinate of the object.
	 * @param newY The new y-coordinate of the object.
	 */
	fun moveTo(newX: Float, newY: Float) {
		if (newX < 0F || newY < 0F)
			throw IllegalArgumentException("Coordinates ($newX, $newY) must be non-negative.")

		x = newX
		y = newY
	}

	/**
	 * Moves the object by the specified [deltaX] and [deltaY] offsets in place.
	 * @param deltaX The offset to move the object along the x-axis.
	 * @param deltaY The offset to move the object along the y-axis.
	 */
	fun moveBy(deltaX: Float, deltaY: Float) {
		moveTo(x + deltaX, y + deltaY)
	}

}
