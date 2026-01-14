package kray.sprites

import kray.Kray
import raylib.Canvas
import raylib.drawModel
import raylib.drawTexture
import raylib.ensureDrawing

// Canvas

/**
 * Draws the given [sprite] to the canvas at its current position or at the specified [x] and [y] coordinates.
 * @param sprite The sprite to draw.
 * @param x The x-coordinate to draw the sprite at. Defaults to the sprite's current x position.
 * @param y The y-coordinate to draw the sprite at. Defaults to the sprite's current y position.
 * @param rotation The rotation to draw the sprite with. Defaults to the sprite's current rotation.
 * @param scale The scale to draw the sprite with. Defaults to the sprite's current scale
 */
fun Canvas.drawSprite(
	sprite: Sprite2D,
	x: Float = sprite.x,
	y: Float = sprite.y,
	rotation: Float = sprite.rotation,
	scale: Float = sprite.scale
) {
	val cx = sprite.width / 2f
	val cy = sprite.height / 2f

	ensureDrawing()
	drawTexture(
		sprite.raw,
		x + cx,
		y + cy,
		rotation,
		cx,
		cy,
		scale
	)
}

/**
 * Draws the given [sprite] to the canvas at its current position or at the specified [x], [y], and [z] coordinates.
 * @param sprite The sprite to draw.
 * @param x The x-coordinate to draw the sprite at. Defaults to the sprite's current x position.
 * @param y The y-coordinate to draw the sprite at. Defaults to the sprite's current y position.
 * @param z The z-coordinate to draw the sprite at. Defaults to the sprite's current z position.
 */
fun Canvas.drawSprite(
	sprite: Sprite3D,
	x: Float = sprite.x,
	y: Float = sprite.y,
	z: Float = sprite.z,
) {
	ensureDrawing()
	drawModel(sprite.raw, x, y, z)
}

// Kray

internal val registeredSprites = mutableSetOf<Sprite<*>>()

/**
 * Gets an immutable copy of all sprites drawn to the canvas so far.
 * @return A set of all drawn sprites.
 */
val Kray.drawnSprites: Set<Sprite<*>>
	get() = registeredSprites.toSet()

/**
 * Adds the given [sprite] to the Kray canvas at its current position or at the specified [x] and [y] coordinates.
 * @param sprite The sprite to add.
 * @param x The x-coordinate to add the sprite at. Defaults to the sprite's current x position.
 * @param y The y-coordinate to add the sprite at. Defaults to the sprite's current y position.
 */
fun Kray.addSprite(sprite: Sprite<*>, x: Float = sprite.x, y: Float = sprite.y) {
	registeredSprites.add(sprite)

	if (sprite is Sprite2D)
		sprite.isDrawn = true
	if (sprite is Sprite3D)
		sprite.isDrawn = true
}

/**
 * Removes the given [sprite] from the Kray canvas.
 * @param sprite The sprite to remove.
 */
fun Kray.removeSprite(sprite: Sprite<*>) {
	if (registeredSprites.remove(sprite)) {
		if (sprite is Sprite2D)
			sprite.isDrawn = false
	}
}
