@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kray.toVector2
import raylib.internal.*

/**
 * Represents a font in raylib.
 */
class Font(internal val raw: CValue<raylib.internal.Font>) {

	/**
	 * The base size of the font.
	 */
	val baseSize: Int
		get() = raw.useContents { baseSize }

	/**
	 * The number of glyphs in the font.
	 */
	val glyphCount: Int
		get() = raw.useContents { glyphCount }

	/**
	 * The texture associated with the font.
	 */
	val texture: Texture2D
		get() = Texture2D(raw.useContents { texture })

	companion object {

		/**
		 * Gets the default font.
		 * @return The default font.
		 */
		fun default(): Font
			= Font(GetFontDefault())

		/**
		 * Loads a font from the specified path.
		 * @param path The path to the font file, relative to [appDir].
		 * @return The loaded font.
		 */
		fun load(path: String): Font = Font(LoadFont(path.inAppDir()))

		/**
		 * Loads a font from the specified file.
		 * @param file The file to load the font from.
		 * @return The loaded font.
		 */
		fun load(file: File) = load(file.absolutePath)

		/**
		 * Loads a font from an image.
		 * @param image The image to load the font from.
		 * @param key The color key to use for transparency.
		 * @param firstChar The first character in the font (ASCII code).
		 * @return The loaded font.
		 */
		fun fromImage(image: Image, key: Color, firstChar: Int): Font {
			return Font(LoadFontFromImage(image.raw, key.raw(), firstChar))
		}
	}

	/**
	 * Whether the font is valid.
	 */
	val isValid: Boolean
		get() = IsFontValid(raw)

	/**
	 * Unloads the font from memory.
	 */
	fun unload() {
		UnloadFont(raw)
	}

}

/**
 * Draws text on the given canvas.
 * @param x The X coordinate to draw the text at.
 * @param y The Y coordinate to draw the text at.
 * @param text The text to draw.
 * @param color The color of the text. Default is white.
 * @param fontSize The size of the font. Default is the base size of the font.
 * @param font The font to use. Default is the default font.
 * @param spacing The spacing between characters. Default is 0.
 * @param rotX The X coordinate of the rotation origin. Default is 0.
 * @param rotY The Y coordinate of the rotation origin. Default is 0.
 * @param rotation The rotation angle in degrees. Default is 0.
 */
fun Canvas.drawText(
	x: Int,
	y: Int,
	text: String,
	color: Color = Color.WHITE,
	fontSize: Int = font.baseSize,
	font: Font = Font.default(),
	spacing: Float = 0F,
	rotX: Float = 0F,
	rotY: Float = 0F,
	rotation: Float = 0F,
) {
	ensureDrawing()
	DrawTextPro(
		font.raw,
		text,
		(x to y).toVector2(),
		(rotX to rotY).toVector2(),
		rotation,
		fontSize.toFloat(),
		spacing,
		color.raw()
	)
}

/**
 * Sets the line spacing for text rendering on the given canvas.
 * @param spacing The line spacing to set.
 */
fun Canvas.setLineSpacing(spacing: Int) {
	SetTextLineSpacing(spacing)
}
