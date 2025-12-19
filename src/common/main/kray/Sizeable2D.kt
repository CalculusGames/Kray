package kray

/**
 * Represents an object that has a size in 2D space.
 */
interface Sizeable2D {

	/**
	 * The width of the object.
	 */
	val width: Int

	/**
	 * The height of the object.
	 */
	val height: Int

	/**
	 * The area of the object's bounding box, based on its size parameters.
	 */
	val area: Int
		get() = width * height

	/**
	 * The perimeter of the object's bounding box, based on its size parameters.
	 */
	val perimeter: Int
		get() = 2 * (width + height)

	/**
	 * Resizes the object to the specified [newWidth] and [newHeight] in place.
	 * @param newWidth The new width of the object.
	 * @param newHeight The new height of the object.
	 * @return This object after resizing.
	 */
	fun resize(newWidth: Int, newHeight: Int): Sizeable2D

}
