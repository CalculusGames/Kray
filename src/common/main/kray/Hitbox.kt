package kray

/**
 * Represents a hitbox.
 */
interface Hitbox {

	/**
	 * The unique ID of this hitbox.
	 *
	 * This is used to differentiate hitboxes that may have identical shapes but are distinct instances,
	 * since function references cannot be reliably compared for equality.
	 */
	val id: Long

}

