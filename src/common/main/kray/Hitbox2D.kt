package kray

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * A 2D hitbox for collision detection.
 * @property minX The left-most side of the hitbox
 * @property minY The top-most side of the hitbox
 * @property maxX The right-most side of the hitbox
 * @property maxY The bottom-most side of the hitbox
 * @property inside A function that takes a pair of (x, y) coordinates and returns true if the point is inside the hitbox.
 */
class Hitbox2D(
	val minX: Float,
	val minY: Float,
	val maxX: Float,
	val maxY: Float,
	val inside: (Pair<Float, Float>) -> Boolean,
) : Hitbox {
	override val id: Long = Random.nextLong()

	/**
	 * Checks if the given float coordinates are inside the hitbox.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(x: Float, y: Float): Boolean = inside(x to y)


	/**
	 * Checks if the given integer coordinates are inside the hitbox.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(x: Int, y: Int): Boolean = inside(x.toFloat() to y.toFloat())

	/**
	 * Checks if the given integer point is inside the hitbox.
	 * @param point A pair representing the (x, y) coordinates.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(point: Pair<Int, Int>): Boolean = inside(point.first, point.second)

	/**
	 * Checks if the given double coordinates are inside the hitbox.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(x: Double, y: Double): Boolean = inside(x.toFloat() to y.toFloat())

	/**
	 * Checks if the given double point is inside the hitbox.
	 * @param point A pair representing the (x, y) coordinates.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(point: Pair<Double, Double>): Boolean = inside(point.first, point.second)

	/**
	 * Returns whether the sprite is left of the given X boundary using [minX].
	 * @param x The X value to check
	 * @return true if left, false otherwise
	 */
	fun isLeft(x: Float, position: Float = 0f): Boolean = minX >= x

	/**
	 * Returns whether the sprite is right of the given X boundary using [maxX].
	 * @param x The X value to check
	 * @return true if right, false otherwise
	 */
	fun isRight(x: Float): Boolean = maxX <= x

	/**
	 * Returns whether the sprite is above the given Y boundary using [minY].
	 * @param y The Y value to check
	 * @return true if above, false otherwise
	 */
	fun isAbove(y: Float): Boolean = minY <= y

	/**
	 * Returns whether the sprite is below the given Y boundary using [maxY].
	 * @param y The Y value to check
	 * @return true if below, false otherwise
	 */
	fun isBelow(y: Float): Boolean = maxY >= y

	/**
	 * Creates a rectangular hitbox defined by its minimum and maximum x and y coordinates.
	 * @param minX The minimum x coordinate.
	 * @param minY The minimum y coordinate.
	 * @param maxX The maximum x coordinate.
	 * @param maxY The maximum y coordinate.
	 */
	constructor(minX: Float, minY: Float, maxX: Float, maxY: Float) : this(
		minX = minX,
		minY = minY,
		maxX = maxX,
		maxY = maxY,
		inside = { (x, y) ->
			x in minX..maxX && y in minY..maxY
		},
	)

	/**
	 * Creates a rectangular hitbox defined by its minimum and maximum points.
	 * @param min A pair representing the minimum (x, y) coordinates.
	 * @param max A pair representing the maximum (x, y) coordinates.
	 */
	constructor(min: Pair<Float, Float>, max: Pair<Float, Float>) : this(
		minX = min.first,
		minY = min.second,
		maxX = max.first,
		maxY = max.second
	)

	/**
	 * Adds a rectangular hitbox to this hitbox.
	 * The resulting hitbox contains all points that are inside either the original hitbox or the new rectangle.
	 * @param x The x coordinate of the top-left corner of the rectangle.
	 * @param y The y coordinate of the top-left corner of the rectangle.
	 * @param width The width of the rectangle.
	 * @param height The height of the rectangle.
	 * @return A new Hitbox2D representing the union of the original hitbox and the new rectangle.
	 */
	fun add(x: Float, y: Float, width: Float, height: Float): Hitbox2D {
		val rectHitbox = rectangle(x, y, width, height)
		return this.add(rectHitbox)
	}

	/**
	 * Combines this hitbox with another hitbox using a union operation.
	 * The resulting hitbox contains all points that are inside either of the original hitboxes.
	 * @param other The other hitbox to combine with.
	 * @return A new Hitbox2D representing the union of the two hitboxes.
	 */
	fun add(other: Hitbox2D): Hitbox2D {
		val minX = minOf(minX, other.minX)
		val minY = minOf(minY, other.minY)
		val maxX = maxOf(maxX, other.maxX)
		val maxY = maxOf(maxY, other.maxY)

		return Hitbox2D(minX, minY, maxX, maxY) { (x, y) ->
			this.inside(x to y) || other.inside(x to y)
		}
	}

	/**
	 * @see add
	 */
	operator fun plus(other: Hitbox2D): Hitbox2D = add(other)

	override fun toString(): String {
		return "Hitbox2D(id=$id, min=[$minX, $minY], max=[$maxX, $maxY])"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Hitbox2D) return false
		return this.id == other.id
	}

	override fun hashCode(): Int = id.hashCode()

	companion object {

		/**
		 * Creates a rectangular hitbox given its position and size.
		 * @param x The x coordinate of the top-left corner.
		 * @param y The y coordinate of the top-left corner.
		 * @param width The width of the rectangle.
		 * @param height The height of the rectangle.
		 * @return A Hitbox2D representing the rectangle.
		 */
		fun rectangle(x: Float, y: Float, width: Float, height: Float): Hitbox2D {
			return Hitbox2D(
				minX = x,
				minY = y,
				maxX = x + width,
				maxY = y + height
			)
		}

		/**
		 * Creates a rectangular hitbox given its position and size using integer parameters.
		 * @param x The x coordinate of the top-left corner.
		 * @param y The y coordinate of the top-left corner.
		 * @param width The width of the rectangle.
		 * @param height The height of the rectangle.
		 * @return A Hitbox2D representing the rectangle.
		 */
		fun rectangle(x: Int, y: Int, width: Int, height: Int): Hitbox2D {
			return rectangle(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
		}

		/**
		 * Creates a rotated rectangular hitbox given its position, size, and rotation angle.
		 * @param x The x coordinate of the top-left corner.
		 * @param y The y coordinate of the top-left corner.
		 * @param width The width of the rectangle.
		 * @param height The height of the rectangle.
		 * @param degrees The rotation angle in degrees.
		 * @return A Hitbox2D representing the rotated rectangle.
		 */
		fun rotatedRectangle(
			x: Float,
			y: Float,
			width: Float,
			height: Float,
			degrees: Float,
			centerX: Float = x + (width / 2f),
			centerY: Float = y + (height / 2f)
		): Hitbox2D {
			if (degrees == 0f) {
				return rectangle(x, y, width, height)
			}

			val angleRad = -degrees.toDouble() * (PI / 180.0)
			val cos = cos(angleRad).toFloat()
			val sin = sin(angleRad).toFloat()

			fun rotate(px: Float, py: Float): Pair<Float, Float> {
				val tx = px - centerX
				val ty = py - centerY

				// rotate backwards
				val rotatedX = cos * tx - sin * ty
				val rotatedY = sin * tx + cos * ty

				val finalX = rotatedX + centerX
				val finalY = rotatedY + centerY

				return finalX to finalY
			}

			val corners = listOf(
				rotate(x, y),                    // top-left
				rotate(x + width, y),            // top-right
				rotate(x + width, y + height),   // bottom-right
				rotate(x, y + height)            // bottom-left
			)

			val minX = corners.minOf { it.first }
			val minY = corners.minOf { it.second }
			val maxX = corners.maxOf { it.first }
			val maxY = corners.maxOf { it.second }

			return Hitbox2D(minX, minY, maxX, maxY) { (px, py) ->
				val (finalX, finalY) = rotate(px, py)
				finalX in x..(x + width) && finalY in y..(y + height)
			}
		}

		/**
		 * Creates a circular hitbox given its center and radius.
		 * @param x The x coordinate of the center.
		 * @param y The y coordinate of the center.
		 * @param radius The radius of the circle.
		 * @return A Hitbox2D representing the circle.
		 */
		fun circular(x: Float, y: Float, radius: Float): Hitbox2D {
			return Hitbox2D(
				minX = x - radius,
				minY = y - radius,
				maxX = x + radius,
				maxY = y + radius,
				inside = { (px, py) ->
					val dx = px - x
					val dy = py - y
					dx * dx + dy * dy <= radius * radius
				}
			)
		}

		/**
		 * Creates a triangular hitbox given its three vertices.
		 * @param x1 The x coordinate of the first vertex.
		 * @param y1 The y coordinate of the first vertex.
		 * @param x2 The x coordinate of the second vertex.
		 * @param y2 The y coordinate of the second vertex.
		 * @param x3 The x coordinate of the third vertex.
		 * @param y3 The y coordinate of the third vertex.
		 * @return A Hitbox2D representing the triangle.
		 */
		fun triangular(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Hitbox2D {
			fun triangleArea(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Float {
				return abs((x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)) / 2.0f)
			}

			val minX = minOf(x1, x2, x3)
			val minY = minOf(y1, y2, x3)
			val maxX = maxOf(x1, x2, x3)
			val maxY = maxOf(y1, y2, x3)

			return Hitbox2D(
				minX = minX,
				minY = minY,
				maxX = maxX,
				maxY = maxY,
				inside = { (px, py) ->
					val areaOrig = triangleArea(x1, y1, x2, y2, x3, y3)
					val area1 = triangleArea(px, py, x2, y2, x3, y3)
					val area2 = triangleArea(x1, y1, px, py, x3, y3)
					val area3 = triangleArea(x1, y1, x2, y2, px, py)
					areaOrig == area1 + area2 + area3
				}
			)
		}

		/**
		 * Creates a full hitbox that contains all points.
		 * @return A Hitbox2D that is always full.
		 */
		fun full(): Hitbox2D {
			return Hitbox2D(
				Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY,
				Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY
			) { true }
		}

		/**
		 * Creates an empty hitbox that contains no points.
		 * @return A Hitbox2D that is always empty.
		 */
		fun empty(): Hitbox2D {
			return Hitbox2D(Float.NaN, Float.NaN, Float.NaN, Float.NaN) { false }
		}

	}

}
