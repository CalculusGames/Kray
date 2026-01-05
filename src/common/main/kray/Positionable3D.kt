package kray

/**
 * Represents an object that has a position in 3D Space.
 */
interface Positionable3D : Positionable {

	/**
	 * The z-coordinate of the object.
	 */
	var z: Float

	/**
	 * Moves the object to the specified [newX], [newY], and [newZ] coordinates in place.
	 * @param newX The new x-coordinate of the object.
	 * @param newY The new y-coordinate of the object.
	 * @param newZ The new z-coordinate of the object.
	 */
	fun moveTo(newX: Float, newY: Float, newZ: Float) {
		if (newX < 0F || newY < 0F || newZ < 0F)
			throw IllegalArgumentException("Coordinates ($newX, $newY, $newZ) must be non-negative.")

		x = newX
		y = newY
		z = newZ
	}

	/**
	 * Moves the object by the specified [deltaX], [deltaY], and [deltaZ] offsets in place.
	 * @param deltaX The offset to move the object along the x-axis.
	 * @param deltaY The offset to move the object along the y-axis.
	 * @param deltaZ The offset to move the object along the z-axis.
	 */
	fun moveBy(deltaX: Float, deltaY: Float, deltaZ: Float) {
		moveTo(x + deltaX, y + deltaY, z + deltaZ)
	}

}
