@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.*
import kray.*
import raylib.MaterialMap.Texture
import raylib.Mesh.Companion.cubicMap
import raylib.Mesh.Companion.heightMap
import raylib.internal.*

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
		val raw = MatrixMultiply(this.raw(), b.raw())
		return raw.useContents { Matrix4(this) }
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
			val raw = MatrixTranslate(position.first, position.second, position.third)
			return raw.useContents { Matrix4(this) }
		}

		/**
		 * Generates a translation matrix given x, y, and z coordinates.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 * @param z The z coordinate.
		 * @return The translation matrix as a [Matrix4].
		 */
		fun translate(x: Float, y: Float, z: Float): Matrix4 {
			return getTranslationMatrix(x to y to z)
		}

		/**
		 * Generates a rotation matrix given an axis and an angle.
		 * @param axis The axis of rotation as a [Triple] of floats (x, y, z).
		 * @param angle The angle of rotation in radians.
		 * @return The rotation matrix as a [Matrix4].
		 */
		fun getRotationMatrix(axis: Triple<Float, Float, Float>, angle: Float): Matrix4 {
			val raw = MatrixRotate(cValue {
				x = axis.first
				y = axis.second
				z = axis.third
			}, angle)

			return raw.useContents { Matrix4(this) }
		}

		/**
		 * Generates a rotation matrix given x, y, z coordinates for the axis and an angle.
		 * @param x The x coordinate of the axis.
		 * @param y The y coordinate of the axis.
		 * @param z The z coordinate of the axis.
		 * @param angle The angle of rotation in radians.
		 * @return The rotation matrix as a [Matrix4].
		 */
		fun rotate(x: Float, y: Float, z: Float, angle: Float): Matrix4 {
			return getRotationMatrix(x to y to z, angle)
		}

		/**
		 * Generates a scaling matrix given a scale vector.
		 * @param scale The scale vector as a [Triple] of floats (sx, sy, sz).
		 * @return The scaling matrix as a [Matrix4].
		 */
		fun getScaleMatrix(scale: Triple<Float, Float, Float>): Matrix4 {
			val raw = MatrixScale(scale.first, scale.second, scale.third)
			return raw.useContents { Matrix4(this) }
		}

		/**
		 * Generates a scaling matrix given x, y, and z scale factors.
		 * @param sx The x scale factor.
		 * @param sy The y scale factor.
		 * @param sz The z scale factor.
		 * @return The scaling matrix as a [Matrix4].
		 */
		fun scale(sx: Float, sy: Float, sz: Float): Matrix4 {
			return getScaleMatrix(sx to sy to sz)
		}

		/**
		 * Generates a uniform scaling matrix given a single scale factor.
		 * @param scale The uniform scale factor.
		 * @return The scaling matrix as a [Matrix4].
		 */
		fun scale(scale: Float): Matrix4 {
			return getScaleMatrix(scale to scale to scale)
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

	/**
	 * The X coordinate of the smaller value for the bounding box vertex.
	 */
	val minX: Float
		get() = min.first

	/**
	 * The Y coordinate of the smaller value for the bounding box vertex.
	 */
	val minY: Float
		get() = min.second

	/**
	 * The Z coordinate of the smaller value for the bounding box vertex.
	 */
	val minZ: Float
		get() = min.third

	/**
	 * The X coordinate of the bigger value for the bounding box vertex.
	 */
	val maxX: Float
		get() = max.first

	/**
	 * The Y coordinate of the bigger value for the bounding box vertex.
	 */
	val maxY: Float
		get() = max.second

	/**
	 * The Z coordinate of the bigger value for the bounding box vertex.
	 */
	val maxZ: Float
		get() = max.third

	/**
	 * The width of this bounding box.
	 */
	val width: Float
		get() = maxX - minX

	/**
	 * The height of this bounding box.
	 */
	val height: Float
		get() = maxY - minY

	/**
	 * The depth of this bounding box.
	 */
	val depth: Float
		get() = maxZ - minZ

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
open class Shader internal constructor(
	internal val raw: CValue<raylib.internal.Shader>
) {

	/**
	 * The ID of the shader program.
	 */
	var id: UInt
		get() = raw.useContents { id }
		set(value) {
			raw.useContents {
				id = value
			}
		}

	/**
	 * The locations of the shader attributes and uniforms.
	 */
	var locs: List<Int>
		get() {
			val list = mutableListOf<Int>()
			raw.useContents {
				for (i in 0 until RL_MAX_SHADER_LOCATIONS) {
					list.add(locs?.get(i) ?: -1)
				}
			}

			return list
		}
		set(value) {
			raw.useContents {
				for (i in 0 until RL_MAX_SHADER_LOCATIONS) {
					locs?.set(i, value.getOrElse(i) { -1 })
				}
			}
		}

	/**
	 * Whether the shader is valid.
	 */
	val isValid: Boolean
		get() = IsShaderValid(raw)

	/**
	 * The data types supported for shader uniform variables.
	 */
	@Suppress("UNCHECKED_CAST")
	class DataType private constructor(internal val value: UInt, internal val convert: NativePlacement.(Any) -> CValuesRef<*>) {
		companion object {
			/**
			 * The [Float] type.
			 */
			val FLOAT: DataType by lazy {
				DataType(SHADER_UNIFORM_FLOAT) {
					val v = alloc<FloatVar>()
					v.value = it as Float
					v.ptr
				}
			}

			/**
			 * The [Pair] type with [Float].
			 */
			val VEC2F: DataType by lazy {
				DataType(SHADER_UNIFORM_VEC2) {
					val pair = it as Pair<Float, Float>
					allocArrayOf(pair.first, pair.second)
				}
			}

			/**
			 * The [Triple] type with [Float].
			 */
			val VEC3F: DataType by lazy {
				DataType(SHADER_UNIFORM_VEC3) {
					val triple = it as Triple<Float, Float, Float>
					allocArrayOf(triple.first, triple.second, triple.third)
				}
			}

			/**
			 * The [Quadruple] type with [Float].
			 */
			val VEC4F: DataType by lazy {
				DataType(SHADER_UNIFORM_VEC4) {
					val quad = it as Quadruple<Float, Float, Float, Float>
					allocArrayOf(quad.first, quad.second, quad.third, quad.fourth)
				}
			}

			/**
			 * The [Int] type.
			 */
			val INTEGER: DataType by lazy {
				DataType(SHADER_UNIFORM_INT) {
					val v = alloc<IntVar>()
					v.value = it as Int
					v.ptr
				}
			}

			/**
			 * The [Pair] type with [Int].
			 */
			val VEC2I: DataType by lazy {
				DataType(SHADER_UNIFORM_IVEC2) {
					val pair = it as Pair<Int, Int>
					allocArrayOf(pair.first, pair.second)
				}
			}

			/**
			 * The [Triple] type with [Int].
			 */
			val VEC3I: DataType by lazy {
				DataType(SHADER_UNIFORM_IVEC3) {
					val triple = it as Triple<Int, Int, Int>
					allocArrayOf(triple.first, triple.second, triple.third)
				}
			}

			/**
			 * The [Quadruple] type with [Int].
			 */
			val VEC4I: DataType by lazy {
				DataType(SHADER_UNIFORM_IVEC4) {
					val quad = it as Quadruple<Int, Int, Int, Int>
					allocArrayOf(quad.first, quad.second, quad.third, quad.fourth)
				}
			}

			/**
			 * The [UInt] type.
			 */
			val UNSIGNED_INTEGER: DataType by lazy {
				DataType(SHADER_UNIFORM_UINT) {
					val v = alloc<UIntVar>()
					v.value = it as UInt
					v.ptr
				}
			}

			/**
			 * The [Pair] type with [UInt].
			 */
			val VEC2U: DataType by lazy {
				DataType(SHADER_UNIFORM_UIVEC2) {
					val pair = it as Pair<UInt, UInt>
					allocArrayOf(pair.first, pair.second)
				}
			}

			/**
			 * The [Triple] type with [UInt].
			 */
			val VEC3U: DataType by lazy {
				DataType(SHADER_UNIFORM_UIVEC3) {
					val triple = it as Triple<UInt, UInt, UInt>
					allocArrayOf(triple.first, triple.second, triple.third)
				}
			}

			/**
			 * The [Quadruple] type with [UInt].
			 */
			val VEC4U: DataType by lazy {
				DataType(SHADER_UNIFORM_UIVEC4) {
					val quad = it as Quadruple<UInt, UInt, UInt, UInt>
					allocArrayOf(quad.first, quad.second, quad.third, quad.fourth)
				}
			}

			/**
			 * The `sampler2d` type, used for 2D textures.
			 *
			 * This is represented as a [UInt] in raylib and corresponds to
			 * the texture unit index. You can pass the value of [Texture2D.id]
			 * from a [Texture2D] to this uniform type.
			 */
			val SAMPLER2D: DataType by lazy {
				DataType(SHADER_UNIFORM_SAMPLER2D) {
					val v = alloc<UIntVar>()
					v.value = it as UInt
					v.ptr
				}
			}
		}
	}

	/**
	 * Common shader uniform variable locations.
	 * @property shaderName The standard name of the uniform variable in shaders.
	 * This is not guarenteed in custom shaders.
	 */
	class UniformLocation private constructor(internal val value: UInt, val shaderName: String) {
		companion object {
			/**
			 * Vertex position attribute location.
			 *
			 * Used to pass vertex position data to the shader.
			 */
			val VERTEX_POSITION: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_POSITION, RL_DEFAULT_SHADER_ATTRIB_NAME_POSITION)
			}

			/**
			 * Vertex texture coordinates attribute location.
			 *
			 * Used for mapping textures onto 3D models.
			 */
			val VERTEX_TEXCOORD1: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_TEXCOORD01, RL_DEFAULT_SHADER_ATTRIB_NAME_TEXCOORD)
			}


			/**
			 * Vertex texture coordinates 2 attribute location.
			 *
			 * Used for models with multiple texture coordinates, such as lightmaps.
			 */
			val VERTEX_TEXCOORD2: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_TEXCOORD02, RL_DEFAULT_SHADER_ATTRIB_NAME_TEXCOORD2)
			}

			/**
			 * Vertex normal attribute location.
			 *
			 * Used for lighting calculations to determine how light interacts with the surface.
			 */
			val VERTEX_NORMAL: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_NORMAL, RL_DEFAULT_SHADER_ATTRIB_NAME_NORMAL)
			}

			/**
			 * Vertex tangent attribute location.
			 *
			 * Used for advanced lighting calculations, such as normal mapping.
			 */
			val VERTEX_TANGENT: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_TANGENT, RL_DEFAULT_SHADER_ATTRIB_NAME_TANGENT)
			}

			/**
			 * Vertex color attribute location.
			 *
			 * Used to pass per-vertex color data to the shader.
			 */
			val VERTEX_COLOR: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_COLOR, RL_DEFAULT_SHADER_ATTRIB_NAME_COLOR)
			}

			/**
			 * MVP matrix (Model-View-Projection) uniform location.
			 *
			 * Used to transform vertices from model space to clip space.
			 */
			val MATRIX_MVP: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MATRIX_MVP, RL_DEFAULT_SHADER_UNIFORM_NAME_MVP)
			}


			/**
			 * View matrix uniform location.
			 *
			 * Used to transform vertices from world space to view space.
			 */
			val MATRIX_VIEW: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MATRIX_VIEW, RL_DEFAULT_SHADER_UNIFORM_NAME_VIEW)
			}

			/**
			 * Projection matrix uniform location.
			 *
			 * Used to transform vertices from world space to clip space.
			 */
			val MATRIX_PROJECTION: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MATRIX_PROJECTION, RL_DEFAULT_SHADER_UNIFORM_NAME_PROJECTION)
			}

			/**
			 * Model matrix uniform location.
			 *
			 * Used to transform vertices from model space to world space.
			 */
			val MATRIX_MODEL: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MATRIX_MODEL, RL_DEFAULT_SHADER_UNIFORM_NAME_MODEL)
			}

			/**
			 * Normal matrix uniform location.
			 *
			 * Used to transform normals for correct lighting calculations.
			 */
			val MATRIX_NORMAL: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MATRIX_NORMAL, RL_DEFAULT_SHADER_UNIFORM_NAME_NORMAL)
			}

			/**
			 * View-Projection matrix uniform location.
			 *
			 * Used to transform vertices from world space to clip space.
			 */
			val VECTOR_VIEW: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VECTOR_VIEW, RL_DEFAULT_SHADER_UNIFORM_NAME_VIEW)
			}

			/**
			 * Diffuse (base) color uniform location.
			 *
			 * Used in lighting calculations for basic surface color.
			 */
			val COLOR_DIFFUSE: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_COLOR_DIFFUSE, RL_DEFAULT_SHADER_UNIFORM_NAME_COLOR)
			}

			/**
			 * Specular color uniform location.
			 *
			 * Used in lighting calculations for shiny surfaces.
			 */
			val COLOR_SPECULAR: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_COLOR_SPECULAR, "specularColor")
			}

			/**
			 * Ambient color uniform location.
			 *
			 * Used to simulate indirect lighting in a scene.
			 */
			val COLOR_AMBIENT: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_COLOR_AMBIENT, "ambientColor")
			}

			/**
			 * Albedo map uniform location.
			 *
			 * Used to define the base color texture of a material.
			 */
			val MAP_ALBEDO: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_ALBEDO, "albedoMap")
			}

			/**
			 * Metalness map uniform location.
			 *
			 * Used to define the metallic properties of a material.
			 */
			val MAP_METALNESS: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_METALNESS, "metalnessMap")
			}

			/**
			 * Normal map uniform location.
			 *
			 * Used to define surface normals for lighting calculations.
			 */
			val MAP_NORMAL: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_NORMAL, "mraMap")
			}

			/**
			 * Roughness map uniform location.
			 *
			 * Used to define the roughness properties of a material.
			 */
			val MAP_ROUGHNESS: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_ROUGHNESS, "roughnessMap")
			}

			/**
			 * Occlusion map uniform location.
			 *
			 * Used to define ambient occlusion properties of a material.
			 */
			val MAP_OCCLUSION: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_OCCLUSION, "occlusionMap")
			}

			/**
			 * Emission map uniform location.
			 *
			 * Used to define self-illumination properties of a material.
			 */
			val MAP_EMISSION: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_EMISSION, "emissionMap")
			}

			/**
			 * Height map uniform location.
			 *
			 * Used to define height information for parallax mapping.
			 */
			val MAP_HEIGHT: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_HEIGHT, "heightMap")
			}

			/**
			 * Cubic map uniform location.
			 *
			 * Used for environment mapping with cube maps.
			 */
			val MAP_CUBIC: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_CUBEMAP, "cubicMap")
			}

			/**
			 * Irradiance map uniform location.
			 *
			 * Used for image-based lighting with irradiance maps.
			 */
			val MAP_IRRADIANCE: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_IRRADIANCE, "irradianceMap")
			}

			/**
			 * Prefilter map uniform location.
			 *
			 * Used for image-based lighting with prefiltered environment maps.
			 */
			val MAP_PREFILTER: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_PREFILTER, "prefilterMap")
			}

			/**
			 * BRDF lookup table uniform location.
			 *
			 * Used for physically based rendering (PBR) calculations.
			 */
			val MAP_BRDF: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_MAP_BRDF, "brdfMap")
			}

			/**
			 * Vertex bone IDs attribute location.
			 *
			 * Used for skeletal animation to define which bones affect each vertex.
			 */
			val VERTEX_BONE_IDS: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_BONEIDS, "boneIds")
			}

			/**
			 * Vertex bone weights attribute location.
			 *
			 * Used for skeletal animation to define influence of bones on vertices.
			 */
			val VERTEX_BONE_WEIGHTS: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_BONEWEIGHTS, "boneWeights")
			}

			/**
			 * Bone matrices uniform location.
			 *
			 * Used for skeletal animation to pass bone transformation matrices to the shader.
			 */
			val BONES_MATRICES: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_BONE_MATRICES, "bonesTransform")
			}

			/**
			 * Vertex instance ID attribute location.
			 *
			 * Used for instanced rendering to differentiate between instances.
			 */
			val VERTEX_INSTANCE_ID: UniformLocation by lazy {
				UniformLocation(SHADER_LOC_VERTEX_INSTANCE_TX, "instanceId")
			}
		}
	}

	// shader uniform variable utilities

	/**
	 * Gets the location of a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @return The location of the uniform variable, or -1 if not found.
	 */
	fun getLocation(uniformName: String): Int {
		return GetShaderLocation(raw, uniformName)
	}

	/**
	 * Sets the location of a shader uniform variable at the given index.
	 * @param index The index of the uniform variable.
	 * @param location The location to set.
	 */
	fun setLocation(index: Int, location: Int) {
		raw.useContents {
			locs?.set(index, location)
		}
	}

	/**
	 * Sets the location of a shader uniform variable.
	 * @param uniformLocation The [UniformLocation] of the uniform variable.
	 * @param location The location to set.
	 */
	fun setLocation(uniformLocation: UniformLocation, location: Int) {
		setLocation(uniformLocation.value.toInt(), location)
	}

	/**
	 * Sets the location of a shader uniform variable by name.
	 * @param index The index of the uniform variable.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 */
	fun setLocation(index: Int, uniformName: String) {
		val location = getLocation(uniformName)
		setLocation(index, location)
	}

	/**
	 * Sets the location of a shader uniform variable by name.
	 * @param uniformLocation The [UniformLocation] of the uniform variable.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 */
	fun setLocation(uniformLocation: UniformLocation, uniformName: String) {
		val location = getLocation(uniformName)
		setLocation(uniformLocation, location)
	}

	/**
	 * Sets the locations of multiple shader uniform variables by name.
	 * @param uniformLocations A map of [UniformLocation]s to their corresponding uniform names.
	 */
	fun setLocations(uniformLocations: Map<UniformLocation, String>) {
		for ((uniformLocation, uniformName) in uniformLocations) {
			setLocation(uniformLocation, uniformName)
		}
	}

	/**
	 * Gets the location of a shader uniform variable at its default name.
	 * @param uniformLocation The [UniformLocation] of the uniform variable.
	 * @return The location of the uniform variable, or -1 if not found.
	 */
	fun getDefaultLocation(uniformLocation: UniformLocation): Int {
		return getLocation(uniformLocation.shaderName)
	}

	/**
	 * Sets the location of a shader uniform variable to its default name.
	 * @param uniformLocation The [UniformLocation] of the uniform variable.
	 */
	fun setDefaultLocation(uniformLocation: UniformLocation) {
		val location = getDefaultLocation(uniformLocation)
		if (location != -1)
			setLocation(uniformLocation.value.toInt(), uniformLocation.shaderName)
	}

	/**
	 * Sets the locations of multiple shader uniform variables to their default names.
	 * @param uniformLocations The [UniformLocation]s of the uniform variables.
	 */
	fun setDefaultLocations(vararg uniformLocations: UniformLocation) {
		for (uniformLocation in uniformLocations) {
			setDefaultLocation(uniformLocation)
		}
	}

	/**
	 * Sets the locations of multiple shader uniform variables to their default names.
	 * @param uniformLocations The [UniformLocation]s of the uniform variables.
	 */
	fun setDefaultLocations(uniformLocations: Iterable<UniformLocation>) {
		for (uniformLocation in uniformLocations) {
			setDefaultLocation(uniformLocation)
		}
	}

	/**
	 * Gets the location of a shader uniform variable at the given index.
	 * @param index The index of the uniform variable.
	 * @return The location of the uniform variable, or -1 if not found.
	 */
	fun getLocationAt(index: Int): Int {
		return raw.useContents {
			locs?.get(index) ?: -1
		}
	}

	/**
	 * Gets the location of a shader attribute variable by name.
	 * @param attributeName The name of the attribute variable.
	 * You can find the attribute names in the shader code.
	 * @return The location of the attribute variable, or -1 if not found.
	 */
	fun getAttributeLocation(attributeName: String): Int {
		return GetShaderLocationAttrib(raw, attributeName)
	}

	/**
	 * Checks if a shader has a specific attribute variable.
	 * @param attributeName The name of the attribute variable.
	 * You can find the attribute names in the shader code.
	 * @return `true` if the attribute variable exists, `false` otherwise.
	 */
	fun hasAttribute(attributeName: String): Boolean {
		return getAttributeLocation(attributeName) != -1
	}

	// shader uniform variable values

	/**
	 * Checks if a shader has a specific uniform variable.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @return `true` if the uniform variable exists, `false` otherwise.
	 */
	fun hasValue(uniformName: String): Boolean {
		return getLocation(uniformName) != -1
	}

	/**
	 * Removes a value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 */
	fun removeValue(location: Int) {
		SetShaderValue(raw, location, null, 0)
	}
	/**
	 * Removes a value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 */
	fun removeValue(uniformName: String) {
		removeValue(getLocation(uniformName))
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
		SetShaderValue(raw, location, rawValue, type.value.toInt())
	}
	/**
	 * Sets a value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The value to set. Supported types are from the [DataType] enum.
	 * @param type The type of the uniform variable.
	 */
	fun setValue(uniformName: String, value: Any, type: DataType) {
		setValue(getLocation(uniformName), value, type)
	}

	/**
	 * Sets a matrix value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The matrix value to set.
	 */
	fun setValue(location: Int, value: Matrix4) {
		SetShaderValueMatrix(raw, location, value.raw())
	}
	/**
	 * Sets a matrix value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The matrix value to set.
	 */
	fun setValue(uniformName: String, value: Matrix4) {
		setValue(getLocation(uniformName), value)
	}

	/**
	 * Sets a texture value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The texture value to set.
	 */
	fun setValue(location: Int, value: Texture2D) {
		SetShaderValueTexture(raw, location, value.raw())
	}
	/**
	 * Sets a texture value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The texture value to set.
	 */
	fun setValue(uniformName: String, value: Texture2D) {
		setValue(getLocation(uniformName), value)
	}

	/// common types

	/**
	 * Sets a boolean value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The boolean value to set.
	 */
	fun setValue(location: Int, value: Boolean) {
		val intValue = if (value) 1 else 0
		setValue(location, intValue, DataType.INTEGER)
	}
	/**
	 * Sets a boolean value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The boolean value to set.
	 */
	fun setValue(uniformName: String, value: Boolean) {
		val intValue = if (value) 1 else 0
		setValue(uniformName, intValue, DataType.INTEGER)
	}

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
	 * Sets a double value for a shader uniform variable. Performs conversion to float.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The double value to set.
	 */
	fun setValue(location: Int, value: Double) {
		setValue(location, value.toFloat(), DataType.FLOAT)
	}

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

	/**
	 * Sets an int array value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The int array value to set. Supported sizes are 2, 3, and 4.
	 */
	fun setValue(location: Int, value: IntArray) {
		when (value.size) {
			2 -> setValue(location, Pair(value[0], value[1]), DataType.VEC2I)
			3 -> setValue(location, Triple(value[0], value[1], value[2]), DataType.VEC3I)
			4 -> setValue(location, Quadruple(value[0], value[1], value[2], value[3]), DataType.VEC4I)
			else -> error("Unsupported int array size: ${value.size}")
		}
	}
	/**
	 * Sets an int array value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The int array value to set. Supported sizes are 2, 3, and 4.
	 */
	fun setValue(uniformName: String, value: IntArray) {
		return setValue(getLocation(uniformName), value)
	}

	/**
	 * Sets a float array value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The float array value to set. Supported sizes are 2, 3, and 4.
	 */
	fun setValue(location: Int, value: FloatArray) {
		when (value.size) {
			2 -> setValue(location, Pair(value[0], value[1]), DataType.VEC2F)
			3 -> setValue(location, Triple(value[0], value[1], value[2]), DataType.VEC3F)
			4 -> setValue(location, Quadruple(value[0], value[1], value[2], value[3]), DataType.VEC4F)
			else -> error("Unsupported float array size: ${value.size}")
		}
	}
	/**
	 * Sets a float array value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The float array value to set. Supported sizes are 2, 3, and 4.
	 */
	fun setValue(uniformName: String, value: FloatArray) {
		return setValue(getLocation(uniformName), value)
	}

	/// other

	/**
	 * Sets a color value for a shader uniform variable.
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The color value to set.
	 */
	fun setValue(location: Int, value: Color) {
		setValue(location, Quadruple(
			value.r.toFloat()/255F,
			value.g.toFloat()/255F,
			value.b.toFloat()/255F,
			value.a.toFloat()/255F
		), DataType.VEC4F)
	}
	/**
	 * Sets a color value for a shader uniform variable by name.
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The color value to set.
	 */
	fun setValue(uniformName: String, value: Color) {
		val location = getLocation(uniformName)
		setValue(location, value)
	}

	/// any type

	/**
	 * Sets a value for a shader uniform variable.
	 * The type of the value is inferred at runtime.
	 * Supported types are: [Boolean], [Float], [Double], [Int], [UInt],
	 * [Pair<Float, Float>], [Triple<Float, Float, Float>],
	 * [Quadruple<Float, Float, Float, Float>], [Matrix4], [Texture2D], and [Color].
	 * @param location The location of the uniform variable.
	 * You can get the location using [getLocation].
	 * @param value The value to set.
	 */
	fun setValue(location: Int, value: Any?) {
		if (value == null) return removeValue(location)

		when (value) {
			is Boolean -> setValue(location, value)
			is Float -> setValue(location, value)
			is Double -> setValue(location, value)
			is Int -> setValue(location, value)
			is UInt -> setValue(location, value)
			is FloatArray -> setValue(location, value)
			is IntArray -> setValue(location, value)
			is Pair<*, *> -> {
				if (value.first is Float && value.second is Float) {
					@Suppress("UNCHECKED_CAST")
					setValue(location, value as Pair<Float, Float>)
				} else {
					error("Unsupported Pair type for shader uniform value")
				}
			}
			is Triple<*, *, *> -> {
				if (value.first is Float && value.second is Float && value.third is Float) {
					@Suppress("UNCHECKED_CAST")
					setValue(location, value as Triple<Float, Float, Float>)
				} else {
					error("Unsupported Triple type for shader uniform value")
				}
			}
			is Quadruple<*, *, *, *> -> {
				if (value.first is Float && value.second is Float && value.third is Float && value.fourth is Float) {
					@Suppress("UNCHECKED_CAST")
					setValue(location, value as Quadruple<Float, Float, Float, Float>)
				} else {
					error("Unsupported Quadruple type for shader uniform value")
				}
			}
			is Matrix4 -> setValue(location, value)
			is Texture2D -> setValue(location, value)
			is Color -> setValue(location, value)
			else -> error("Unsupported type for shader uniform value: ${value::class}")
		}
	}

	/**
	 * Sets a value for a shader uniform variable by name.
	 * The type of the value is inferred at runtime.
	 * Supported types are: [Boolean], [Float], [Double], [Int], [UInt],
	 * [Pair<Float, Float>], [Triple<Float, Float, Float>],
	 * [Quadruple<Float, Float, Float, Float>], [Matrix4], [Texture2D], and [Color].
	 * @param uniformName The name of the uniform variable.
	 * You can find the uniform names in the shader code.
	 * @param value The value to set.
	 */
	fun setValue(uniformName: String, value: Any) {
		setValue(getLocation(uniformName), value)
	}

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
			val rawShader = LoadShader(vsFileName.inAppDir(), fsFileName.inAppDir())
			return Shader(rawShader)
		}

		/**
		 * Loads a shader from the given single file containing both vertex and fragment shaders.
		 * @param fileName The shader file name.
		 * @return The loaded [Shader].
		 */
		fun load(fileName: String): Shader {
			val rawShader = LoadShader(null, fileName.inAppDir())
			return Shader(rawShader)
		}

		/**
		 * Loads a shader from the given vertex and fragment shader code in memory.
		 * @param vsCode The vertex shader code.
		 * @param fsCode The fragment shader code.
		 * @return The loaded [Shader].
		 */
		fun loadInMemory(vsCode: String, fsCode: String): Shader {
			val rawShader = LoadShaderFromMemory(vsCode, fsCode)
			return Shader(rawShader)
		}
	}

	/**
	 * Unloads the shader from VRAM.
	 *
	 * This function should be called when the shader is no longer needed
	 * to free up resources.
	 */
	fun unload() {
		UnloadShader(raw)
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + locs.hashCode()
		return result
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Shader) return false

		return id == other.id && locs == other.locs
	}
}

/**
 * Starts using the given shader for 3D drawing on the canvas.
 * @param shader The shader to start using.
 */
fun Canvas.startShader(shader: Shader) {
	BeginShaderMode(shader.raw)
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
	value class Texture private constructor(internal val value: UInt) {

		companion object {
			/**
			 * Albedo (diffuse) texture map.
			 *
			 * Used to define the base color of the material. For example, the color of a wall,
			 * floor, or any other surface.
			 */
			val ALBEDO = Texture(0U)

			/**
			 * Metalness texture map.
			 *
			 * Used to define the metallic properties of the material. For example, whether
			 * the surface is metallic or non-metallic.
			 */
			val METALNESS = Texture(1U)

			/**
			 * Normal texture map.
			 *
			 * Used to define the surface normals for lighting calculations. For example,
			 * simulating bumps, dents, and other surface details.
			 */
			val NORMAL = Texture(2U)

			/**
			 * Roughness texture map.
			 *
			 * Used to define the roughness properties of the material. For example, how
			 * shiny or matte the surface appears.
			 */
			val ROUGHNESS = Texture(3U)

			/**
			 * Ambient Occlusion (AO) texture map.
			 *
			 * Used to define the ambient occlusion properties of the material. For example,
			 * shadows in crevices and corners where light is occluded.
			 */
			val AO = Texture(4U)

			/**
			 * Emission texture map.
			 *
			 * Used to define the emissive properties of the material. For example,
			 * glowing surfaces, light sources, etc.
			 */
			val EMISSION = Texture(5U)

			/**
			 * Height (displacement) texture map.
			 *
			 * Used to define the height or displacement properties of the material.
			 * This map can be used to create parallax effects or simulate surface
			 * details.
			 */
			val HEIGHT = Texture(6U)

			/**
			 * Cubemap texture map.
			 *
			 * Used for environment mapping, reflections, and skyboxes. For example,
			 * simulating reflective surfaces like water or shiny metals.
			 */
			val CUBEMAP = Texture(7U)

			/**
			 * Irradiance texture map.
			 *
			 * Used for image-based lighting (IBL) to simulate diffuse lighting from
			 * the environment.
			 */
			val IRRADIANCE = Texture(8U)

			/**
			 * Prefilter texture map.
			 *
			 * Used for image-based lighting (IBL) to simulate specular reflections
			 * from the environment.
			 */
			val PREFILTER = Texture(9U)

			/**
			 * BRDF lookup texture map.
			 *
			 * Used for physically based rendering (PBR) to simulate how light
			 * interacts with surfaces. For example, simulating realistic reflections
			 * and highlights.
			 */
			val BRDF = Texture(10U)
		}
	}
}

/**
 * Represents a raylib material.
 *
 * Materials define the appearance of 3D models by combining shaders and textures.
 */
class Material(internal val raw: CPointer<raylib.internal.Material>) {

	/**
	 * The shader associated with the material.
	 */
	var shader: Shader
		get() = Shader(raw.pointed.shader.readValue())
		set(value) {
			val shader = raw.pointed.shader
			value.raw.useContents {
				shader.id = this.id
				for (i in 0 until RL_MAX_SHADER_LOCATIONS) {
					shader.locs?.set(i, locs?.get(i) ?: -1)
				}
			}
		}

	/**
	 * The maps of the material.
	 */
	var maps: List<MaterialMap>
		get() {
			val list = mutableListOf<MaterialMap>()
			raw.pointed.apply {
				for (i in 0 until MAX_MATERIAL_MAPS) {
					val raw = maps?.get(i)
					if (raw != null) list.add(MaterialMap(raw))
				}
			}

			return list
		}
		set(value) {
			raw.pointed.apply {
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
	 * This matches the C pattern: `material.maps[index].color = RED`
	 *
	 * @param index The index of the material map
	 * @param color The color to set
	 */
	fun setMapColor(index: Int, color: Color) {
		raw.pointed.apply {
			val map = maps?.get(index)
			if (map != null) {
				map.color.r = color.r
				map.color.g = color.g
				map.color.b = color.b
				map.color.a = color.a
			}
		}
	}

	/**
	 * Sets the color of a material map directly.
	 * This matches the C pattern: `material.maps[mapType].color = RED`
	 *
	 * @param mapType The type of material map (e.g., ALBEDO/DIFFUSE)
	 * @param color The color to set
	 */
	fun setMapColor(mapType: Texture, color: Color) {
		setMapColor(mapType.value.toInt(), color)
	}

	/**
	 * Whether the material is valid.
	 */
	val isValid: Boolean
		get() = IsMaterialValid(raw.pointed.readValue())

	/**
	 * Sets a texture map for the material.
	 * @param index The index of the material map.
	 * @param texture The texture to set for the material map.
	 */
	fun setTextureMap(index: Int, texture: Texture2D) = memScoped {
		SetMaterialTexture(raw, index, texture.raw())
	}

	/**
	 * Sets a texture map for the material.
	 * @param mapType The type of the material map.
	 * @param texture The texture to set for the material map.
	 */
	fun setTextureMap(mapType: Texture, texture: Texture2D) {
		setTextureMap(mapType.value.toInt(), texture)
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
		fun default(shader: Shader? = null, apply: Material.() -> Unit = {}): Material {
			val default = LoadMaterialDefault()
			val ptr = nativeHeap.alloc<raylib.internal.Material>()
			default.useContents {
				ptr.shader.id = this.shader.id
				ptr.shader.locs = this.shader.locs
				ptr.maps = maps
				ptr.params[0] = params[0]
				ptr.params[1] = params[1]
				ptr.params[2] = params[2]
				ptr.params[3] = params[3]
			}

			val material = Material(ptr.ptr)
			if (shader != null) material.shader = shader
			material.apply()
			return material
		}

		/**
		 * Loads materials from a file.
		 * @param fileName The file name to load the materials from.
		 * @return A list of loaded materials.
		 */
		fun load(fileName: String): List<Material> = memScoped {
			val count = alloc<IntVar>()
			val materials = LoadMaterials(fileName.inAppDir(), count.ptr)

			List(count.value) { i ->
				val material = materials?.get(i)?.ptr ?: default().raw
				Material(material)
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
class Mesh(internal val raw: CValue<raylib.internal.Mesh>) {

	/**
	 * The number of vertices on the mesh.
	 */
	val vertexCount: Int
		get() = raw.useContents { vertexCount }

	/**
	 * The number of triangle objects on the mesh.
	 */
	val triangleCount: Int
		get() = raw.useContents { triangleCount }

	private var verticesCache: List<Vertex>? = null

	/**
	 * The vertices of the mesh.
	 */
	var vertices: List<Vertex>
		get() {
			if (verticesCache != null) return verticesCache!!

			val count = vertexCount
			val out = ArrayList<Vertex>(count)

			raw.useContents {
				for (i in 0 until count) {
					val vi = i * 3
					val ti = i * 2
					val tai = i * 4
					val ci = i * 4

					out.add(
						Vertex(
							vertices?.get(vi) ?: 0f,
							vertices?.get(vi + 1) ?: 0f,
							vertices?.get(vi + 2) ?: 0f,
							texcoords?.get(ti) ?: 0f,
							texcoords?.get(ti + 1) ?: 0f,
							texcoords2?.get(ti) ?: 0f,
							texcoords2?.get(ti + 1) ?: 0f,
							normals?.get(vi) ?: 0f,
							normals?.get(vi + 1) ?: 0f,
							normals?.get(vi + 2) ?: 0f,
							tangents?.get(tai) ?: 0f,
							tangents?.get(tai + 1) ?: 0f,
							tangents?.get(tai + 2) ?: 0f,
							tangents?.get(tai + 3) ?: 0f,
							Color(
								colors?.get(ci) ?: 0u,
								colors?.get(ci + 1) ?: 0u,
								colors?.get(ci + 2) ?: 0u,
								colors?.get(ci + 3) ?: 255u
							)
						)
					)
				}
			}

			verticesCache = out
			return out
		}
		set(value) {
			verticesCache = value

			raw.useContents {
				val count = value.size

				// free old arrays before allocating new ones
				vertices?.let { nativeHeap.free(it) }
				texcoords?.let { nativeHeap.free(it) }
				texcoords2?.let { nativeHeap.free(it) }
				normals?.let { nativeHeap.free(it) }
				tangents?.let { nativeHeap.free(it) }
				colors?.let { nativeHeap.free(it) }

				vertexCount = count
				triangleCount = count / 3

				// allocate new arrays
				vertices = nativeHeap.allocArray(count * 3)
				texcoords = nativeHeap.allocArray(count * 2)
				texcoords2 = nativeHeap.allocArray(count * 2)
				normals = nativeHeap.allocArray(count * 3)
				tangents = nativeHeap.allocArray(count * 4)
				colors = nativeHeap.allocArray(count * 4)

				val verts = vertices!!
				val texc = texcoords!!
				val texc2 = texcoords2!!
				val norms = normals!!
				val tangs = tangents!!
				val cols = colors!!

				value.forEachIndexed { i, v ->
					val vi = i * 3
					val ti = i * 2
					val tai = i * 4
					val ci = i * 4

					// pos
					verts[vi] = v.x
					verts[vi + 1] = v.y
					verts[vi + 2] = v.z

					// texcoord1
					texc[ti] = v.tx
					texc[ti + 1] = v.ty

					// texcoord2
					texc2[ti] = v.tx2
					texc2[ti + 1] = v.ty2

					// normal
					norms[vi] = v.nx
					norms[vi + 1] = v.ny
					norms[vi + 2] = v.nz

					// tangent
					tangs[tai] = v.tax
					tangs[tai + 1] = v.tay
					tangs[tai + 2] = v.taz
					tangs[tai + 3] = v.taw

					// color
					cols[ci] = v.color.r
					cols[ci + 1] = v.color.g
					cols[ci + 2] = v.color.b
					cols[ci + 3] = v.color.a
				}
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
			raw.useContents {
				for (i in 0 until vertexCount * 3 step 3) {
					val x = animVertices?.get(i) ?: 0F
					val y = animVertices?.get(i + 1) ?: 0F
					val z = animVertices?.get(i + 2) ?: 0F
					list.add(Triple(x, y, z))
				}
			}

			return list
		}
		set(value) {
			raw.useContents {
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
			raw.useContents {
				for (i in 0 until vertexCount * 3 step 3) {
					val x = animNormals?.get(i) ?: 0F
					val y = animNormals?.get(i + 1) ?: 0F
					val z = animNormals?.get(i + 2) ?: 0F
					list.add(Triple(x, y, z))
				}
			}

			return list
		}
		set(value) {
			raw.useContents {
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
		get() = raw.useContents { boneCount }

	/**
	 * The bone IDs affecting each vertex of the mesh.
	 *
	 * There are up to 4 bone IDs per vertex. This list will have a size of `vertexCount * 4`.
	 */
	val boneWeights: List<Float>
		get() {
			val list = mutableListOf<Float>()
			raw.useContents {
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
		return raw.useContents { boneIds?.get(vertexIndex * 4 + boneIndex)?.toInt() ?: 0 }
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
			raw.useContents {
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
		return raw.useContents { boneMatrices?.get(boneIndex)?.let { Matrix4(it) } }
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

	/**
	 * The bounding box of the mesh.
	 */
	val boundingBox: BoundingBox
		get() = GetMeshBoundingBox(raw).useContents { BoundingBox(this) }

	/**
	 * Uploads the mesh data to the GPU.
	 *
	 * This method is usually called automatically, especially by the generation
	 * functions in the [Mesh] companion object. However, if you manually modify
	 * the mesh data (e.g., vertices, normals, texture coordinates), you should call
	 * this method to ensure that the changes are reflected in the GPU memory.
	 *
	 * @param dynamic Whether the mesh is dynamic (i.e., will be updated frequently).
	 */
	fun upload(dynamic: Boolean = true) = memScoped {
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

			raw.useContents {
				for (i in 0 until vertexCount * 4 step 4) {
					val x = tangents?.get(i) ?: 0F
					val y = tangents?.get(i + 1) ?: 0F
					val z = tangents?.get(i + 2) ?: 0F
					val w = tangents?.get(i + 3) ?: 0F
					list.add(Quadruple(x, y, z, w))
				}
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
	fun createTangents() = memScoped {
		GenMeshTangents(raw.ptr)
	}

	/**
	 * Exports the mesh data to a file.
	 *
	 * As of raylib 4.0, this function supports only the `.obj` file format.
	 * @param fileName The name of the file to export to.
	 */
	fun export(fileName: String) {
		ExportMesh(raw, fileName.inAppDir())
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
		ExportMeshAsCode(raw, fileName.inAppDir())
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

	/**
	 * Applies a transformation matrix to the mesh.
	 *
	 * This matrix can include translation, rotation, and scaling transformations.
	 * The transformation is applied to both the vertices and normals of the mesh.
	 * Note that normals are transformed without translation to maintain their direction.
	 *
	 * @param matrix The transformation matrix to apply.
	 */
	fun transform(matrix: Matrix4) {
		raw.useContents {
			val v = vertices ?: return
			val n = normals

			for (i in 0 until vertexCount) {
				val o = i * 3

				val x = v[o]
				val y = v[o + 1]
				val z = v[o + 2]

				v[o] = matrix.m0 * x + matrix.m4 * y + matrix.m8 * z + matrix.m12
				v[o + 1] = matrix.m1 * x + matrix.m5 * y + matrix.m9 * z + matrix.m13
				v[o + 2] = matrix.m2 * x + matrix.m6 * y + matrix.m10 * z + matrix.m14

				if (n != null) {
					val nx = n[o]
					val ny = n[o + 1]
					val nz = n[o + 2]

					n[o] = matrix.m0 * nx + matrix.m4 * ny + matrix.m8 * nz
					n[o + 1] = matrix.m1 * nx + matrix.m5 * ny + matrix.m9 * nz
					n[o + 2] = matrix.m2 * nx + matrix.m6 * ny + matrix.m10 * nz
				}
			}
		}
	}

	/**
	 * Generates various standard 3D meshes.
	 *
	 * When generating a mesh using these functions, the mesh data is automatically
	 * uploaded to the GPU. Therefore, there is no need to call [upload] manually.
	 * As a result, the generated meshes are ready for rendering immediately after creation.
	 * However, you **should not** call this method in the [Window.lifecycle] block,
	 * as it will **repeatedly generate and upload the mesh on every frame**, leading to
	 * performance issues. Instead, generate the mesh once outside the lifecycle block
	 * and reuse it as needed.
	 */
	companion object {

		/**
		 * Generates a polygonal mesh.
		 * @param sides The number of sides of the polygon.
		 * @param radius The radius of the polygon.
		 * @return The generated mesh.
		 */
		fun poly(sides: Int, radius: Float): Mesh {
			val raw = GenMeshPoly(sides, radius)
			return Mesh(raw)
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
			return Mesh(raw)
		}

		/**
		 * Generates a cube mesh.
		 * @param size The size of the cube.
		 * @return The generated mesh.
		 */
		fun cube(size: Float): Mesh {
			val raw = GenMeshCube(size, size, size)
			return Mesh(raw)
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
			return Mesh(raw)
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
		fun sphere(radius: Float, rings: Int, slices: Int = 16): Mesh {
			val raw = GenMeshSphere(radius, rings, slices)
			return Mesh(raw)
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
		fun hemiSphere(radius: Float, rings: Int, slices: Int = 16): Mesh {
			val raw = GenMeshHemiSphere(radius, rings, slices)
			return Mesh(raw)
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
		fun cylinder(radius: Float, height: Float, slices: Int = 16): Mesh {
			val raw = GenMeshCylinder(radius, height, slices)
			return Mesh(raw)
		}

		/**
		 * Generates a cone mesh.
		 * @param radius The radius of the cone base.
		 * @param height The height of the cone.
		 * @param slices The number of slices of the cone.
		 * Increasing the number of slices increases the number of subdivisions around
		 * the main axis, which improves the cone's roundness. Default is 16.
		 * @return The generated mesh.
		 */
		fun cone(radius: Float, height: Float, slices: Int = 16): Mesh {
			val raw = GenMeshCone(radius, height, slices)
			return Mesh(raw)
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
			return Mesh(raw)
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
			return Mesh(raw)
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
			return Mesh(raw)
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
			return Mesh(raw)
		}

		/**
		 * Generates a cubicmap mesh from an image.
		 * @param cubicMap The cubicmap image.
		 * @param cubeSize The size of each cube in the cubicmap.
		 * @return The generated mesh.
		 */
		fun cubicMap(cubicMap: Image, cubeSize: Float): Mesh {
			val raw = GenMeshCubicmap(cubicMap.raw, (cubeSize to cubeSize to cubeSize).toVector3())
			return Mesh(raw)
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
			return Mesh(raw)
		}

		/**
		 * Generates a cubicmap mesh from an image.
		 * @param cubicMap The cubicmap image.
		 * @param cubeSize The size of each cube in the cubicmap.
		 * @return The generated mesh.
		 */
		fun cubicMap(cubicMap: Image, cubeSize: Triple<Float, Float, Float>): Mesh {
			val raw = GenMeshCubicmap(cubicMap.raw, cubeSize.toVector3())
			return Mesh(raw)
		}

		// mesh utility functions

		/**
		 * Concatenates two meshes into a single mesh.
		 * @param a The first mesh.
		 * @param b The second mesh.
		 * @return The concatenated mesh.
		 */
		fun add(a: Mesh, b: Mesh): Mesh {
			val combined = ArrayList<Vertex>(a.vertexCount + b.vertexCount)
			combined.addAll(a.vertices)
			combined.addAll(b.vertices)

			val mesh = Mesh(
				cValue {
					vertexCount = combined.size
					triangleCount = combined.size / 3
				}
			)

			mesh.vertices = combined
			mesh.upload(dynamic = false)

			return mesh
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
	rotX: Float = 0F,
	rotY: Float = 0F,
	rotZ: Float = 0F,
	rotAngle: Float = 0F,
	scaleX: Float = 0F,
	scaleY: Float = 0F,
	scaleZ: Float = 0F
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
	rotX: Float = 0F,
	rotY: Float = 0F,
	rotZ: Float = 0F,
	rotAngle: Float = 0F,
	scaleX: Float = 1F,
	scaleY: Float = 1F,
	scaleZ: Float = 1F
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

	DrawMesh(mesh.raw, material.raw.pointed.readValue(), transform.raw())
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
		DrawMeshInstanced(mesh.raw, material.raw.pointed.readValue(), array, transformations.size)
	}
}

/**
 * Draws a 3D mesh on the canvas with identity transform (at origin).
 * @param mesh The mesh to draw.
 * @param material The material to apply to the mesh.
 */
fun Canvas.drawMesh(mesh: Mesh, material: Material) {
	drawMesh(mesh, material, Matrix4.IDENTITY)
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
class Model(internal val raw: CValue<raylib.internal.Model>) {
	/**
	 * The model transformation matrix.
	 */
	@Suppress("DuplicatedCode")
	var transform: Matrix4
		get() = raw.useContents { Matrix4(transform) }
		set(value) {
			raw.useContents {
				transform.m0 = value.m0
				transform.m1 = value.m1
				transform.m2 = value.m2
				transform.m3 = value.m3
				transform.m4 = value.m4
				transform.m5 = value.m5
				transform.m6 = value.m6
				transform.m7 = value.m7
				transform.m8 = value.m8
				transform.m9 = value.m9
				transform.m10 = value.m10
				transform.m11 = value.m11
				transform.m12 = value.m12
				transform.m13 = value.m13
				transform.m14 = value.m14
				transform.m15 = value.m15
			}
		}

	/**
	 * The number of meshes in the model.
	 */
	val meshCount: Int
		get() = raw.useContents { meshCount }

	/**
	 * The meshes in the model.
	 */
	var meshes: List<Mesh>
		get() {
			val list = mutableListOf<Mesh>()
			raw.useContents {
				for (i in 0 until meshCount) {
					meshes?.get(i)?.let { list.add(Mesh(it.readValue())) }
				}
			}

			return list
		}
		set(value) {
			raw.useContents {
				// free old array if it exists
				meshes?.let { nativeHeap.free(it.rawValue) }

				val array = nativeHeap.allocArray<raylib.internal.Mesh>(value.size) newMesh@{ i ->
					val mesh = value[i].raw

					mesh.useContents {
						this@newMesh.vertices = vertices
						this@newMesh.texcoords = texcoords
						this@newMesh.texcoords2 = texcoords2
						this@newMesh.normals = normals
						this@newMesh.tangents = tangents
						this@newMesh.colors = colors
						this@newMesh.indices = indices
						this@newMesh.animVertices = animVertices
						this@newMesh.animNormals = animNormals
						this@newMesh.boneIds = boneIds
						this@newMesh.boneWeights = boneWeights
						this@newMesh.vertexCount = vertexCount
						this@newMesh.triangleCount = triangleCount
						this@newMesh.vaoId = vaoId
					}
				}

				meshes = array
				meshCount = value.size
			}
		}

	/**
	 * The number of materials in the model.
	 */
	val materialCount: Int
		get() = raw.useContents { materialCount }

	/**
	 * The materials in the model.
	 */
	var materials: List<Material>
		get() {
			val list = mutableListOf<Material>()
			raw.useContents {
				for (i in 0 until materialCount) {
					materials?.get(i)?.let {
						list.add(Material(it.ptr))
					}
				}
			}

			return list
		}
		set(value) {
			raw.useContents {
				// free old array if it exists
				materials?.let { nativeHeap.free(it.rawValue) }

				val array = nativeHeap.allocArray<raylib.internal.Material>(value.size) newMaterial@{ i ->
					val material = value[i].raw

					material.pointed.apply {
						this@newMaterial.shader.id = shader.id
						this@newMaterial.shader.locs = shader.locs
						this@newMaterial.maps = maps
					}
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

			return raw.useContents {
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

		raw.useContents {
			meshMaterial = meshMaterial?.let {
				val newArray = nativeHeap.allocArray<IntVarOf<Int>>(currentMeshes.size) { i ->
					this.value = if (i < currentMaterials.size) i else 0
				}

				nativeHeap.free(it.rawValue)
				newArray
			} ?: nativeHeap.allocArray<IntVarOf<Int>>(currentMeshes.size) { i ->
				this.value = if (i < currentMaterials.size) i else 0
			}
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

	/**
	 * Whether the model is valid.
	 */
	val isValid: Boolean
		get() = IsModelValid(raw)

	internal var boundingBoxCache: BoundingBox? = null

	/**
	 * The bounding box of the model.
	 */
	val boundingBox: BoundingBox
		get() {
			if (boundingBoxCache != null) return boundingBoxCache!!

			boundingBoxCache = GetModelBoundingBox(raw)
				.useContents { BoundingBox(this) }

			return boundingBoxCache!!
		}

	/**
	 * Sets the material for a specific mesh in the model.
	 * @param meshId The ID of the mesh to set the material for.
	 * @param materialId The ID of the material to set.
	 */
	fun setMeshMaterial(meshId: Int, materialId: Int) {
		SetModelMeshMaterial(raw, meshId, materialId)
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
			return Model(raw)
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
			val raw = LoadModelFromMesh(mesh.raw)
			return Model(raw)
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
	DrawModel(model.raw, (x to y to z).toVector3(), scale, tint.raw())
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
	DrawModel(model.raw, position.toVector3(), scale, tint.raw())
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
		model.raw,
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
		model.raw,
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
		model.raw,
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
		model.raw,
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
	DrawModelWires(model.raw, (x to y to z).toVector3(), scale, tint.raw())
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
	DrawModelWires(model.raw, position.toVector3(), scale, tint.raw())
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
		model.raw,
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
		model.raw,
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
