@file:OptIn(ExperimentalForeignApi::class)
package raylib

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.cValue
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kray.toByteArray
import kray.toVector2
import raylib.internal.*

/**
 * Represents a color with red, green, blue, and alpha components.
 * @property r The red component (0-255).
 * @property g The green component (0-255).
 * @property b The blue component (0-255).
 * @property a The alpha component (0-255).
 */
data class Color(val r: UByte, val g: UByte, val b: UByte, val a: UByte = 255.toUByte()) {

	init {
		require(r.toInt() in 0..255) { "red value must be between 0 and 255"}
		require(g.toInt() in 0..255) { "green value must be between 0 and 255" }
		require(b.toInt() in 0..255) { "blue value must be between 0 and 255" }
		require(a.toInt() in 0..255) { "alpha value must be between 0 and 255" }
	}

    /**
     * Creates a Color instance from a raw raylib Color structure.
     * @param raw The raw raylib Color structure.
     */
    constructor(raw: raylib.internal.Color) : this(
        r = raw.r,
        g = raw.g,
        b = raw.b,
        a = raw.a
    )

	/**
	 * Creates a Color instance from a raw raylib Color CValue.
	 * @param raw The raw raylib Color CValue.
	 */
	constructor(raw: CValue<raylib.internal.Color>) : this(
		raw.useContents {
			((a.toInt() shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()).toLong()
		}
	)

    /**
     * Creates a Color instance from integer values for red, green, blue, and alpha components.
     * @param r The red component (0-255).
     * @param g The green component (0-255).
     * @param b The blue component (0-255).
     * @param a The alpha component (0-255).
     */
    constructor(r: Int, g: Int, b: Int, a: Int = 255) : this(r.toUByte(), g.toUByte(), b.toUByte(), a.toUByte())

    /**
     * Creates a Color instance from a single integer representing an ARGB color.
     * @param argb The ARGB color as an integer.
     */
    constructor(argb: Int) : this(
		a = ((argb shr 24) and 0xFF).toUByte(),
		r = ((argb shr 16) and 0xFF).toUByte(),
		g = ((argb shr 8) and 0xFF).toUByte(),
		b = (argb and 0xFF).toUByte(),
    )

    /**
     * Creates a Color instance from a single long integer representing an ARGB color.
     * @param argb The RGBA color as a long integer.
     */
    constructor(argb: Long) : this(
		a = ((argb shr 24) and 0xFF).toUByte(),
        r = ((argb shr 16) and 0xFF).toUByte(),
        g = ((argb shr 8) and 0xFF).toUByte(),
        b = (argb and 0xFF).toUByte(),
    )

    /**
     * Creates a Color instance from a hexadecimal color string.
     * The string should be in the format "#RRGGBB" or "#RRGGBBAA". The hashtag is required.
     * @param hex The hexadecimal color string.
     */
    constructor(hex: String) : this(
        r = hex.substring(1, 3).toUByte(16),
        g = hex.substring(3, 5).toUByte(16),
        b = hex.substring(5, 7).toUByte(16),
        a = if (hex.length >= 9) hex.substring(7, 9).toUByte(16) else 255.toUByte()
    )

    /**
     * Creates a Color instance from HSV (Hue, Saturation, Value) components.
     * @param hue The hue component (0-360).
     * @param saturation The saturation component (0-1).
     * @param value The value component (0-1).
     */
    constructor(hue: Float, saturation: Float, value: Float) : this(raw = ColorFromHSV(hue, saturation, value).useContents { this })

	/**
	 * The color as an RGBA long.
	 */
	val rgba: Long
		get() {
			val r0 = r.toLong()
			val g0 = g.toLong()
			val b0 = b.toLong()
			val a0 = a.toLong()

			return (r0 shl 24) or (g0 shl 16) or (b0 shl 8) or a0
		}

	/**
	 * The color as an RGB integer. This explicitly does not include the alpha component.
	 */
	val rgb: Int
		get() {
			val r0 = r.toInt()
			val g0 = g.toInt()
			val b0 = b.toInt()

			return (r0 shl 16) or (g0 shl 8) or b0
		}

	/**
	 * The color as an ARGB long.
	 */
	val argb: Long
		get() {
			val r0 = r.toLong()
			val g0 = g.toLong()
			val b0 = b.toLong()
			val a0 = a.toLong()

			return (a0 shl 24) or (r0 shl 16) or (g0 shl 8) or b0
		}

	/**
	 * The color as a hexadecimal string in ARGB format.
	 */
	val hex8: String
		get() = argb.toString(16).run {
			var str = this
			while (length < 8) {
				str = "0$str"
			}
			str
		}

	/**
	 * The color as a hexadecimal string in RGB format.
	 */
	val hex6: String
		get() = rgb.toString(16).run {
			var str = this
			while (length < 6) {
				str = "0$str"
			}
			str
		}

    internal fun raw(): CValue<raylib.internal.Color> {
        return cValue<raylib.internal.Color> {
            r = this@Color.r
            g = this@Color.g
            b = this@Color.b
            a = this@Color.a
        }
    }

	/**
	 * Creates a linear interpolated color between this color and another color.
	 * @param other The other color to interpolate
	 * @param factor The factor of the interpolation (0.0-1.0)
	 * @return A new Color instance interpolated between the two
	 */
	fun lerp(other: Color, factor: Float): Color {
		val newRaw = ColorLerp(raw(), other.raw(), factor)
		return Color(newRaw)
	}

	/**
	 * Returns a new Color instance with the alpha component adjusted by the specified factor.
	 * @param factor The factor to adjust the alpha component (0.0 to 1.0).
	 * @return A new Color instance with the adjusted alpha component.
	 */
	fun transparent(factor: Float): Color {
		val newAlpha = (a.toInt() * factor).toInt().coerceIn(0, 255)
		return Color(r, g, b, newAlpha.toUByte())
	}

	/**
	 * Applies contrast to this color between a factor of 0.0 and 1.0.
	 * @param factor The factor of the contrast (0.0-1.0)
	 * @return A new Color instance with the contrast applied
	 */
	fun contrast(factor: Float): Color {
		val newRaw = ColorContrast(raw(), factor)
		return Color(newRaw)
	}

	/**
	 * Applies brightness to this color between a factor of 0.0 and 1.0.
	 * @param factor The factor of the contrast (0.0-1.0)
	 * @return A new Color instance with the brightness applied
	 */
	fun brightness(factor: Float): Color {
		val newRaw = ColorBrightness(raw(), factor)
		return Color(newRaw)
	}

	/**
	 * Takes this color and tints it with another color.
	 * @param tint The color to tint with
	 * @return A new Color instance with the tint applied
	 */
	fun tint(other: Color): Color {
		val newRaw = ColorTint(raw(), other.raw())
		return Color(newRaw)
	}

    /**
     * Predefined color constants.
     */
    companion object {
		//<editor-fold desc="Color Constants" defaultState="collapsed">

        /**
         * Air Force blue (#5D8AA8)
         */
        val AIR_FORCE_BLUE = Color(93, 138, 168)

        /**
         * Alice blue (#F0F8FF)
         */
        val ALICE_BLUE = Color(240, 248, 255)

        /**
         * Alizarin crimson (#E32636)
         */
        val ALIZARIN_CRIMSON = Color(227, 38, 54)

        /**
         * Almond (#EFDECD)
         */
        val ALMOND = Color(239, 222, 205)

        /**
         * Amaranth (#E52B50)
         */
        val AMARANTH = Color(229, 43, 80)

        /**
         * Amber (#FFBF00)
         */
        val AMBER = Color(255, 191, 0)

        /**
         * American rose (#FF033E)
         */
        val AMERICAN_ROSE = Color(255, 3, 62)

        /**
         * Amethyst (#9966CC)
         */
        val AMETHYST = Color(153, 102, 204)

        /**
         * Android Green (#A4C639)
         */
        val ANDROID_GREEN = Color(164, 198, 57)

        /**
         * Antique brass (#CD9575)
         */
        val ANTIQUE_BRASS = Color(205, 149, 117)

        /**
         * Antique fuchsia (#915C83)
         */
        val ANTIQUE_FUCHSIA = Color(145, 92, 131)

        /**
         * Antique white (#FAEBD7)
         */
        val ANTIQUE_WHITE = Color(250, 235, 215)

        /**
         * Anti-flash white (#F2F3F4)
         */
        val ANTI_FLASH_WHITE = Color(242, 243, 244)

        /**
         * Ao (#008000)
         */
        val AO = Color(0, 128, 0)

        /**
         * Apple green (#8DB600)
         */
        val APPLE_GREEN = Color(141, 182, 0)

        /**
         * Apricot (#FBCEB1)
         */
        val APRICOT = Color(251, 206, 177)

        /**
         * Aqua (#00FFFF)
         */
        val AQUA = Color(0, 255, 255)

        /**
         * Aquamarine (#7FFFD4)
         */
        val AQUAMARINE = Color(127, 255, 212)

        /**
         * Army green (#4B5320)
         */
        val ARMY_GREEN = Color(75, 83, 32)

        /**
         * Arylide yellow (#E9D66B)
         */
        val ARYLIDE_YELLOW = Color(233, 214, 107)

        /**
         * Ash grey (#B2BEB5)
         */
        val ASH_GREY = Color(178, 190, 181)

        /**
         * Asparagus (#87A96B)
         */
        val ASPARAGUS = Color(135, 169, 107)

        /**
         * Atomic tangerine (#FF9966)
         */
        val ATOMIC_TANGERINE = Color(255, 153, 102)

        /**
         * Auburn (#A52A2A)
         */
        val AUBURN = Color(165, 42, 42)

        /**
         * Aureolin (#FDEE00)
         */
        val AUREOLIN = Color(253, 238, 0)

        /**
         * AuroMetalSaurus (#6E7F80)
         */
        val AUROMETALSAURUS = Color(110, 127, 128)

        /**
         * Awesome (#FF2052)
         */
        val AWESOME = Color(255, 32, 82)

        /**
         * Azure (#007FFF)
         */
        val AZURE = Color(0, 127, 255)

        /**
         * Azure mist/web (#F0FFFF)
         */
        val AZURE_MIST_WEB = Color(240, 255, 255)

        /**
         * Baby blue (#89CFF0)
         */
        val BABY_BLUE = Color(137, 207, 240)

        /**
         * Baby blue eyes (#A1CAF1)
         */
        val BABY_BLUE_EYES = Color(161, 202, 241)

        /**
         * Baby pink (#F4C2C2)
         */
        val BABY_PINK = Color(244, 194, 194)

        /**
         * Ball Blue (#21ABCD)
         */
        val BALL_BLUE = Color(33, 171, 205)

        /**
         * Banana Mania (#FAE7B5)
         */
        val BANANA_MANIA = Color(250, 231, 181)

        /**
         * Banana yellow (#FFE135)
         */
        val BANANA_YELLOW = Color(255, 225, 53)

        /**
         * Battleship grey (#848482)
         */
        val BATTLESHIP_GREY = Color(132, 132, 130)

        /**
         * Bazaar (#98777B)
         */
        val BAZAAR = Color(152, 119, 123)

        /**
         * Beau blue (#BCD4E6)
         */
        val BEAU_BLUE = Color(188, 212, 230)

        /**
         * Beaver (#9F8170)
         */
        val BEAVER = Color(159, 129, 112)

        /**
         * Beige (#F5F5DC)
         */
        val BEIGE = Color(245, 245, 220)

        /**
         * Bisque (#FFE4C4)
         */
        val BISQUE = Color(255, 228, 196)

        /**
         * Bistre (#3D2B1F)
         */
        val BISTRE = Color(61, 43, 31)

        /**
         * Bittersweet (#FE6F5E)
         */
        val BITTERSWEET = Color(254, 111, 94)

        /**
         * Black (#000000)
         */
        val BLACK = Color(0, 0, 0)

        /**
         * Blanched Almond (#FFEBCD)
         */
        val BLANCHED_ALMOND = Color(255, 235, 205)

        /**
         * Bleu de France (#318CE7)
         */
        val BLEU_DE_FRANCE = Color(49, 140, 231)

        /**
         * Blizzard Blue (#ACE5EE)
         */
        val BLIZZARD_BLUE = Color(172, 229, 238)

        /**
         * Blond (#FAF0BE)
         */
        val BLOND = Color(250, 240, 190)

        /**
         * Blue (#0000FF)
         */
        val BLUE = Color(0, 0, 255)

        /**
         * Blue Bell (#A2A2D0)
         */
        val BLUE_BELL = Color(162, 162, 208)

        /**
         * Blue Gray (#6699CC)
         */
        val BLUE_GRAY = Color(102, 153, 204)

        /**
         * Blue green (#0D98BA)
         */
        val BLUE_GREEN = Color(13, 152, 186)

        /**
         * Blue purple (#8A2BE2)
         */
        val BLUE_PURPLE = Color(138, 43, 226)

        /**
         * Blue violet (#8A2BE2)
         */
        val BLUE_VIOLET = Color(138, 43, 226)

        /**
         * Blush (#DE5D83)
         */
        val BLUSH = Color(222, 93, 131)

        /**
         * Bole (#79443B)
         */
        val BOLE = Color(121, 68, 59)

        /**
         * Bondi blue (#0095B6)
         */
        val BONDI_BLUE = Color(0, 149, 182)

        /**
         * Bone (#E3DAC9)
         */
        val BONE = Color(227, 218, 201)

        /**
         * Boston University Red (#CC0000)
         */
        val BOSTON_UNIVERSITY_RED = Color(204, 0, 0)

        /**
         * Bottle green (#006A4E)
         */
        val BOTTLE_GREEN = Color(0, 106, 78)

        /**
         * Boysenberry (#873260)
         */
        val BOYSENBERRY = Color(135, 50, 96)

        /**
         * Brandeis blue (#0070FF)
         */
        val BRANDEIS_BLUE = Color(0, 112, 255)

        /**
         * Brass (#B5A642)
         */
        val BRASS = Color(181, 166, 66)

        /**
         * Brick red (#CB4154)
         */
        val BRICK_RED = Color(203, 65, 84)

        /**
         * Bright cerulean (#1DACD6)
         */
        val BRIGHT_CERULEAN = Color(29, 172, 214)

        /**
         * Bright green (#66FF00)
         */
        val BRIGHT_GREEN = Color(102, 255, 0)

        /**
         * Bright lavender (#BF94E4)
         */
        val BRIGHT_LAVENDER = Color(191, 148, 228)

        /**
         * Bright maroon (#C32148)
         */
        val BRIGHT_MAROON = Color(195, 33, 72)

        /**
         * Bright pink (#FF007F)
         */
        val BRIGHT_PINK = Color(255, 0, 127)

        /**
         * Bright turquoise (#08E8DE)
         */
        val BRIGHT_TURQUOISE = Color(8, 232, 222)

        /**
         * Bright ube (#D19FE8)
         */
        val BRIGHT_UBE = Color(209, 159, 232)

        /**
         * Brilliant lavender (#F4BBFF)
         */
        val BRILLIANT_LAVENDER = Color(244, 187, 255)

        /**
         * Brilliant rose (#FF55A3)
         */
        val BRILLIANT_ROSE = Color(255, 85, 163)

        /**
         * Brink pink (#FB607F)
         */
        val BRINK_PINK = Color(251, 96, 127)

        /**
         * British racing green (#004225)
         */
        val BRITISH_RACING_GREEN = Color(0, 66, 37)

        /**
         * Bronze (#CD7F32)
         */
        val BRONZE = Color(205, 127, 50)

        /**
         * Brown (#A52A2A)
         */
        val BROWN = Color(165, 42, 42)

        /**
         * Bubbles (#E7FEFF)
         */
        val BUBBLES = Color(231, 254, 255)

        /**
         * Bubble gum (#FFC1CC)
         */
        val BUBBLE_GUM = Color(255, 193, 204)

        /**
         * Buff (#F0DC82)
         */
        val BUFF = Color(240, 220, 130)

        /**
         * Bulgarian rose (#480607)
         */
        val BULGARIAN_ROSE = Color(72, 6, 7)

        /**
         * Burgundy (#800020)
         */
        val BURGUNDY = Color(128, 0, 32)

        /**
         * Burlywood (#DEB887)
         */
        val BURLYWOOD = Color(222, 184, 135)

        /**
         * Burnt orange (#CC5500)
         */
        val BURNT_ORANGE = Color(204, 85, 0)

        /**
         * Burnt sienna (#E97451)
         */
        val BURNT_SIENNA = Color(233, 116, 81)

        /**
         * Burnt umber (#8A3324)
         */
        val BURNT_UMBER = Color(138, 51, 36)

        /**
         * Byzantine (#BD33A4)
         */
        val BYZANTINE = Color(189, 51, 164)

        /**
         * Byzantium (#702963)
         */
        val BYZANTIUM = Color(112, 41, 99)

        /**
         * Cadet (#536872)
         */
        val CADET = Color(83, 104, 114)

        /**
         * Cadet blue (#5F9EA0)
         */
        val CADET_BLUE = Color(95, 158, 160)

        /**
         * Cadet grey (#91A3B0)
         */
        val CADET_GREY = Color(145, 163, 176)

        /**
         * Cadmium green (#006B3C)
         */
        val CADMIUM_GREEN = Color(0, 107, 60)

        /**
         * Cadmium orange (#ED872D)
         */
        val CADMIUM_ORANGE = Color(237, 135, 45)

        /**
         * Cadmium red (#E30022)
         */
        val CADMIUM_RED = Color(227, 0, 34)

        /**
         * Cadmium yellow (#FFF600)
         */
        val CADMIUM_YELLOW = Color(255, 246, 0)

        /**
         * Café au lait (#A67B5B)
         */
        val CAFE_AU_LAIT = Color(166, 123, 91)

        /**
         * Café noir (#4B3621)
         */
        val CAFE_NOIR = Color(75, 54, 33)

        /**
         * Cal Poly Pomona green (#1E4D2B)
         */
        val CAL_POLY_POMONA_GREEN = Color(30, 77, 43)

        /**
         * Cambridge Blue (#A3C1AD)
         */
        val CAMBRIDGE_BLUE = Color(163, 193, 173)

        /**
         * Camel (#C19A6B)
         */
        val CAMEL = Color(193, 154, 107)

        /**
         * Camouflage green (#78866B)
         */
        val CAMOUFLAGE_GREEN = Color(120, 134, 107)

        /**
         * Canary (#FFFF99)
         */
        val CANARY = Color(255, 255, 153)

        /**
         * Canary yellow (#FFEF00)
         */
        val CANARY_YELLOW = Color(255, 239, 0)

        /**
         * Candy apple red (#FF0800)
         */
        val CANDY_APPLE_RED = Color(255, 8, 0)

        /**
         * Candy pink (#E4717A)
         */
        val CANDY_PINK = Color(228, 113, 122)

        /**
         * Capri (#00BFFF)
         */
        val CAPRI = Color(0, 191, 255)

        /**
         * Caput mortuum (#592720)
         */
        val CAPUT_MORTUUM = Color(89, 39, 32)

        /**
         * Cardinal (#C41E3A)
         */
        val CARDINAL = Color(196, 30, 58)

        /**
         * Caribbean green (#00CC99)
         */
        val CARIBBEAN_GREEN = Color(0, 204, 153)

        /**
         * Carmine (#FF0040)
         */
        val CARMINE = Color(255, 0, 64)

        /**
         * Carmine pink (#EB4C42)
         */
        val CARMINE_PINK = Color(235, 76, 66)

        /**
         * Carmine red (#FF0038)
         */
        val CARMINE_RED = Color(255, 0, 56)

        /**
         * Carnation pink (#FFA6C9)
         */
        val CARNATION_PINK = Color(255, 166, 201)

        /**
         * Carnelian (#B31B1B)
         */
        val CARNELIAN = Color(179, 27, 27)

        /**
         * Carolina blue (#99BADD)
         */
        val CAROLINA_BLUE = Color(153, 186, 221)

        /**
         * Carrot orange (#ED9121)
         */
        val CARROT_ORANGE = Color(237, 145, 33)

        /**
         * Celadon (#ACE1AF)
         */
        val CELADON = Color(172, 225, 175)

        /**
         * Celeste (#B2FFFF)
         */
        val CELESTE = Color(178, 255, 255)

        /**
         * Celestial blue (#4997D0)
         */
        val CELESTIAL_BLUE = Color(73, 151, 208)

        /**
         * Cerise (#DE3163)
         */
        val CERISE = Color(222, 49, 99)

        /**
         * Cerise pink (#EC3B83)
         */
        val CERISE_PINK = Color(236, 59, 131)

        /**
         * Cerulean (#007BA7)
         */
        val CERULEAN = Color(0, 123, 167)

        /**
         * Cerulean blue (#2A52BE)
         */
        val CERULEAN_BLUE = Color(42, 82, 190)

        /**
         * CG Blue (#007AA5)
         */
        val CG_BLUE = Color(0, 122, 165)

        /**
         * CG Red (#E03C31)
         */
        val CG_RED = Color(224, 60, 49)

        /**
         * Chamoisee (#A0785A)
         */
        val CHAMOISEE = Color(160, 120, 90)

        /**
         * Champagne (#FAD6A5)
         */
        val CHAMPAGNE = Color(250, 214, 165)

        /**
         * Charcoal (#36454F)
         */
        val CHARCOAL = Color(54, 69, 79)

        /**
         * Chartreuse (#7FFF00)
         */
        val CHARTREUSE = Color(127, 255, 0)

        /**
         * Cherry (#DE3163)
         */
        val CHERRY = Color(222, 49, 99)

        /**
         * Cherry blossom pink (#FFB7C5)
         */
        val CHERRY_BLOSSOM_PINK = Color(255, 183, 197)

        /**
         * Chestnut (#CD5C5C)
         */
        val CHESTNUT = Color(205, 92, 92)

        /**
         * Chocolate (#D2691E)
         */
        val CHOCOLATE = Color(210, 105, 30)

        /**
         * Chrome yellow (#FFA700)
         */
        val CHROME_YELLOW = Color(255, 167, 0)

        /**
         * Cinereous (#98817B)
         */
        val CINEREOUS = Color(152, 129, 123)

        /**
         * Cinnabar (#E34234)
         */
        val CINNABAR = Color(227, 66, 52)

        /**
         * Cinnamon (#D2691E)
         */
        val CINNAMON = Color(210, 105, 30)

        /**
         * Citrine (#E4D00A)
         */
        val CITRINE = Color(228, 208, 10)

        /**
         * Classic rose (#FBCCE7)
         */
        val CLASSIC_ROSE = Color(251, 204, 231)

        /**
         * Cobalt (#0047AB)
         */
        val COBALT = Color(0, 71, 171)

        /**
         * Cocoa brown (#D2691E)
         */
        val COCOA_BROWN = Color(210, 105, 30)

        /**
         * Coffee (#6F4E37)
         */
        val COFFEE = Color(111, 78, 55)

        /**
         * Columbia blue (#9BDDFF)
         */
        val COLUMBIA_BLUE = Color(155, 221, 255)

        /**
         * Cool black (#002E63)
         */
        val COOL_BLACK = Color(0, 46, 99)

        /**
         * Cool grey (#8C92AC)
         */
        val COOL_GREY = Color(140, 146, 172)

        /**
         * Copper (#B87333)
         */
        val COPPER = Color(184, 115, 51)

        /**
         * Copper rose (#996666)
         */
        val COPPER_ROSE = Color(153, 102, 102)

        /**
         * Coquelicot (#FF3800)
         */
        val COQUELICOT = Color(255, 56, 0)

        /**
         * Coral (#FF7F50)
         */
        val CORAL = Color(255, 127, 80)

        /**
         * Coral pink (#F88379)
         */
        val CORAL_PINK = Color(248, 131, 121)

        /**
         * Coral red (#FF4040)
         */
        val CORAL_RED = Color(255, 64, 64)

        /**
         * Cordovan (#893F45)
         */
        val CORDOVAN = Color(137, 63, 69)

        /**
         * Corn (#FBEC5D)
         */
        val CORN = Color(251, 236, 93)

        /**
         * Cornell Red (#B31B1B)
         */
        val CORNELL_RED = Color(179, 27, 27)

        /**
         * Cornflower (#9ACEEB)
         */
        val CORNFLOWER = Color(154, 206, 235)

        /**
         * Cornflower blue (#6495ED)
         */
        val CORNFLOWER_BLUE = Color(100, 149, 237)

        /**
         * Cornsilk (#FFF8DC)
         */
        val CORNSILK = Color(255, 248, 220)

        /**
         * Cosmic latte (#FFF8E7)
         */
        val COSMIC_LATTE = Color(255, 248, 231)

        /**
         * Cotton candy (#FFBCD9)
         */
        val COTTON_CANDY = Color(255, 188, 217)

        /**
         * Cream (#FFFDD0)
         */
        val CREAM = Color(255, 253, 208)

        /**
         * Crimson (#DC143C)
         */
        val CRIMSON = Color(220, 20, 60)

        /**
         * Crimson glory (#BE0032)
         */
        val CRIMSON_GLORY = Color(190, 0, 50)

        /**
         * Crimson Red (#990000)
         */
        val CRIMSON_RED = Color(153, 0, 0)

        /**
         * Cyan (#00FFFF)
         */
        val CYAN = Color(0, 255, 255)

        /**
         * Daffodil (#FFFF31)
         */
        val DAFFODIL = Color(255, 255, 49)

        /**
         * Dandelion (#F0E130)
         */
        val DANDELION = Color(240, 225, 48)

        /**
         * Dark blue (#00008B)
         */
        val DARK_BLUE = Color(0, 0, 139)

        /**
         * Dark brown (#654321)
         */
        val DARK_BROWN = Color(101, 67, 33)

        /**
         * Dark byzantium (#5D3954)
         */
        val DARK_BYZANTIUM = Color(93, 57, 84)

        /**
         * Dark candy apple red (#A40000)
         */
        val DARK_CANDY_APPLE_RED = Color(164, 0, 0)

        /**
         * Dark cerulean (#08457E)
         */
        val DARK_CERULEAN = Color(8, 69, 126)

        /**
         * Dark chestnut (#986960)
         */
        val DARK_CHESTNUT = Color(152, 105, 96)

        /**
         * Dark coral (#CD5B45)
         */
        val DARK_CORAL = Color(205, 91, 69)

        /**
         * Dark cyan (#008B8B)
         */
        val DARK_CYAN = Color(0, 139, 139)

        /**
         * Dark electric blue (#536878)
         */
        val DARK_ELECTRIC_BLUE = Color(83, 104, 120)

        /**
         * Dark goldenrod (#B8860B)
         */
        val DARK_GOLDENROD = Color(184, 134, 11)

        /**
         * Dark gray (#A9A9A9)
         */
        val DARK_GRAY = Color(169, 169, 169)

        /**
         * Dark green (#013220)
         */
        val DARK_GREEN = Color(1, 50, 32)

        /**
         * Dark jungle green (#1A2421)
         */
        val DARK_JUNGLE_GREEN = Color(26, 36, 33)

        /**
         * Dark khaki (#BDB76B)
         */
        val DARK_KHAKI = Color(189, 183, 107)

        /**
         * Dark lava (#483C32)
         */
        val DARK_LAVA = Color(72, 60, 50)

        /**
         * Dark lavender (#734F96)
         */
        val DARK_LAVENDER = Color(115, 79, 150)

        /**
         * Dark magenta (#8B008B)
         */
        val DARK_MAGENTA = Color(139, 0, 139)

        /**
         * Dark midnight blue (#003366)
         */
        val DARK_MIDNIGHT_BLUE = Color(0, 51, 102)

        /**
         * Dark olive green (#556B2F)
         */
        val DARK_OLIVE_GREEN = Color(85, 107, 47)

        /**
         * Dark orange (#FF8C00)
         */
        val DARK_ORANGE = Color(255, 140, 0)

        /**
         * Dark orchid (#9932CC)
         */
        val DARK_ORCHID = Color(153, 50, 204)

        /**
         * Dark pastel blue (#779ECB)
         */
        val DARK_PASTEL_BLUE = Color(119, 158, 203)

        /**
         * Dark pastel green (#03C03C)
         */
        val DARK_PASTEL_GREEN = Color(3, 192, 60)

        /**
         * Dark pastel purple (#966FD6)
         */
        val DARK_PASTEL_PURPLE = Color(150, 111, 214)

        /**
         * Dark pastel red (#C23B22)
         */
        val DARK_PASTEL_RED = Color(194, 59, 34)

        /**
         * Dark pink (#E75480)
         */
        val DARK_PINK = Color(231, 84, 128)

        /**
         * Dark powder blue (#003399)
         */
        val DARK_POWDER_BLUE = Color(0, 51, 153)

        /**
         * Dark raspberry (#872657)
         */
        val DARK_RASPBERRY = Color(135, 38, 87)

        /**
         * Dark red (#8B0000)
         */
        val DARK_RED = Color(139, 0, 0)

        /**
         * Dark salmon (#E9967A)
         */
        val DARK_SALMON = Color(233, 150, 122)

        /**
         * Dark scarlet (#560319)
         */
        val DARK_SCARLET = Color(86, 3, 25)

        /**
         * Dark sea green (#8FBC8F)
         */
        val DARK_SEA_GREEN = Color(143, 188, 143)

        /**
         * Dark sienna (#3C1414)
         */
        val DARK_SIENNA = Color(60, 20, 20)

        /**
         * Dark slate blue (#483D8B)
         */
        val DARK_SLATE_BLUE = Color(72, 61, 139)

        /**
         * Dark slate gray (#2F4F4F)
         */
        val DARK_SLATE_GRAY = Color(47, 79, 79)

        /**
         * Dark spring green (#177245)
         */
        val DARK_SPRING_GREEN = Color(23, 114, 69)

        /**
         * Dark tan (#918151)
         */
        val DARK_TAN = Color(145, 129, 81)

        /**
         * Dark tangerine (#FFA812)
         */
        val DARK_TANGERINE = Color(255, 168, 18)

        /**
         * Dark taupe (#483C32)
         */
        val DARK_TAUPE = Color(72, 60, 50)

        /**
         * Dark terra cotta (#CC4E5C)
         */
        val DARK_TERRA_COTTA = Color(204, 78, 92)

        /**
         * Dark turquoise (#00CED1)
         */
        val DARK_TURQUOISE = Color(0, 206, 209)

        /**
         * Dark violet (#9400D3)
         */
        val DARK_VIOLET = Color(148, 0, 211)

        /**
         * Dartmouth green (#00693E)
         */
        val DARTMOUTH_GREEN = Color(0, 105, 62)

        /**
         * Davy grey (#555555)
         */
        val DAVY_GREY = Color(85, 85, 85)

        /**
         * Debian red (#D70A53)
         */
        val DEBIAN_RED = Color(215, 10, 83)

        /**
         * Deep carmine (#A9203E)
         */
        val DEEP_CARMINE = Color(169, 32, 62)

        /**
         * Deep carmine pink (#EF3038)
         */
        val DEEP_CARMINE_PINK = Color(239, 48, 56)

        /**
         * Deep carrot orange (#E9692C)
         */
        val DEEP_CARROT_ORANGE = Color(233, 105, 44)

        /**
         * Deep cerise (#DA3287)
         */
        val DEEP_CERISE = Color(218, 50, 135)

        /**
         * Deep champagne (#FAD6A5)
         */
        val DEEP_CHAMPAGNE = Color(250, 214, 165)

        /**
         * Deep chestnut (#B94E48)
         */
        val DEEP_CHESTNUT = Color(185, 78, 72)

        /**
         * Deep coffee (#704241)
         */
        val DEEP_COFFEE = Color(112, 66, 65)

        /**
         * Deep fuchsia (#C154C1)
         */
        val DEEP_FUCHSIA = Color(193, 84, 193)

        /**
         * Deep jungle green (#004B49)
         */
        val DEEP_JUNGLE_GREEN = Color(0, 75, 73)

        /**
         * Deep lilac (#9955BB)
         */
        val DEEP_LILAC = Color(153, 85, 187)

        /**
         * Deep magenta (#CC00CC)
         */
        val DEEP_MAGENTA = Color(204, 0, 204)

        /**
         * Deep peach (#FFCBA4)
         */
        val DEEP_PEACH = Color(255, 203, 164)

        /**
         * Deep pink (#FF1493)
         */
        val DEEP_PINK = Color(255, 20, 147)

        /**
         * Deep saffron (#FF9933)
         */
        val DEEP_SAFFRON = Color(255, 153, 51)

        /**
         * Deep sky blue (#00BFFF)
         */
        val DEEP_SKY_BLUE = Color(0, 191, 255)

        /**
         * Denim (#1560BD)
         */
        val DENIM = Color(21, 96, 189)

        /**
         * Desert (#C19A6B)
         */
        val DESERT = Color(193, 154, 107)

        /**
         * Desert sand (#EDC9AF)
         */
        val DESERT_SAND = Color(237, 201, 175)

        /**
         * Dim gray (#696969)
         */
        val DIM_GRAY = Color(105, 105, 105)

        /**
         * Dodger blue (#1E90FF)
         */
        val DODGER_BLUE = Color(30, 144, 255)

        /**
         * Dogwood rose (#D71868)
         */
        val DOGWOOD_ROSE = Color(215, 24, 104)

        /**
         * Dollar bill (#85BB65)
         */
        val DOLLAR_BILL = Color(133, 187, 101)

        /**
         * Drab (#967117)
         */
        val DRAB = Color(150, 113, 23)

        /**
         * Duke blue (#00009C)
         */
        val DUKE_BLUE = Color(0, 0, 156)

        /**
         * Earth yellow (#E1A95F)
         */
        val EARTH_YELLOW = Color(225, 169, 95)

        /**
         * Ecru (#C2B280)
         */
        val ECRU = Color(194, 178, 128)

        /**
         * Eggplant (#614051)
         */
        val EGGPLANT = Color(97, 64, 81)

        /**
         * Eggshell (#F0EAD6)
         */
        val EGGSHELL = Color(240, 234, 214)

        /**
         * Egyptian blue (#1034A6)
         */
        val EGYPTIAN_BLUE = Color(16, 52, 166)

        /**
         * Electric blue (#7DF9FF)
         */
        val ELECTRIC_BLUE = Color(125, 249, 255)

        /**
         * Electric crimson (#FF003F)
         */
        val ELECTRIC_CRIMSON = Color(255, 0, 63)

        /**
         * Electric cyan (#00FFFF)
         */
        val ELECTRIC_CYAN = Color(0, 255, 255)

        /**
         * Electric green (#00FF00)
         */
        val ELECTRIC_GREEN = Color(0, 255, 0)

        /**
         * Electric indigo (#6F00FF)
         */
        val ELECTRIC_INDIGO = Color(111, 0, 255)

        /**
         * Electric lavender (#F4BBFF)
         */
        val ELECTRIC_LAVENDER = Color(244, 187, 255)

        /**
         * Electric lime (#CCFF00)
         */
        val ELECTRIC_LIME = Color(204, 255, 0)

        /**
         * Electric purple (#BF00FF)
         */
        val ELECTRIC_PURPLE = Color(191, 0, 255)

        /**
         * Electric ultramarine (#3F00FF)
         */
        val ELECTRIC_ULTRAMARINE = Color(63, 0, 255)

        /**
         * Electric violet (#8F00FF)
         */
        val ELECTRIC_VIOLET = Color(143, 0, 255)

        /**
         * Electric yellow (#FFFF00)
         */
        val ELECTRIC_YELLOW = Color(255, 255, 0)

        /**
         * Emerald (#50C878)
         */
        val EMERALD = Color(80, 200, 120)

        /**
         * Eton blue (#96C8A2)
         */
        val ETON_BLUE = Color(150, 200, 162)

        /**
         * Fallow (#C19A6B)
         */
        val FALLOW = Color(193, 154, 107)

        /**
         * Falu red (#801818)
         */
        val FALU_RED = Color(128, 24, 24)

        /**
         * Famous (#FF00FF)
         */
        val FAMOUS = Color(255, 0, 255)

        /**
         * Fandango (#B53389)
         */
        val FANDANGO = Color(181, 51, 137)

        /**
         * Fashion fuchsia (#F400A1)
         */
        val FASHION_FUCHSIA = Color(244, 0, 161)

        /**
         * Fawn (#E5AA70)
         */
        val FAWN = Color(229, 170, 112)

        /**
         * Feldgrau (#4D5D53)
         */
        val FELDGRAU = Color(77, 93, 83)

        /**
         * Fern (#71BC78)
         */
        val FERN = Color(113, 188, 120)

        /**
         * Fern green (#4F7942)
         */
        val FERN_GREEN = Color(79, 121, 66)

        /**
         * Ferrari Red (#FF2800)
         */
        val FERRARI_RED = Color(255, 40, 0)

        /**
         * Field drab (#6C541E)
         */
        val FIELD_DRAB = Color(108, 84, 30)

        /**
         * Firebrick (#B22222)
         */
        val FIREBRICK = Color(178, 34, 34)

        /**
         * Fire engine red (#CE2029)
         */
        val FIRE_ENGINE_RED = Color(206, 32, 41)

        /**
         * Flame (#E25822)
         */
        val FLAME = Color(226, 88, 34)

        /**
         * Flamingo pink (#FC8EAC)
         */
        val FLAMINGO_PINK = Color(252, 142, 172)

        /**
         * Flavescent (#F7E98E)
         */
        val FLAVESCENT = Color(247, 233, 142)

        /**
         * Flax (#EEDC82)
         */
        val FLAX = Color(238, 220, 130)

        /**
         * Floral white (#FFFAF0)
         */
        val FLORAL_WHITE = Color(255, 250, 240)

        /**
         * Fluorescent orange (#FFBF00)
         */
        val FLUORESCENT_ORANGE = Color(255, 191, 0)

        /**
         * Fluorescent pink (#FF1493)
         */
        val FLUORESCENT_PINK = Color(255, 20, 147)

        /**
         * Fluorescent yellow (#CCFF00)
         */
        val FLUORESCENT_YELLOW = Color(204, 255, 0)

        /**
         * Folly (#FF004F)
         */
        val FOLLY = Color(255, 0, 79)

        /**
         * Forest green (#228B22)
         */
        val FOREST_GREEN = Color(34, 139, 34)

        /**
         * French beige (#A67B5B)
         */
        val FRENCH_BEIGE = Color(166, 123, 91)

        /**
         * French blue (#0072BB)
         */
        val FRENCH_BLUE = Color(0, 114, 187)

        /**
         * French lilac (#86608E)
         */
        val FRENCH_LILAC = Color(134, 96, 142)

        /**
         * French rose (#F64A8A)
         */
        val FRENCH_ROSE = Color(246, 74, 138)

        /**
         * Fuchsia (#FF00FF)
         */
        val FUCHSIA = Color(255, 0, 255)

        /**
         * Fuchsia pink (#FF77FF)
         */
        val FUCHSIA_PINK = Color(255, 119, 255)

        /**
         * Fulvous (#E48400)
         */
        val FULVOUS = Color(228, 132, 0)

        /**
         * Fuzzy Wuzzy (#CC6666)
         */
        val FUZZY_WUZZY = Color(204, 102, 102)

        /**
         * Gainsboro (#DCDCDC)
         */
        val GAINSBORO = Color(220, 220, 220)

        /**
         * Gamboge (#E49B0F)
         */
        val GAMBOGE = Color(228, 155, 15)

        /**
         * Ghost white (#F8F8FF)
         */
        val GHOST_WHITE = Color(248, 248, 255)

        /**
         * Ginger (#B06500)
         */
        val GINGER = Color(176, 101, 0)

        /**
         * Glaucous (#6082B6)
         */
        val GLAUCOUS = Color(96, 130, 182)

        /**
         * Glitter (#E6E8FA)
         */
        val GLITTER = Color(230, 232, 250)

        /**
         * Gold (#FFD700)
         */
        val GOLD = Color(255, 215, 0)

        /**
         * Goldenrod (#DAA520)
         */
        val GOLDENROD = Color(218, 165, 32)

        /**
         * Golden brown (#996515)
         */
        val GOLDEN_BROWN = Color(153, 101, 21)

        /**
         * Golden poppy (#FCC200)
         */
        val GOLDEN_POPPY = Color(252, 194, 0)

        /**
         * Golden yellow (#FFDF00)
         */
        val GOLDEN_YELLOW = Color(255, 223, 0)

        /**
         * Granny Smith Apple (#A8E4A0)
         */
        val GRANNY_SMITH_APPLE = Color(168, 228, 160)

        /**
         * Gray (#808080)
         */
        val GRAY = Color(128, 128, 128)

        /**
         * Gray asparagus (#465945)
         */
        val GRAY_ASPARAGUS = Color(70, 89, 69)

        /**
         * Green (#00FF00)
         */
        val GREEN = Color(0, 255, 0)

        /**
         * Green Blue (#1164B4)
         */
        val GREEN_BLUE = Color(17, 100, 180)

        /**
         * Green yellow (#ADFF2F)
         */
        val GREEN_YELLOW = Color(173, 255, 47)

        /**
         * Grullo (#A99A86)
         */
        val GRULLO = Color(169, 154, 134)

        /**
         * Guppie green (#00FF7F)
         */
        val GUPPIE_GREEN = Color(0, 255, 127)

        /**
         * Halayà úbe (#663854)
         */
        val HALAYA_U_BE = Color(102, 56, 84)

        /**
         * Hansa yellow (#E9D66B)
         */
        val HANSA_YELLOW = Color(233, 214, 107)

        /**
         * Han blue (#446CCF)
         */
        val HAN_BLUE = Color(68, 108, 207)

        /**
         * Han purple (#5218FA)
         */
        val HAN_PURPLE = Color(82, 24, 250)

        /**
         * Harlequin (#3FFF00)
         */
        val HARLEQUIN = Color(63, 255, 0)

        /**
         * Harvard crimson (#C90016)
         */
        val HARVARD_CRIMSON = Color(201, 0, 22)

        /**
         * Harvest Gold (#DA9100)
         */
        val HARVEST_GOLD = Color(218, 145, 0)

        /**
         * Heart Gold (#808000)
         */
        val HEART_GOLD = Color(128, 128, 0)

        /**
         * Heliotrope (#DF73FF)
         */
        val HELIOTROPE = Color(223, 115, 255)

        /**
         * Hollywood cerise (#F400A1)
         */
        val HOLLYWOOD_CERISE = Color(244, 0, 161)

        /**
         * Honeydew (#F0FFF0)
         */
        val HONEYDEW = Color(240, 255, 240)

        /**
         * Hooker green (#49796B)
         */
        val HOOKER_GREEN = Color(73, 121, 107)

        /**
         * Hot magenta (#FF1DCE)
         */
        val HOT_MAGENTA = Color(255, 29, 206)

        /**
         * Hot pink (#FF69B4)
         */
        val HOT_PINK = Color(255, 105, 180)

        /**
         * Hunter green (#355E3B)
         */
        val HUNTER_GREEN = Color(53, 94, 59)

        /**
         * Icterine (#FCF75E)
         */
        val ICTERINE = Color(252, 247, 94)

        /**
         * Inchworm (#B2EC5D)
         */
        val INCHWORM = Color(178, 236, 93)

        /**
         * Indian red (#CD5C5C)
         */
        val INDIAN_RED = Color(205, 92, 92)

        /**
         * Indian yellow (#E3A857)
         */
        val INDIAN_YELLOW = Color(227, 168, 87)

        /**
         * India green (#138808)
         */
        val INDIA_GREEN = Color(19, 136, 8)

        /**
         * Indigo (#4B0082)
         */
        val INDIGO = Color(75, 0, 130)

        /**
         * International Klein Blue (#002FA7)
         */
        val INTERNATIONAL_KLEIN_BLUE = Color(0, 47, 167)

        /**
         * International orange (#FF4F00)
         */
        val INTERNATIONAL_ORANGE = Color(255, 79, 0)

        /**
         * Iris (#5A4FCF)
         */
        val IRIS = Color(90, 79, 207)

        /**
         * Isabelline (#F4F0EC)
         */
        val ISABELLINE = Color(244, 240, 236)

        /**
         * Islamic green (#009000)
         */
        val ISLAMIC_GREEN = Color(0, 144, 0)

        /**
         * Ivory (#FFFFF0)
         */
        val IVORY = Color(255, 255, 240)

        /**
         * Jade (#00A86B)
         */
        val JADE = Color(0, 168, 107)

        /**
         * Jasmine (#F8DE7E)
         */
        val JASMINE = Color(248, 222, 126)

        /**
         * Jasper (#D73B3E)
         */
        val JASPER = Color(215, 59, 62)

        /**
         * Jazzberry jam (#A50B5E)
         */
        val JAZZBERRY_JAM = Color(165, 11, 94)

        /**
         * Jonquil (#FADA5E)
         */
        val JONQUIL = Color(250, 218, 94)

        /**
         * June bud (#BDDA57)
         */
        val JUNE_BUD = Color(189, 218, 87)

        /**
         * Jungle green (#29AB87)
         */
        val JUNGLE_GREEN = Color(41, 171, 135)

        /**
         * Kelly green (#4CBB17)
         */
        val KELLY_GREEN = Color(76, 187, 23)

        /**
         * Khaki (#C3B091)
         */
        val KHAKI = Color(195, 176, 145)

        /**
         * KU Crimson (#E8000D)
         */
        val KU_CRIMSON = Color(232, 0, 13)

        /**
         * Languid lavender (#D6CADD)
         */
        val LANGUID_LAVENDER = Color(214, 202, 221)

        /**
         * Lapis lazuli (#26619C)
         */
        val LAPIS_LAZULI = Color(38, 97, 156)

        /**
         * Laser Lemon (#FEFE22)
         */
        val LASER_LEMON = Color(254, 254, 34)

        /**
         * Laurel green (#A9BA9D)
         */
        val LAUREL_GREEN = Color(169, 186, 157)

        /**
         * Lava (#CF1020)
         */
        val LAVA = Color(207, 16, 32)

        /**
         * Lavender (#E6E6FA)
         */
        val LAVENDER = Color(230, 230, 250)

        /**
         * Lavender blue (#CCCCFF)
         */
        val LAVENDER_BLUE = Color(204, 204, 255)

        /**
         * Lavender blush (#FFF0F5)
         */
        val LAVENDER_BLUSH = Color(255, 240, 245)

        /**
         * Lavender gray (#C4C3D0)
         */
        val LAVENDER_GRAY = Color(196, 195, 208)

        /**
         * Lavender indigo (#9457EB)
         */
        val LAVENDER_INDIGO = Color(148, 87, 235)

        /**
         * Lavender magenta (#EE82EE)
         */
        val LAVENDER_MAGENTA = Color(238, 130, 238)

        /**
         * Lavender mist (#E6E6FA)
         */
        val LAVENDER_MIST = Color(230, 230, 250)

        /**
         * Lavender pink (#FBAED2)
         */
        val LAVENDER_PINK = Color(251, 174, 210)

        /**
         * Lavender purple (#967BB6)
         */
        val LAVENDER_PURPLE = Color(150, 123, 182)

        /**
         * Lavender rose (#FBA0E3)
         */
        val LAVENDER_ROSE = Color(251, 160, 227)

        /**
         * Lawn green (#7CFC00)
         */
        val LAWN_GREEN = Color(124, 252, 0)

        /**
         * La Salle Green (#087830)
         */
        val LA_SALLE_GREEN = Color(8, 120, 48)

        /**
         * Lemon (#FFF700)
         */
        val LEMON = Color(255, 247, 0)

        /**
         * Lemon chiffon (#FFFACD)
         */
        val LEMON_CHIFFON = Color(255, 250, 205)

        /**
         * Lemon lime (#BFFF00)
         */
        val LEMON_LIME = Color(191, 255, 0)

        /**
         * Lemon Yellow (#FFF44F)
         */
        val LEMON_YELLOW = Color(255, 244, 79)

        /**
         * Light apricot (#FDD5B1)
         */
        val LIGHT_APRICOT = Color(253, 213, 177)

        /**
         * Light blue (#ADD8E6)
         */
        val LIGHT_BLUE = Color(173, 216, 230)

        /**
         * Light brown (#B5651D)
         */
        val LIGHT_BROWN = Color(181, 101, 29)

        /**
         * Light carmine pink (#E66771)
         */
        val LIGHT_CARMINE_PINK = Color(230, 103, 113)

        /**
         * Light coral (#F08080)
         */
        val LIGHT_CORAL = Color(240, 128, 128)

        /**
         * Light cornflower blue (#93CCEA)
         */
        val LIGHT_CORNFLOWER_BLUE = Color(147, 204, 234)

        /**
         * Light Crimson (#F56991)
         */
        val LIGHT_CRIMSON = Color(245, 105, 145)

        /**
         * Light cyan (#E0FFFF)
         */
        val LIGHT_CYAN = Color(224, 255, 255)

        /**
         * Light fuchsia pink (#F984EF)
         */
        val LIGHT_FUCHSIA_PINK = Color(249, 132, 239)

        /**
         * Light goldenrod yellow (#FAFAD2)
         */
        val LIGHT_GOLDENROD_YELLOW = Color(250, 250, 210)

        /**
         * Light gray (#D3D3D3)
         */
        val LIGHT_GRAY = Color(211, 211, 211)

        /**
         * Light green (#90EE90)
         */
        val LIGHT_GREEN = Color(144, 238, 144)

        /**
         * Light khaki (#F0E68C)
         */
        val LIGHT_KHAKI = Color(240, 230, 140)

        /**
         * Light pastel purple (#B19CD9)
         */
        val LIGHT_PASTEL_PURPLE = Color(177, 156, 217)

        /**
         * Light pink (#FFB6C1)
         */
        val LIGHT_PINK = Color(255, 182, 193)

        /**
         * Light salmon (#FFA07A)
         */
        val LIGHT_SALMON = Color(255, 160, 122)

        /**
         * Light salmon pink (#FF9999)
         */
        val LIGHT_SALMON_PINK = Color(255, 153, 153)

        /**
         * Light sea green (#20B2AA)
         */
        val LIGHT_SEA_GREEN = Color(32, 178, 170)

        /**
         * Light sky blue (#87CEFA)
         */
        val LIGHT_SKY_BLUE = Color(135, 206, 250)

        /**
         * Light slate gray (#778899)
         */
        val LIGHT_SLATE_GRAY = Color(119, 136, 153)

        /**
         * Light taupe (#B38B6D)
         */
        val LIGHT_TAUPE = Color(179, 139, 109)

        /**
         * Light Thulian pink (#E68FAC)
         */
        val LIGHT_THULIAN_PINK = Color(230, 143, 172)

        /**
         * Light yellow (#FFFFED)
         */
        val LIGHT_YELLOW = Color(255, 255, 237)

        /**
         * Lilac (#C8A2C8)
         */
        val LILAC = Color(200, 162, 200)

        /**
         * Lime (#BFFF00)
         */
        val LIME = Color(191, 255, 0)

        /**
         * Lime green (#32CD32)
         */
        val LIME_GREEN = Color(50, 205, 50)

        /**
         * Lincoln green (#195905)
         */
        val LINCOLN_GREEN = Color(25, 89, 5)

        /**
         * Linen (#FAF0E6)
         */
        val LINEN = Color(250, 240, 230)

        /**
         * Lion (#C19A6B)
         */
        val LION = Color(193, 154, 107)

        /**
         * Liver (#534B4F)
         */
        val LIVER = Color(83, 75, 79)

        /**
         * Lust (#E62020)
         */
        val LUST = Color(230, 32, 32)

        /**
         * Macaroni and Cheese (#FFBD88)
         */
        val MACARONI_AND_CHEESE = Color(255, 189, 136)

        /**
         * Magenta (#FF00FF)
         */
        val MAGENTA = Color(255, 0, 255)

        /**
         * Magic mint (#AAF0D1)
         */
        val MAGIC_MINT = Color(170, 240, 209)

        /**
         * Magnolia (#F8F4FF)
         */
        val MAGNOLIA = Color(248, 244, 255)

        /**
         * Mahogany (#C04000)
         */
        val MAHOGANY = Color(192, 64, 0)

        /**
         * Maize (#FBEC5D)
         */
        val MAIZE = Color(251, 236, 93)

        /**
         * Majorelle Blue (#6050DC)
         */
        val MAJORELLE_BLUE = Color(96, 80, 220)

        /**
         * Malachite (#0BDA51)
         */
        val MALACHITE = Color(11, 218, 81)

        /**
         * Manatee (#979AAA)
         */
        val MANATEE = Color(151, 154, 170)

        /**
         * Mango Tango (#FF8243)
         */
        val MANGO_TANGO = Color(255, 130, 67)

        /**
         * Mantis (#74C365)
         */
        val MANTIS = Color(116, 195, 101)

        /**
         * Maroon (#800000)
         */
        val MAROON = Color(128, 0, 0)

        /**
         * Mauve (#E0B0FF)
         */
        val MAUVE = Color(224, 176, 255)

        /**
         * Mauvelous (#EF98AA)
         */
        val MAUVELOUS = Color(239, 152, 170)

        /**
         * Mauve taupe (#915F6D)
         */
        val MAUVE_TAUPE = Color(145, 95, 109)

        /**
         * Maya blue (#73C2FB)
         */
        val MAYA_BLUE = Color(115, 194, 251)

        /**
         * Meat brown (#E5B73B)
         */
        val MEAT_BROWN = Color(229, 183, 59)

        /**
         * Medium aquamarine (#66DDAA)
         */
        val MEDIUM_AQUAMARINE = Color(102, 221, 170)

        /**
         * Medium blue (#0000CD)
         */
        val MEDIUM_BLUE = Color(0, 0, 205)

        /**
         * Medium candy apple red (#E2062C)
         */
        val MEDIUM_CANDY_APPLE_RED = Color(226, 6, 44)

        /**
         * Medium carmine (#AF4035)
         */
        val MEDIUM_CARMINE = Color(175, 64, 53)

        /**
         * Medium champagne (#F3E5AB)
         */
        val MEDIUM_CHAMPAGNE = Color(243, 229, 171)

        /**
         * Medium electric blue (#035096)
         */
        val MEDIUM_ELECTRIC_BLUE = Color(3, 80, 150)

        /**
         * Medium jungle green (#1C352D)
         */
        val MEDIUM_JUNGLE_GREEN = Color(28, 53, 45)

        /**
         * Medium lavender magenta (#DDA0DD)
         */
        val MEDIUM_LAVENDER_MAGENTA = Color(221, 160, 221)

        /**
         * Medium orchid (#BA55D3)
         */
        val MEDIUM_ORCHID = Color(186, 85, 211)

        /**
         * Medium Persian blue (#0067A5)
         */
        val MEDIUM_PERSIAN_BLUE = Color(0, 103, 165)

        /**
         * Medium purple (#9370DB)
         */
        val MEDIUM_PURPLE = Color(147, 112, 219)

        /**
         * Medium red violet (#BB3385)
         */
        val MEDIUM_RED_VIOLET = Color(187, 51, 133)

        /**
         * Medium sea green (#3CB371)
         */
        val MEDIUM_SEA_GREEN = Color(60, 179, 113)

        /**
         * Medium slate blue (#7B68EE)
         */
        val MEDIUM_SLATE_BLUE = Color(123, 104, 238)

        /**
         * Medium spring bud (#C9DC87)
         */
        val MEDIUM_SPRING_BUD = Color(201, 220, 135)

        /**
         * Medium spring green (#00FA9A)
         */
        val MEDIUM_SPRING_GREEN = Color(0, 250, 154)

        /**
         * Medium taupe (#674C47)
         */
        val MEDIUM_TAUPE = Color(103, 76, 71)

        /**
         * Medium teal blue (#0054B4)
         */
        val MEDIUM_TEAL_BLUE = Color(0, 84, 180)

        /**
         * Medium turquoise (#48D1CC)
         */
        val MEDIUM_TURQUOISE = Color(72, 209, 204)

        /**
         * Medium violet red (#C71585)
         */
        val MEDIUM_VIOLET_RED = Color(199, 21, 133)

        /**
         * Melon (#FDBCB4)
         */
        val MELON = Color(253, 188, 180)

        /**
         * Midnight blue (#191970)
         */
        val MIDNIGHT_BLUE = Color(25, 25, 112)

        /**
         * Midnight green (#004953)
         */
        val MIDNIGHT_GREEN = Color(0, 73, 83)

        /**
         * Mikado yellow (#FFC40C)
         */
        val MIKADO_YELLOW = Color(255, 196, 12)

        /**
         * Mint (#3EB489)
         */
        val MINT = Color(62, 180, 137)

        /**
         * Mint cream (#F5FFFA)
         */
        val MINT_CREAM = Color(245, 255, 250)

        /**
         * Mint green (#98FF98)
         */
        val MINT_GREEN = Color(152, 255, 152)

        /**
         * Misty rose (#FFE4E1)
         */
        val MISTY_ROSE = Color(255, 228, 225)

        /**
         * Moccasin (#FAEBD7)
         */
        val MOCCASIN = Color(250, 235, 215)

        /**
         * Mode beige (#967117)
         */
        val MODE_BEIGE = Color(150, 113, 23)

        /**
         * Moonstone blue (#73A9C2)
         */
        val MOONSTONE_BLUE = Color(115, 169, 194)

        /**
         * Mordant red 19 (#AE0C00)
         */
        val MORDANT_RED_19 = Color(174, 12, 0)

        /**
         * Moss green (#ADDFAD)
         */
        val MOSS_GREEN = Color(173, 223, 173)

        /**
         * Mountain Meadow (#30BA8F)
         */
        val MOUNTAIN_MEADOW = Color(48, 186, 143)

        /**
         * Mountbatten pink (#997A8D)
         */
        val MOUNTBATTEN_PINK = Color(153, 122, 141)

        /**
         * MSU Green (#18453B)
         */
        val MSU_GREEN = Color(24, 69, 59)

        /**
         * Mulberry (#C54B8C)
         */
        val MULBERRY = Color(197, 75, 140)

        /**
         * Munsell (#F2F3F4)
         */
        val MUNSELL = Color(242, 243, 244)

        /**
         * Mustard (#FFDB58)
         */
        val MUSTARD = Color(255, 219, 88)

        /**
         * Myrtle (#21421E)
         */
        val MYRTLE = Color(33, 66, 30)

        /**
         * Nadeshiko pink (#F6ADC6)
         */
        val NADESHIKO_PINK = Color(246, 173, 198)

        /**
         * Napier green (#2A8000)
         */
        val NAPIER_GREEN = Color(42, 128, 0)

        /**
         * Naples yellow (#FADA5E)
         */
        val NAPLES_YELLOW = Color(250, 218, 94)

        /**
         * Navajo white (#FFDEAD)
         */
        val NAVAJO_WHITE = Color(255, 222, 173)

        /**
         * Navy blue (#000080)
         */
        val NAVY_BLUE = Color(0, 0, 128)

        /**
         * Neon Carrot (#FFA343)
         */
        val NEON_CARROT = Color(255, 163, 67)

        /**
         * Neon fuchsia (#FE59C2)
         */
        val NEON_FUCHSIA = Color(254, 89, 194)

        /**
         * Neon green (#39FF14)
         */
        val NEON_GREEN = Color(57, 255, 20)

        /**
         * Non-photo blue (#A4DDED)
         */
        val NON_PHOTO_BLUE = Color(164, 221, 237)

        /**
         * North Texas Green (#059033)
         */
        val NORTH_TEXAS_GREEN = Color(5, 144, 51)

        /**
         * Ocean Boat Blue (#0077BE)
         */
        val OCEAN_BOAT_BLUE = Color(0, 119, 190)

        /**
         * Ochre (#CC7722)
         */
        val OCHRE = Color(204, 119, 34)

        /**
         * Office green (#008000)
         */
        val OFFICE_GREEN = Color(0, 128, 0)

        /**
         * Old gold (#CFB53B)
         */
        val OLD_GOLD = Color(207, 181, 59)

        /**
         * Old lace (#FDF5E6)
         */
        val OLD_LACE = Color(253, 245, 230)

        /**
         * Old lavender (#796878)
         */
        val OLD_LAVENDER = Color(121, 104, 120)

        /**
         * Old mauve (#673147)
         */
        val OLD_MAUVE = Color(103, 49, 71)

        /**
         * Old rose (#C08081)
         */
        val OLD_ROSE = Color(192, 128, 129)

        /**
         * Olive (#808000)
         */
        val OLIVE = Color(128, 128, 0)

        /**
         * Olive Drab (#6B8E23)
         */
        val OLIVE_DRAB = Color(107, 142, 35)

        /**
         * Olive Green (#BAB86C)
         */
        val OLIVE_GREEN = Color(186, 184, 108)

        /**
         * Olivine (#9AB973)
         */
        val OLIVINE = Color(154, 185, 115)

        /**
         * Onyx (#0F0F0F)
         */
        val ONYX = Color(15, 15, 15)

        /**
         * Opera mauve (#B784A7)
         */
        val OPERA_MAUVE = Color(183, 132, 167)

        /**
         * Orange (#FFA500)
         */
        val ORANGE = Color(255, 165, 0)

        /**
         * Orange peel (#FF9F00)
         */
        val ORANGE_PEEL = Color(255, 159, 0)

        /**
         * Orange red (#FF4500)
         */
        val ORANGE_RED = Color(255, 69, 0)

        /**
         * Orange Yellow (#F8D568)
         */
        val ORANGE_YELLOW = Color(248, 213, 104)

        /**
         * Orchid (#DA70D6)
         */
        val ORCHID = Color(218, 112, 214)

        /**
         * Otter brown (#654321)
         */
        val OTTER_BROWN = Color(101, 67, 33)

        /**
         * Outer Space (#414A4C)
         */
        val OUTER_SPACE = Color(65, 74, 76)

        /**
         * Outrageous Orange (#FF6E4A)
         */
        val OUTRAGEOUS_ORANGE = Color(255, 110, 74)

        /**
         * Oxford Blue (#002147)
         */
        val OXFORD_BLUE = Color(0, 33, 71)

        /**
         * Pacific Blue (#1CA9C9)
         */
        val PACIFIC_BLUE = Color(28, 169, 201)

        /**
         * Pakistan green (#006600)
         */
        val PAKISTAN_GREEN = Color(0, 102, 0)

        /**
         * Palatinate blue (#273BE2)
         */
        val PALATINATE_BLUE = Color(39, 59, 226)

        /**
         * Palatinate purple (#682860)
         */
        val PALATINATE_PURPLE = Color(104, 40, 96)

        /**
         * Pale aqua (#BCD4E6)
         */
        val PALE_AQUA = Color(188, 212, 230)

        /**
         * Pale blue (#AFEEEE)
         */
        val PALE_BLUE = Color(175, 238, 238)

        /**
         * Pale brown (#987654)
         */
        val PALE_BROWN = Color(152, 118, 84)

        /**
         * Pale carmine (#AF4035)
         */
        val PALE_CARMINE = Color(175, 64, 53)

        /**
         * Pale cerulean (#9BC4E2)
         */
        val PALE_CERULEAN = Color(155, 196, 226)

        /**
         * Pale chestnut (#DDADAF)
         */
        val PALE_CHESTNUT = Color(221, 173, 175)

        /**
         * Pale copper (#DA8A67)
         */
        val PALE_COPPER = Color(218, 138, 103)

        /**
         * Pale cornflower blue (#ABCDEF)
         */
        val PALE_CORNFLOWER_BLUE = Color(171, 205, 239)

        /**
         * Pale gold (#E6BE8A)
         */
        val PALE_GOLD = Color(230, 190, 138)

        /**
         * Pale goldenrod (#EEE8AA)
         */
        val PALE_GOLDENROD = Color(238, 232, 170)

        /**
         * Pale green (#98FB98)
         */
        val PALE_GREEN = Color(152, 251, 152)

        /**
         * Pale lavender (#DCD0FF)
         */
        val PALE_LAVENDER = Color(220, 208, 255)

        /**
         * Pale magenta (#F984E5)
         */
        val PALE_MAGENTA = Color(249, 132, 229)

        /**
         * Pale pink (#FADADD)
         */
        val PALE_PINK = Color(250, 218, 221)

        /**
         * Pale plum (#DDA0DD)
         */
        val PALE_PLUM = Color(221, 160, 221)

        /**
         * Pale red violet (#DB7093)
         */
        val PALE_RED_VIOLET = Color(219, 112, 147)

        /**
         * Pale robin egg blue (#96DED1)
         */
        val PALE_ROBIN_EGG_BLUE = Color(150, 222, 209)

        /**
         * Pale silver (#C9C0BB)
         */
        val PALE_SILVER = Color(201, 192, 187)

        /**
         * Pale spring bud (#ECEBBD)
         */
        val PALE_SPRING_BUD = Color(236, 235, 189)

        /**
         * Pale taupe (#BC987E)
         */
        val PALE_TAUPE = Color(188, 152, 126)

        /**
         * Pale violet red (#DB7093)
         */
        val PALE_VIOLET_RED = Color(219, 112, 147)

        /**
         * Pansy purple (#78184A)
         */
        val PANSY_PURPLE = Color(120, 24, 74)

        /**
         * Papaya whip (#FFEFD5)
         */
        val PAPAYA_WHIP = Color(255, 239, 213)

        /**
         * Paris Green (#50C878)
         */
        val PARIS_GREEN = Color(80, 200, 120)

        /**
         * Pastel blue (#AEC6CF)
         */
        val PASTEL_BLUE = Color(174, 198, 207)

        /**
         * Pastel brown (#836953)
         */
        val PASTEL_BROWN = Color(131, 105, 83)

        /**
         * Pastel gray (#CFCFC4)
         */
        val PASTEL_GRAY = Color(207, 207, 196)

        /**
         * Pastel green (#77DD77)
         */
        val PASTEL_GREEN = Color(119, 221, 119)

        /**
         * Pastel magenta (#F49AC2)
         */
        val PASTEL_MAGENTA = Color(244, 154, 194)

        /**
         * Pastel orange (#FFB347)
         */
        val PASTEL_ORANGE = Color(255, 179, 71)

        /**
         * Pastel pink (#FFD1DC)
         */
        val PASTEL_PINK = Color(255, 209, 220)

        /**
         * Pastel purple (#B39EB5)
         */
        val PASTEL_PURPLE = Color(179, 158, 181)

        /**
         * Pastel red (#FF6961)
         */
        val PASTEL_RED = Color(255, 105, 97)

        /**
         * Pastel violet (#CB99C9)
         */
        val PASTEL_VIOLET = Color(203, 153, 201)

        /**
         * Pastel yellow (#FDFD96)
         */
        val PASTEL_YELLOW = Color(253, 253, 150)

        /**
         * Patriarch (#800080)
         */
        val PATRIARCH = Color(128, 0, 128)

        /**
         * Payne grey (#536878)
         */
        val PAYNE_GREY = Color(83, 104, 120)

        /**
         * Peach (#FFE5B4)
         */
        val PEACH = Color(255, 229, 180)

        /**
         * Peach puff (#FFDAB9)
         */
        val PEACH_PUFF = Color(255, 218, 185)

        /**
         * Peach yellow (#FADFAD)
         */
        val PEACH_YELLOW = Color(250, 223, 173)

        /**
         * Pear (#D1E231)
         */
        val PEAR = Color(209, 226, 49)

        /**
         * Pearl (#EAE0C8)
         */
        val PEARL = Color(234, 224, 200)

        /**
         * Pearl Aqua (#88D8C0)
         */
        val PEARL_AQUA = Color(136, 216, 192)

        /**
         * Peridot (#E6E200)
         */
        val PERIDOT = Color(230, 226, 0)

        /**
         * Periwinkle (#CCCCFF)
         */
        val PERIWINKLE = Color(204, 204, 255)

        /**
         * Persian blue (#1C39BB)
         */
        val PERSIAN_BLUE = Color(28, 57, 187)

        /**
         * Persian indigo (#32127A)
         */
        val PERSIAN_INDIGO = Color(50, 18, 122)

        /**
         * Persian orange (#D99058)
         */
        val PERSIAN_ORANGE = Color(217, 144, 88)

        /**
         * Persian pink (#F77FBE)
         */
        val PERSIAN_PINK = Color(247, 127, 190)

        /**
         * Persian plum (#701C1C)
         */
        val PERSIAN_PLUM = Color(112, 28, 28)

        /**
         * Persian red (#CC3333)
         */
        val PERSIAN_RED = Color(204, 51, 51)

        /**
         * Persian rose (#FE28A2)
         */
        val PERSIAN_ROSE = Color(254, 40, 162)

        /**
         * Phlox (#DF00FF)
         */
        val PHLOX = Color(223, 0, 255)

        /**
         * Phthalo blue (#000F89)
         */
        val PHTHALO_BLUE = Color(0, 15, 137)

        /**
         * Phthalo green (#123524)
         */
        val PHTHALO_GREEN = Color(18, 53, 36)

        /**
         * Piggy pink (#FDDDE6)
         */
        val PIGGY_PINK = Color(253, 221, 230)

        /**
         * Pine green (#01796F)
         */
        val PINE_GREEN = Color(1, 121, 111)

        /**
         * Pink (#FFC0CB)
         */
        val PINK = Color(255, 192, 203)

        /**
         * Pink Flamingo (#FC74FD)
         */
        val PINK_FLAMINGO = Color(252, 116, 253)

        /**
         * Pink pearl (#E7ACCF)
         */
        val PINK_PEARL = Color(231, 172, 207)

        /**
         * Pink Sherbet (#F78FA7)
         */
        val PINK_SHERBET = Color(247, 143, 167)

        /**
         * Pistachio (#93C572)
         */
        val PISTACHIO = Color(147, 197, 114)

        /**
         * Platinum (#E5E4E2)
         */
        val PLATINUM = Color(229, 228, 226)

        /**
         * Plum (#DDA0DD)
         */
        val PLUM = Color(221, 160, 221)

        /**
         * Portland Orange (#FF5A36)
         */
        val PORTLAND_ORANGE = Color(255, 90, 54)

        /**
         * Powder blue (#B0E0E6)
         */
        val POWDER_BLUE = Color(176, 224, 230)

        /**
         * Princeton orange (#FF8F00)
         */
        val PRINCETON_ORANGE = Color(255, 143, 0)

        /**
         * Prussian blue (#003153)
         */
        val PRUSSIAN_BLUE = Color(0, 49, 83)

        /**
         * Psychedelic purple (#DF00FF)
         */
        val PSYCHEDELIC_PURPLE = Color(223, 0, 255)

        /**
         * Puce (#CC8899)
         */
        val PUCE = Color(204, 136, 153)

        /**
         * Pumpkin (#FF7518)
         */
        val PUMPKIN = Color(255, 117, 24)

        /**
         * Purple (#800080)
         */
        val PURPLE = Color(128, 0, 128)

        /**
         * Purple Heart (#69359C)
         */
        val PURPLE_HEART = Color(105, 53, 156)

        /**
         * Purple Mountain's Majesty (#9D81BA)
         */
        val PURPLE_MOUNTAINS_MAJESTY = Color(157, 129, 186)

        /**
         * Purple mountain majesty (#9678B6)
         */
        val PURPLE_MOUNTAIN_MAJESTY = Color(150, 120, 182)

        /**
         * Purple pizzazz (#FE4EDA)
         */
        val PURPLE_PIZZAZZ = Color(254, 78, 218)

        /**
         * Purple taupe (#50404D)
         */
        val PURPLE_TAUPE = Color(80, 64, 77)

        /**
         * Rackley (#5D8AA8)
         */
        val RACKLEY = Color(93, 138, 168)

        /**
         * Radical Red (#FF355E)
         */
        val RADICAL_RED = Color(255, 53, 94)

        /**
         * Raspberry (#E30B5D)
         */
        val RASPBERRY = Color(227, 11, 93)

        /**
         * Raspberry glace (#915F6D)
         */
        val RASPBERRY_GLACE = Color(145, 95, 109)

        /**
         * Raspberry pink (#E25098)
         */
        val RASPBERRY_PINK = Color(226, 80, 152)

        /**
         * Raspberry rose (#B3446C)
         */
        val RASPBERRY_ROSE = Color(179, 68, 108)

        /**
         * Raw Sienna (#D68A59)
         */
        val RAW_SIENNA = Color(214, 138, 89)

        /**
         * Razzle dazzle rose (#FF33CC)
         */
        val RAZZLE_DAZZLE_ROSE = Color(255, 51, 204)

        /**
         * Razzmatazz (#E3256B)
         */
        val RAZZMATAZZ = Color(227, 37, 107)

        /**
         * Red (#FF0000)
         */
        val RED = Color(255, 0, 0)

        /**
         * Red brown (#A52A2A)
         */
        val RED_BROWN = Color(165, 42, 42)

        /**
         * Red Orange (#FF5349)
         */
        val RED_ORANGE = Color(255, 83, 73)

        /**
         * Red violet (#C71585)
         */
        val RED_VIOLET = Color(199, 21, 133)

        /**
         * Rich black (#004040)
         */
        val RICH_BLACK = Color(0, 64, 64)

        /**
         * Rich carmine (#D70040)
         */
        val RICH_CARMINE = Color(215, 0, 64)

        /**
         * Rich electric blue (#0892D0)
         */
        val RICH_ELECTRIC_BLUE = Color(8, 146, 208)

        /**
         * Rich lilac (#B666D2)
         */
        val RICH_LILAC = Color(182, 102, 210)

        /**
         * Rich maroon (#B03060)
         */
        val RICH_MAROON = Color(176, 48, 96)

        /**
         * Rifle green (#414833)
         */
        val RIFLE_GREEN = Color(65, 72, 51)

        /**
         * Robin's Egg Blue (#1FCECB)
         */
        val ROBINS_EGG_BLUE = Color(31, 206, 203)

        /**
         * Rose (#FF007F)
         */
        val ROSE = Color(255, 0, 127)

        /**
         * Rosewood (#65000B)
         */
        val ROSEWOOD = Color(101, 0, 11)

        /**
         * Rose bonbon (#F9429E)
         */
        val ROSE_BONBON = Color(249, 66, 158)

        /**
         * Rose ebony (#674846)
         */
        val ROSE_EBONY = Color(103, 72, 70)

        /**
         * Rose gold (#B76E79)
         */
        val ROSE_GOLD = Color(183, 110, 121)

        /**
         * Rose madder (#E32636)
         */
        val ROSE_MADDER = Color(227, 38, 54)

        /**
         * Rose pink (#FF66CC)
         */
        val ROSE_PINK = Color(255, 102, 204)

        /**
         * Rose quartz (#AA98A9)
         */
        val ROSE_QUARTZ = Color(170, 152, 169)

        /**
         * Rose taupe (#905D5D)
         */
        val ROSE_TAUPE = Color(144, 93, 93)

        /**
         * Rose vale (#AB4E52)
         */
        val ROSE_VALE = Color(171, 78, 82)

        /**
         * Rosso corsa (#D40000)
         */
        val ROSSO_CORSA = Color(212, 0, 0)

        /**
         * Rosy brown (#BC8F8F)
         */
        val ROSY_BROWN = Color(188, 143, 143)

        /**
         * Royal azure (#0038A8)
         */
        val ROYAL_AZURE = Color(0, 56, 168)

        /**
         * Royal blue (#4169E1)
         */
        val ROYAL_BLUE = Color(65, 105, 225)

        /**
         * Royal fuchsia (#CA2C92)
         */
        val ROYAL_FUCHSIA = Color(202, 44, 146)

        /**
         * Royal purple (#7851A9)
         */
        val ROYAL_PURPLE = Color(120, 81, 169)

        /**
         * Ruby (#E0115F)
         */
        val RUBY = Color(224, 17, 95)

        /**
         * Ruddy (#FF0028)
         */
        val RUDDY = Color(255, 0, 40)

        /**
         * Ruddy brown (#BB6528)
         */
        val RUDDY_BROWN = Color(187, 101, 40)

        /**
         * Ruddy pink (#E18E96)
         */
        val RUDDY_PINK = Color(225, 142, 150)

        /**
         * Rufous (#A81C07)
         */
        val RUFOUS = Color(168, 28, 7)

        /**
         * Russet (#80461B)
         */
        val RUSSET = Color(128, 70, 27)

        /**
         * Rust (#B7410E)
         */
        val RUST = Color(183, 65, 14)

        /**
         * Sacramento State green (#00563F)
         */
        val SACRAMENTO_STATE_GREEN = Color(0, 86, 63)

        /**
         * Saddle brown (#8B4513)
         */
        val SADDLE_BROWN = Color(139, 69, 19)

        /**
         * Safety orange (#FF6700)
         */
        val SAFETY_ORANGE = Color(255, 103, 0)

        /**
         * Saffron (#F4C430)
         */
        val SAFFRON = Color(244, 196, 48)

        /**
         * Saint Patrick Blue (#23297A)
         */
        val SAINT_PATRICK_BLUE = Color(35, 41, 122)

        /**
         * Salmon (#FF8C69)
         */
        val SALMON = Color(255, 140, 105)

        /**
         * Salmon pink (#FF91A4)
         */
        val SALMON_PINK = Color(255, 145, 164)

        /**
         * Sand (#C2B280)
         */
        val SAND = Color(194, 178, 128)

        /**
         * Sandstorm (#ECD540)
         */
        val SANDSTORM = Color(236, 213, 64)

        /**
         * Sandy brown (#F4A460)
         */
        val SANDY_BROWN = Color(244, 164, 96)

        /**
         * Sandy taupe (#967117)
         */
        val SANDY_TAUPE = Color(150, 113, 23)

        /**
         * Sand dune (#967117)
         */
        val SAND_DUNE = Color(150, 113, 23)

        /**
         * Sapphire (#0F52BA)
         */
        val SAPPHIRE = Color(15, 82, 186)

        /**
         * Sap green (#507D2A)
         */
        val SAP_GREEN = Color(80, 125, 42)

        /**
         * Satin sheen gold (#CBA135)
         */
        val SATIN_SHEEN_GOLD = Color(203, 161, 53)

        /**
         * Scarlet (#FF2400)
         */
        val SCARLET = Color(255, 36, 0)

        /**
         * School bus yellow (#FFD800)
         */
        val SCHOOL_BUS_YELLOW = Color(255, 216, 0)

        /**
         * Screamin Green (#76FF7A)
         */
        val SCREAMIN_GREEN = Color(118, 255, 122)

        /**
         * Seal brown (#321414)
         */
        val SEAL_BROWN = Color(50, 20, 20)

        /**
         * Seashell (#FFF5EE)
         */
        val SEASHELL = Color(255, 245, 238)

        /**
         * Sea blue (#006994)
         */
        val SEA_BLUE = Color(0, 105, 148)

        /**
         * Sea green (#2E8B57)
         */
        val SEA_GREEN = Color(46, 139, 87)

        /**
         * Selective yellow (#FFBA00)
         */
        val SELECTIVE_YELLOW = Color(255, 186, 0)

        /**
         * Sepia (#704214)
         */
        val SEPIA = Color(112, 66, 20)

        /**
         * Shadow (#8A795D)
         */
        val SHADOW = Color(138, 121, 93)

        /**
         * Shamrock (#45CEA2)
         */
        val SHAMROCK = Color(69, 206, 162)

        /**
         * Shamrock green (#009E60)
         */
        val SHAMROCK_GREEN = Color(0, 158, 96)

        /**
         * Shocking pink (#FC0FC0)
         */
        val SHOCKING_PINK = Color(252, 15, 192)

        /**
         * Sienna (#882D17)
         */
        val SIENNA = Color(136, 45, 23)

        /**
         * Silver (#C0C0C0)
         */
        val SILVER = Color(192, 192, 192)

        /**
         * Sinopia (#CB410B)
         */
        val SINOPIA = Color(203, 65, 11)

        /**
         * Skobeloff (#007474)
         */
        val SKOBELOFF = Color(0, 116, 116)

        /**
         * Sky blue (#87CEEB)
         */
        val SKY_BLUE = Color(135, 206, 235)

        /**
         * Sky magenta (#CF71AF)
         */
        val SKY_MAGENTA = Color(207, 113, 175)

        /**
         * Slate blue (#6A5ACD)
         */
        val SLATE_BLUE = Color(106, 90, 205)

        /**
         * Slate gray (#708090)
         */
        val SLATE_GRAY = Color(112, 128, 144)

        /**
         * Smalt (#003399)
         */
        val SMALT = Color(0, 51, 153)

        /**
         * Smokey topaz (#933D41)
         */
        val SMOKEY_TOPAZ = Color(147, 61, 65)

        /**
         * Smoky black (#100C08)
         */
        val SMOKY_BLACK = Color(16, 12, 8)

        /**
         * Snow (#FFFAFA)
         */
        val SNOW = Color(255, 250, 250)

        /**
         * Spiro Disco Ball (#0FC0FC)
         */
        val SPIRO_DISCO_BALL = Color(15, 192, 252)

        /**
         * Spring bud (#A7FC00)
         */
        val SPRING_BUD = Color(167, 252, 0)

        /**
         * Spring green (#00FF7F)
         */
        val SPRING_GREEN = Color(0, 255, 127)

        /**
         * Steel blue (#4682B4)
         */
        val STEEL_BLUE = Color(70, 130, 180)

        /**
         * Stil de grain yellow (#FADA5E)
         */
        val STIL_DE_GRAIN_YELLOW = Color(250, 218, 94)

        /**
         * Stizza (#990000)
         */
        val STIZZA = Color(153, 0, 0)

        /**
         * Stormcloud (#008080)
         */
        val STORMCLOUD = Color(0, 128, 128)

        /**
         * Straw (#E4D96F)
         */
        val STRAW = Color(228, 217, 111)

        /**
         * Sunglow (#FFCC33)
         */
        val SUNGLOW = Color(255, 204, 51)

        /**
         * Sunset (#FAD6A5)
         */
        val SUNSET = Color(250, 214, 165)

        /**
         * Sunset Orange (#FD5E53)
         */
        val SUNSET_ORANGE = Color(253, 94, 83)

        /**
         * Tan (#D2B48C)
         */
        val TAN = Color(210, 180, 140)

        /**
         * Tangelo (#F94D00)
         */
        val TANGELO = Color(249, 77, 0)

        /**
         * Tangerine (#F28500)
         */
        val TANGERINE = Color(242, 133, 0)

        /**
         * Tangerine yellow (#FFCC00)
         */
        val TANGERINE_YELLOW = Color(255, 204, 0)

        /**
         * Taupe (#483C32)
         */
        val TAUPE = Color(72, 60, 50)

        /**
         * Taupe gray (#8B8589)
         */
        val TAUPE_GRAY = Color(139, 133, 137)

        /**
         * Tawny (#CD5700)
         */
        val TAWNY = Color(205, 87, 0)

        /**
         * Teal (#008080)
         */
        val TEAL = Color(0, 128, 128)

        /**
         * Teal blue (#367588)
         */
        val TEAL_BLUE = Color(54, 117, 136)

        /**
         * Teal green (#006D5B)
         */
        val TEAL_GREEN = Color(0, 109, 91)

        /**
         * Tea green (#D0F0C0)
         */
        val TEA_GREEN = Color(208, 240, 192)

        /**
         * Tea rose (#F4C2C2)
         */
        val TEA_ROSE = Color(244, 194, 194)

        /**
         * Terra cotta (#E2725B)
         */
        val TERRA_COTTA = Color(226, 114, 91)

        /**
         * Thistle (#D8BFD8)
         */
        val THISTLE = Color(216, 191, 216)

        /**
         * Thulian pink (#DE6FA1)
         */
        val THULIAN_PINK = Color(222, 111, 161)

        /**
         * Tickle Me Pink (#FC89AC)
         */
        val TICKLE_ME_PINK = Color(252, 137, 172)

        /**
         * Tiffany Blue (#0ABAB5)
         */
        val TIFFANY_BLUE = Color(10, 186, 181)

        /**
         * Tiger eye (#E08D3C)
         */
        val TIGER_EYE = Color(224, 141, 60)

        /**
         * Timberwolf (#DBD7D2)
         */
        val TIMBERWOLF = Color(219, 215, 210)

        /**
         * Titanium yellow (#EEE600)
         */
        val TITANIUM_YELLOW = Color(238, 230, 0)

        /**
         * Tomato (#FF6347)
         */
        val TOMATO = Color(255, 99, 71)

        /**
         * Toolbox (#746CC0)
         */
        val TOOLBOX = Color(116, 108, 192)

        /**
         * Topaz (#FFC87C)
         */
        val TOPAZ = Color(255, 200, 124)

        /**
         * Tractor red (#FD0E35)
         */
        val TRACTOR_RED = Color(253, 14, 53)

        /**
         * Trolley Grey (#808080)
         */
        val TROLLEY_GREY = Color(128, 128, 128)

        /**
         * Tropical rain forest (#00755E)
         */
        val TROPICAL_RAIN_FOREST = Color(0, 117, 94)

        /**
         * True Blue (#0073CF)
         */
        val TRUE_BLUE = Color(0, 115, 207)

        /**
         * Tufts Blue (#417DC1)
         */
        val TUFTS_BLUE = Color(65, 125, 193)

        /**
         * Tumbleweed (#DEAA88)
         */
        val TUMBLEWEED = Color(222, 170, 136)

        /**
         * Turkish rose (#B57281)
         */
        val TURKISH_ROSE = Color(181, 114, 129)

        /**
         * Turquoise (#30D5C8)
         */
        val TURQUOISE = Color(48, 213, 200)

        /**
         * Turquoise blue (#00FFEF)
         */
        val TURQUOISE_BLUE = Color(0, 255, 239)

        /**
         * Turquoise green (#A0D6B4)
         */
        val TURQUOISE_GREEN = Color(160, 214, 180)

        /**
         * Tuscan red (#66424D)
         */
        val TUSCAN_RED = Color(102, 66, 77)

        /**
         * Twilight lavender (#8A496B)
         */
        val TWILIGHT_LAVENDER = Color(138, 73, 107)

        /**
         * Tyrian purple (#66023C)
         */
        val TYRIAN_PURPLE = Color(102, 2, 60)

        /**
         * UA blue (#0033AA)
         */
        val UA_BLUE = Color(0, 51, 170)

        /**
         * UA red (#D9004C)
         */
        val UA_RED = Color(217, 0, 76)

        /**
         * Ube (#8878C3)
         */
        val UBE = Color(136, 120, 195)

        /**
         * UCLA Blue (#536895)
         */
        val UCLA_BLUE = Color(83, 104, 149)

        /**
         * UCLA Gold (#FFB300)
         */
        val UCLA_GOLD = Color(255, 179, 0)

        /**
         * UFO Green (#3CD070)
         */
        val UFO_GREEN = Color(60, 208, 112)

        /**
         * Ultramarine (#120A8F)
         */
        val ULTRAMARINE = Color(18, 10, 143)

        /**
         * Ultramarine blue (#4166F5)
         */
        val ULTRAMARINE_BLUE = Color(65, 102, 245)

        /**
         * Ultra pink (#FF6FFF)
         */
        val ULTRA_PINK = Color(255, 111, 255)

        /**
         * Umber (#635147)
         */
        val UMBER = Color(99, 81, 71)

        /**
         * United Nations blue (#5B92E5)
         */
        val UNITED_NATIONS_BLUE = Color(91, 146, 229)

        /**
         * University of California Gold (#B78727)
         */
        val UNIVERSITY_OF_CALIFORNIA_GOLD = Color(183, 135, 39)

        /**
         * Unmellow Yellow (#FFFF66)
         */
        val UNMELLOW_YELLOW = Color(255, 255, 102)

        /**
         * Upsdell red (#AE2029)
         */
        val UPSDELL_RED = Color(174, 32, 41)

        /**
         * UP Forest green (#014421)
         */
        val UP_FOREST_GREEN = Color(1, 68, 33)

        /**
         * UP Maroon (#7B1113)
         */
        val UP_MAROON = Color(123, 17, 19)

        /**
         * Urobilin (#E1AD21)
         */
        val UROBILIN = Color(225, 173, 33)

        /**
         * USC Cardinal (#990000)
         */
        val USC_CARDINAL = Color(153, 0, 0)

        /**
         * USC Gold (#FFCC00)
         */
        val USC_GOLD = Color(255, 204, 0)

        /**
         * Utah Crimson (#D3003F)
         */
        val UTAH_CRIMSON = Color(211, 0, 63)

        /**
         * Vanilla (#F3E5AB)
         */
        val VANILLA = Color(243, 229, 171)

        /**
         * Vegas gold (#C5B358)
         */
        val VEGAS_GOLD = Color(197, 179, 88)

        /**
         * Venetian red (#C80815)
         */
        val VENETIAN_RED = Color(200, 8, 21)

        /**
         * Verdigris (#43B3AE)
         */
        val VERDIGRIS = Color(67, 179, 174)

        /**
         * Vermilion (#E34234)
         */
        val VERMILION = Color(227, 66, 52)

        /**
         * Veronica (#A020F0)
         */
        val VERONICA = Color(160, 32, 240)

        /**
         * Violet (#EE82EE)
         */
        val VIOLET = Color(238, 130, 238)

        /**
         * Violet Blue (#324AB2)
         */
        val VIOLET_BLUE = Color(50, 74, 178)

        /**
         * Violet Red (#F75394)
         */
        val VIOLET_RED = Color(247, 83, 148)

        /**
         * Viridian (#40826D)
         */
        val VIRIDIAN = Color(64, 130, 109)

        /**
         * Vivid auburn (#922724)
         */
        val VIVID_AUBURN = Color(146, 39, 36)

        /**
         * Vivid burgundy (#9F1D35)
         */
        val VIVID_BURGUNDY = Color(159, 29, 53)

        /**
         * Vivid cerise (#DA1D81)
         */
        val VIVID_CERISE = Color(218, 29, 129)

        /**
         * Vivid tangerine (#FFA089)
         */
        val VIVID_TANGERINE = Color(255, 160, 137)

        /**
         * Vivid violet (#9F00FF)
         */
        val VIVID_VIOLET = Color(159, 0, 255)

        /**
         * Warm black (#004242)
         */
        val WARM_BLACK = Color(0, 66, 66)

        /**
         * Waterspout (#00FFFF)
         */
        val WATERSPOUT = Color(0, 255, 255)

        /**
         * Wenge (#645452)
         */
        val WENGE = Color(100, 84, 82)

        /**
         * Wheat (#F5DEB3)
         */
        val WHEAT = Color(245, 222, 179)

        /**
         * White (#FFFFFF)
         */
        val WHITE = Color(255, 255, 255)

        /**
         * White smoke (#F5F5F5)
         */
        val WHITE_SMOKE = Color(245, 245, 245)

        /**
         * Wild blue yonder (#A2ADD0)
         */
        val WILD_BLUE_YONDER = Color(162, 173, 208)

        /**
         * Wild Strawberry (#FF43A4)
         */
        val WILD_STRAWBERRY = Color(255, 67, 164)

        /**
         * Wild Watermelon (#FC6C85)
         */
        val WILD_WATERMELON = Color(252, 108, 133)

        /**
         * Wine (#722F37)
         */
        val WINE = Color(114, 47, 55)

        /**
         * Wisteria (#C9A0DC)
         */
        val WISTERIA = Color(201, 160, 220)

        /**
         * Xanadu (#738678)
         */
        val XANADU = Color(115, 134, 120)

        /**
         * Yale Blue (#0F4D92)
         */
        val YALE_BLUE = Color(15, 77, 146)

        /**
         * Yellow (#FFFF00)
         */
        val YELLOW = Color(255, 255, 0)

        /**
         * Yellow green (#9ACD32)
         */
        val YELLOW_GREEN = Color(154, 205, 50)

        /**
         * Yellow Orange (#FFAE42)
         */
        val YELLOW_ORANGE = Color(255, 174, 66)

        /**
         * Zaffre (#0014A8)
         */
        val ZAFFRE = Color(0, 20, 168)

        /**
         * Zinnwaldite brown (#2C1608)
         */
        val ZINNWALDITE_BROWN = Color(44, 22, 8)

		//</editor-fold>
    }

	override fun hashCode(): Int {
		return argb.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		if (other !is Color) return false
		return other.argb == argb
	}

	override fun toString(): String {
		return "#${hex8}"
	}
}

/**
 * The format of the image or texture.
 * @property bpp Bytes per pixel in uncompressed format. If the format is compressed, this is `0`, as it needs a different calculation.
 */
enum class PictureFormat(internal val value: PixelFormat, val bpp: Int = 0) {
	/**
	 * 8 bit per pixel (no alpha)
	 *
	 * Commonly used for grayscale images
	 */
	UNCOMPRESSED_GRAYSCALE(PIXELFORMAT_UNCOMPRESSED_GRAYSCALE, 1),
	/**
	 * 8*2 bpp (2 channels) for gray + alpha
	 *
	 * Commonly used for grayscale images with transparency
	 */
	UNCOMPRESSED_GRAY_ALPHA(PIXELFORMAT_UNCOMPRESSED_GRAY_ALPHA, 2),
	/**
	 * 16 bpp for R5G5B5
	 *
	 * Commonly used in older graphics systems
	 */
	UNCOMPRESSED_R5G6B5(PIXELFORMAT_UNCOMPRESSED_R5G6B5, 2),
	/**
	 * 24 bpp for R8G8B8
	 *
	 * Commonly used for standard RGB images
	 */
	UNCOMPRESSED_R8G8B8(PIXELFORMAT_UNCOMPRESSED_R8G8B8, 3),
	/**
	 * 16 bpp for R5G5B5A1
	 *
	 * Commonly used for images with 1-bit alpha transparency
	 */
	UNCOMPRESSED_R5G5B5A1(PIXELFORMAT_UNCOMPRESSED_R5G5B5A1, 2),
	/**
	 * 16 bpp for R4G4B4A4
	 *
	 * Commonly used for images with 4-bit alpha transparency
	 */
	UNCOMPRESSED_R4G4B4A4(PIXELFORMAT_UNCOMPRESSED_R4G4B4A4, 2),
	/**
	 * 32 bpp for R8G8B8A8
	 *
	 * Commonly used for standard RGBA images
	 */
	UNCOMPRESSED_R8G8B8A8(PIXELFORMAT_UNCOMPRESSED_R8G8B8A8, 4),
	/**
	 * 32 bpp for R32
	 *
	 * High precision single channel (red) images
	 */
	UNCOMPRESSED_R32(PIXELFORMAT_UNCOMPRESSED_R32, 4),
	/**
	 * 32*3 bpp for R32G32
	 *
	 * High precision two channel (red, green) images
	 */
	UNCOMPRESSED_R32G32B32(PIXELFORMAT_UNCOMPRESSED_R32G32B32, 12),
	/**
	 * 32*3 bpp for R32G32B32
	 *
	 * High precision three channel (red, green, blue) images
	 */
	UNCOMPRESSED_R32G32B32A32(PIXELFORMAT_UNCOMPRESSED_R32G32B32A32, 16),
	/**
	 * 4 bpp (no alpha) for RGB data in DXT1 format
	 *
	 * Commonly used for images without transparency
	 */
	COMPRESSED_DXT1_RGB(PIXELFORMAT_COMPRESSED_DXT1_RGB),
	/**
	 * 4 bpp (1 bit alpha) for RGBA data in DXT1 format
	 *
	 * Commonly used for images with 1-bit alpha transparency
	 */
	COMPRESSED_DXT1_RGBA(PIXELFORMAT_COMPRESSED_DXT1_RGBA),
	/**
	 * 8 bpp for RGBA data in DXT3 format
	 *
	 * Commonly used for images with sharp alpha transitions
	 */
	COMPRESSED_DXT3_RGBA(PIXELFORMAT_COMPRESSED_DXT3_RGBA),
	/**
	 * 8 bpp for RGBA data in DXT5 format
	 *
	 * Commonly used for images with smooth alpha transitions
	 */
	COMPRESSED_DXT5_RGBA(PIXELFORMAT_COMPRESSED_DXT5_RGBA),
	/**
	 * 4 bpp for RGB data in ETC1 format
	 *
	 * Commonly used in OpenGL ES 2.0+
	 */
	COMPRESSED_ETC1_RGB(PIXELFORMAT_COMPRESSED_ETC1_RGB),
	/**
	 * 8 bpp for RGBA data in ETC2/EAC format
	 *
	 * Commonly used in OpenGL ES 3.0+
	 */
	COMPRESSED_ETC2_RGB(PIXELFORMAT_COMPRESSED_ETC2_RGB),
	/**
	 * 8 bpp for RGBA data in ETC2/EAC format with 1-bit alpha
	 *
	 * Commonly used in OpenGL ES 3.0+
	 */
	COMPRESSED_ETC2_EAC_RGBA(PIXELFORMAT_COMPRESSED_ETC2_EAC_RGBA),
	/**
	 * 4 bpp for RGB data in PVRT format
	 *
	 * Commonly used in PowerVR GPUs
	 */
	COMPRESSED_PVRT_RGB(PIXELFORMAT_COMPRESSED_PVRT_RGB),
	/**
	 * 4 bpp for RGBA data in PVRT format
	 *
	 * Commonly used in PowerVR GPUs
	 */
	COMPRESSED_PVRT_RGBA(PIXELFORMAT_COMPRESSED_PVRT_RGBA),
	/**
	 * 8 bpp for RGBA data in ASTC 4x4 format
	 *
	 * High quality compression for mobile and desktop
	 */
	COMPRESSED_ASTC_4X4_RGBA(PIXELFORMAT_COMPRESSED_ASTC_4x4_RGBA),
	/**
	 * 2 bpp for RGBA data in ASTC 8x8 format
	 *
	 * High compression ratio for mobile and desktop
	 */
	COMPRESSED_ASTC_8X8_RGBA(PIXELFORMAT_COMPRESSED_ASTC_8x8_RGBA)
}

/**
 * Represents a raylib image.
 *
 * Note that images are immutable and modification operations return new Image instances.
 */
class Image internal constructor(internal val raw: CValue<raylib.internal.Image>) {

    companion object {
        /**
         * Loads an image from the specified file path.
         * @param path The file path of the image to load, relative to [appDir].
         * @return An Image object representing the loaded image.
         */
        fun load(path: String): Image = Image(LoadImage(path.inAppDir()))

		/**
		 * Loads an image from the specified file.
		 * @param file The file to load
		 * @return An Image object representing the loaded image
		 */
		fun load(file: File): Image = load(file.absolutePath)

        /**
         * Takes a screenshot of the current screen.
         * @return An Image object representing the screenshot.
         */
        fun screenshot(): Image {
            val rawImage = LoadImageFromScreen()
            return Image(rawImage)
        }

        /**
         * Creates an image filled with the specified color.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param color The color to fill the image with.
         * @return An Image object representing the created image.
         */
        fun fromColor(width: Int, height: Int, color: Color): Image {
            val rawImage = GenImageColor(width, height, color.raw())
            return Image(rawImage)
        }

        /**
         * Creates an image with a linear gradient between two colors.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param angle The angle of the gradient in degrees (0-360).
         * @param start The starting color of the gradient.
         * @param end The ending color of the gradient.
         * @return An Image object representing the created gradient image.
         */
        fun fromLinearGradient(width: Int, height: Int, angle: Int, start: Color, end: Color): Image {
            val rawImage = GenImageGradientLinear(width, height, angle, start.raw(), end.raw())
            return Image(rawImage)
        }

        /**
         * Creates an image with a radial gradient between two colors.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param density The density of the gradient.
         * @param inner The inner color of the gradient.
         * @param outer The outer color of the gradient.
         * @return An Image object representing the created radial gradient image.
         */
        fun fromRadialGradient(width: Int, height: Int, density: Float, inner: Color, outer: Color): Image {
            val rawImage = GenImageGradientRadial(width, height, density, inner.raw(), outer.raw())
            return Image(rawImage)
        }

        /**
         * Creates an image with a square gradient between two colors.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param density The density of the gradient.
         * @param inner The inner color of the gradient.
         * @param outer The outer color of the gradient.
         * @return An Image object representing the created square gradient image.
         */
        fun fromSquareGradient(width: Int, height: Int, density: Float, inner: Color, outer: Color): Image {
            val rawImage = GenImageGradientSquare(width, height, density, inner.raw(), outer.raw())
            return Image(rawImage)
        }

        /**
         * Creates a checked pattern image with two colors.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param checksX The number of checks in the X direction.
         * @param checksY The number of checks in the Y direction.
         * @param col1 The first color.
         * @param col2 The second color.
         * @return An Image object representing the created checked pattern image.
         */
        fun fromChecked(width: Int, height: Int, checksX: Int, checksY: Int, col1: Color, col2: Color): Image {
            val rawImage = GenImageChecked(width, height, checksX, checksY, col1.raw(), col2.raw())
            return Image(rawImage)
        }

        /**
         * Creates an image filled with white noise.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param factor The noise factor (0.0 to 1.0).
         * @return An Image object representing the created white noise image.
         */
        fun fromWhiteNoise(width: Int, height: Int, factor: Float = 0.5F): Image {
            val rawImage = GenImageWhiteNoise(width, height, factor)
            return Image(rawImage)
        }

        /**
         * Creates an image filled with Perlin noise.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param offsetX The X offset for the noise.
         * @param offsetY The Y offset for the noise.
         * @param scale The scale of the noise.
         * @return An Image object representing the created Perlin noise image.
         */
        fun fromPerlinNoise(width: Int, height: Int, offsetX: Int = 0, offsetY: Int = 0, scale: Float = 1.0F): Image {
            val rawImage = GenImagePerlinNoise(width, height, offsetX, offsetY, scale)
            return Image(rawImage)
        }

        /**
         * Creates an image filled with a cellular noise pattern.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param cellSize The size of the cells.
         * @return An Image object representing the created cellular noise image.
         */
        fun fromCellular(width: Int, height: Int, cellSize: Int): Image {
            val rawImage = GenImageCellular(width, height, cellSize)
            return Image(rawImage)
        }

        /**
         * Creates a grayscale image from the provided text string.
         * @param width The width of the image.
         * @param height The height of the image.
         * @param text The text to render in the image.
         * @return An Image object representing the created text image.
         */
        fun fromText(width: Int, height: Int, text: String): Image {
            val rawImage = GenImageText(width, height, text)
            return Image(rawImage)
        }
    }


    /**
     * The width of the image in pixels.
     */
    val width: Int
        get() = raw.useContents { width }

    /**
     * The height of the image in pixels.
     */
    val height: Int
        get() = raw.useContents { height }

	/**
	 * A byte array representation of this image's data.
	 */
	val bytes: ByteArray?
		get() = raw.useContents {
			val bpp = this@Image.format.bpp
			val size = if (bpp != 0) width * height * bpp else {
				when (this@Image.format) {
					// 4x4 compressed
					PictureFormat.COMPRESSED_DXT1_RGB,
					PictureFormat.COMPRESSED_DXT1_RGBA,
					PictureFormat.COMPRESSED_ETC1_RGB,
					PictureFormat.COMPRESSED_ETC2_RGB,
					PictureFormat.COMPRESSED_PVRT_RGB,
					PictureFormat.COMPRESSED_PVRT_RGBA -> {
						val blocksWide = (width + 3) / 4 // block width = 4
						val blocksHigh = (height + 3) / 4 // block height = 4
						blocksWide * blocksHigh * 8 // 1 byte per block
					}
					PictureFormat.COMPRESSED_DXT3_RGBA,
					PictureFormat.COMPRESSED_DXT5_RGBA,
					PictureFormat.COMPRESSED_ETC2_EAC_RGBA,
					PictureFormat.COMPRESSED_ASTC_4X4_RGBA -> {
						val blocksWide = (width + 3) / 4 // block width = 4
						val blocksHigh = (height + 3) / 4 // blocks height = 4
						blocksWide * blocksHigh * 16 // 2 bytes per block
					}
					// 8x8 compressed
					PictureFormat.COMPRESSED_ASTC_8X8_RGBA -> {
						val blocksWide = (width + 7) / 8 // block width = 8
						val blocksHigh = (height + 7) / 8 // block height = 8
						blocksWide * blocksHigh * 16 // 2 bytes per block
					}
					else -> error("format ${this@Image.format} is not configured properly; please report this bug")
				}
			}

			data?.toByteArray(size)
		}

    /**
     * The pixel format of the image.
     */
    val format: PictureFormat
        get() {
            val rawFormat = raw.useContents { format }
            return PictureFormat.entries.find { it.value.toInt() == rawFormat }
                ?: throw IllegalStateException("Unknown image format: $rawFormat")
        }

	/**
	 * Whether the current image is valid.
	 * @return true if valid, false otherwise
	 */
	val isValid: Boolean
		get() = IsImageValid(raw)

    /**
     * Gets the color of the pixel at the specified (x, y) coordinates.
     * @param x The X coordinate of the pixel.
     * @param y The Y coordinate of the pixel.
     * @return A Color object representing the color of the pixel.
     */
    operator fun get(x: Int, y: Int): Color {
		return GetImageColor(raw, x, y).useContents { Color(this) }
    }

    /**
     * Gets the color data from the image.
     * @return An array of Color objects representing the image's pixel colors.
     */
    fun getColors(): Array<Color> {
        val array = LoadImageColors(raw) ?: return arrayOf()
        val size = raw.useContents { width * height }
        return Array(size) { i -> Color(array[i]) }
    }

	private fun NativePlacement.copyPtr(): CPointer<raylib.internal.Image> {
		val ptr = alloc<raylib.internal.Image>()
		raw.useContents {
			ptr.width = width
			ptr.height = height
			ptr.mipmaps = mipmaps
			ptr.format = format
			ptr.data = data
		}

		return ptr.ptr
	}

	private fun NativePlacement.ptrOf(image: Image): CPointer<raylib.internal.Image> {
		val ptr = alloc<raylib.internal.Image>()
		image.raw.useContents {
			ptr.width = width
			ptr.height = height
			ptr.mipmaps = mipmaps
			ptr.format = format
			ptr.data = data
		}

		return ptr.ptr
	}

	/**
	 * Converts this image to another image format.
	 * @param format The image format to convert to
	 * @return A new Image object that is in the new format
	 */
	fun convertTo(format: PictureFormat): Image = memScoped {
		val copy = copyPtr()
		ImageFormat(copy, format.value.toInt())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Flips the image horizontally.
	 * @return A new Image object that is the horizontally flipped version of this image.
	 */
	fun flipH(): Image = memScoped {
		val copy = copyPtr()
		ImageFlipHorizontal(copy)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Flips the image vertically.
	 * @return A new Image object that is the vertically flipped version of this image.
	 */
	fun flipV(): Image = memScoped {
		val copy = copyPtr()
		ImageFlipVertical(copy)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Rotates the image clockwise 90 degrees.
	 * @return A new Image object that is the rotated version of this image.
	 */
	fun rotateCW(): Image = memScoped {
		val copy = copyPtr()
		ImageRotateCW(copy)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Rotates the image counter-clockwise 90 degrees.
	 * @return A new Image object that is the rotated version of this image.
	 */
	fun rotateCCW(): Image = memScoped {
		val copy = copyPtr()
		ImageRotateCCW(copy)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Rotates the image a certain number of degrees.
	 *
	 * Input negative values to rotate counter-clockwise.
	 * @param degrees Degrees to rotate (-359 to 359)
	 * @return A new Image object that is the rotated version of this image.
	 */
	fun rotate(degrees: Int): Image = memScoped {
		val copy = copyPtr()
		ImageRotateCW(copy)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a pixel on the current image.
	 * @param x The X coordinate of the pixel
	 * @param y The Y coordinate of the pixel
	 * @return A new Image object with the drawn pixel on the image
	 */
	fun draw(x: Int, y: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawPixel(copy, x, y, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a line on the current image.
	 * @param x1 The X coordinate of the first point.
	 * @param y1 The Y coordinate of the first point.
	 * @param x2 The X coordinate of the second point.
	 * @param y2 The Y coordinate of the second point.
	 * @param color The color of the line.
	 * @return A new Image object with the drawn line on the image
	 */
	fun line(x1: Int, y1: Int, x2: Int, y2: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawLine(copy, x1, y1, x2, y2, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a line on the current image.
	 * @param x1 The X coordinate of the first point.
	 * @param y1 The Y coordinate of the first point.
	 * @param x2 The X coordinate of the second point.
	 * @param y2 The Y coordinate of the second point.
	 * @param thick The thickness of the line.
	 * @param color The color of the line.
	 * @return A new Image object with the drawn line on the image.
	 */
	fun line(x1: Int, y1: Int, x2: Int, y2: Int, thick: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawLineEx(
			copy, (x1 to y1).toVector2(), (x2 to y2).toVector2(), thick, color.raw()
		)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a circle outline on the image.
	 * @param cx The X coordinate of the center.
	 * @param cy The Y coordinate of the center.
	 * @param radius The radius of the circle
	 * @param color The color of the circle.
	 * @return A new Image object with the circle drawn on the image.
	 */
	fun circle(cx: Int, cy: Int, radius: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawCircleLines(copy, cx, cy, radius, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a filled circle on the image.
	 * @param cx The X coordinate of the center.
	 * @param cy The Y coordinate of the center.
	 * @param radius The radius of the circle
	 * @param color The color of the circle.
	 * @return A new Image object with the circle drawn on the image.
	 */
	fun fillCircle(cx: Int, cy: Int, radius: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawCircle(copy, cx, cy, radius, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a rectangle outline on the image.
	 * @param x The X coordinate of the top-left corner.
	 * @param y The Y coordinate of the top-left corner.
	 * @param width The width of the rectangle.
	 * @param height The height of the rectangle.
	 * @param thick The thickness of the outline.
	 * @param color The color of the outline.
	 * @return A new Image object with the rectangle drawn on the image.
	 */
	fun rect(x: Int, y: Int, width: Int, height: Int, thick: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawRectangleLines(
			copy,
			cValue {
				this.x = x.toFloat()
				this.y = y.toFloat()
				this.width = width.toFloat()
				this.height = height.toFloat()
			}, thick, color.raw()
		)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a filled rectangle on the image.
	 * @param x The X coordinate of the top-left corner.
	 * @param y The Y coordinate of the top-left corner.
	 * @param width The width of the rectangle.
	 * @param height The height of the rectangle.
	 * @param color The color of the rectangle.
	 * @return A new Image object with the rectangle drawn on the image.
	 */
	fun fillRect(x: Int, y: Int, width: Int, height: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawRectangle(copy, x, y, width, height, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a triangle outline on the image.
	 * @param x1 The X coordinate of the first vertex
	 * @param y1 The Y coordinate of the first vertex
	 * @param x2 The X coordinate of the second vertex
	 * @param y2 The Y coordinate of the second vertex
	 * @param x3 The X coordinate of the third vertex
	 * @param y3 The Y coordinate of the third vertex
	 * @param color The color of the outline
	 * @return A new Image object with the triangle drawn on the image
	 */
	fun triangle(
		x1: Int,
		y1: Int,
		x2: Int,
		y2: Int,
		x3: Int,
		y3: Int,
		color: Color
	): Image = memScoped {
		val copy = copyPtr()
		ImageDrawTriangleLines(copy,
			(x1 to y1).toVector2(),
			(x2 to y2).toVector2(),
			(x3 to y3).toVector2(),
			color.raw()
		)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a filled triangle on the image.
	 * @param x1 The X coordinate of the first vertex
	 * @param y1 The Y coordinate of the first vertex
	 * @param x2 The X coordinate of the second vertex
	 * @param y2 The Y coordinate of the second vertex
	 * @param x3 The X coordinate of the third vertex
	 * @param y3 The Y coordinate of the third vertex
	 * @param color The color of the triangle
	 * @return A new Image object with the triangle drawn on the image
	 */
	fun fillTriangle(
		x1: Int,
		y1: Int,
		x2: Int,
		y2: Int,
		x3: Int,
		y3: Int,
		color: Color
	): Image = memScoped {
		val copy = copyPtr()
		ImageDrawTriangle(
			copy,
			(x1 to y1).toVector2(),
			(x2 to y2).toVector2(),
			(x3 to y3).toVector2(),
			color.raw()
		)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a filled triangle on the image, linear interpolating three colors to the center.
	 * @param x1 The X coordinate of the first vertex
	 * @param y1 The Y coordinate of the first vertex
	 * @param x2 The X coordinate of the second vertex
	 * @param y2 The Y coordinate of the second vertex
	 * @param x3 The X coordinate of the third vertex
	 * @param y3 The Y coordinate of the third vertex
	 * @param color1 The color of the triangle at the first vertex
	 * @param color2 The color of the triangle at the second vertex
	 * @param color3 The color of the triangle at the third vertex
	 * @return A new Image object with the triangle drawn on the image
	 */
	fun fillTriangle(
		x1: Int,
		y1: Int,
		x2: Int,
		y2: Int,
		x3: Int,
		y3: Int,
		color1: Color,
		color2: Color,
		color3: Color
	): Image = memScoped {
		val copy = copyPtr()
		ImageDrawTriangleEx(copy,
			(x1 to y1).toVector2(),
			(x2 to y2).toVector2(),
			(x3 to y3).toVector2(),
			color1.raw(), color2.raw(), color3.raw()
		)
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a triangle fan on the image.
	 * @param color The color of the triangle fan
	 * @param points The points to draw the triangle fan through
	 * @return A new Image object with the triangle fan drawn on the image
	 */
	fun triangleFan(color: Color, points: List<Pair<Int, Int>>): Image = memScoped {
		if (points.size < 3) return@memScoped this@Image
		val copy = copyPtr()
		val array = allocArray<Vector2>(points.size) { i ->
			x = points[i].first.toFloat()
			y = points[i].second.toFloat()
		}

		ImageDrawTriangleFan(copy, array, points.size, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a triangle fan on the image.
	 * @param color The color of the triangle fan
	 * @param points The points to draw the triangle fan through
	 * @return A new Image object with the triangle fan drawn on the image
	 */
	fun triangleFan(color: Color, vararg points: Pair<Int, Int>) : Image
		= triangleFan(color, points.toList())

	/**
	 * Draws a triangle strip on the image.
	 * @param color The color of the triangle strip
	 * @param points The points to draw the triangle strip through
	 * @return A new Image object with the triangle strip drawn on the image
	 */
	fun triangleStrip(color: Color, points: List<Pair<Int, Int>>) = memScoped {
		if (points.size < 3) return@memScoped this@Image

		val copy = copyPtr()
		val array = allocArray<Vector2>(points.size) { i ->
			x = points[i].first.toFloat()
			y = points[i].second.toFloat()
		}
		ImageDrawTriangleStrip(copy, array, points.size, color.raw())

		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws a triangle strip on the image.
	 * @param color The color of the triangle strip
	 * @param points The points to draw the triangle strip through
	 * @return A new Image object with the triangle strip drawn on the image
	 */
	fun triangleStrip(color: Color, vararg points: Pair<Int, Int>): Image
		= triangleStrip(color, points.toList())

	/**
	 * Draws a source image onto this image at the specified destination rectangle,
	 * using the specified source rectangle and tint color.
	 * @param src The source image to draw from.
	 * @param sx The x-coordinate of the source rectangle's top-left corner.
	 * @param sy The y-coordinate of the source rectangle's top-left corner.
	 * @param sw The width of the source rectangle.
	 * @param sh The height of the source rectangle.
	 * @param dx The x-coordinate of the destination rectangle's top-left corner.
	 * @param dy The y-coordinate of the destination rectangle's top-left corner.
	 * @param dw The width of the destination rectangle.
	 * @param dh The height of the destination rectangle.
	 * @param tint The color to tint the drawn image. Default is white (no tint).
	 * @return A new Image object with the source image drawn onto it.
	 */
	fun draw(
		src: Image,
		sx: Int, sy: Int, sw: Int, sh: Int,
		dx: Int, dy: Int, dw: Int, dh: Int,
		tint: Color = Color.WHITE
	): Image = memScoped {
		val copy = copyPtr()
		val srcCopy = ptrOf(src).pointed.readValue()

		val srcRect = cValue<Rectangle> {
			x = sx.toFloat()
			y = sy.toFloat()
			width = sw.toFloat()
			height = sh.toFloat()
		}

		val destRect = cValue<Rectangle> {
			x = dx.toFloat()
			y = dy.toFloat()
			width = dw.toFloat()
			height = dh.toFloat()
		}

		ImageDraw(copy, srcCopy, srcRect, destRect, tint.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Draws the entire source image onto this image, scaling it to fit the dimensions of this image,
	 * using the specified tint color.
	 * @param src The source image to draw from.
	 * @param x The x-coordinate of the destination rectangle's top-left corner.
	 * @param y The y-coordinate of the destination rectangle's top-left corner.
	 * @param tint The color to tint the drawn image. Default is white (no tint).
	 * @return A new Image object with the source image drawn onto it.
	 */
	fun draw(src: Image, x: Int, y: Int, tint: Color = Color.WHITE): Image = draw(
		src,
		0, 0, src.width, src.height,
		x, y, this.width, this.height,
		tint
	)

	/**
	 * Draws text on the image at the specified position, with the given font size and color.
	 * @param x The X coordinate of the text position.
	 * @param y The Y coordinate of the text position.
	 * @param text The text string to draw.
	 * @param fontSize The font size of the text.
	 * @param color The color of the text.
	 * @return A new Image object with the text drawn on the image.
	 */
	fun drawText(x: Int, y: Int, text: String, fontSize: Int, color: Color): Image = memScoped {
		val copy = copyPtr()
		ImageDrawText(copy, text, x, y, fontSize, color.raw())
		return Image(copy.pointed.readValue())
	}

	/**
	 * Unloads the raw image from memory.
	 *
	 * **This should only be called if the image wasn't loaded from a file.**
	 * This should be called when an image is no longer in use.
	 */
	fun unload() {
		UnloadImage(raw)
	}

}

/**
 * Draws an image on the canvas at the specified position with an optional tint color.
 * @param image The image to draw.
 * @param x The X coordinate where the image will be drawn.
 * @param y The Y coordinate where the image will be drawn.
 * @param tint The color to tint the image. Default is white (no tint).
 */
fun Canvas.drawImage(
	image: Image,
	x: Int,
	y: Int,
	tint: Color = Color.WHITE
) {
	ensureDrawing()

	// convert to Texture2D, pass to drawTexture
	val texture = Texture2D.load(image)
	drawTexture(texture, x, y, tint)
}

/**
 * Represents a two-dimensional texture in raylib.
 * @property id The unique identifier for the texture.
 * @property width The width of the texture in pixels.
 * @property height The height of the texture in pixels.
 * @property mipmaps The number of mipmap levels (aka levels of detail) in the texture.
 * Higher value of mipmaps = more levels of detail. Most commonly used value is 1 (no mipmaps).
 * @property format The pixel format of the texture.
 */
class Texture2D(
	val id: UInt,
	val width: Int,
	val height: Int,
	val mipmaps: Int = 1,
	val format: PictureFormat = PictureFormat.UNCOMPRESSED_R32G32B32A32
) {

	companion object {

		/**
		 * Loads a texture from the specified file path.
		 * @param path The file path of the texture to load.
		 * @return A Texture2D object representing the loaded texture.
		 */
		fun load(path: String): Texture2D {
			val rawTexture = LoadTexture(path.inAppDir())
			val texture = Texture2D(rawTexture)
			UnloadTexture(rawTexture)

			return texture
		}

		/**
		 * Loads a texture from the specified file path.
		 * @param file The file of the texture to load
		 * @return A Texture2D object representing the loaded texture.
		 */
		fun load(file: File): Texture2D = load(file.absolutePath)

	}

	/**
	 * Creates a Texture2D instance from a raw raylib Texture2D C struct.
	 * @param raw The raw CValue representing the raylib Texture2D.
	 */
	constructor(raw: CValue<raylib.internal.Texture2D>) : this(raw.useContents { this })

	/**
	 * Creates a Texture2D instance from a raw raylib Texture2D struct.
	 * @param raw The raw raylib Texture2D struct.
	 */
	constructor(raw: raylib.internal.Texture2D) : this(
		id = raw.id,
		width = raw.width,
		height = raw.height,
		mipmaps = raw.mipmaps,
		format = PictureFormat.entries.find { it.value.toInt() == raw.format }
			?: throw IllegalStateException("Unknown texture format: ${raw.format}")
	)

	internal fun raw(): CValue<raylib.internal.Texture2D> = cValue {
		id = this@Texture2D.id
		width = this@Texture2D.width
		height = this@Texture2D.height
		mipmaps = this@Texture2D.mipmaps
		format = this@Texture2D.format.value.toInt()
	}

}

/**
 * Represents a render texture in raylib, which includes a color texture and a depth texture.
 *
 * This is typically used for off-screen rendering.
 * @property id The unique identifier for the render texture.
 * @property texture The color texture of the render texture.
 * @property depth The depth texture of the render texture.
 */
data class RenderTexture2D(
	val id: UInt,
	val texture: Texture2D,
	val depth: Texture2D
) {

	internal fun raw(): CValue<raylib.internal.RenderTexture2D> = cValue {
		id = this@RenderTexture2D.id
		texture.id = this@RenderTexture2D.texture.id
		texture.width = this@RenderTexture2D.texture.width
		texture.height = this@RenderTexture2D.texture.height
		texture.mipmaps = this@RenderTexture2D.texture.mipmaps
		texture.format = this@RenderTexture2D.texture.format.value.toInt()
		depth.id = this@RenderTexture2D.depth.id
		depth.width = this@RenderTexture2D.depth.width
		depth.height = this@RenderTexture2D.depth.height
		depth.mipmaps = this@RenderTexture2D.depth.mipmaps
		depth.format = this@RenderTexture2D.depth.format.value.toInt()
	}

}

/**
 * Starts rendering to the specified [RenderTexture2D].
 *
 * When using this function, all subsequent drawing operations will be directed to the provided render texture
 * @param renderTexture The render texture to start rendering to.
 */
fun Window.startTextureMode(renderTexture: RenderTexture2D) {
	BeginTextureMode(renderTexture.raw())
}

/**
 * Ends rendering to the current [RenderTexture2D] and returns to the default framebuffer.
 */
fun Window.endTextureMode() {
	EndTextureMode()
}

/**
 * Executes a block of code while rendering to the specified [RenderTexture2D].
 *
 * This function starts rendering to the provided render texture, executes the given block of code,
 * and then ends rendering to the render texture.
 *
 * @param renderTexture The render texture to render to.
 * @param block The block of code to execute while rendering to the render texture.
 */
suspend fun Window.textureMode(renderTexture: RenderTexture2D, block: suspend Window.() -> Unit) {
	startTextureMode(renderTexture)
	this.block()
	endTextureMode()
}

/**
 * Gets an [Image] from a [Texture2D].
 * @param texture The texture to get the image from.
 * @return An Image object representing the image extracted from the texture.
 */
fun Image.Companion.load(texture: Texture2D) = Image(LoadImageFromTexture(texture.raw()))

/**
 * Creates a [Texture2D] from an [Image].
 * @param image The image to create the texture from.
 * @return A Texture2D object representing the created texture.
 */
fun Texture2D.Companion.load(image: Image): Texture2D {
	val rawTexture = LoadTextureFromImage(image.raw)
	return Texture2D(rawTexture)
}

/**
 * Draws a texture on the canvas at the specified position with an optional tint color.
 * @param texture The texture to draw.
 * @param x The X coordinate where the texture will be drawn.
 * @param y The X coordinate where the texture will be drawn.
 * @param tint The color to tint the texture. Default is white (no tint).
 */
fun Canvas.drawTexture(
	texture: Texture2D,
	x: Int,
	y: Int,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawTexture(texture.raw(), x, y, tint.raw())
}
