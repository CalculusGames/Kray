package kray

import raylib.Matrix4

/**
 * Represents an object that has a position in 3D Space.
 */
interface Positionable3D : Positionable {

	/**
	 * The z-coordinate of the object.
	 */
	var z: Float

	/**
	 * The position of the object.
	 */
	val position: Triple<Float, Float, Float>
		get() = x to y to z

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

	/**
	 * The transformation matrix of the positionable.
	 */
	var transform: Matrix4

	/**
	 * Translates the [transform] matrix.
	 * @param x The x coordinate of the translation
	 * @param y The y coordinate of the translation
	 * @param z The z coordinate of the translation
	 */
	fun translate(x: Float, y: Float, z: Float) {
		transform *= Matrix4.translate(x, y, z)
	}

	/**
	 * Rotates the [transform] matrix.
	 * @param pitch The amount to rotate by on the X axis
	 * @param yaw The amount to rotate by on the Y axis
	 * @param roll The amount to rotate by on the Z axis
	 */
	fun rotate(pitch: Float, yaw: Float, roll: Float) {
		transform *= Matrix4.rotate(pitch, yaw, roll)
	}

	/**
	 * Scales the [transform] matrix.
	 * @param sx The scale across the X axis.
	 * @param sy The scale across the Y axis.
	 * @param sz The scale across the Z axis.
	 */
	fun scale(sx: Float, sy: Float, sz: Float) {
		transform *= Matrix4.scale(sx, sy, sz)
	}

	/**
	 * Scales the [transform] matrix.
	 * @param scale The scale for X, Y, and Z.
	 */
	fun scale(scale: Float) = scale(scale, scale, scale)

	/**
	 * Returns whether the sprite is left of the given X boundary.
	 * @param x The X value to check
	 * @return true if left, false otherwise
	 */
	fun isLeftX(x: Float): Boolean

	/**
	 * Returns whether the sprite is right of the given X boundary.
	 * @param x The X value to check
	 * @return true if right, false otherwise
	 */
	fun isRightX(x: Float): Boolean

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

	/**
	 * Returns whether the sprite is left of the given Z boundary.
	 * @param z The Z value to check
	 * @return true if left, false otherwise
	 */
	fun isLeftZ(z: Float): Boolean

	/**
	 * Returns whether the sprite is right of the given X boundary.
	 * @param z The Z value to check
	 * @return true if right, false otherwise
	 */
	fun isRightZ(z: Float): Boolean

}
