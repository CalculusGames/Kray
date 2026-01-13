package kray

import raylib.Matrix4
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * A 3D hitbox for collision detection.
 * @property minX The side of the hitbox facing negative infinity on the X axis
 * @property minY The side of the hitbox facing **positive** infinity on the Y axis
 * @property minZ The side of the hitbox facing negative infinity on the Z axis
 * @property maxX The side of the hitbox facing positive infinity on the X axis
 * @property maxY The side of the hitbox facing **negative** infinity on the Y axis
 * @property maxZ The side of the hitbox facing positive infinity on the Z axis
 * @property inside A function that takes a triple of (x, y, z) coordinates and returns true if the point is inside the hitbox.
 */
class Hitbox3D(
	val minX: Float,
	val minY: Float,
	val minZ: Float,
	val maxX: Float,
	val maxY: Float,
	val maxZ: Float,
	val inside: (Triple<Float, Float, Float>) -> Boolean,
) : Hitbox {
	override val id: Long = Random.nextLong()

	/**
	 * Checks if the point defined by [x], [y], and [z] coordinates is inside the hitbox.
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @param z The z coordinate of the point.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(x: Float, y: Float, z: Float): Boolean = inside(x to y to z)

	/**
	 * Checks if the point defined by [x], [y], and [z] coordinates is inside the hitbox.
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @param z The z coordinate of the point.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(x: Int, y: Int, z: Int): Boolean = inside(x.toFloat(), y.toFloat(), z.toFloat())

	/**
	 * Checks if the point defined by the [point] triple is inside the hitbox.
	 * @param point A triple representing the (x, y, z) coordinates of the point.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(point: Triple<Int, Int, Int>): Boolean =
		inside(point.first.toFloat(), point.second.toFloat(), point.third.toFloat())

	/**
	 * Checks if the point defined by the [x], [y], and [z] triple is inside the hitbox.
	 * @param x The X coordinate of the triple
	 * @param y The Y coordinate of the triple
	 * @param z The Z coordinate of the triple
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(x: Double, y: Double, z: Double): Boolean =
		inside(x.toFloat(), y.toFloat(), z.toFloat())

	/**
	 * Checks if the point defined by the [point] triple is inside the hitbox.
	 * @param point A triple representing the (x, y, z) coordinates of the
	 * point.
	 * @return True if the point is inside the hitbox, false otherwise.
	 */
	fun inside(point: Triple<Double, Double, Double>): Boolean =
		inside(point.first.toFloat(), point.second.toFloat(), point.third.toFloat())


	/**
	 * Returns whether the sprite is left of the given X boundary using [minX].
	 * @param x The X value to check
	 * @return true if left, false otherwise
	 */
	fun isLeftX(x: Float): Boolean = minX >= x

	/**
	 * Returns whether the sprite is right of the given X boundary using [maxX].
	 * @param x The X value to check
	 * @return true if right, false otherwise
	 */
	fun isRightX(x: Float): Boolean = maxX <= x

	/**
	 * Returns whether the sprite is left of the given Z boundary using [minZ].
	 * @param z The Z value to check
	 * @return true if left, false otherwise
	 */
	fun isLeftZ(z: Float): Boolean = minX >= z

	/**
	 * Returns whether the sprite is right of the given Z boundary using [maxZ].
	 * @param z The Z value to check
	 * @return true if right, false otherwise
	 */
	fun isRightZ(z: Float): Boolean = maxZ <= z

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
	 * Creates a rectangular hitbox defined by its minimum and maximum x, y, and z coordinates.
	 * @param minX The minimum x coordinate.
	 * @param minY The minimum y coordinate.
	 * @param minZ The minimum z coordinate.
	 * @param maxX The maximum x coordinate.
	 * @param maxY The maximum y coordinate.
	 * @param maxZ The maximum z coordinate.
	 */
	constructor(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float) : this(
		minX = minX,
		minY = minY,
		minZ = minZ,
		maxX = maxX,
		maxY = maxY,
		maxZ = maxZ,
		inside = { (x, y, z) ->
			x in minX..maxX && y in minY..maxY && z in minZ..maxZ
		}
	)

	/**
	 * Creates a rectangular hitbox defined by its minimum and maximum points.
	 * @param min A triple representing the minimum (x, y, z) coordinates.
	 * @param max A triple representing the maximum (x, y, z) coordinates.
	 */
	constructor(min: Triple<Float, Float, Float>, max: Triple<Float, Float, Float>) : this(
		minX = min.first,
		minY = min.second,
		minZ = min.third,
		maxX = max.first,
		maxY = max.second,
		maxZ = max.third
	)

	/**
	 * Adds a cubic hitbox to this hitbox.
	 * The resulting hitbox contains all points that are inside either the original hitbox or the new cube.
	 * @param x The starting x coordinate of the cube.
	 * @param y The starting y coordinate of the cube.
	 * @param z The starting z coordinate of the cube.
	 * @param size The length of each edge of the cube.
	 * @return A new Hitbox3D representing the union of the original hitbox and the new cube.
	 */
	fun addCube(x: Float, y: Float, z: Float, size: Float): Hitbox3D {
		val cubeHitbox = cube(x, y, z, size)
		return this.add(cubeHitbox)
	}

	/**
	 * Adds a rectangular hitbox to this hitbox.
	 * The resulting hitbox contains all points that are inside either the original hitbox or the new rectangle.
	 * @param minX The minimum x coordinate of the rectangle.
	 * @param minY The minimum y coordinate of the rectangle.
	 * @param minZ The minimum z coordinate of the rectangle.
	 * @param maxX The maximum x coordinate of the rectangle.
	 * @param maxY The maximum y coordinate of the rectangle.
	 * @param maxZ The maximum z coordinate of the rectangle.
	 * @return A new Hitbox3D representing the union of the original hitbox and the new rectangle.
	 */
	fun addRectangle(
		minX: Float, minY: Float, minZ: Float,
		maxX: Float, maxY: Float, maxZ: Float
	): Hitbox3D {
		val rectangleHitbox = rectangle(minX, minY, minZ, maxX, maxY, maxZ)
		return this.add(rectangleHitbox)
	}

	/**
	 * Adds another hitbox to this hitbox.
	 * The resulting hitbox contains all points that are inside either the original hitbox or the other hitbox.
	 * @param other The other Hitbox3D to add.
	 * @return A new Hitbox3D representing the union of the original hitbox and the other hitbox.
	 */
	fun add(other: Hitbox3D): Hitbox3D {
		val minX = minOf(minX, other.minX)
		val minY = minOf(minY, other.minY)
		val minZ = minOf(minZ, other.minZ)
		val maxX = minOf(maxX, other.maxX)
		val maxY = minOf(maxY, other.maxY)
		val maxZ = minOf(maxZ, other.maxZ)

		return Hitbox3D(minX, minY, minZ, maxX, maxY, maxZ) { (px, py, pz) ->
			this.inside(px to py to pz) || other.inside(px to py to pz)
		}
	}

	/**
	 * @see add
	 */
	operator fun plus(other: Hitbox3D): Hitbox3D = add(other)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Hitbox3D) return false
		return this.id == other.id
	}

	override fun hashCode(): Int = id.hashCode()

	override fun toString(): String {
		return "Hitbox3D(id=$id, min=[$minX, $minY, $minZ], max=[$maxX, $maxY, $maxZ])"
	}

	companion object {

		/**
		 * Creates a cubic hitbox defined by its starting point and size.
		 * @param x The starting x coordinate.
		 * @param y The starting y coordinate.
		 * @param z The starting z coordinate.
		 * @param size The length of each edge of the cube.
		 * @return A new Hitbox3D representing the cube.
		 */
		fun cube(x: Float, y: Float, z: Float, size: Float): Hitbox3D {
			return Hitbox3D(
				minX = x,
				minY = y,
				minZ = z,
				maxX = x + size,
				maxY = y + size,
				maxZ = z + size
			)
		}

		/**
		 * Creates a cubic hitbox defined by its starting point and size.
		 * @param x The starting x coordinate.
		 * @param y The starting y coordinate.
		 * @param z The starting z coordinate.
		 * @param size The length of each edge of the cube.
		 * @return A new Hitbox3D representing the cube.
		 */
		fun cube(x: Int, y: Int, z: Int, size: Int): Hitbox3D {
			return cube(x.toFloat(), y.toFloat(), z.toFloat(), size.toFloat())
		}

		/**
		 * Creates a rotated cubic hitbox defined by its starting point, size, and rotation angles.
		 * @param x The starting x coordinate.
		 * @param y The starting y coordinate.
		 * @param z The starting z coordinate.
		 * @param size The length of each edge of the cube.
		 * @param degreesX The rotation angle around the x-axis in degrees.
		 * @param degreesY The rotation angle around the y-axis in degrees.
		 * @param degreesZ The rotation angle around the z-axis in degrees.
		 * @return A new Hitbox3D representing the rotated cube.
		 */
		fun rotatedCube(
			x: Float,
			y: Float,
			z: Float,
			size: Float,
			degreesX: Float,
			degreesY: Float,
			degreesZ: Float
		): Hitbox3D {
			return rotatedRectangle(
				x,
				y,
				z,
				size,
				size,
				size,
				degreesX,
				degreesY,
				degreesZ,
				centerX = x + size / 2f,
				centerY = y + size / 2f,
				centerZ = z + size / 2f
			)
		}

		/**
		 * Creates a rotated cubic hitbox defined by its starting point, size, and rotation angles.
		 * @param x The starting x coordinate.
		 * @param y The starting y coordinate.
		 * @param z The starting z coordinate.
		 * @param size The length of each edge of the cube.
		 * @param degreesX The rotation angle around the x-axis in degrees.
		 * @param degreesY The rotation angle around the y-axis in degrees.
		 * @param degreesZ The rotation angle around the z-axis in degrees.
		 * @return A new Hitbox3D representing the rotated cube.
		 */
		fun rotatedCube(
			x: Int,
			y: Int,
			z: Int,
			size: Int,
			degreesX: Float,
			degreesY: Float,
			degreesZ: Float
		): Hitbox3D {
			return rotatedCube(
				x.toFloat(),
				y.toFloat(),
				z.toFloat(),
				size.toFloat(),
				degreesX,
				degreesY,
				degreesZ
			)
		}

		/**
		 * Creates a rectangular hitbox defined by its minimum and maximum x, y, and z coordinates.
		 * @param minX The minimum x coordinate.
		 * @param minY The minimum y coordinate.
		 * @param minZ The minimum z coordinate.
		 * @param maxX The maximum x coordinate.
		 * @param maxY The maximum y coordinate.
		 * @param maxZ The maximum z coordinate.
		 * @return A new Hitbox3D representing the rectangle.
		 */
		fun rectangle(
			minX: Float, minY: Float, minZ: Float,
			maxX: Float, maxY: Float, maxZ: Float
		): Hitbox3D {
			return Hitbox3D(
				minX = minX,
				minY = minY,
				minZ = minZ,
				maxX = maxX,
				maxY = maxY,
				maxZ = maxZ
			)
		}

		/**
		 * Creates a rectangular hitbox defined by its minimum and maximum x, y, and z coordinates.
		 * @param minX The minimum x coordinate.
		 * @param minY The minimum y coordinate.
		 * @param minZ The minimum z coordinate.
		 * @param maxX The maximum x coordinate.
		 * @param maxY The maximum y coordinate.
		 * @param maxZ The maximum z coordinate.
		 * @return A new Hitbox3D representing the rectangle.
		 */
		fun rectangle(
			minX: Int, minY: Int, minZ: Int,
			maxX: Int, maxY: Int, maxZ: Int
		): Hitbox3D {
			return rectangle(
				minX.toFloat(), minY.toFloat(), minZ.toFloat(),
				maxX.toFloat(), maxY.toFloat(), maxZ.toFloat()
			)
		}

		/**
		 * Creates a rotated rectangular hitbox defined by its minimum and maximum x, y, and z coordinates and rotation angles.
		 * @param x The starting x coordinate.
		 * @param y The starting y coordinate.
		 * @param z The starting z coordinate.
		 * @param width The width of the rectangle.
		 * @param height The height of the rectangle.
		 * @param depth The depth of the rectangle.
		 * @param pitch The rotation angle around the x-axis in degrees.
		 * @param yaw The rotation angle around the y-axis in degrees.
		 * @param roll The rotation angle around the z-axis in degrees.
		 * @return A new Hitbox3D representing the rotated rectangle.
		 */
		fun rotatedRectangle(
			x: Float,
			y: Float,
			z: Float,
			width: Float,
			height: Float,
			depth: Float,
			pitch: Float,
			yaw: Float,
			roll: Float,
			centerX: Float = x + width / 2f,
			centerY: Float = y + height / 2f,
			centerZ: Float = z + depth / 2f
		): Hitbox3D {
			val pitchRad = -pitch * (PI / 180.0)
			val yawRad = -yaw * (PI / 180.0)
			val rollRad = -roll * (PI / 180.0)

			val cosPitch = cos(pitchRad).toFloat()
			val sinPitch = sin(pitchRad).toFloat()
			val cosYaw = cos(yawRad).toFloat()
			val sinYaw = sin(yawRad).toFloat()
			val cosRoll = cos(rollRad).toFloat()
			val sinRoll = sin(rollRad).toFloat()

			fun rotate(px: Float, py: Float, pz: Float): Triple<Float, Float, Float> {
				var tx = px - centerX
				var ty = py - centerY
				var tz = pz - centerZ

				// raylib uses YXZ order, so inverse is ZXY (roll, pitch, yaw)

				// inverse roll
				var tempX = cosRoll * tx - sinRoll * ty
				var tempY = sinRoll * tx + cosRoll * ty
				tx = tempX
				ty = tempY

				// inverse pitch
				tempY = cosPitch * ty - sinPitch * tz
				var tempZ = sinPitch * ty + cosPitch * tz
				ty = tempY
				tz = tempZ

				// inverse yaw
				tempX = cosYaw * tx + sinYaw * tz
				tempZ = -sinYaw * tx + cosYaw * tz
				tx = tempX
				tz = tempZ

				val finalX = tx + centerX
				val finalY = ty + centerY
				val finalZ = tz + centerZ

				return finalX to finalY to finalZ
			}

			val (rminX, rminY, rminZ) = rotate(x, y, z)
			val (rmaxX, rmaxY, rmaxZ) = rotate(x + width, y + height, z + depth)

			return Hitbox3D(rminX, rminY, rminZ, rmaxX, rmaxY, rmaxZ) { (px, py, pz) ->
				val (finalX, finalY, finalZ) = rotate(px, py, pz)
				finalX in x..(x + width) && finalY in y..(y + height) && finalZ in z..(z + depth)
			}
		}

		/**
		 * Creates a transformed rectangular hitbox defined by its position, size, and transformation matrix.
		 * @param x The starting x coordinate.
		 * @param y The starting y coordinate.
		 * @param z The starting z coordinate.
		 * @param width The width of the rectangle.
		 * @param height The height of the rectangle.
		 * @param depth The depth of the rectangle.
		 * @param transform The transformation matrix to apply.
		 * @return A new Hitbox3D representing the transformed rectangle.
		 */
		fun transformedRectangle(
			x: Float,
			y: Float,
			z: Float,
			width: Float,
			height: Float,
			depth: Float,
			transform: Matrix4
		): Hitbox3D {
			val invTransform = transform.inverted()
			fun transform(px: Float, py: Float, pz: Float): Triple<Float, Float, Float> {
				// apply inverse transform to point
				return invTransform * (px to py to pz)
			}

			val (tminX, tminY, tminZ) = transform(x, y, z)
			val (tmaxX, tmaxY, tmaxZ) = transform(x + width, y + height, z + depth)

			return Hitbox3D(tminX, tminY, tminZ, tmaxX, tmaxY, tmaxZ) { (px, py, pz) ->

				val (finalX, finalY, finalZ) = transform(px, py, pz)

				finalX in x..(x + width) && finalY in y..(y + height) && finalZ in z..(z + depth)
			}
		}

		/**
		 * Creates a spherical hitbox defined by its center and radius.
		 * @param x The x coordinate of the center.
		 * @param y The y coordinate of the center.
		 * @param z The z coordinate of the center.
		 * @param radius The radius of the sphere.
		 * @return A new Hitbox3D representing the sphere.
		 */
		fun sphere(x: Float, y: Float, z: Float, radius: Float): Hitbox3D {
			return Hitbox3D(
				minX = x - radius,
				minY = y - radius,
				minZ = z - radius,
				maxX = x + radius,
				maxY = y + radius,
				maxZ = z + radius,
				inside = { (px, py, pz) ->
					val dx = px - x
					val dy = py - y
					val dz = pz - z
					(dx * dx + dy * dy + dz * dz) <= (radius * radius)
				}
			)
		}

		/**
		 * Creates a full hitbox that contains all points.
		 * @return A Hitbox3D representing a full hitbox.
		 */
		fun full(): Hitbox3D {
			return Hitbox3D(
				Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY,
				Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY,
			) { true }
		}

		/**
		 * Creates an empty hitbox that contains no points.
		 * @return A Hitbox3D representing an empty hitbox.
		 */
		fun empty(): Hitbox3D {
			return Hitbox3D(
				Float.NaN,
				Float.NaN,
				Float.NaN,
				Float.NaN,
				Float.NaN,
				Float.NaN
			) { false }
		}

	}

}
