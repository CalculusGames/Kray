package kray

/**
 * Represents an object that has a position in 3D Space.
 */
interface Positionable3D {

	/**
	 * The x-coordinate of the object.
	 */
	var x: Int

	/**
	 * The y-coordinate of the object.
	 */
	var y: Int

	/**
	 * The z-coordinate of the object.
	 */
	var z: Int

	/**
	 * Moves the object to the specified [newX], [newY], and [newZ] coordinates in place.
	 * @param newX The new x-coordinate of the object.
	 * @param newY The new y-coordinate of the object.
	 * @param newZ The new z-coordinate of the object.
	 */
	fun moveTo(newX: Int, newY: Int, newZ: Int) {
		if (newX < 0 || newY < 0 || newZ < 0)
			throw IllegalArgumentException("Coordinates ($newX, $newY, $newZ) must be non-negative integers.")

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
	fun moveBy(deltaX: Int, deltaY: Int, deltaZ: Int) {
		moveTo(x + deltaX, y + deltaY, z + deltaZ)
	}

}
