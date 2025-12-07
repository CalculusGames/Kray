@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.*
import kray.*
import raylib.Mesh.Companion.cubicMap
import raylib.Mesh.Companion.heightMap
import raylib.internal.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * A 4x4 matrix.
 */
@Suppress("DuplicatedCode")
data class Matrix4(
	val m0: Float, val m1: Float, val m2: Float, val m3: Float,
	val m4: Float, val m5: Float, val m6: Float, val m7: Float,
	val m8: Float, val m9: Float, val m10: Float, val m11: Float,
	val m12: Float, val m13: Float, val m14: Float, val m15: Float
) {

	/**
	 * Creates a [Matrix4] from a raw raylib [Matrix].
	 * @param raw The raw matrix.
	 */
	constructor(raw: Matrix) : this(
		raw.m0, raw.m1, raw.m2, raw.m3,
		raw.m4, raw.m5, raw.m6, raw.m7,
		raw.m8, raw.m9, raw.m10, raw.m11,
		raw.m12, raw.m13, raw.m14, raw.m15
	)

	internal fun raw(): CValue<Matrix> = cValue<Matrix> {
		m0 = this@Matrix4.m0
		m1 = this@Matrix4.m1
		m2 = this@Matrix4.m2
		m3 = this@Matrix4.m3
		m4 = this@Matrix4.m4
		m5 = this@Matrix4.m5
		m6 = this@Matrix4.m6
		m7 = this@Matrix4.m7
		m8 = this@Matrix4.m8
		m9 = this@Matrix4.m9
		m10 = this@Matrix4.m10
		m11 = this@Matrix4.m11
		m12 = this@Matrix4.m12
		m13 = this@Matrix4.m13
		m14 = this@Matrix4.m14
		m15 = this@Matrix4.m15
	}

	/**
	 * Converts the matrix to a float array.
	 * @return The float array representation of the matrix.
	 */
	fun toFloatArray(): FloatArray = floatArrayOf(
		m0, m4, m8, m12,
		m1, m5, m9, m13,
		m2, m6, m10, m14,
		m3, m7, m11, m15
	)
	/**
	 * Multiplies this matrix by another matrix.
	 * @param b The matrix to multiply by.
	 * @return The resulting matrix.
	 */
	fun multiply(b: Matrix4): Matrix4 {
		val a = this

		return Matrix4(
			a.m0 * b.m0 + a.m4 * b.m1 + a.m8 * b.m2 + a.m12 * b.m3,
			a.m1 * b.m0 + a.m5 * b.m1 + a.m9 * b.m2 + a.m13 * b.m3,
			a.m2 * b.m0 + a.m6 * b.m1 + a.m10 * b.m2 + a.m14 * b.m3,
			a.m3 * b.m0 + a.m7 * b.m1 + a.m11 * b.m2 + a.m15 * b.m3,

			a.m0 * b.m4 + a.m4 * b.m5 + a.m8 * b.m6 + a.m12 * b.m7,
			a.m1 * b.m4 + a.m5 * b.m5 + a.m9 * b.m6 + a.m13 * b.m7,
			a.m2 * b.m4 + a.m6 * b.m5 + a.m10 * b.m6 + a.m14 * b.m7,
			a.m3 * b.m4 + a.m7 * b.m5 + a.m11 * b.m6 + a.m15 * b.m7,

			a.m0 * b.m8 + a.m4 * b.m9 + a.m8 * b.m10 + a.m12 * b.m11,
			a.m1 * b.m8 + a.m5 * b.m9 + a.m9 * b.m10 + a.m13 * b.m11,
			a.m2 * b.m8 + a.m6 * b.m9 + a.m10 * b.m10 + a.m14 * b.m11,
			a.m3 * b.m8 + a.m7 * b.m9 + a.m11 * b.m10 + a.m15 * b.m11,

			a.m0 * b.m12 + a.m4 * b.m13 + a.m8 * b.m14 + a.m12 * b.m15,
			a.m1 * b.m12 + a.m5 * b.m13 + a.m9 * b.m14 + a.m13 * b.m15,
			a.m2 * b.m12 + a.m6 * b.m13 + a.m10 * b.m14 + a.m14 * b.m15,
			a.m3 * b.m12 + a.m7 * b.m13 + a.m11 * b.m14 + a.m15 * b.m15
		)
	}

	companion object {

		/**
		 * Creates a [Matrix4] from a float array.
		 * @param array The float array.
		 * @return The matrix.
		 */
		fun fromFloatArray(array: FloatArray): Matrix4 {
			require(array.size == 16) { "Array must have exactly 16 elements." }
			return Matrix4(
				array[0], array[4], array[8], array[12],
				array[1], array[5], array[9], array[13],
				array[2], array[6], array[10], array[14],
				array[3], array[7], array[11], array[15]
			)
		}

		/**
		 * Generates a translation matrix given a position.
		 * @param position The position as a [Triple] of floats (x, y, z).
		 * @return The translation matrix as a [Matrix4].
		 */
		fun getTranslationMatrix(position: Triple<Float, Float, Float>): Matrix4 {
			return Matrix4(
				1F, 0F, 0F, 0F,
				0F, 1F, 0F, 0F,
				0F, 0F, 1F, 0F,
				position.first, position.second, position.third, 1F
			)
		}

		/**
		 * Generates a rotation matrix given an axis and an angle.
		 * @param axis The axis of rotation as a [Triple] of floats (x, y, z).
		 * @param angle The angle of rotation in radians.
		 * @return The rotation matrix as a [Matrix4].
		 */
		fun getRotationMatrix(axis: Triple<Float, Float, Float>, angle: Float): Matrix4 {
			val x = axis.first
			val y = axis.second
			val z = axis.third
			val c = cos(angle)
			val s = sin(angle)
			val t = 1 - c

			return Matrix4(
				t * x * x + c, t * x * y + s * z, t * x * z - s * y, 0F,
				t * x * y - s * z, t * y * y + c, t * y * z + s * x, 0F,
				t * x * z + s * y, t * y * z - s * x, t * z * z + c, 0F,
				0F, 0F, 0F, 1F
			)
		}

		/**
		 * Generates a scaling matrix given a scale vector.
		 * @param scale The scale vector as a [Triple] of floats (sx, sy, sz).
		 * @return The scaling matrix as a [Matrix4].
		 */
		fun getScaleMatrix(scale: Triple<Float, Float, Float>): Matrix4 {
			return Matrix4(
				scale.first, 0F, 0F, 0F,
				0F, scale.second, 0F, 0F,
				0F, 0F, scale.third, 0F,
				0F, 0F, 0F, 1F
			)
		}

		/**
		 * Generates a transformation matrix given position, rotation, and scale.
		 * @param position The position as a [Triple] of floats (x, y, z).
		 * @param rotationAxis The axis of rotation as a [Triple] of floats (x, y, z).
		 * @param rotationAngle The angle of rotation in radians.
		 * @param scale The scale vector as a [Triple] of floats (sx, sy, sz).
		 * @return The transformation matrix as a [Matrix4].
		 */
		fun getTransformationMatrix(
			position: Triple<Float, Float, Float>,
			rotationAxis: Triple<Float, Float, Float>,
			rotationAngle: Float,
			scale: Triple<Float, Float, Float>
		): Matrix4 {
			val translationMatrix = getTranslationMatrix(position)
			val rotationMatrix = getRotationMatrix(rotationAxis, rotationAngle)
			val scaleMatrix = getScaleMatrix(scale)

			return translationMatrix
				.multiply(rotationMatrix)
				.multiply(scaleMatrix)
		}

		/**
		 * The identity matrix.
		 */
		val IDENTITY: Matrix4
			get() = Matrix4(
				1F, 0F, 0F, 0F,
				0F, 1F, 0F, 0F,
				0F, 0F, 1F, 0F,
				0F, 0F, 0F, 1F
			)

	}

}

/**
 * A 3D bounding box.
 */
data class BoundingBox(
	val min: Triple<Float, Float, Float>,
	val max: Triple<Float, Float, Float>
) {
	/**
	 * Creates a [BoundingBox] from raw min and max coordinates.
	 */
	constructor(
		minX: Float, minY: Float, minZ: Float,
		maxX: Float, maxY: Float, maxZ: Float
	) : this(
		Triple(minX, minY, minZ),
		Triple(maxX, maxY, maxZ)
	)

	/**
	 * Creates a [BoundingBox] from a raw raylib [BoundingBox].
	 * @param raw The raw bounding box.
	 */
	constructor(raw: raylib.internal.BoundingBox) : this(
		Triple(raw.min.x, raw.min.y, raw.min.z),
		Triple(raw.max.x, raw.max.y, raw.max.z)
	)

	internal fun raw(): CValue<raylib.internal.BoundingBox> = cValue<raylib.internal.BoundingBox> {
		min.x = this@BoundingBox.min.first
		min.y = this@BoundingBox.min.second
		min.z = this@BoundingBox.min.third
		max.x = this@BoundingBox.max.first
		max.y = this@BoundingBox.max.second
		max.z = this@BoundingBox.max.third
	}
}

/**
 * Draws a 3D bounding box on the canvas.
 * @param box The bounding box to draw.
 * @param color The color of the bounding box lines. Defaults to red.
 */
fun Canvas.drawBoundingBox(box: BoundingBox, color: Color = Color.RED) {
	ensureDrawing()
	DrawBoundingBox(box.raw(), color.raw())
}

/**
 * Represents a raylib shader.
 *
 * Shaders are used to program the GPU's graphics pipeline. They allow for custom
 * rendering effects and can manipulate how objects are drawn on the screen.
 */
class Shader(internal val raw: raylib.internal.Shader) {

	/**
	 * The ID of the shader program.
	 */
	var id: UInt
		get() = raw.id
		set(value) {
			raw.id = value
		}

	/**
	 * The locations of the shader attributes and uniforms.
	 */
	var locs: List<Int>
		get() {
			val list = mutableListOf<Int>()
			for (i in 0 until RL_MAX_SHADER_LOCATIONS) {
				list.add(raw.locs?.get(i) ?: -1)
			}
			return list
		}
		set(value) {
			for (i in 0 until RL_MAX_SHADER_LOCATIONS) {
				raw.locs?.set(i, value.getOrElse(i) { -1 })
			}
		}

	internal fun asCValue(): CValue<raylib.internal.Shader> = cValue<raylib.internal.Shader> {
		id = raw.id
		locs = raw.locs
	}

	/**
	 * Whether the shader is valid.
	 */
	val isValid: Boolean
		get() = IsShaderValid(asCValue())

	/**
	 * The data types supported for shader uniform variables.
	 */
	@Suppress("UNCHECKED_CAST")
	enum class DataType(internal val value: UInt, internal val convert: NativePlacement.(Any) -> CValuesRef<*>) {
		/**
		 * The [Float] type.
		 */
		FLOAT(SHADER_UNIFORM_FLOAT, {
			val v = alloc<FloatVar>()
			v.value = it as Float
			v.ptr
		}),

		/**
		 * The [Pair] type with [Float].
		 */
		VEC2F(SHADER_UNIFORM_VEC2, {
			val pair = it as Pair<Float, Float>
			allocArrayOf(pair.first, pair.second)
		}),

		/**
		 * The [Triple] type with [Float].
		 */
		VEC3F(SHADER_UNIFORM_VEC3, {
			val triple = it as Triple<Float, Float, Float>
			allocArrayOf(triple.first, triple.second, triple.third)
		}),

		/**
		 * The [Quadruple] type with [Float].
		 */
		VEC4F(SHADER_UNIFORM_VEC4, {
			val quad = it as Quadruple<Float, Float, Float, Float>
			allocArrayOf(quad.first, quad.second, quad.third, quad.fourth)
		}),

		/**
		 * The [Int] type.
		 */
		INTEGER(SHADER_UNIFORM_INT, {
			val v = alloc<IntVar>()
			v.value = it as Int
			v.ptr
		}),

		/**
		 * The [Pair] type with [Int].
		 */
		VEC2I(SHADER_UNIFORM_IVEC2, {
			val pair = it as Pair<Int, Int>
			allocArrayOf(pair.first, pair.second)
		}),

		/**
		 * The [Triple] type with [Int].
		 */
		VEC3I(SHADER_UNIFORM_IVEC3, {
			val triple = it as Triple<Int, Int, Int>
			allocArrayOf(triple.first, triple.second, triple.third)
		}),

		/**
		 * The [Quadruple] type with [Int].
		 */
		VEC4I(SHADER_UNIFORM_IVEC4, {
			val quad = it as Quadruple<Int, Int, Int, Int>
			allocArrayOf(quad.first, quad.second, quad.third, quad.fourth)
		}),

		/**
		 * The [UInt] type.
		 */
		UNSIGNED_INTEGER(SHADER_UNIFORM_UINT, {
			val v = alloc<UIntVar>()
			v.value = it as UInt
			v.ptr
		}),

		/**
		 * The [Pair] type with [UInt].
		 */
		VEC2U(SHADER_UNIFORM_UIVEC2, {
			val pair = it as Pair<UInt, UInt>
			allocArrayOf(pair.first, pair.second)
		}),

		/**
		 * The [Triple] type with [UInt].
		 */
		VEC3U(SHADER_UNIFORM_UIVEC3, {
			val triple = it as Triple<UInt, UInt, UInt>
			allocArrayOf(triple.first, triple.second, triple.third)
		}),

		/**
		 * The [Quadruple] type with [UInt].
		 */
		VEC4U(SHADER_UNIFORM_UIVEC4, {
			val quad = it as Quadruple<UInt, UInt, UInt, UInt>
			allocArrayOf(quad.first, quad.second, quad.third, quad.fourth)
		}),

		/**
		 * The `sampler2d` type, used for 2D textures.
		 *
		 * This is represented as a [UInt] in raylib and corresponds to
		 * the texture unit index.
		 */
		SAMPLER2D(SHADER_UNIFORM_SAMPLER2D, {
			val v = alloc<UIntVar>()
			v.value = it as UInt
			v.ptr
		})
	}

	// shader uniform variable utilities

	/**
	 * Gets the location of a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @return The location of the uniform variable, or -1 if not found.
	 */
	fun getLocation(uniformName: String): Int {
		return GetShaderLocation(asCValue(), uniformName)
	}

	/**
	 * Gets the location of a shader attribute variable by name.
	 * @param attributeName The name of the attribute variable.
	 * You can find the attribute names in the shader code.
	 * @return The location of the attribute variable, or -1 if not found.
	 */
	fun getAttributeLocation(attributeName: String): Int {
		return GetShaderLocationAttrib(asCValue(), attributeName)
	}

	/**
	 * Sets a value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The value to set. Supported types are from the [DataType] enum.
	 * @param type The type of the uniform variable.
	 */
	fun setValue(location: Int, value: Any, type: DataType) = memScoped {
		val rawValue = type.convert(this, value)
		SetShaderValue(asCValue(), location, rawValue, type.value.toInt())
	}

	/**
	 * Sets a matrix value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The matrix value to set.
	 */
	fun setValue(location: Int, value: Matrix4) {
		SetShaderValueMatrix(asCValue(), location, value.raw())
	}

	/**
	 * Sets a texture value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The texture value to set.
	 */
	fun setValue(location: Int, value: Texture2D) {
		SetShaderValueTexture(asCValue(), location, value.raw())
	}

	/**
	 * Sets a value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The value to set. Supported types are from the [DataType] enum.
	 * @param type The type of the uniform variable.
	 */
	fun setValue(uniformName: String, value: Any, type: DataType) {
		val location = getLocation(uniformName)
		setValue(location, value, type)
	}

	/**
	 * Sets a matrix value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The matrix value to set.
	 */
	fun setValue(uniformName: String, value: Matrix4) {
		val location = getLocation(uniformName)
		setValue(location, value)
	}

	/**
	 * Sets a texture value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The texture value to set.
	 */
	fun setValue(uniformName: String, value: Texture2D) {
		val location = getLocation(uniformName)
		setValue(location, value)
	}

	/// common types

	/**
	 * Sets a float value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The float value to set.
	 */
	fun setValue(location: Int, value: Float) = setValue(location, value, DataType.FLOAT)
	/**
	 * Sets a float value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The float value to set.
	 */
	fun setValue(uniformName: String, value: Float) = setValue(uniformName, value, DataType.FLOAT)

	/**
	 * Sets an integer value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The integer value to set.
	 */
	fun setValue(location: Int, value: Int) = setValue(location, value, DataType.INTEGER)
	/**
	 * Sets an integer value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The integer value to set.
	 */
	fun setValue(uniformName: String, value: Int) = setValue(uniformName, value, DataType.INTEGER)

	/**
	 * Sets an unsigned integer value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The unsigned integer value to set.
	 */
	fun setValue(location: Int, value: UInt) = setValue(location, value, DataType.UNSIGNED_INTEGER)
	/**
	 * Sets an unsigned integer value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The unsigned integer value to set.
	 */
	fun setValue(uniformName: String, value: UInt) = setValue(uniformName, value, DataType.UNSIGNED_INTEGER)

	/// vector types

	/**
	 * Sets a 2D Vector (Pair<Float, Float>) value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The vec2 value to set.
	 */
	fun setValue(location: Int, value: Pair<Float, Float>) = setValue(location, value, DataType.VEC2F)
	/**
	 * Sets a 2D Vector (Pair<Float, Float>) value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The vec2 value to set.
	 */
	fun setValue(uniformName: String, value: Pair<Float, Float>) = setValue(uniformName, value, DataType.VEC2F)

	/**
	 * Sets a 3D Vector (Triple<Float, Float, Float>) value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The vec3 value to set.
	 */
	fun setValue(location: Int, value: Triple<Float, Float, Float>) = setValue(location, value, DataType.VEC3F)
	/**
	 * Sets a 3D Vector (Triple<Float, Float, Float>) value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The vec3 value to set.
	 */
	fun setValue(uniformName: String, value: Triple<Float, Float, Float>) = setValue(uniformName, value, DataType.VEC3F)

	/**
	 * Sets a 4D Vector (Quadruple<Float, Float, Float, Float>) value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The vec4 value to set.
	 */
	fun setValue(location: Int, value: Quadruple<Float, Float, Float, Float>) = setValue(location, value, DataType.VEC4F)
	/**
	 * Sets a 4D Vector (Quadruple<Float, Float, Float, Float>) value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The vec4 value to set.
	 */
	fun setValue(uniformName: String, value: Quadruple<Float, Float, Float, Float>) = setValue(uniformName, value, DataType.VEC4F)

	// other

	companion object {
		/**
		 * The maximum number of shader locations.
		 * This will always be the size of [locs].
		 */
		val MAX_SHADER_LOCATIONS: Int
			get() = RL_MAX_SHADER_LOCATIONS

		/**
		 * Loads a shader from the given vertex and fragment shader file names.
		 * @param vsFileName The vertex shader file name.
		 * @param fsFileName The fragment shader file name.
		 * @return The loaded [Shader].
		 */
		fun load(vsFileName: String, fsFileName: String): Shader {
			val rawShader = LoadShader(vsFileName, fsFileName)
			return rawShader.useContents { Shader(this) }
		}

		/**
		 * Loads a shader from the given vertex and fragment shader code in memory.
		 * @param vsCode The vertex shader code.
		 * @param fsCode The fragment shader code.
		 * @return The loaded [Shader].
		 */
		fun loadInMemory(vsCode: String, fsCode: String): Shader {
			val rawShader = LoadShaderFromMemory(vsCode, fsCode)
			return rawShader.useContents { Shader(this) }
		}
	}

	override fun hashCode(): Int {
		var result = raw.id.hashCode()
		result = 31 * result + locs.hashCode()
		return result
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Shader) return false

		return raw.id == other.raw.id && locs == other.locs
	}
}

/**
 * Starts using the given shader for 3D drawing on the canvas.
 * @param shader The shader to start using.
 */
fun Canvas.startShader(shader: Shader) {
	BeginShaderMode(shader.asCValue())
}

/**
 * Stops using the current shader for 3D drawing on the canvas.
 */
fun Canvas.stopShader() {
	EndShaderMode()
}

/**
 * Executes a block of code while using the given shader for 3D drawing on the canvas.
 * @param shader The shader to use.
 * @param block The block of code to execute while the shader is active.
 */
fun Canvas.shader(shader: Shader, block: Canvas.() -> Unit) {
	startShader(shader)
	block()
	stopShader()
}

/**
 * Represents a raylib material map.
 *
 * Material maps define how textures and colors are applied to 3D models.
 * @property texture An optional texture associated with the material map.
 * @property color The color associated with the material map, setting tinting effects.
 * @property value An optional value associated with the material map (e.g., intensity).
 */
data class MaterialMap(
	val texture: Texture2D? = null,
	val color: Color = Color.WHITE,
	val value: Float? = null
) {
	/**
	 * Creates a [MaterialMap] from a raw [raylib.internal.MaterialMap].
	 * @param raw The raw material map.
	 */
	constructor(raw: raylib.internal.MaterialMap) : this(
		Texture2D(raw.texture),
		Color(raw.color),
		raw.value
	)

	internal fun raw(): CValue<raylib.internal.MaterialMap> = cValue<raylib.internal.MaterialMap> {
		if (this@MaterialMap.texture != null) {
			val tex = this@MaterialMap.texture.raw()
			tex.useContents {
				texture.id = id
				texture.width = width
				texture.height = height
				texture.mipmaps = mipmaps
				texture.format = format
			}
		}

		val col = this@MaterialMap.color.raw()
		col.useContents {
			color.r = r
			color.g = g
			color.b = b
			color.a = a
		}

		if (this@MaterialMap.value != null)
			value = this@MaterialMap.value
	}

	/**
	 * The types of material maps.
	 */
	enum class Type(internal val value: UInt) {
		/**
		 * Albedo (diffuse) texture map.
		 *
		 * Used to define the base color of the material. For example, the color of a wall,
		 * floor, or any other surface.
		 */
		ALBEDO(MATERIAL_MAP_ALBEDO),
		/**
		 * Metalness texture map.
		 *
		 * Used to define the metallic properties of the material. For example, whether
		 * the surface is metallic or non-metallic.
		 */
		METALNESS(MATERIAL_MAP_METALNESS),
		/**
		 * Normal texture map.
		 *
		 * Used to define the surface normals for lighting calculations. For example,
		 * simulating bumps, dents, and other surface details.
		 */
		NORMAL(MATERIAL_MAP_NORMAL),
		/**
		 * Roughness texture map.
		 *
		 * Used to define the roughness properties of the material. For example, how
		 * shiny or matte the surface appears.
		 */
		ROUGHNESS(MATERIAL_MAP_ROUGHNESS),
		/**
		 * Ambient Occlusion (AO) texture map.
		 *
		 * Used to define the ambient occlusion properties of the material. For example,
		 * shadows in crevices and corners where light is occluded.
		 */
		AO(MATERIAL_MAP_OCCLUSION),
		/**
		 * Emission texture map.
		 *
		 * Used to define the emissive properties of the material. For example,
		 * glowing surfaces, light sources, etc.
		 */
		EMISSION(MATERIAL_MAP_EMISSION),
		/**
		 * Height (displacement) texture map.
		 *
		 * Used to define the height or displacement properties of the material.
		 * This map can be used to create parallax effects or simulate surface
		 * details.
		 */
		HEIGHT(MATERIAL_MAP_HEIGHT),
		/**
		 * Cubemap texture map.
		 *
		 * Used for environment mapping, reflections, and skyboxes. For example,
		 * simulating reflective surfaces like water or shiny metals.
		 */
		CUBEMAP(MATERIAL_MAP_CUBEMAP),
		/**
		 * Irradiance texture map.
		 *
		 * Used for image-based lighting (IBL) to simulate diffuse lighting from
		 * the environment.
		 */
		IRRADIANCE(MATERIAL_MAP_IRRADIANCE),
		/**
		 * Prefilter texture map.
		 *
		 * Used for image-based lighting (IBL) to simulate specular reflections
		 * from the environment.
		 */
		PREFILTER(MATERIAL_MAP_PREFILTER),
		/**
		 * BRDF lookup texture map.
		 *
		 * Used for physically based rendering (PBR) to simulate how light
		 * interacts with surfaces. For example, simulating realistic reflections
		 * and highlights.
		 */
		BRDF(MATERIAL_MAP_BRDF)
	}
}

/**
 * Represents a raylib material.
 *
 * Materials define the appearance of 3D models by combining shaders and textures.
 */
class Material(internal val raw: raylib.internal.Material) {

	/**
	 * The shader associated with the material.
	 */
	var shader: Shader
		get() = Shader(raw.shader)
		set(value) {
			raw.shader.id = value.id
			for (i in 0 until RL_MAX_SHADER_LOCATIONS) {
				raw.shader.locs?.set(i, value.locs.getOrElse(i) { -1 })
			}
		}

	/**
	 * The maps of the material.
	 */
	var maps: List<MaterialMap>
		get() {
			val list = mutableListOf<MaterialMap>()
			raw.apply {
				for (i in 0 until MAX_MATERIAL_MAPS) {
					val raw = maps?.get(i)
					if (raw != null) list.add(MaterialMap(raw))
				}
			}
			return list
		}
		set(value) {
			raw.apply {
				for (i in 0 until MAX_MATERIAL_MAPS) {
					if (i < value.size) {
						val map = value[i].raw()
						val target = maps?.get(i)

						if (target != null)
							map.useContents {
								target.texture.id = this.texture.id
								target.texture.width = this.texture.width
								target.texture.height = this.texture.height
								target.texture.mipmaps = this.texture.mipmaps
								target.texture.format = this.texture.format
								target.color.r = this.color.r
								target.color.g = this.color.g
								target.color.b = this.color.b
								target.color.a = this.color.a
								target.value = this.value
							}
					} else {
						// set to default
						maps?.get(i)?.texture?.id = 0u
						maps?.get(i)?.color?.r = 255.toUByte()
						maps?.get(i)?.color?.g = 255.toUByte()
						maps?.get(i)?.color?.b = 255.toUByte()
						maps?.get(i)?.color?.a = 255.toUByte()
						maps?.get(i)?.value = 0F
					}
				}
			}
		}

	/**
	 * Sets the color of a material map directly.
	 * This matches the C pattern: `material.maps[MATERIAL_MAP_DIFFUSE].color = RED`
	 *
	 * @param mapType The type of material map (e.g., ALBEDO/DIFFUSE)
	 * @param color The color to set
	 */
	fun setMapColor(mapType: MaterialMap.Type, color: Color) {
		val index = mapType.value.toInt()
		val map = raw.maps?.get(index)
		if (map != null) {
			map.color.r = color.r
			map.color.g = color.g
			map.color.b = color.b
			map.color.a = color.a
		}
	}

	internal fun asCValue(): CValue<raylib.internal.Material> = cValue<raylib.internal.Material> {
		shader.id = raw.shader.id
		shader.locs = raw.shader.locs
		maps = raw.maps
		for (i in 0 until 4) {
			params[i] = raw.params[i]
		}
	}

	/**
	 * Draw-safe view used for drawMesh and drawModel paths.
	 * Currently identical to asCValue, but kept separate so we can
	 * evolve export vs draw semantics independently.
	 */
	internal fun asDrawCValue(): CValue<raylib.internal.Material> = asCValue()

	/**
	 * Whether the material is valid.
	 */
	val isValid: Boolean
		get() = IsMaterialValid(asCValue())

	/**
	 * Sets a texture map for the material.
	 * @param mapType The type of the material map.
	 * @param texture The texture to set for the material map.
	 */
	fun setTextureMap(mapType: MaterialMap.Type, texture: Texture2D) {
		SetMaterialTexture(raw.ptr, mapType.value.toInt(), texture.raw())
	}

	companion object {
		/**
		 * The maximum number of material maps.
		 * This is the maximum size of the [maps] list.
		 */
		val MAX_MATERIAL_MAPS: Int
			get() = raylib.internal.MAX_MATERIAL_MAPS

		/**
		 * Loads the default material.
		 * @return The default material.
		 */
		fun default() = LoadMaterialDefault().useContents { Material(this) }

		/**
		 * Loads materials from a file.
		 * @param fileName The file name to load the materials from.
		 * @return A list of loaded materials.
		 */
		fun load(fileName: String): List<Material> = memScoped {
			val count = alloc<IntVar>()
			val materials = LoadMaterials(fileName.inAppDir(), count.ptr)

			List(count.value) { i ->
				Material(materials?.get(i) ?: default().raw)
			}
		}

		/**
		 * Loads materials from a file.
		 * @param file The file to load the materials from.
		 * @return A list of loaded materials.
		 */
		fun load(file: File) = load(file.absolutePath)
	}
}

/**
 * Represents a raylib mesh.
 */
class Mesh(internal val raw: raylib.internal.Mesh) {

	/**
	 * The number of vertices on the mesh.
	 */
	val vertexCount: Int
		get() = raw.vertexCount

	/**
	 * The number of triangle objects on the mesh.
	 */
	val triangleCount: Int
		get() = raw.triangleCount

	/**
	 * The vertices of the mesh.
	 */
	@Suppress("DuplicatedCode")
	var vertices: List<Vertex>
		get() {
			val list = mutableListOf<Vertex>()

			raw.apply {
				var vi = 0
				var ti = 0
				var ti2 = 0
				var ni = 0
				var tai = 0
				var ci = 0

				for (i in 0 until vertexCount) {
					list.add(
						Vertex(
							vertices?.get(vi++) ?: 0F, // x
							vertices?.get(vi++) ?: 0F, // y
							vertices?.get(vi++) ?: 0F, // z
							texcoords?.get(ti++) ?: 0F, // tx
							texcoords?.get(ti++) ?: 0F, // ty
							texcoords2?.get(ti2++) ?: 0F, // tx2
							texcoords2?.get(ti2++) ?: 0F, // ty2
							normals?.get(ni++) ?: 0F, // nx
							normals?.get(ni++) ?: 0F, // ny
							normals?.get(ni++) ?: 0F, // nz
							tangents?.get(tai++) ?: 0F, // tax
							tangents?.get(tai++) ?: 0F, // tay
							tangents?.get(tai++) ?: 0F, // taz
							tangents?.get(tai++) ?: 0F, // taw
							Color(
								colors?.get(ci++) ?: 0.toUByte(), // r
								colors?.get(ci++) ?: 0.toUByte(), // g
								colors?.get(ci++) ?: 0.toUByte(), // b
								colors?.get(ci++) ?: 0.toUByte(), // a
							)
						)
					)
				}
			}

			return list
		}
		set(value) {
			raw.apply {
				// free old arrays if they exist
				vertices?.let { nativeHeap.free(it.rawValue) }
				texcoords?.let { nativeHeap.free(it.rawValue) }
				texcoords2?.let { nativeHeap.free(it.rawValue) }
				normals?.let { nativeHeap.free(it.rawValue) }
				tangents?.let { nativeHeap.free(it.rawValue) }
				colors?.let { nativeHeap.free(it.rawValue) }

				// vertices
				val vArray = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 3) { i ->
					val vertex = value[i / 3]
					this.value = when (i % 3) {
						0 -> vertex.x
						1 -> vertex.y
						2 -> vertex.z
						else -> 0F
					}
				}
				vertices = vArray

				// texcoords
				val tArray = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 2) { i ->
					val vertex = value[i / 2]
					this.value = when (i % 2) {
						0 -> vertex.tx
						1 -> vertex.ty
						else -> 0F
					}
				}
				texcoords = tArray

				// texcoords2
				val t2Array = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 2) { i ->
					val vertex = value[i / 2]
					this.value = when (i % 2) {
						0 -> vertex.tx2
						1 -> vertex.ty2
						else -> 0F
					}
				}
				texcoords2 = t2Array

				// normals
				val nArray = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 3) { i ->
					val vertex = value[i / 3]
					this.value = when (i % 3) {
						0 -> vertex.nx
						1 -> vertex.ny
						2 -> vertex.nz
						else -> 0F
					}
				}
				normals = nArray

				// tangents
				val taArray = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 4) { i ->
					val vertex = value[i / 4]
					this.value = when (i % 4) {
						0 -> vertex.tax
						1 -> vertex.tay
						2 -> vertex.taz
						3 -> vertex.taw
						else -> 0F
					}
				}
				tangents = taArray

				// colors
				val cArray = nativeHeap.allocArray<UByteVarOf<UByte>>(value.size * 4) { i ->
					val vertex = value[i / 4]
					this.value = when (i % 4) {
						0 -> vertex.color.r
						1 -> vertex.color.g
						2 -> vertex.color.b
						3 -> vertex.color.a
						else -> 0.toUByte()
					}
				}
				colors = cArray
			}
		}

	/**
	 * Adds a vertex to the mesh.
	 * @param vertex The vertex to add.
	 */
	fun addVertex(vertex: Vertex) {
		val currentVertices = vertices.toMutableList()
		currentVertices.add(vertex)
		vertices = currentVertices
		raw.vertexCount = currentVertices.size
	}

	/**
	 * Removes a vertex from the mesh at the specified index.
	 * @param index The index of the vertex to remove.
	 */
	fun removeVertex(index: Int) {
		val currentVertices = vertices.toMutableList()
		if (index in currentVertices.indices) {
			currentVertices.removeAt(index)
			vertices = currentVertices
			raw.vertexCount = currentVertices.size
		}
	}

	/**
	 * Represents a mesh vertex.
	 * @property x The X coordinate of the vertex.
	 * @property y The Y coordinate of the vertex.
	 * @property z The Z coordinate of the vertex.
	 * @property tx The X coordinate of the vertex's texture position.
	 * @property ty The Y Coordinate of the vertex's texture position.
	 * @property tx2 The X coordinate of the vertex's second texture position.
	 * @property ty2 The Y coordinate of the vertex's second texture position.
	 * @property nx The X coordinate of the vertex normal.
	 * @property ny The Y coordinate of the vertex normal.
	 * @property nz The Z coordinate of the vertex normal.
	 * @property tax The X coordinate of the vertex's tangent.
	 * @property tay The Y coordinate of the vertex's tangent.
	 * @property taz The Z coordinate of the vertex's tangent.
	 * @property taw The W coordinate of the vertex's tangent.
	 * @property color The color of the vertex.
	 */
	data class Vertex(
		val x: Float, val y: Float, val z: Float,
		val tx: Float, val ty: Float, val tx2: Float, val ty2: Float,
		val nx: Float, val ny: Float, val nz: Float,
		val tax: Float, val tay: Float, val taz: Float, val taw: Float,
		val color: Color
	)

	/**
	 * The positions of the animated vertices of the mesh after bone transformations.
	 */
	var animatedVertices: List<Triple<Float, Float, Float>>
		get() {
			val list = mutableListOf<Triple<Float, Float, Float>>()
			for (i in 0 until raw.vertexCount * 3 step 3) {
				val x = raw.animVertices?.get(i) ?: 0F
				val y = raw.animVertices?.get(i + 1) ?: 0F
				val z = raw.animVertices?.get(i + 2) ?: 0F
				list.add(Triple(x, y, z))
			}

			return list
		}
		set(value) {
			raw.apply {
				// free old array if it exists
				animVertices?.let { nativeHeap.free(it.rawValue) }

				val array = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 3) { i ->
					val animated = value[i / 3]
					this.value = when (i % 3) {
						0 -> animated.first
						1 -> animated.second
						2 -> animated.third
						else -> 0F
					}
				}

				animVertices = array
			}
		}

	/**
	 * The normals of the animated vertices of the mesh after bone transformations.
	 */
	var animatedNormals: List<Triple<Float, Float, Float>>
		get() {
			val list = mutableListOf<Triple<Float, Float, Float>>()
			for (i in 0 until raw.vertexCount * 3 step 3) {
				val x = raw.animNormals?.get(i) ?: 0F
				val y = raw.animNormals?.get(i + 1) ?: 0F
				val z = raw.animNormals?.get(i + 2) ?: 0F
				list.add(Triple(x, y, z))
			}

			return list
		}
		set(value) {
			raw.apply {
				// free old array if it exists
				animNormals?.let { nativeHeap.free(it.rawValue) }

				val array = nativeHeap.allocArray<FloatVarOf<Float>>(value.size * 3) { i ->
					val animatedNormal = value[i / 3]
					this.value = when (i % 3) {
						0 -> animatedNormal.first
						1 -> animatedNormal.second
						2 -> animatedNormal.third
						else -> 0F
					}
				}

				animNormals = array
			}
		}

	/**
	 * The number of bones in the mesh.
	 */
	val boneCount: Int
		get() = raw.boneCount

	/**
	 * The bone IDs affecting each vertex of the mesh.
	 *
	 * There are up to 4 bone IDs per vertex. This list will have a size of `vertexCount * 4`.
	 */
	val boneWeights: List<Float>
		get() {
			val list = mutableListOf<Float>()
			raw.apply {
				for (i in 0 until vertexCount * 4) {
					boneWeights?.get(i)?.let { list.add(it) }
				}
			}
			return list
		}

	/**
	 * Gets the bone ID for a specific vertex and bone index. The ID can be used to
	 * look up the corresponding bone transformation matrix.
	 * @param vertexIndex The index of the vertex.
	 * @param boneIndex The index of the bone.
	 * @return The bone ID, or 0 if the vertex is not found in the mesh.
	 */
	fun getBoneId(vertexIndex: Int, boneIndex: Int): Int {
		return raw.boneIds?.get(vertexIndex * 4 + boneIndex)?.toInt() ?: 0
	}

	/**
	 * Gets the bone ID for a specific vertex and bone index. The ID can be used to
	 * look up the corresponding bone transformation matrix.
	 * @param vertex The vertex.
	 * @param boneIndex The index of the bone.
	 * @return The bone ID, or 0 if the vertex is not found in the mesh.
	 */
	fun getBoneId(vertex: Vertex, boneIndex: Int): Int {
		val vertexIndex = vertices.indexOf(vertex)
		if (vertexIndex == -1) return 0

		return getBoneId(vertexIndex, boneIndex)
	}

	/**
	 * The bone transformation matrices of the mesh.
	 */
	val boneMatrices: List<Matrix4>
		get() {
			val list = mutableListOf<Matrix4>()
			raw.apply {
				for (i in 0 until boneCount) {
					boneMatrices?.get(i)?.let { list.add(Matrix4(it)) }
				}
			}
			return list
		}

	/**
	 * Gets the transformation matrix for a specific bone index.
	 * @param boneIndex The index of the bone.
	 * @return The bone transformation matrix, or null if the bone index is invalid.
	 */
	fun getTransformForBone(boneIndex: Int): Matrix4? {
		return raw.boneMatrices?.get(boneIndex)?.let { Matrix4(it) }
	}

	/**
	 * Gets the transformation matrix for a specific vertex and bone index.
	 * @param vertexIndex The index of the vertex.
	 * @param boneIndex The index of the bone.
	 * @return The bone transformation matrix, or null if the bone index is invalid.
	 */
	fun getTransform(vertexIndex: Int, boneIndex: Int): Matrix4? {
		val boneId = getBoneId(vertexIndex, boneIndex)
		return getTransformForBone(boneId)
	}

	/**
	 * Gets the transformation matrix for a specific vertex and bone index.
	 * @param vertex The vertex.
	 * @param boneIndex The index of the bone.
	 * @return The bone transformation matrix, or null if the bone index is invalid.
	 */
	fun getTransform(vertex: Vertex, boneIndex: Int): Matrix4? {
		val vertexIndex = vertices.indexOf(vertex)
		if (vertexIndex == -1) return null

		return getTransform(vertexIndex, boneIndex)
	}

	/**
	 * Gets the transformation matrices for all bones affecting each vertex.
	 * @return A map of vertices to their corresponding list of bone transformation matrices.
	 */
	val transformations: Map<Vertex, List<Matrix4>>
		get() {
			val map = mutableMapOf<Vertex, List<Matrix4>>()
			for (vertex in vertices) {
				val transforms = mutableListOf<Matrix4>()
				for (boneIndex in 0 until 4) {
					getTransform(vertex, boneIndex)?.let { transforms.add(it) }
				}
				map[vertex] = transforms
			}
			return map
		}

	@Suppress("DuplicatedCode")
	internal fun asCValue(): CValue<raylib.internal.Mesh> = cValue<raylib.internal.Mesh> {
		// Mirror the native Mesh struct from raylib.h
		vertexCount = raw.vertexCount
		triangleCount = raw.triangleCount

		vertices = raw.vertices
		texcoords = raw.texcoords
		texcoords2 = raw.texcoords2
		normals = raw.normals
		tangents = raw.tangents
		colors = raw.colors
		indices = raw.indices

		animVertices = raw.animVertices
		animNormals = raw.animNormals
		boneIds = raw.boneIds
		boneWeights = raw.boneWeights
		boneMatrices = raw.boneMatrices
		boneCount = raw.boneCount

		vaoId = raw.vaoId
		vboId = raw.vboId
	}

	/**
	 * Draw-safe view for drawMesh.
	 * Uses the same layout as asCValue but kept separate so we can
	 * tweak behavior for draw-only scenarios if needed.
	 */
	internal fun asDrawCValue(): CValue<raylib.internal.Mesh> = asCValue()

	/**
	 * The bounding box of the mesh.
	 */
	val boundingBox: BoundingBox
		get() = GetMeshBoundingBox(asCValue()).useContents { BoundingBox(this) }

	/**
	 * Uploads the mesh data to the GPU.
	 * @param dynamic Whether the mesh is dynamic (i.e., will be updated frequently).
	 */
	fun upload(dynamic: Boolean = true) {
		UploadMesh(raw.ptr, dynamic)
	}

	/**
	 * The tangents of the mesh.
	 *
	 * These are 4D vectors representing the tangent space of each vertex in the mesh.
	 * Tangents are used in advanced lighting techniques, such as normal mapping, to
	 * provide additional information about the surface orientation.
	 *
	 * They can be computed using [createTangents].
	 */
	val tangents: List<Quadruple<Float, Float, Float, Float>>
		get() {
			val list = mutableListOf<Quadruple<Float, Float, Float, Float>>()
			for (i in 0 until raw.vertexCount * 4 step 4) {
				val x = raw.tangents?.get(i) ?: 0F
				val y = raw.tangents?.get(i + 1) ?: 0F
				val z = raw.tangents?.get(i + 2) ?: 0F
				val w = raw.tangents?.get(i + 3) ?: 0F
				list.add(Quadruple(x, y, z, w))
			}

			return list
		}

	/**
	 * Computes the mesh tangents.
	 *
	 * This function calculates the tangents for the mesh based on its vertices,
	 * texture coordinates, and normals. Tangents are essential for advanced lighting
	 * techniques such as normal mapping. They are used to determine how light interacts
	 * with the surface of the mesh.
	 */
	fun createTangents() {
		GenMeshTangents(raw.ptr)
	}

	/**
	 * Exports the mesh data to a file.
	 *
	 * As of raylib 4.0, this function supports only the `.obj` file format.
	 * @param fileName The name of the file to export to.
	 */
	fun export(fileName: String) {
		ExportMesh(asCValue(), fileName.inAppDir())
	}

	/**
	 * Exports the mesh data to a file.
	 *
	 * As of raylib 4.0, this function supports only the `.obj` file format.
	 * @param file The file to export to.
	 */
	fun export(file: File) {
		export(file.absolutePath)
	}

	/**
	 * Exports the mesh data as code to a C header file.
	 *
	 * This function generates code that represents the mesh data, which can be
	 * included in source code files for easy integration into projects.
	 * @param fileName The name of the file to export to.
	 */
	fun exportAsCode(fileName: String) {
		ExportMeshAsCode(asCValue(), fileName.inAppDir())
	}

	/**
	 * Exports the mesh data as code to a C header file.
	 *
	 * This function generates code that represents the mesh data, which can be
	 * included in source code files for easy integration into projects.
	 * @param file The file to export to.
	 */
	fun exportAsCode(file: File) {
		exportAsCode(file.absolutePath)
	}

	companion object {

		/**
		 * Generates a polygonal mesh.
		 * @param sides The number of sides of the polygon.
		 * @param radius The radius of the polygon.
		 * @return The generated mesh.
		 */
		fun poly(sides: Int, radius: Float): Mesh {
			val raw = GenMeshPoly(sides, radius)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a plane mesh.
		 * @param width The width of the plane.
		 * @param length The length of the plane.
		 * @param resX The number of subdivisions in the X axis.
		 * @param resZ The number of subdivisions in the Z axis.
		 * @return The generated mesh.
		 */
		fun plane(width: Float, length: Float, resX: Int, resZ: Int): Mesh {
			val raw = GenMeshPlane(width, length, resX, resZ)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a cube mesh.
		 * @param size The size of the cube.
		 * @return The generated mesh.
		 */
		fun cube(size: Float): Mesh {
			val raw = GenMeshCube(size, size, size)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a rectangular prism mesh.
		 * @param width The width of the rectangular prism.
		 * @param height The height of the rectangular prism.
		 * @param length The length of the rectangular prism.
		 * @return The generated mesh.
		 */
		fun rectPrism(width: Float, height: Float, length: Float): Mesh {
			val raw = GenMeshCube(width, height, length)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a sphere mesh.
		 * @param radius The radius of the sphere.
		 * @param rings The number of rings of the sphere.
		 * Increasing the number of rings increases the number of vertical subdivisions,
		 * which improves the sphere's roundness.
		 * @param slices The number of slices of the sphere.
		 * Increasing the number of slices increases the number of horizontal subdivisions,
		 * which improves the sphere's roundness.
		 * @return The generated mesh.
		 */
		fun sphere(radius: Float, rings: Int, slices: Int): Mesh {
			val raw = GenMeshSphere(radius, rings, slices)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a hemisphere mesh.
		 * @param radius The radius of the hemisphere.
		 * @param rings The number of rings of the hemisphere.
		 * Increasing the number of rings increases the number of vertical subdivisions,
		 * which improves the hemisphere's roundness.
		 * @param slices The number of slices of the hemisphere.
		 * Increasing the number of slices increases the number of horizontal subdivisions,
		 * which improves the hemisphere's roundness.
		 * @return The generated mesh.
		 */
		fun hemiSphere(radius: Float, rings: Int, slices: Int): Mesh {
			val raw = GenMeshHemiSphere(radius, rings, slices)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a cylinder mesh.
		 * @param radius The radius of the cylinder.
		 * @param height The height of the cylinder.
		 * @param slices The number of slices of the cylinder.
		 * Increasing the number of slices increases the number of subdivisions around
		 * the main axis, which improves the cylinder's roundness.
		 * @return The generated mesh.
		 */
		fun cylinder(radius: Float, height: Float, slices: Int): Mesh {
			val raw = GenMeshCylinder(radius, height, slices)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a cone mesh.
		 * @param radius The radius of the cone base.
		 * @param height The height of the cone.
		 * @param slices The number of slices of the cone.
		 * Increasing the number of slices increases the number of subdivisions around
		 * the main axis, which improves the cone's roundness.
		 * @return The generated mesh.
		 */
		fun cone(radius: Float, height: Float, slices: Int): Mesh {
			val raw = GenMeshCone(radius, height, slices)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a torus mesh.
		 * @param radius The radius of the torus.
		 * @param size The size of the torus tube.
		 * @param radSeg The number of segments around the main radius.
		 * Increasing the number of segments increases the number of subdivisions
		 * around the main radius, which improves the torus's roundness.
		 * @param sides The number of sides of the torus tube.
		 * Increasing the number of sides increases the number of subdivisions
		 * around the tube, which improves the torus's roundness.
		 * @return The generated mesh.
		 */
		fun torus(radius: Float, size: Float, radSeg: Int, sides: Int): Mesh {
			val raw = GenMeshTorus(radius, size, radSeg, sides)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a knot mesh.
		 * @param radius The radius of the knot.
		 * @param size The size of the knot tube.
		 * @param radSeg The number of segments around the main radius.
		 * Increasing the number of segments increases the number of subdivisions
		 * around the main radius, which improves the knot's roundness.
		 * @param sides The number of sides of the knot tube.
		 * Increasing the number of sides increases the number of subdivisions
		 * around the tube, which improves the knot's roundness.
		 * @return The generated mesh.
		 */
		fun knot(radius: Float, size: Float, radSeg: Int, sides: Int): Mesh {
			val raw = GenMeshKnot(radius, size, radSeg, sides)
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a heightmap mesh from an image.
		 * @param heightMap The heightmap image.
		 * @param width The width of the mesh in 3D space.
		 * @param height The height of the mesh in 3D space.
		 * @param depth The depth of the mesh in 3D space.
		 * @return The generated mesh.
		 */
		fun heightMap(
			heightMap: Image,
			width: Float = 1F,
			height: Float = 1F,
			depth: Float = 1F
		): Mesh {
			val raw = GenMeshHeightmap(heightMap.raw, (width to height to depth).toVector3())
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a heightmap mesh from an image.
		 * @param heightMap The heightmap image.
		 * @param size The size of the mesh in 3D space.
		 * @return The generated mesh.
		 */
		fun heightMap(
			heightMap: Image,
			size: Triple<Float, Float, Float>
		): Mesh {
			val raw = GenMeshHeightmap(heightMap.raw, size.toVector3())
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a cubicmap mesh from an image.
		 * @param cubicMap The cubicmap image.
		 * @param cubeSize The size of each cube in the cubicmap.
		 * @return The generated mesh.
		 */
		fun cubicMap(cubicMap: Image, cubeSize: Float): Mesh {
			val raw = GenMeshCubicmap(cubicMap.raw, (cubeSize to cubeSize to cubeSize).toVector3())
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a cubicmap mesh from an image.
		 * @param cubicMap The cubicmap image.
		 * @param width The width of each cube in the cubicmap.
		 * @param height The height of each cube in the cubicmap.
		 * @param depth The depth of each cube in the cubicmap.
		 * @return The generated mesh.
		 */
		fun cubicMap(cubicMap: Image, width: Float, height: Float, depth: Float): Mesh {
			val raw = GenMeshCubicmap(cubicMap.raw, (width to height to depth).toVector3())
			return raw.useContents { Mesh(this) }
		}

		/**
		 * Generates a cubicmap mesh from an image.
		 * @param cubicMap The cubicmap image.
		 * @param cubeSize The size of each cube in the cubicmap.
		 * @return The generated mesh.
		 */
		fun cubicMap(cubicMap: Image, cubeSize: Triple<Float, Float, Float>): Mesh {
			val raw = GenMeshCubicmap(cubicMap.raw, cubeSize.toVector3())
			return raw.useContents { Mesh(this) }
		}
	}
}

/**
 * Draws a 3D mesh on the canvas with position, rotation, and scale.
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 * @param x The X position of the mesh.
 * @param y The Y position of the mesh.
 * @param z The Z position of the mesh.
 * @param rotX The X axis of rotation.
 * @param rotY The Y axis of rotation.
 * @param rotZ The Z axis of rotation.
 * @param rotAngle The angle of rotation in degrees.
 * @param scaleX The X scale of the mesh.
 * @param scaleY The Y scale of the mesh.
 * @param scaleZ The Z scale of the mesh.
 */
fun Canvas.drawMesh(
	mesh: Mesh,
	material: Material,
	x: Float,
	y: Float,
	z: Float,
	rotX: Float,
	rotY: Float,
	rotZ: Float,
	rotAngle: Float,
	scaleX: Float,
	scaleY: Float,
	scaleZ: Float
) {
	ensureDrawing()
	val radians = rotAngle * (PI / 180F)
	val matrix = Matrix4.getTransformationMatrix(
		x to y to z,
		rotX to rotY to rotZ,
		radians,
		scaleX to scaleY to scaleZ
	)

	drawMesh(mesh, material, matrix)
}

/**
 * Draws a 3D mesh on the canvas with position, rotation, and scale.
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 * @param x The X position of the mesh.
 * @param y The Y position of the mesh.
 * @param z The Z position of the mesh.
 * @param rotX The X axis of rotation.
 * @param rotY The Y axis of rotation.
 * @param rotZ The Z axis of rotation.
 * @param rotAngle The angle of rotation in degrees.
 * @param scaleX The X scale of the mesh.
 * @param scaleY The Y scale of the mesh.
 * @param scaleZ The Z scale of the mesh.
 */
fun Canvas.drawMesh(
	mesh: Mesh,
	material: Material,
	x: Int,
	y: Int,
	z: Int,
	rotX: Float,
	rotY: Float,
	rotZ: Float,
	rotAngle: Float,
	scaleX: Float,
	scaleY: Float,
	scaleZ: Float
) {
	drawMesh(
		mesh, material,
		x.toFloat(), y.toFloat(), z.toFloat(),
		rotX, rotY, rotZ, rotAngle,
		scaleX, scaleY, scaleZ
	)
}

/**
 * Draws a 3D mesh on the canvas with position, rotation, and scale.
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 * @param position The position of the mesh as a [Triple] of floats (x, y, z).
 * @param rotationAxis The axis of rotation as a [Triple] of floats (x, y, z).
 * @param rotationAngle The angle of rotation in degrees.
 * @param scale The scale of the mesh as a [Triple] of floats (sx, sy, sz).
 */
fun Canvas.drawMesh(
	mesh: Mesh,
	material: Material,
	position: Triple<Float, Float, Float>,
	rotationAxis: Triple<Float, Float, Float>,
	rotationAngle: Float,
	scale: Triple<Float, Float, Float>
) {
	ensureDrawing()
	val radians = rotationAngle * (PI / 180F)
	val matrix = Matrix4.getTransformationMatrix(
		position,
		rotationAxis,
		radians,
		scale
	)

	drawMesh(mesh, material, matrix)
}

/**
 * Draws a 3D mesh on the canvas.
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 * @param transform The transformation matrix to apply to the mesh.
 */
fun Canvas.drawMesh(mesh: Mesh, material: Material, transform: Matrix4) {
	ensureDrawing()
	val meshValue = mesh.asDrawCValue()
	val materialValue = material.asDrawCValue()
	val transformValue = transform.raw()

	DrawMesh(meshValue, materialValue, transformValue)
}

/**
 * Draws multiple instances of a 3D mesh on the canvas.
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 * @param transformations The list of transformation matrices to apply to each instance of the mesh.
 */
fun Canvas.drawMesh(mesh: Mesh, material: Material, transformations: List<Matrix4>) {
	ensureDrawing()
	memScoped {
		val array = allocArray<Matrix>(transformations.size) { i ->
			val matrix = transformations[i]
			m0 = matrix.m0
			m1 = matrix.m1
			m2 = matrix.m2
			m3 = matrix.m3
			m4 = matrix.m4
			m5 = matrix.m5
			m6 = matrix.m6
			m7 = matrix.m7
			m8 = matrix.m8
			m9 = matrix.m9
			m10 = matrix.m10
			m11 = matrix.m11
			m12 = matrix.m12
			m13 = matrix.m13
			m14 = matrix.m14
			m15 = matrix.m15
		}
		DrawMeshInstanced(mesh.asCValue(), material.asCValue(), array, transformations.size)
	}
}

/**
 * Draws multiple instances of a 3D mesh on the canvas.
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 * @param transformations The transformation matrices to apply to each instance of the mesh.
 */
fun Canvas.drawMesh(mesh: Mesh, material: Material, vararg transformations: Matrix4) {
	drawMesh(mesh, material, transformations.toList())
}

/**
 * Represents a raylib model.
 */
class Model(internal val raw: raylib.internal.Model) {
	/**
	 * The model transformation matrix.
	 */
	@Suppress("DuplicatedCode")
	var transform: Matrix4
		get() = Matrix4(raw.transform)
		set(value) {
			raw.transform.m0 = value.m0
			raw.transform.m1 = value.m1
			raw.transform.m2 = value.m2
			raw.transform.m3 = value.m3
			raw.transform.m4 = value.m4
			raw.transform.m5 = value.m5
			raw.transform.m6 = value.m6
			raw.transform.m7 = value.m7
			raw.transform.m8 = value.m8
			raw.transform.m9 = value.m9
			raw.transform.m10 = value.m10
			raw.transform.m11 = value.m11
			raw.transform.m12 = value.m12
			raw.transform.m13 = value.m13
			raw.transform.m14 = value.m14
			raw.transform.m15 = value.m15
		}

	/**
	 * The number of meshes in the model.
	 */
	val meshCount: Int
		get() = raw.meshCount

	/**
	 * The meshes in the model.
	 */
	var meshes: List<Mesh>
		get() {
			val list = mutableListOf<Mesh>()
			for (i in 0 until meshCount) {
				raw.meshes?.get(i)?.let { list.add(Mesh(it)) }
			}
			return list
		}
		set(value) {
			raw.apply {
				// free old array if it exists
				meshes?.let { nativeHeap.free(it.rawValue) }

				val array = nativeHeap.allocArray<raylib.internal.Mesh>(value.size) { i ->
					val mesh = value[i].raw
					vertices = mesh.vertices
					texcoords = mesh.texcoords
					texcoords2 = mesh.texcoords2
					normals = mesh.normals
					tangents = mesh.tangents
					colors = mesh.colors
					indices = mesh.indices
					animVertices = mesh.animVertices
					animNormals = mesh.animNormals
					boneIds = mesh.boneIds
					boneWeights = mesh.boneWeights
					vertexCount = mesh.vertexCount
					triangleCount = mesh.triangleCount
					vaoId = mesh.vaoId
				}

				meshes = array
				meshCount = value.size
			}
		}

	/**
	 * The number of materials in the model.
	 */
	val materialCount: Int
		get() = raw.materialCount

	/**
	 * The materials in the model.
	 */
	var materials: List<Material>
		get() {
			val list = mutableListOf<Material>()
			for (i in 0 until materialCount) {
				raw.materials?.get(i)?.let { list.add(Material(it)) }
			}
			return list
		}
		set(value) {
			raw.apply {
				// free old array if it exists
				materials?.let { nativeHeap.free(it.rawValue) }

				val array = nativeHeap.allocArray<raylib.internal.Material>(value.size) { i ->
					val material = value[i].raw
					shader.id = material.shader.id
					shader.locs = material.shader.locs
					maps = material.maps
				}

				materials = array
				materialCount = value.size
			}
		}

	/**
	 * Adds a material to the model.
	 * @param material The material to add.
	 */
	fun addMaterial(material: Material) {
		val currentMaterials = materials.toMutableList()
		currentMaterials.add(material)
		materials = currentMaterials
	}

	/**
	 * Removes the material at the specified index.
	 * @param index The index of the material to remove.
	 */
	fun removeMaterial(index: Int) {
		val currentMaterials = materials.toMutableList()
		if (index in currentMaterials.indices) {
			currentMaterials.removeAt(index)
			materials = currentMaterials
		}
	}

	/**
	 * Sets the material at the specified index.
	 * @param index The index of the material to set.
	 * @param material The material to set.
	 */
	fun setMaterial(index: Int, material: Material) {
		val currentMaterials = materials.toMutableList()
		if (index in currentMaterials.indices) {
			currentMaterials[index] = material
			materials = currentMaterials
		}
	}

	/**
	 * The list of meshes and their associated materials.
	 */
	val parts: List<Pair<Mesh, Material>>
		get() {
			val meshList = meshes
			val materialList = materials

			return raw.run {
				List(meshCount) { i ->
					val materialIndex = meshMaterial?.get(i) ?: 0
					val clampedIndex = materialIndex.coerceIn(0, materialCount - 1)

					meshList[i] to materialList[clampedIndex]
				}
			}
		}

	private fun update(currentMeshes: List<Mesh>, currentMaterials: List<Material>) {
		meshes = currentMeshes
		materials = currentMaterials

		raw.meshMaterial = raw.meshMaterial?.let {
			val newArray = nativeHeap.allocArray<IntVarOf<Int>>(currentMeshes.size) { i ->
				this.value = if (i < currentMaterials.size) i else 0
			}
			nativeHeap.free(it.rawValue)
			newArray
		} ?: nativeHeap.allocArray<IntVarOf<Int>>(currentMeshes.size) { i ->
			this.value = if (i < currentMaterials.size) i else 0
		}
	}

	/**
	 * Adds a mesh and its associated material to the model.
	 * @param mesh The mesh to add.
	 * @param material The material to associate with the mesh.
	 */
	fun addPart(mesh: Mesh, material: Material) {
		val currentMeshes = meshes.toMutableList()
		val currentMaterials = materials.toMutableList()

		currentMeshes.add(mesh)
		currentMaterials.add(material)
		update(currentMeshes, currentMaterials)
	}

	/**
	 * Removes a mesh and its associated material from the model at the specified index.
	 * @param index The index of the part to remove.
	 */
	fun removePart(index: Int) {
		val currentMeshes = meshes.toMutableList()
		val currentMaterials = materials.toMutableList()

		if (index in currentMeshes.indices && index in currentMaterials.indices) {
			currentMeshes.removeAt(index)
			currentMaterials.removeAt(index)
			update(currentMeshes, currentMaterials)
		}
	}

	/**
	 * Sets the mesh and its associated material at the specified index.
	 * @param index The index of the part to set.
	 * @param mesh The mesh to set.
	 * @param material The material to set.
	 */
	fun setPart(index: Int, mesh: Mesh, material: Material) {
		val currentMeshes = meshes.toMutableList()
		val currentMaterials = materials.toMutableList()

		if (index in currentMeshes.indices && index in currentMaterials.indices) {
			currentMeshes[index] = mesh
			currentMaterials[index] = material
			update(currentMeshes, currentMaterials)
		}
	}

	internal fun asCValue(): CValue<raylib.internal.Model> = cValue<raylib.internal.Model> {
		transform.m0 = raw.transform.m0
		transform.m1 = raw.transform.m1
		transform.m2 = raw.transform.m2
		transform.m3 = raw.transform.m3
		transform.m4 = raw.transform.m4
		transform.m5 = raw.transform.m5
		transform.m6 = raw.transform.m6
		transform.m7 = raw.transform.m7
		transform.m8 = raw.transform.m8
		transform.m9 = raw.transform.m9
		transform.m10 = raw.transform.m10
		transform.m11 = raw.transform.m11
		transform.m12 = raw.transform.m12
		transform.m13 = raw.transform.m13
		transform.m14 = raw.transform.m14
		transform.m15 = raw.transform.m15

		meshes = raw.meshes
		materials = raw.materials
		meshMaterial = raw.meshMaterial
		meshCount = raw.meshCount
		materialCount = raw.materialCount
	}

	/**
	 * Whether the model is valid.
	 */
	val isValid: Boolean
		get() = IsModelValid(asCValue())

	/**
	 * The bounding box of the model.
	 */
	val boundingBox: BoundingBox
		get() = GetModelBoundingBox(asCValue()).useContents { BoundingBox(this) }

	/**
	 * Sets the material for a specific mesh in the model.
	 * @param meshId The ID of the mesh to set the material for.
	 * @param materialId The ID of the material to set.
	 */
	fun setMeshMaterial(meshId: Int, materialId: Int) {
		SetModelMeshMaterial(raw.ptr, meshId, materialId)
	}

	/**
	 * Sets the material for a specific mesh in the model.
	 * @param meshId The ID of the mesh to set the material for.
	 * @param material The material to set.
	 */
	fun setMeshMaterial(meshId: Int, material: Material) {
		val materialIndex = materials.indexOf(material)
		if (materialIndex != -1) {
			setMeshMaterial(meshId, materialIndex)
		}
	}

	/**
	 * Sets the material for a specific mesh in the model.
	 * @param mesh The mesh to set the material for.
	 * @param material The material to set.
	 */
	fun setMeshMaterial(mesh: Mesh, material: Material) {
		val meshIndex = meshes.indexOf(mesh)
		if (meshIndex != -1) {
			setMeshMaterial(meshIndex, material)
		}
	}

	companion object {

		/**
		 * Loads a model from the specified file path.
		 * @param path The file path to load the model from.
		 * @return The loaded [Model].
		 */
		fun load(path: String): Model {
			val raw = LoadModel(path.inAppDir())
			return raw.useContents { Model(this) }
		}

		/**
		 * Loads a model from the specified file.
		 * @param file The file to load the model from.
		 * @return The loaded [Model].
		 */
		fun load(file: File) : Model = load(file.absolutePath)

		/**
		 * Creates a model from the specified mesh.
		 * @param mesh The mesh to create the model from.
		 * @return The created [Model].
		 */
		fun fromMesh(mesh: Mesh): Model {
			val raw = LoadModelFromMesh(mesh.asCValue())
			return raw.useContents { Model(this) }
		}
	}

}

/**
 * Draws a 3D model on the canvas.
 * @param model The model to draw.
 * @param x The X coordinate of the model position.
 * @param y The Y Coordinate of the model position.
 * @param z The Z coordinate of the model position.
 * @param scale The scale of the model.
 * @param tint The tint color to apply to the model.
 */
fun Canvas.drawModel(
	model: Model,
	x: Int,
	y: Int,
	z: Int,
	scale: Float = 1F,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawModel(model.asCValue(), (x to y to z).toVector3(), scale, tint.raw())
}

/**
 * Draws a 3D model on the canvas.
 * @param model The model to draw.
 * @param position The position of the model as a Triple of X, Y, Z coordinates.
 * @param scale The scale of the model.
 * @param tint The tint color to apply to the model.
 */
fun Canvas.drawModel(
	model: Model,
	position: Triple<Int, Int, Int>,
	scale: Float = 1F,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawModel(model.asCValue(), position.toVector3(), scale, tint.raw())
}

/**
 * Draws a 3D model on the canvas with extended parameters.
 * @param model The model to draw.
 * @param x The X coordinate of the model position.
 * @param y The Y coordinate of the model position.
 * @param z The Z coordinate of the model position.
 * @param rotX The X axis position where to rotate
 * @param rotY The Y axis position where to rotate
 * @param rotZ The Z axis position where to rotate
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param scaleX The scale of the model along the X axis.
 * @param scaleY The scale of the model along the Y axis.
 * @param scaleZ The scale of the model along the Z axis.
 * @param tint The tint color to apply to the model.
 */
fun Canvas.drawModel(
	model: Model,
	x: Int,
	y: Int,
	z: Int,
	rotX: Float,
	rotY: Float,
	rotZ: Float,
	rotAngle: Float,
	scaleX: Float = 1F,
	scaleY: Float = 1F,
	scaleZ: Float = 1F,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawModelEx(
		model.asCValue(),
		(x to y to z).toVector3(),
		(rotX to rotY to rotZ).toVector3(),
		rotAngle,
		(scaleX to scaleY to scaleZ).toVector3(),
		tint.raw()
	)
}

/**
 * Draws a 3D model on the canvas with extended parameters.
 * @param model The model to draw.
 * @param x The X coordinate of the model position.
 * @param y The Y coordinate of the model position.
 * @param z The Z coordinate of the model position.
 * @param rotX The X axis position where to rotate
 * @param rotY The Y axis position where to rotate
 * @param rotZ The Z axis position where to rotate
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param scale The scale of the model.
 * @param tint The tint color to apply to the model.
 */
fun Canvas.drawModel(
	model: Model,
	x: Int,
	y: Int,
	z: Int,
	rotX: Float,
	rotY: Float,
	rotZ: Float,
	rotAngle: Float,
	scale: Float = 1F,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawModelEx(
		model.asCValue(),
		(x to y to z).toVector3(),
		(rotX to rotY to rotZ).toVector3(),
		rotAngle,
		(scale to scale to scale).toVector3(),
		tint.raw()
	)
}

/**
 * Draws a 3D model on the canvas with extended parameters.
 * @param model The model to draw.
 * @param position The position of the model as a Triple of X, Y, Z coordinates.
 * @param rotation The rotation of the model as a Triple of X, Y, Z axis positions.
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param scaleX The scale of the model along the X axis.
 * @param scaleY The scale of the model along the Y axis.
 * @param scaleZ The scale of the model along the Z axis.
 * @param tint The tint color to apply to the model.
 */
fun Canvas.drawModel(
	model: Model,
	position: Triple<Int, Int, Int>,
	rotation: Triple<Float, Float, Float>,
	rotAngle: Float,
	scaleX: Float = 1F,
	scaleY: Float = 1F,
	scaleZ: Float = 1F,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawModelEx(
		model.asCValue(),
		position.toVector3(),
		rotation.toVector3(),
		rotAngle,
		(scaleX to scaleY to scaleZ).toVector3(),
		tint.raw()
	)
}

/**
 * Draws a 3D model on the canvas with extended parameters.
 * @param model The model to draw.
 * @param position The position of the model as a Triple of X, Y, Z coordinates.
 * @param rotation The rotation of the model as a Triple of X, Y, Z axis positions.
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param scale The scale of the model as a Triple of X, Y, Z scales.
 * @param tint The tint color to apply to the model.
 */
fun Canvas.drawModel(
	model: Model,
	position: Triple<Int, Int, Int>,
	rotation: Triple<Float, Float, Float>,
	rotAngle: Float,
	scale: Triple<Float, Float, Float> = (1F to 1F to 1F),
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawModelEx(
		model.asCValue(),
		position.toVector3(),
		rotation.toVector3(),
		rotAngle,
		scale.toVector3(),
		tint.raw()
	)
}

/**
 * Draws the wires of a 3D model on the canvas.
 *
 * This function is useful for debugging purposes to visualize the model's wireframe.
 * @param model The model to draw.
 * @param x The X coordinate of the model position.
 * @param y The Y coordinate of the model position.
 * @param z The Z coordinate of the model position.
 * @param scale The scale of the model.
 * @param tint The tint color to apply to the model wires. Default is black.
 */
fun Canvas.drawModelWires(
	model: Model,
	x: Int,
	y: Int,
	z: Int,
	scale: Float = 1F,
	tint: Color = Color.BLACK
) {
	ensureDrawing()
	DrawModelWires(model.asCValue(), (x to y to z).toVector3(), scale, tint.raw())
}

/**
 * Draws the wires of a 3D model on the canvas.
 *
 * This function is useful for debugging purposes to visualize the model's wireframe.
 * @param model The model to draw.
 * @param position The position of the model as a Triple of X, Y, Z coordinates.
 * @param scale The scale of the model.
 * @param tint The tint color to apply to the model wires. Default is black.
 */
fun Canvas.drawModelWires(
	model: Model,
	position: Triple<Int, Int, Int>,
	scale: Float = 1F,
	tint: Color = Color.BLACK
) {
	ensureDrawing()
	DrawModelWires(model.asCValue(), position.toVector3(), scale, tint.raw())
}

/**
 * Draws the wires of a 3D model on the canvas with extended parameters.
 *
 * This function is useful for debugging purposes to visualize the model's wireframe.
 * @param model The model to draw.
 * @param x The X coordinate of the model position.
 * @param y The Y coordinate of the model position.
 * @param z The Z coordinate of the model position.
 * @param rotX The X axis position where to rotate
 * @param rotY The Y axis position where to rotate
 * @param rotZ The Z axis position where to rotate
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param scaleX The scale of the model along the X axis.
 * @param scaleY The scale of the model along the Y axis.
 * @param scaleZ The scale of the model along the Z axis.
 * @param tint The tint color to apply to the model wires. Default is black.
 */
fun Canvas.drawModelWires(
	model: Model,
	x: Int,
	y: Int,
	z: Int,
	rotX: Float,
	rotY: Float,
	rotZ: Float,
	rotAngle: Float,
	scaleX: Float = 1F,
	scaleY: Float = 1F,
	scaleZ: Float = 1F,
	tint: Color = Color.BLACK
) {
	ensureDrawing()
	DrawModelWiresEx(
		model.asCValue(),
		(x to y to z).toVector3(),
		(rotX to rotY to rotZ).toVector3(),
		rotAngle,
		(scaleX to scaleY to scaleZ).toVector3(),
		tint.raw()
	)
}

/**
 * Draws the wires of a 3D model on the canvas with extended parameters.
 * @param model The model to draw.
 * @param position The position of the model as a Triple of X, Y, Z coordinates.
 * @param rotation The rotation of the model as a Triple of X, Y, Z axis positions.
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param scaleX The scale of the model along the X axis.
 * @param scaleY The scale of the model along the Y axis.
 * @param scaleZ The scale of the model along the Z axis.
 * @param tint The tint color to apply to the model wires. Default is black.
 */
fun Canvas.drawModelWires(
	model: Model,
	position: Triple<Int, Int, Int>,
	rotation: Triple<Float, Float, Float>,
	rotAngle: Float,
	scaleX: Float = 1F,
	scaleY: Float = 1F,
	scaleZ: Float = 1F,
	tint: Color = Color.BLACK
) {
	ensureDrawing()
	DrawModelWiresEx(
		model.asCValue(),
		position.toVector3(),
		rotation.toVector3(),
		rotAngle,
		(scaleX to scaleY to scaleZ).toVector3(),
		tint.raw()
	)
}

/**
 * Draws a 3D point on the canvas.
 * @param x The X coordiante of the point.
 * @param y The Y coordiante of the point.
 * @param z The Z coordiante of the point.
 * @param color The color of the point
 */
fun Canvas.draw(x: Int, y: Int, z: Int, color: Color) {
	ensureDrawing()
	DrawPoint3D((x to y to z).toVector3(), color.raw())
}

/**
 * Draws a 3D line on the canvas.
 * @param x1 The X coordiante of the starting point.
 * @param y1 The Y coordiante of the starting point.
 * @param z1 The Z coordiante of the starting point.
 * @param x2 The X coordiante of the ending point.
 * @param y2 The Y coordiante of the ending point.
 * @param z2 The Z coordiante of the ending point.
 * @param color The color of the line
 */
fun Canvas.line3(
	x1: Int,
	y1: Int,
	z1: Int,
	x2: Int,
	y2: Int,
	z2: Int,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawLine3D((x1 to y1 to z1).toVector3(), (x2 to y2 to z2).toVector3(), color.raw())
}

/**
 * Draws a circle in 3D space.
 * @param cx The X coordinate for the circle center.
 * @param cy The Y coordinate for the circle center.
 * @param cz The Z coordinate for the circle center.
 * @param radius The radius of the 3D circle.
 * @param rotX The X axis position where to rotate
 * @param rotY The Y axis position where to rotate
 * @param rotZ The Z axis position where to rotate
 * @param rotAngle The rotational angle along the specified axis in degrees
 * @param color The color of the circle outline.
 */
fun Canvas.circle3(
	cx: Int,
	cy: Int,
	cz: Int,
	radius: Float,
	rotX: Float,
	rotY: Float,
	rotZ: Float,
	rotAngle: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCircle3D(
		(cx to cy to cz).toVector3(),
		radius,
		(rotX to rotY to rotZ).toVector3(),
		rotAngle, color.raw()
	)
}

/**
 * Draws a triangle in 3D space.
 * @param x1 The X coordinate of the first vertex.
 * @param y1 The Y coordinate of the first vertex.
 * @param z1 The Z coordinate of the first vertex.
 * @param x2 The X coordinate of the second vertex.
 * @param y2 The Y Coordinate of the second vertex.
 * @param z2 The Z coordinate of the second vertex.
 * @param x3 The X coordinate of the third vertex.
 * @param y3 The Y coordinate of the third vertex.
 * @param z3 The Z coordinate of the third vertex.
 * @param color The color of the triange outline.
 */
fun Canvas.triangle3(
	x1: Int,
	y1: Int,
	z1: Int,
	x2: Int,
	y2: Int,
	z2: Int,
	x3: Int,
	y3: Int,
	z3: Int,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawTriangle3D(
		(x1 to y1 to z1).toVector3(),
		(x2 to y2 to z2).toVector3(),
		(x3 to y3 to z3).toVector3(),
		color.raw()
	)
}

/**
 * Draws a triangle strip in 3D space.
 * @param color The color of the triangle strip.
 * @param points The list of points making up the triangle strip.
 */
fun Canvas.triangleStrip3(color: Color = Color.BLACK, points: List<Triple<Int, Int, Int>>) {
	if (points.size < 3) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector3>(points.size) { i ->
			x = points[i].first.toFloat()
			y = points[i].second.toFloat()
			z = points[i].third.toFloat()
		}
	}

	DrawTriangleStrip3D(array, points.size, color.raw())
}

/**
 * Draws a triangle strip in 3D space.
 * @param color The color of the triangle strip.
 * @param points The vararg points making up the triangle strip.
 */
fun Canvas.triangleStrip3(color: Color = Color.BLACK, vararg points: Triple<Int, Int, Int>) {
	triangleStrip3(color, points.toList())
}

/**
 * Draws a rectangular prism's wires in 3D space.
 * @param x The X coordinate of the rectangle center.
 * @param y The Y coordinate of the rectangle center.
 * @param z The Z coordinate of the rectangle center.
 * @param width The width of the rectangle.
 * @param height The height of the rectangle.
 * @param length The length of the rectangle.
 * @param color The color of the prism wires.
 */
fun Canvas.rectPrism(
	x: Int,
	y: Int,
	z: Int,
	width: Float,
	height: Float,
	length: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCubeWires(
		(x to y to z).toVector3(),
		width,
		height,
		length,
		color.raw()
	)
}

/**
 * Draws a rectangular prism in 3D space.
 * @param x The X coordinate of the rectangle center.
 * @param y The Y coordinate of the rectangle center.
 * @param z The Z coordinate of the rectangle center.
 * @param width The width of the rectangle.
 * @param height The height of the rectangle.
 * @param length The length of the rectangle.
 * @param color The color of the rectangle.
 */
fun Canvas.fillRectPrism(
	x: Int,
	y: Int,
	z: Int,
	width: Float,
	height: Float,
	length: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCube(
		(x to y to z).toVector3(),
		width,
		height,
		length,
		color.raw()
	)
}

/**
 * Draws a sphere's wires in 3D space.
 * @param x The X coordinate of the sphere center.
 * @param y The Y coordinate of the sphere center.
 * @param z The Z coordinate of the sphere center.
 * @param radius The radius of the sphere.
 * @param color The color of the sphere wires.
 */
fun Canvas.sphere(
	x: Int,
	y: Int,
	z: Int,
	radius: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawSphereWires(
		(x to y to z).toVector3(),
		radius,
		8,
		8,
		color.raw()
	)
}

/**
 * Draws a sphere's wires in 3D space.
 * @param x The X coordinate of the sphere center.
 * @param y The Y coordinate of the sphere center.
 * @param z The Z coordinate of the sphere center.
 * @param radius The radius of the sphere.
 * @param rings The number of rings to use for drawing. Defaults to 8.
 * Increasing the number of rings will result in a smoother sphere.
 * @param slices The number of slices to use for drawing. Defaults to 8.
 * Increasing the number of slices will result in a smoother sphere.
 * @param color The color of the sphere wires.
 */
fun Canvas.sphere(
	x: Int,
	y: Int,
	z: Int,
	radius: Float,
	rings: Int = 8,
	slices: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawSphereWires(
		(x to y to z).toVector3(),
		radius,
		rings,
		slices,
		color.raw()
	)
}

/**
 * Draws a sphere in 3D space.
 * @param x The X coordinate of the sphere center.
 * @param y The Y coordinate of the sphere center.
 * @param z The Z coordinate of the sphere center.
 * @param radius The radius of the sphere.
 * @param color The color of the sphere.
 */
fun Canvas.fillSphere(
	x: Int,
	y: Int,
	z: Int,
	radius: Float,
	color: Color
) {
	ensureDrawing()
	DrawSphere(
		(x to y to z).toVector3(),
		radius,
		color.raw()
	)
}

/**
 * Draws a sphere in 3D space.
 * @param x The X coordinate of the sphere center.
 * @param y The Y coordinate of the sphere center.
 * @param z The Z coordinate of the sphere center.
 * @param radius The radius of the sphere.
 * @param rings The number of rings to use for drawing. Defaults to 8.
 * Increasing the number of rings will result in a smoother sphere.
 * @param slices The number of slices to use for drawing. Defaults to 8.
 * Increasing the number of slices will result in a smoother sphere.
 * @param color The color of the sphere.
 */
fun Canvas.fillSphere(
	x: Int,
	y: Int,
	z: Int,
	radius: Float,
	rings: Int = 8,
	slices: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawSphereEx(
		(x to y to z).toVector3(),
		radius,
		rings,
		slices,
		color.raw()
	)
}

/**
 * Draws a cylinder's wires in 3D space.
 * @param x The X coordinate of the cylinder center.
 * @param y The Y coordinate of the cylinder center.
 * @param z The Z coordinate of the cylinder center.
 * @param radiusTop The radius of the top base of the cylinder.
 * @param radiusBottom The radius of the bottom base of the cylinder. Defaults to [radiusTop].
 * @param height The height of the cylinder.
 * @param color The color of the cylinder wires.
 */
fun Canvas.cylinder(
	x: Int,
	y: Int,
	z: Int,
	radiusTop: Float,
	radiusBottom: Float = radiusTop,
	height: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCylinderWires(
		(x to y to z).toVector3(),
		radiusTop,
		radiusBottom,
		height,
		8,
		color.raw()
	)
}

/**
 * Draws a cylinder's wires in 3D space.
 * @param x The X coordinate of the cylinder center.
 * @param y The Y coordinate of the cylinder center.
 * @param z The Z coordinate of the cylinder center.
 * @param radius The radius of the cylinder.
 * @param height The height of the cylinder.
 * @param color The color of the cylinder wires.
 */
fun Canvas.cylinder(
	x: Int,
	y: Int,
	z: Int,
	radius: Float,
	height: Float,
	color: Color = Color.BLACK
) {
	cylinder(x, y, z, radius, radius, height, color)
}

/**
 * Draws a cylinder's wires in 3D space.
 * @param x The X coordinate of the cylinder center.
 * @param y The Y coordinate of the cylinder center.
 * @param z The Z coordinate of the cylinder center.
 * @param radiusTop The radius of the top base of the cylinder.
 * @param radiusBottom The radius of the bottom base of the cylinder. Defaults to [radiusTop].
 * @param height The height of the cylinder.
 * @param slices The number of slices to use for drawing. Defaults to 8.
 * Increasing the number of slices will result in a smoother cylinder.
 * @param color The color of the cylinder wires.
 */
fun Canvas.cylinder(
	x: Int,
	y: Int,
	z: Int,
	radiusTop: Float,
	radiusBottom: Float = radiusTop,
	height: Float,
	slices: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCylinderWires(
		(x to y to z).toVector3(),
		radiusTop,
		radiusBottom,
		height,
		slices,
		color.raw()
	)
}

/**
 * Draws a cylinder in 3D space with differing start and end points between bases.
 * @param x1 The X coordinate of the starting point.
 * @param y1 The Y coordinate of the starting point.
 * @param z1 The Z coordinate of the starting point.
 * @param x2 The X coordinate of the ending point.
 * @param y2 The Y coordinate of the ending point.
 * @param z2 The Z coordinate of the ending point.
 * @param radiusTop The radius of the top base of the cylinder.
 * @param radiusBottom The radius of the bottom base of the cylinder. Defaults to [radiusTop].
 * @param sides The number of sides to use for drawing. Defaults to 8.
 * Increasing the number of sides will result in a smoother cylinder.
 * @param color The color of the cylinder.
 */
fun Canvas.cylinder(
	x1: Int,
	y1: Int,
	z1: Int,
	x2: Int,
	y2: Int,
	z2: Int,
	radiusTop: Float,
	radiusBottom: Float = radiusTop,
	sides: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCylinderEx(
		(x1 to y1 to z1).toVector3(),
		(x2 to y2 to z2).toVector3(),
		radiusTop,
		radiusBottom,
		sides,
		color.raw()
	)
}

/**
 * Draws a cylinder in 3D space.
 * @param x The X coordinate of the cylinder center.
 * @param y The Y coordinate of the cylinder center.
 * @param z The Z coordinate of the cylinder center.
 * @param radiusTop The radius of the top base of the cylinder.
 * @param radiusBottom The radius of the bottom base of the cylinder. Defaults to [radiusTop].
 * @param height The height of the cylinder.
 * @param color The color of the cylinder.
 */
fun Canvas.fillCylinder(
	x: Int,
	y: Int,
	z: Int,
	radiusTop: Float,
	radiusBottom: Float = radiusTop,
	height: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCylinder(
		(x to y to z).toVector3(),
		radiusTop,
		radiusBottom,
		height,
		8,
		color.raw()
	)
}

/**
 * Draws a cylinder in 3D space.
 * @param x The X coordinate of the cylinder center.
 * @param y The Y coordinate of the cylinder center.
 * @param z The Z coordinate of the cylinder center.
 * @param radius The radius of the cylinder.
 * @param height The height of the cylinder.
 * @param color The color of the cylinder.
 */
fun Canvas.fillCylinder(
	x: Int,
	y: Int,
	z: Int,
	radius: Float,
	height: Float,
	color: Color = Color.BLACK
) {
	fillCylinder(x, y, z, radius, radius, height, color)
}

/**
 * Draws a cylinder in 3D space.
 * @param x The X coordinate of the cylinder center.
 * @param y The Y coordinate of the cylinder center.
 * @param z The Z coordinate of the cylinder center.
 * @param radiusTop The radius of the top base of the cylinder.
 * @param radiusBottom The radius of the bottom base of the cylinder. Defaults to [radiusTop].
 * @param height The height of the cylinder.
 * @param slices The number of slices to use for drawing. Defaults to 8.
 * Increasing the number of slices will result in a smoother cylinder.
 * @param color The color of the cylinder.
 */
fun Canvas.fillCylinder(
	x: Int,
	y: Int,
	z: Int,
	radiusTop: Float,
	radiusBottom: Float = radiusTop,
	height: Float,
	slices: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCylinder(
		(x to y to z).toVector3(),
		radiusTop,
		radiusBottom,
		height,
		slices,
		color.raw()
	)
}

/**
 * Draws a cylinder in 3D space with differing start and end points between bases.
 * @param x1 The X coordinate of the starting point.
 * @param y1 The Y coordinate of the starting point.
 * @param z1 The Z coordinate of the starting point.
 * @param x2 The X coordinate of the ending point.
 * @param y2 The Y coordinate of the ending point.
 * @param z2 The Z coordinate of the ending point.
 * @param radiusTop The radius of the top base of the cylinder.
 * @param radiusBottom The radius of the bottom base of the cylinder. Defaults to [radiusTop].
 * @param sides The number of sides to use for drawing. Defaults to 8.
 * Increasing the number of sides will result in a smoother cylinder.
 * @param color The color of the cylinder.
 */
fun Canvas.fillCylinder(
	x1: Int,
	y1: Int,
	z1: Int,
	x2: Int,
	y2: Int,
	z2: Int,
	radiusTop: Float,
	radiusBottom: Float = radiusTop,
	sides: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCylinderEx(
		(x1 to y1 to z1).toVector3(),
		(x2 to y2 to z2).toVector3(),
		radiusTop,
		radiusBottom,
		sides,
		color.raw()
	)
}

/**
 * Draws a capsule's wires in 3D space.
 * @param x1 The X coordinate of the starting point.
 * @param y1 The Y coordinate of the starting point.
 * @param z1 The Z coordinate of the starting point.
 * @param x2 The X coordinate of the ending point.
 * @param y2 The Y coordinate of the ending point.
 * @param z2 The Z coordinate of the ending point.
 * @param radius The radius of the capsule.
 * @param slices The number of slices to use for drawing. Defaults to 8.
 * Increasing the number of slices will result in a smoother capsule.
 * @param rings The number of rings to use for drawing. Defaults to 8.
 * Increasing the number of rings will result in a smoother capsule.
 * @param color The color of the capsule wires.
 */
fun Canvas.capsule(
	x1: Int,
	y1: Int,
	z1: Int,
	x2: Int,
	y2: Int,
	z2: Int,
	radius: Float,
	slices: Int = 8,
	rings: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCapsuleWires(
		(x1 to y1 to z1).toVector3(),
		(x2 to y2 to z2).toVector3(),
		radius,
		slices,
		rings,
		color.raw()
	)
}

/**
 * Draws a capsule in 3D space.
 * @param x1 The X coordinate of the starting point.
 * @param y1 The Y coordinate of the starting point.
 * @param z1 The Z coordinate of the starting point.
 * @param x2 The X coordinate of the ending point.
 * @param y2 The Y coordinate of the ending point.
 * @param z2 The Z coordinate of the ending point.
 * @param radius The radius of the capsule.
 * @param slices The number of slices to use for drawing. Defaults to 8.
 * Increasing the number of slices will result in a smoother capsule.
 * @param rings The number of rings to use for drawing. Defaults to 8.
 * Increasing the number of rings will result in a smoother capsule.
 * @param color The color of the capsule.
 */
fun Canvas.fillCapsule(
	x1: Int,
	y1: Int,
	z1: Int,
	x2: Int,
	y2: Int,
	z2: Int,
	radius: Float,
	slices: Int = 8,
	rings: Int = 8,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawCapsule(
		(x1 to y1 to z1).toVector3(),
		(x2 to y2 to z2).toVector3(),
		radius,
		slices,
		rings,
		color.raw()
	)
}

/**
 * Draws a rectangular plane in 3D space.
 * @param x The X coordinate of the rectangle center.
 * @param y The Y coordinate of the rectangle center.
 * @param z The Z coordinate of the rectangle center.
 * @param width The width of the rectangle.
 * @param height The height of the rectangle.
 * @param color The color of the rectangle.
 */
fun Canvas.rect3(
	x: Int,
	y: Int,
	z: Int,
	width: Float,
	height: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawPlane(
		(x to y to z).toVector3(),
		(width to height).toVector2(),
		color.raw()
	)
}

/**
 * Draws a ray in 3D space.
 * @param px The X coordinate of the ray origin.
 * @param py The Y coordinate of the ray origin.
 * @param pz The Z coordinate of the ray origin.
 * @param dx The X component of the ray direction.
 * @param dy The Y component of the ray direction.
 * @param dz The Z component of the ray direction.
 * @param color The color of the ray.
 */
fun Canvas.ray(
	px: Int,
	py: Int,
	pz: Int,
	dx: Float,
	dy: Float,
	dz: Float,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawRay(
		cValue<Ray> {
			position.x = px.toFloat()
			position.y = py.toFloat()
			position.z = pz.toFloat()
			direction.x = dx
			direction.y = dy
			direction.z = dz
		},
		color.raw()
	)
}

/**
 * Draws a grid in 3D space.
 * @param slices The number of slices to draw.
 * @param spacing The spacing between each slice.
 */
fun Canvas.grid(slices: Int = 10, spacing: Float = 1F) {
	ensureDrawing()
	DrawGrid(slices, spacing)
}

/**
 * Draws a billboard texture in 3D space.
 * @param camera The camera to use for rendering the billboard.
 * @param texture The texture to draw as a billboard.
 * @param x The X coordinate of the billboard position.
 * @param y The Y coordinate of the billboard position.
 * @param z The Z coordinate of the billboard position.
 * @param scale The scale of the billboard.
 * @param tint The tint color to apply to the billboard. Default is white.
 */
fun Canvas.billboard(
	camera: Camera3D,
	texture: Texture2D,
	x: Int,
	y: Int,
	z: Int,
	scale: Float = 1F,
	tint: Color = Color.WHITE
) {
	ensureDrawing()
	DrawBillboard(
		camera.raw(),
		texture.raw(),
		(x to y to z).toVector3(),
		scale,
		tint.raw()
	)
}
