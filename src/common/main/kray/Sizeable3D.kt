package kray

import kray.physics.Hitbox3D

/**
 * Represent an object that has size in 3D space.
 */
interface Sizeable3D {

	/**
	 * The width of the object.
	 */
	val width: Int

	/**
	 * The height of the object.
	 */
	val height: Int

	/**
	 * The depth of the object.
	 */
	val depth: Int

	/**
	 * The volume of the object's bounding box, based on its size parameters.
	 */
	val volume: Int
		get() = width * height * depth

	/**
	 * The surface area of the object's bounding box, based on its size parameters.
	 */
	val surfaceArea: Int
		get() = 2 * (width * height + width * depth + height * depth)

	/**
	 * The hitbox of the object used for collision detection.
	 */
	val hitbox: Hitbox3D
}
