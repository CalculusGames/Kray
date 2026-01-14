@file:OptIn(ExperimentalUuidApi::class)

package kray

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * An interface for objects that have a position in space.
 */
interface Positionable {

	/**
	 * The ID of the positionable object.
	 */
	val id: Uuid

	/**
	 * The x-coordinate of the object.
	 */
	var x: Float

	/**
	 * The x-coordinate of the object.
	 */
	var y: Float

}
