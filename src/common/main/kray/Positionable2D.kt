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

	/**
	 * The rotation angle of the object in degrees.
	 */
	var rotation: Float

	/**
	 * Rotates the object by the specified [degrees] in place.
	 * @param degrees The angle in degrees to rotate the object.
	 */
	fun spin(degrees: Float) {
		rotation = (rotation + degrees) % 360F
	}

	/**
	 * Returns whether the sprite is left of the given X boundary.
	 * @param x The X value to check
	 * @return true if left, false otherwise
	 */
	fun isLeft(x: Float): Boolean

	/**
	 * Returns whether the sprite is right of the given X boundary.
	 * @param x The X value to check
	 * @return true if right, false otherwise
	 */
	fun isRight(x: Float): Boolean

	/**
	 * Returns whether the sprite is above the given Y boundary.
	 * @param y The Y value to check
	 * @return true if above, false otherwise
	 */
	fun isAbove(y: Float): Boolean

	/**
	 * Returns whether the sprite is below the given Y boundary.
	 * @param y The Y value to check
	 * @return true if below, false otherwise
	 */
	fun isBelow(y: Float): Boolean

}
