@file:OptIn(ExperimentalForeignApi::class)

package kray.shaders

import kotlinx.cinterop.ExperimentalForeignApi
import kray.to
import raylib.Color
import raylib.Sampler2D
import raylib.Shader
import raylib.Window

/**
 * Represents a built-in shader loaded from the application's resources.
 *
 * The properties of this class use delegated properties to manage shader uniform values.
 * The getter is used to retrieve the current value of the uniform from the shader,
 * while the setter updates the uniform value in the shader. The getter will always return
 * the last value set through the setter, or `null` if it has not been set yet.
 */
abstract class BuiltInShader private constructor(loaded: Shader) : Shader(loaded.raw) {

	internal constructor(vsFileName: String, fsFileName: String) : this(load(vsFileName, fsFileName))
	internal constructor(fileName: String) : this(load(fileName))

	internal inner class ShaderPropertyDelegate<T : Any>(
		private val internalName: String? = null,
		private val isSampler2D: Boolean = false,
		defaultValue: T? = null,
	) {
		private var value: T? = defaultValue

		operator fun getValue(thisRef: Shader, property: kotlin.reflect.KProperty<*>): T? {
			val location = thisRef.getLocation(internalName ?: property.name)
			if (location == -1) {
				error("Uniform '${property.name}' not found in shader.")
			}

			return value
		}

		operator fun setValue(thisRef: Shader, property: kotlin.reflect.KProperty<*>, value: T?) {
			val location = thisRef.getLocation(internalName ?: property.name)
			if (location == -1) {
				error("Uniform '${property.name}' not found in shader.")
			}

			if (isSampler2D) {
				if (value !is UInt) {
					error("Uniform '${property.name}' is a sampler2D and requires a UInt value representing the texture unit.")
				}

				thisRef.setValue(location, value, DataType.SAMPLER2D)
			} else {
				thisRef.setValue(location, value)
			}

			this.value = value
		}
	}
}

/**
 * The [ASCII_SHADER] implementation.
 */
class AsciiShader internal constructor() : BuiltInShader("shaders/raylib/ascii.fs") {
	/**
	 * The resolution uniform property.
	 */
	var resolution: Pair<Int, Int>? by ShaderPropertyDelegate()

	/**
	 * The font size uniform property.
	 *
	 * Font sizes less than 9 may not render correctly.
	 */
	var fontSize: Float? by ShaderPropertyDelegate()

	/**
	 * The texture uniform property.
	 */
	var texture: Sampler2D? by ShaderPropertyDelegate("texture0", isSampler2D = true)
}

/**
 * The ASCII shader used for rendering text in ASCII style.
 */
val ASCII_SHADER: AsciiShader
	get() = AsciiShader()

/**
 * The [BASE_SHADER] implementation.
 */
class BaseShader internal constructor() : BuiltInShader("shaders/raylib/base.vs", "shaders/raylib/base.fs") {
	/**
	 * The texture uniform property.
	 */
	var texture: Sampler2D? by ShaderPropertyDelegate("texture0", isSampler2D = true)

	init {
		setDefaultLocation(UniformLocation.MATRIX_MVP)
	}
}

/**
 * The base shader used for standard rendering.
 */
val BASE_SHADER: BaseShader
	get() = BaseShader()

/**
 * Represents a shader used for rendering with various effects.
 */
open class EffectShader internal constructor(fileName: String) : BuiltInShader(fileName) {
	/**
	 * The texture uniform property.
	 */
	var texture: Sampler2D? by ShaderPropertyDelegate("texture0", isSampler2D = true)

	/**
	 * The diffuse color uniform property.
	 *
	 * This color is multiplied with the texture color.
	 */
	var diffuse: Color? by ShaderPropertyDelegate("colDiffuse")

	init {
		diffuse = Color.WHITE
	}
}

/**
 * The [BLOOM_SHADER] implementation.
 */
class BloomShader internal constructor() : EffectShader("shaders/bloom.fs") {

	/**
	 * The size uniform property.
	 */
	var size: Pair<Float, Float>? by ShaderPropertyDelegate()

	/**
	 * The samples uniform property.
	 *
	 * Represents the pixels per axis. Higher values mean bigger glow. Default is 5.
	 */
	var samples: Float? by ShaderPropertyDelegate()

	/**
	 * The quality uniform property.
	 *
	 * Represents the size factor. Lower values mean smaller glow but better quality.
	 * Default is 2.5.
	 */
	var quality: Float? by ShaderPropertyDelegate()

	init {
		size = 800F to 450F
		samples = 5F
		quality = 2.5F
	}
}

/**
 * The bloom shader used for rendering with bloom effect.
 */
val BLOOM_SHADER: BloomShader
	get() = BloomShader()

/**
 * The [BLUR_SHADER] implementation.
 */
class BlurShader internal constructor() : EffectShader("shaders/blur.fs") {

	/**
	 * The render width uniform property.
	 * Represents the width of the blur effect area.
	 */
	var renderWidth: Float? by ShaderPropertyDelegate()

	/**
	 * The render height uniform property.
	 * Represents the height of the blur effect area.
	 */
	var renderHeight: Float? by ShaderPropertyDelegate()

	init {
		renderWidth = 800F
		renderHeight = 450F
	}

}

/**
 * The blur shader used for rendering with blur effect.
 *
 * The affect will cover a 800x450 area.
 */
val BLUR_SHADER: BlurShader
	get() = BlurShader()

/**
 * The [COLOR_CORRECTION_SHADER] implementation.
 */
class ColorCorrectionShader internal constructor() : EffectShader("shaders/raylib/color_correction.fs") {
	/**
	 * The contrast uniform property on a scale of -1.0 to 1.0.
	 */
	var contrast: Float? by ShaderPropertyDelegate()

	/**
	 * The saturation uniform property on a scale of 0.0 to 2.0.
	 */
	var saturation: Float? by ShaderPropertyDelegate()

	/**
	 * The brightness uniform property on a scale of -1.0 to 1.0.
	 */
	var brightness: Float? by ShaderPropertyDelegate()

	init {
		contrast = 0.0F
		saturation = 1.0F
		brightness = 0.0F
	}
}

/**
 * The color correction shader used for rendering with color correction effect.
 */
val COLOR_CORRECTION_SHADER: ColorCorrectionShader
	get() = ColorCorrectionShader()

/**
 * The [COLOR_MIX_SHADER] implementation.
 */
class ColorMixShader internal constructor() : EffectShader("shaders/raylib/color_mix.fs") {
	/**
	 * The second texture uniform property.
	 */
	var texture2: Sampler2D? by ShaderPropertyDelegate("texture1", isSampler2D = true)

	/**
	 * The divider uniform property on a scale of 0.0 to 1.0.
	 * Higher values give more weight to the first texture.
	 */
	var divider: Float? by ShaderPropertyDelegate()

}

/**
 * The color mix shader used for rendering with color mixing effect.
 */
val COLOR_MIX_SHADER: ColorMixShader
	get() = ColorMixShader()

/**
 * The [CROSS_HATCHING_SHADER] implementation.
 */
class CrossHatchingShader internal constructor() : EffectShader("shaders/cross_hatching.fs") {

	/**
	 * The hatch y offset uniform property.
	 *
	 * Default is 5.
	 */
	var offsetY: Float? by ShaderPropertyDelegate("hatchOffsetY")

	/**
	 * The 1st luminance threshold uniform property.
	 *
	 * The luminance threshold for the first cross-hatching layer. Increasing
	 * this value will make the first layer appear in darker areas of the image.
	 *
	 * Default is 0.9.
	 */
	var lumThreshold1: Float? by ShaderPropertyDelegate("lumThreshold01")

	/**
	 * The 2nd luminance threshold uniform property.
	 *
	 * The luminance threshold for the second cross-hatching layer. Increasing
	 * this value will make the second layer appear in darker areas of the image.
	 *
	 * Default is 0.7.
	 */
	var lumThreshold2: Float? by ShaderPropertyDelegate("lumThreshold02")

	/**
	 * The 3rd luminance threshold uniform property.
	 *
	 * The luminance threshold for the third cross-hatching layer. Increasing
	 * this value will make the third layer appear in darker areas of the image.
	 *
	 * Default is 0.5.
	 */
	var lumThreshold3: Float? by ShaderPropertyDelegate("lumThreshold03")

	/**
	 * The 4th luminance threshold uniform property.
	 *
	 * The luminance threshold for the fourth cross-hatching layer. Increasing
	 * this value will make the fourth layer appear in darker areas of the image.
	 *
	 * Default is 0.3.
	 */
	var lumThreshold4: Float? by ShaderPropertyDelegate("lumThreshold04")


	init {
		offsetY = 5.0F
		lumThreshold1 = 0.9F
		lumThreshold2 = 0.7F
		lumThreshold3 = 0.5F
		lumThreshold4 = 0.3F
	}
}

/**
 * The cross-hatching shader used for rendering with cross-hatching effect.
 */
val CROSS_HATCHING_SHADER: CrossHatchingShader
	get() = CrossHatchingShader()

/**
 * The [CROSS_STITCHING_SHADER] implementation.
 */
class CrossStitchingShader internal constructor() : EffectShader("shaders/cross_stitching.fs") {

	/**
	 * The render width uniform property.
	 * Represents the width of the cross stitching effect area.
	 */
	var renderWidth: Float? by ShaderPropertyDelegate()

	/**
	 * The render height uniform property.
	 * Represents the height of the cross stitching effect area.
	 */
	var renderHeight: Float? by ShaderPropertyDelegate()

	/**
	 * The stitching size uniform property.
	 * Default is 6.
	 */
	var size: Float? by ShaderPropertyDelegate("stitchingSize")

	/**
	 * The invert uniform property.
	 */
	var inverted: Boolean? by ShaderPropertyDelegate("invert")

	init {
		renderWidth = 800F
		renderHeight = 450F
		size = 6.0F
		inverted = false
	}

}

/**
 * The cross-stitching shader used for rendering with cross-stitching effect.
 */
val CROSS_STITCHING_SHADER: CrossStitchingShader
	get() = CrossStitchingShader()

/**
 * The [CUBES_PANNING_SHADER] implementation.
 */
class CubesPanningShader internal constructor() : BuiltInShader("shaders/cubes_panning.fs") {

	/**
	 * The time uniform property.
	 *
	 * This should be set inside the window lifecycle loop to [Window.time].
	 */
	var time: Float? by ShaderPropertyDelegate("uTime")

	/**
	 * The divisions uniform property.
	 *
	 * This controls the number of cube divisions in the panning effect.
	 */
	var divisions: Float? by ShaderPropertyDelegate()

	/**
	 * The angle uniform property.
	 *
	 * This controls the rotation angle of the cubes in the panning effect.
	 */
	var angle: Float? by ShaderPropertyDelegate()

	init {
		time = 0F
		divisions = 5F
		angle = 0F
	}
}

/**
 * The shader used for rendering with cubes panning effect.
 *
 * This shader creates a dynamic panning effect using cubes,
 * where the cubes appear to move and rotate over time, creating
 * a visually engaging and immersive experience.
 */
val CUBES_PANNING_SHADER: CubesPanningShader
	get() = CubesPanningShader()

/**
 * The [DEPTH_SHADER] implementation.
 */
class DepthShader internal constructor() : BuiltInShader("shaders/raylib/depth_render.fs") {

	/**
	 * The depth texture uniform property.
	 */
	var depthTexture: Sampler2D? by ShaderPropertyDelegate("depthTexture", isSampler2D = true)

	/**
	 * The flip Y uniform property.
	 *
	 * Set this to true to flip the depth texture vertically.
	 */
	var flipY: Boolean? by ShaderPropertyDelegate("flipY")

	/**
	 * The near plane uniform property.
	 *
	 * This represents the near clipping plane distance used in depth calculations.
	 */
	var nearPlane: Float? by ShaderPropertyDelegate("nearPlane")

	/**
	 * The far plane uniform property.
	 *
	 * This represents the far clipping plane distance used in depth calculations.
	 */
	var farPlane: Float? by ShaderPropertyDelegate("farPlane")

	init {
		flipY = false
		nearPlane = 0.1F
		farPlane = 100F
	}
}

/**
 * The shader used for rendering depth information.
 *
 * This shader visualizes depth data, which can be useful for effects
 * like depth of field, shadow mapping, or other depth-based rendering techniques.
 */
val DEPTH_SHADER: DepthShader
	get() = DepthShader()

/**
 * The [FISHEYE_SHADER] implementation.
 */
class FisheyeShader internal constructor() : EffectShader("shaders/raylib/fisheye.fs")

/**
 * The shader used for rendering with fisheye effect.
 *
 * A fisheye effect simulates the distortion seen through a fisheye lens,
 * creating a wide-angle, hemispherical view that exaggerates the center
 * of the image while compressing the edges, resulting in a unique and
 * immersive visual experience.
 */
val FISHEYE_SHADER: FisheyeShader
	get() = FisheyeShader()

class FogShader internal constructor() : LightingShader(
	"shaders/raylib/lighting.vs",
	"shaders/raylib/fog.fs"
) {

	/**
	 * The fog color uniform property.
	 */
	var fogColor: Color? by ShaderPropertyDelegate()

	/**
	 * The fog density uniform property.
	 */
	var fogDensity: Float? by ShaderPropertyDelegate()

	/**
	 * The texture uniform property.
	 *
	 * This texture is used for the base color of the object.
	 */
	var texture: Sampler2D? by ShaderPropertyDelegate("texture0", isSampler2D = true)

	/**
	 * The diffuse color uniform property.
	 *
	 * This color is multiplied with the texture color.
	 */
	var diffuse: Color? by ShaderPropertyDelegate("colDiffuse")

	init {
		fogColor = Color.WHITE
		fogDensity = 0.05F
	}
}

/**
 * The [GBUFFER_SHADER] implementation.
 */
class GBufferShader internal constructor() : BuiltInShader(
	"shaders/raylib/gbuffer.vs",
	"shaders/raylib/gbuffer.fs"
) {

	/**
	 * The diffuse texture uniform property.
	 *
	 * In G-Buffer rendering, the diffuse texture stores the base color information
	 * of the scene's geometry, which is later used in lighting calculations during
	 * the deferred shading process.
	 */
	var diffuseTexture: Sampler2D? by ShaderPropertyDelegate(isSampler2D = true)

	/**
	 * The specular texture uniform property.
	 *
	 * In G-Buffer rendering, the specular texture stores the specular reflection
	 * information of the scene's geometry, which is used to calculate highlights
	 * and shiny surfaces during the lighting pass of deferred shading.
	 */
	var specularTexture: Sampler2D? by ShaderPropertyDelegate(isSampler2D = true)

	init {
		setDefaultLocations(UniformLocation.MATRIX_MVP, UniformLocation.MATRIX_MODEL)
	}
}

/**
 * The shader used for rendering with G-Buffer technique.
 *
 * G-Buffer (Geometry Buffer) is a technique used in deferred shading
 * to store various attributes of the scene's geometry, such as positions,
 * normals, and colors, into multiple render targets. This allows for
 * efficient lighting calculations in screen space, enabling complex
 * lighting effects and optimizations in 3D rendering.
 */
val GBUFFER_SHADER: GBufferShader
	get() = GBufferShader()

/**
 * The [GRAYSCALE_SHADER] implementation.
 */
class GrayscaleShader internal constructor() : EffectShader("shaders/raylib/grayscale.fs")

/**
 * The shader used for rendering with grayscale.
 */
val GRAYSCALE_SHADER: GrayscaleShader
	get() = GrayscaleShader()

/**
 * The [JULIA_SET_SHADER] implementation.
 */
class JuliaSetShader internal constructor() : BuiltInShader("shaders/julia_set.fs") {

	/**
	 * The c uniform property.
	 *
	 * C in the Julia set formula (first is real part, second is imaginary part).
	 */
	var c: Pair<Float, Float>? by ShaderPropertyDelegate()

	/**
	 * The offset uniform property.
	 *
	 * Offset to pan the view of the Julia set (first is x offset, second is y offset).
	 */
	var offset: Pair<Float, Float>? by ShaderPropertyDelegate()

	/**
	 * The zoom uniform property.
	 */
	var zoom: Float? by ShaderPropertyDelegate()

	/**
	 * The maximum iterations uniform property.
	 *
	 * Determines the detail level of the fractal. Higher values yield more detail
	 * but require more computation. Default is 255.
	 */
	var maxIterations: Int? by ShaderPropertyDelegate()

	/**
	 * The color cycles uniform property.
	 *
	 * Controls the number of color cycles in the fractal rendering. Higher values
	 * produce more vibrant and varied color patterns. Default is 2.0.
	 */
	var colorCycles: Float? by ShaderPropertyDelegate()

	init {
		zoom = 1.0F
		maxIterations = 255
		colorCycles = 2.0F
	}
}

/**
 * The shader used for rendering the Julia set fractal.
 *
 * A Julia set is a complex fractal structure defined by a mathematical formula.
 * This shader visualizes the Julia set by iterating complex numbers and
 * determining their membership in the set, resulting in intricate and
 * visually captivating patterns.
 */
val JULIA_SET_SHADER: JuliaSetShader
	get() = JuliaSetShader()

/**
 * The lighting shader implementations, [LIGHTING_SHADER] and [LIGHTING_SHADER_INSTANCED] implementations.
 */
open class LightingShader internal constructor(
	vsFileName: String,
	fsFileName: String = "shaders/raylib/lighting.fs"
) : BuiltInShader(vsFileName, fsFileName) {
	companion object {
		/**
		 * The maximum number of lights supported in [LIGHTING_SHADER].
		 */
		const val MAX_LIGHTS = 4

		/**
		 * The default light color used if none is specified.
		 */
		val DEFAULT_AMBIENT_LIGHT = Color(51, 51, 51)
	}

	/**
	 * The ambient light color uniform property.
	 */
	var ambient: Color? by ShaderPropertyDelegate()

	init {
		setDefaultLocations(UniformLocation.MATRIX_MVP, UniformLocation.MATRIX_MODEL)
		setLocation(UniformLocation.VECTOR_VIEW, "viewPos")

		ambient = DEFAULT_AMBIENT_LIGHT
	}

	/**
	 * The current number of lights added to the shader.
	 */
	var currentLightsCount = 0
		private set

	/**
	 * Adds a light to the shader.
	 * @param position The position of the light in 3D space.
	 * @param target The target point the light is pointing to.
	 * @param color The color of the light. Defaults to white.
	 * @param directional Whether the light is directional or point light.
	 * If the light is directional, the position is treated as a direction vector.
	 * If the light is a point, the light will attenuate with distance.
	 */
	fun addLight(
		position: Triple<Float, Float, Float>,
		target: Triple<Float, Float, Float> = 0F to 0F to 0F,
		color: Color = Color.WHITE,
		directional: Boolean = true,
	) {
		if (currentLightsCount >= MAX_LIGHTS) {
			error("Cannot add more than $MAX_LIGHTS lights to the shader.")
		}

		setValue("lights[$currentLightsCount].enabled", true)
		setValue("lights[$currentLightsCount].type", directional)
		setValue("lights[$currentLightsCount].position", position)
		setValue("lights[$currentLightsCount].target", target)
		setValue("lights[$currentLightsCount].color", color)

		currentLightsCount++
	}

	/**
	 * Adds a light to the shader using individual float parameters for position and target.
	 * @param x The x-coordinate of the light's position.
	 * @param y The y-coordinate of the light's position.
	 * @param z The z-coordinate of the light's position.
	 * @param targetX The x-coordinate of the light's target point.
	 * @param targetY The y-coordinate of the light's target point.
	 * @param targetZ The z-coordinate of the light's target point.
	 * @param color The color of the light. Defaults to white.
	 * @param directional Whether the light is directional or point light.
	 * If the light is directional, the position is treated as a direction vector.
	 * If the light is a point, the light will attenuate with distance.
	 */
	fun addLight(
		x: Float,
		y: Float,
		z: Float,
		targetX: Float = 0F,
		targetY: Float = 0F,
		targetZ: Float = 0F,
		color: Color = Color.WHITE,
		directional: Boolean = true
	) = addLight(
		x to y to z,
		targetX to targetY to targetZ,
		color,
		directional
	)
}

/**
 * The shader used for rendering with basic lighting.
 */
val LIGHTING_SHADER: LightingShader
	get() = LightingShader("shaders/raylib/lighting.vs")

/**
 * The shader used for rendering with lighting and instancing support.
 *
 * Instancing allows for efficient rendering of multiple instances of the same
 * geometry with different transformations, reducing the number of draw calls
 * and improving performance in scenes with many similar objects.
 */
val LIGHTING_SHADER_INSTANCED: LightingShader
	get() = LightingShader("shaders/raylib/lighting_instancing.vs")

/**
 * The [RAYMARCHING_SHADER] implementation.
 */
class RaymarchingShader internal constructor() : BuiltInShader("shaders/raylib/raymarching.fs") {

	/**
	 * The viewing eye uniform property.
	 *
	 * This should correspond to [raylib.Camera3D.position] on every window
	 * lifecycle frame.
	 */
	var viewEye: Triple<Float, Float, Float>? by ShaderPropertyDelegate()

	/**
	 * The viewing center uniform property.
	 *
	 * This should correspond to [raylib.Camera3D.target] on every window
	 * lifecycle frame.
	 */
	var viewTarget: Triple<Float, Float, Float>? by ShaderPropertyDelegate("viewCenter")

	/**
	 * The run time uniform property.
	 *
	 * This should correspond to [Window.frameTime] on every window
	 * lifecycle frame.
	 */
	var frameTime: Float? by ShaderPropertyDelegate("runTime")

	/**
	 * The resolution uniform property.
	 *
	 * This represents the bounds resolution for the shader. For best quality,
	 * set the bounds to the values [Window.screenWidth] and [Window.screenHeight]
	 * respectively.
	 */
	var resolution: Pair<Float, Float>? by ShaderPropertyDelegate()

}

/**
 * The shader used for rendering with raymarching technique.
 *
 * Raymarching is a rendering technique that simulates the way light interacts
 * with surfaces by marching rays through a scene and calculating color and
 * lighting effects based on distance fields. It often produces highly detailed
 * and complex visual effects, making it popular for procedural generation and
 * real-time graphics.
 */
val RAYMARCHING_SHADER: RaymarchingShader
	get() = RaymarchingShader()

/**
 * The [OUTLINE_SHADER] implmentation.
 */
class OutlineShader internal constructor() : EffectShader("shaders/raylib/outline.fs") {

	/**
	 * The texture size uniform property.
	 */
	var size: Pair<Float, Float>? by ShaderPropertyDelegate("textureSize")

	/**
	 * The outline size uniform property.
	 */
	var outlineSize: Float? by ShaderPropertyDelegate("outlineSize")

	/**
	 * The outline color uniform property.
	 */
	var outlineColor: Color? by ShaderPropertyDelegate("outlineColor")

}

/**
 * The shader used for rendering an outline of a texture.
 */
val OUTLINE_SHADER: OutlineShader
	get() = OutlineShader()

/**
 * The [OVERDRAW_SHADER] implementation.
 */
class OverdrawShader internal constructor() : EffectShader("shaders/raylib/overdraw.fs")

/**
 * The shader used for rendering with overdraw visualization.
 *
 * Overdraw visualization helps in identifying areas where multiple
 * fragments are drawn over each other, which can impact performance.
 */
val OVERDRAW_SHADER: OverdrawShader
	get() = OverdrawShader()

/**
 * The [SWIRL_SHADER] implementation.
 */
class SwirlShader internal constructor() : EffectShader("shaders/swirl.fs") {

	/**
	 * The render width uniform property.
	 * Represents the width of the swirl effect area.
	 */
	var renderWidth: Float? by ShaderPropertyDelegate()

	/**
	 * The render height uniform property.
	 * Represents the height of the swirl effect area.
	 */
	var renderHeight: Float? by ShaderPropertyDelegate()

	/**
	 * The radius uniform property.
	 * Represents the radius of the swirl effect.
	 */
	var radius: Float? by ShaderPropertyDelegate()

	/**
	 * The angle uniform property.
	 * Represents the angle of the swirl effect in radians.
	 */
	var angle: Float? by ShaderPropertyDelegate()

	/**
	 * The center uniform property.
	 * Represents the center point of the swirl effect as a pair of
	 * normalized coordinates (0.0 to 1.0).
	 */
	var center: Pair<Float, Float>? by ShaderPropertyDelegate()

	init {
		renderWidth = 800F
		renderHeight = 450F
		radius = 250F
		angle = 0.8F
		center = 200F to 200F
	}

}

/**
 * The shader used for rendering with a swirl effect.
 */
val SWIRL_SHADER: SwirlShader
	get() = SwirlShader()

/**
 * The [WAVE_SHADER] implementation.
 */
class WaveShader internal constructor() : EffectShader("shaders/raylib/wave.fs") {

	/**
	 * The seconds uniform property.
	 *
	 * This should correspond to [Window.time] during the window lifecycle
	 * loop.
	 */
	var seconds: Float? by ShaderPropertyDelegate()

	var size: Pair<Float, Float>? by ShaderPropertyDelegate()
	var freqX: Float? by ShaderPropertyDelegate()
	var freqY: Float? by ShaderPropertyDelegate()
	var ampX: Float? by ShaderPropertyDelegate()
	var ampY: Float? by ShaderPropertyDelegate()
	var speedX: Float? by ShaderPropertyDelegate()
	var speedY: Float? by ShaderPropertyDelegate()

	init {
		size = 1F to 1F
		freqX = 1F
		freqY = 1F
		ampX = 1F
		ampY = 1F
		speedX = 1F
		speedY = 1F
	}

}

/**
 * The shader used for rendering with a wave effect.
 */
val WAVE_SHADER: WaveShader
	get() = WaveShader()
