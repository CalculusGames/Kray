package kray.physics

import kray.Kray.collisions
import kray.Kray.window
import kray.Positionable
import kray.Positionable2D
import kray.Positionable3D
import kray.sprites.Sprite2D
import kray.sprites.Sprite3D
import kray.sprites.registeredSprites
import kotlin.math.sqrt

private val massMultipliers = mutableMapOf<Positionable, Double>()

/**
 * The mass multiplier of the [Positionable] object.
 * This is used in physics calculations to determine the effective mass of the object.
 * The default value is 1.0.
 */
var Positionable.mass: Double
	get() = massMultipliers[this] ?: 1.0
	set(value) {
		massMultipliers[this] = value
	}

private val staticObject = mutableSetOf<Positionable>()

/**
 * Indicates whether the [Positionable] object is static (immovable) in the physics simulation.
 * Static objects do not respond to gravity, collisions, or other forces.
 */
var Positionable.isStatic: Boolean
	get() = staticObject.contains(this)
	set(value) {
		if (value) {
			staticObject.add(this)
		} else {
			staticObject.remove(this)
		}
	}

/**
 * The gravitational acceleration constant used in physics calculations.
 * The default value is 9.81 m/s².
 */
var gravity: Double = 9.81
	set(value) {
		if (value < 0.0)
			throw IllegalArgumentException("Gravity cannot be negative.")

		field = value
	}

/**
 * The default friction coefficient used in physics calculations.
 * The default value is 0.5.
 */
var defaultFrictionCoefficient: Double = 0.5
	set(value) {
		if (value < 0.0)
			throw IllegalArgumentException("Default friction coefficient cannot be negative.")

		field = value
	}

private val frictionCoefficients = mutableMapOf<Positionable, Double>()

/**
 * The friction coefficient of the [Positionable] object.
 * This is used in physics calculations to determine the frictional force acting on the object.
 * The default value is [defaultFrictionCoefficient].
 */
var Positionable.frictionCoefficient: Double
	get() = frictionCoefficients[this] ?: defaultFrictionCoefficient
	set(value) {
		if (value < 0.0)
			throw IllegalArgumentException("Friction coefficient cannot be negative.")

		frictionCoefficients[this] = value
	}

/**
 * The default restitution coefficient used in physics calculations.
 *
 * Determines how bouncy objects are after collisions. The default value is 0.5.
 */
var defaultRestitutionCoefficient: Double = 0.5
	set(value) {
		if (value < 0.0)
			throw IllegalArgumentException("Default restitution coefficient cannot be negative.")

		field = value
	}

private val restitutionCoefficients = mutableMapOf<Positionable, Double>()

/**
 * The restitution coefficient of the [Positionable] object.
 * This is used in physics calculations to determine how bouncy the object is after collisions.
 * The default value is [defaultRestitutionCoefficient].
 */
var Positionable.restitutionCoefficient: Double
	get() = restitutionCoefficients[this] ?: defaultRestitutionCoefficient
	set(value) {
		if (value < 0.0)
			throw IllegalArgumentException("Restitution coefficient cannot be negative.")

		restitutionCoefficients[this] = value
	}

private val acceleration2D = mutableMapOf<Positionable2D, Pair<Double, Double>>()

/**
 * The x-component of the 2D acceleration of the [Positionable2D] object.
 */
var Positionable2D.ax: Double
	get() = acceleration2D[this]?.first ?: 0.0
	set(value) {
		val current = acceleration2D[this] ?: Pair(0.0, 0.0)
		acceleration2D[this] = Pair(value, current.second)
	}

/**
 * The y-component of the 2D acceleration of the [Positionable2D] object.
 */
var Positionable2D.ay: Double
	get() = acceleration2D[this]?.second ?: 0.0
	set(value) {
		val current = acceleration2D[this] ?: Pair(0.0, 0.0)
		acceleration2D[this] = Pair(current.first, value)
	}

private val velocity2D = mutableMapOf<Positionable2D, Pair<Double, Double>>()

/**
 * The x-component of the 2D velocity of the [Positionable2D] object.
 */
var Positionable2D.vx: Double
	get() = velocity2D[this]?.first ?: 0.0
	set(value) {
		val current = velocity2D[this] ?: Pair(0.0, 0.0)
		velocity2D[this] = Pair(value, current.second)
	}

/**
 * The y-component of the 2D velocity of the [Positionable2D] object
 */
var Positionable2D.vy: Double
	get() = velocity2D[this]?.second ?: 0.0
	set(value) {
		val current = velocity2D[this] ?: Pair(0.0, 0.0)
		velocity2D[this] = Pair(current.first, value)
	}

private val acceleration3D = mutableMapOf<Positionable3D, Triple<Double, Double, Double>>()

/**
 * The x-component of the 3D acceleration of the [Positionable3D] object.
 */
var Positionable3D.ax: Double
	get() = acceleration3D[this]?.first ?: 0.0
	set(value) {
		val current = acceleration3D[this] ?: Triple(0.0, 0.0, 0.0)
		acceleration3D[this] = Triple(value, current.second, current.third)
	}

/**
 * The y-component of the 3D acceleration of the [Positionable3D] object.
 */
var Positionable3D.ay: Double
	get() = acceleration3D[this]?.second ?: 0.0
	set(value) {
		val current = acceleration3D[this] ?: Triple(0.0, 0.0, 0.0)
		acceleration3D[this] = Triple(current.first, value, current.third)
	}

/**
 * The z-component of the 3D acceleration of the [Positionable3D] object.
 */
var Positionable3D.az: Double
	get() = acceleration3D[this]?.third ?: 0.0
	set(value) {
		val current = acceleration3D[this] ?: Triple(0.0, 0.0, 0.0)
		acceleration3D[this] = Triple(current.first, current.second, value)
	}

private val velocity3D = mutableMapOf<Positionable3D, Triple<Double, Double, Double>>()

/**
 * The x-component of the 3D velocity of the [Positionable3D] object.
 */
var Positionable3D.vx: Double
	get() = velocity3D[this]?.first ?: 0.0
	set(value) {
		val current = velocity3D[this] ?: Triple(0.0, 0.0, 0.0)
		velocity3D[this] = Triple(value, current.second, current.third)
	}

/**
 * The y-component of the 3D velocity of the [Positionable3D] object.
 */
var Positionable3D.vy: Double
	get() = velocity3D[this]?.second ?: 0.0
	set(value) {
		val current = velocity3D[this] ?: Triple(0.0, 0.0, 0.0)
		velocity3D[this] = Triple(current.first, value, current.third)
	}

/**
 * The z-component of the 3D velocity of the [Positionable3D] object.
 */
var Positionable3D.vz: Double
	get() = velocity3D[this]?.third ?: 0.0
	set(value) {
		val current = velocity3D[this] ?: Triple(0.0, 0.0, 0.0)
		velocity3D[this] = Triple(current.first, current.second, value)
	}

/**
 * The y-coordinate representing the ground level in the physics simulation.
 * Objects should not fall below this y-coordinate.
 */
var groundY: Float = 0F
	set(value) {
		if (value < 0F)
			throw IllegalArgumentException("Ground Y cannot be negative.")

		field = value
	}

/**
 * The maximum x-coordinate boundary in the physics simulation.
 * Unlike the ground, sprites will bounce off this boundary instead of stopping.
 */
var maxX: Float = Float.MAX_VALUE
	set(value) {
		if (value <= 0F)
			throw IllegalArgumentException("Max X must be positive.")

		field = value
	}

/**
 * The maximum y-coordinate boundary in the physics simulation.
 * Unlike the ground, sprites will bounce off this boundary instead of stopping.
 */
var maxZ: Float = Float.MAX_VALUE
	set(value) {
		if (value <= 0F)
			throw IllegalArgumentException("Max Z must be positive.")

		field = value
	}

/**
 * Advances the physics engine by one tick, updating the positions and velocities of all non-static objects
 * based on the applied forces such as gravity and friction.
 * @return A set of all [Positionable] objects that were moved during this tick.
 */
fun engineTick(): Set<Positionable> {
	val changed = mutableSetOf<Positionable>()

	for (sprite in registeredSprites) {
		if (sprite.isStatic) continue

		when (sprite) {
			is Sprite2D -> {
				val oldX = sprite.x
				val oldY = sprite.y

				// apply acceleration to velocity
				sprite.vx += sprite.ax * window.frameTime
				sprite.vy += sprite.ay * window.frameTime

				// apply gravity
				sprite.vy += gravity * window.frameTime

				// apply velocity to position
				sprite.x += (sprite.vx * window.frameTime).toFloat()
				sprite.y += (sprite.vy * window.frameTime).toFloat()

				// apply collisions with other sprites
				val collisions = sprite.collisions
				for (other in collisions) {
					// respond with impact physics
					val normalX = (other.x - sprite.x).toDouble()
					val normalY = (other.y - sprite.y).toDouble()
					val magnitude = sqrt(normalX * normalX + normalY * normalY)

					if (magnitude != 0.0) {
						val unitNormalX = normalX / magnitude
						val unitNormalY = normalY / magnitude

						val relativeVelocityX = sprite.vx - other.vx
						val relativeVelocityY = sprite.vy - other.vy

						val velocityAlongNormal = relativeVelocityX * unitNormalX + relativeVelocityY * unitNormalY

						if (velocityAlongNormal > 0) continue // they are moving apart

						val impulseMagnitude = -(1 + sprite.restitutionCoefficient) * velocityAlongNormal /
							(1 / sprite.mass + 1 / other.mass)

						val impulseX = impulseMagnitude * unitNormalX
						val impulseY = impulseMagnitude * unitNormalY

						sprite.vx += impulseX / sprite.mass
						sprite.vy += impulseY / sprite.mass

						other.vx -= impulseX / other.mass
						other.vy -= impulseY / other.mass
					}
				}

				// apply collision with X boundary
				if (sprite.x < 0F) {
					sprite.x = 0F
					sprite.vx = -sprite.vx * sprite.restitutionCoefficient
				} else if (sprite.x > maxX) {
					sprite.x = maxX
					sprite.vx = -sprite.vx * sprite.restitutionCoefficient
				}

				// apply friction if on ground
				if (sprite.y <= groundY) {
					val normalForce = sprite.mass * gravity
					val frictionForce = sprite.frictionCoefficient * normalForce
					val frictionAcceleration = frictionForce / sprite.mass

					if (sprite.vx > 0) {
						sprite.vx -= frictionAcceleration * window.frameTime
						if (sprite.vx < 0) sprite.vx = 0.0
					} else if (sprite.vx < 0) {
						sprite.vx += frictionAcceleration * window.frameTime
						if (sprite.vx > 0) sprite.vx = 0.0
					}
				}

				// ensure sprite does not fall below ground level
				if (sprite.y > groundY) {
					sprite.y = groundY
					sprite.vy = 0.0
				}

				// finish by checking if position changed
				if (sprite.x != oldX || sprite.y != oldY) {
					changed.add(sprite)
				}
			}

			is Sprite3D -> {

			}
		}
	}

	return changed
}
