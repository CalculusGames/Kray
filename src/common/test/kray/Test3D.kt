@file:OptIn(ExperimentalForeignApi::class)

package kray

import kotlinx.cinterop.ExperimentalForeignApi
import kray.shaders.LIGHTING_SHADER
import kray.shaders.RAYMARCHING_SHADER
import kray.shaders.RAYTRACING_SHADER
import raylib.*
import raylib.Shader.UniformLocation
import kotlin.test.Test
import kotlin.test.assertFalse

class Test3D {

	@Test
	fun testMesh() {
		if (Window.isHeadless) return
		assertFalse { Window.isHeadless }

		Window.open(800, 600, "Test Mesh")

		val shader = LIGHTING_SHADER
		shader.addLight(10F, 8F, 6F)

		val material = Material.default()
		material.shader = shader

		val camera = Camera3D(
			20f to 30f to 20f,
			0f to 0f to 0f,
			0f to 1f to 0f,
			45f,
			CameraProjection3D.PERSPECTIVE
		)

		val cube = Mesh.cube(5F)
		val cone = Mesh.cone(2F, 5F)

		Window.fps = 60
		Window.lifecycleForFrames(60 * 5) {
			camera.update(CameraMode3D.ORBITAL)

			shader.setValue(shader.getDefaultLocation(UniformLocation.VECTOR_VIEW), camera.position)
			Canvas.draw {
				setBackgroundColor(Color.GRAY)

				camera3D(camera) {
					material.setMapColor(MaterialMap.Texture.ALBEDO, Color.RED)
					drawMesh(cube, material, 0, 0, 0)
					material.setMapColor(MaterialMap.Texture.ALBEDO, Color.BLUE)
					drawMesh(cone, material, 0, 5, 0)
					material.setMapColor(MaterialMap.Texture.ALBEDO, Color.GREEN)
					drawMesh(cube, material, 0, 12, 0)

					grid() // not visible with shader
				}
			}
		}

		material.unload()
		cube.unload()
		cone.unload()
	}

	@Test
	fun testModel() {
		if (Window.isHeadless) return
		assertFalse { Window.isHeadless }

		Window.open(800, 600, "Test Model")

		val camera = Camera3D(
			20f to 30f to 20f,
			0f to 0f to 0f,
			0f to 1f to 0f,
			45f,
			CameraProjection3D.PERSPECTIVE
		)

		val material = Material.default()
		material.setMapColor(MaterialMap.Texture.ALBEDO, Color.RED)

		val mesh = Mesh.cylinder(3F, 5F)
		val model = Model.fromMesh(mesh, material)

		Window.fps = 60
		Window.lifecycleForFrames(60 * 5) {
			camera.update(CameraMode3D.ORBITAL)
			Canvas.draw {
				setBackgroundColor(Color.GRAY)

				camera3D(camera) {
					drawModel(model, 0, 0, 0)
					grid()
				}
			}
		}

		model.unload()
		mesh.unload()
		material.unload()
	}

	@Test
	fun testRaymarching() {
		if (Window.isHeadless) return
		assertFalse { Window.isHeadless }

		Window.open(800, 600, "Test Raymarching Shader")

		val shader = RAYMARCHING_SHADER

		val material = Material.default()
		material.shader = shader

		val camera = Camera3D(
			20f to 30f to 20f,
			0f to 0f to 0f,
			0f to 1f to 0f,
			45f,
			CameraProjection3D.PERSPECTIVE
		)

		val cube = Mesh.cube(5F)
		val knot = Mesh.knot(2F, 0.5F, 16, 16)
		val cone = Mesh.cone(2F, 5F)

		Window.fps = 60
		Window.lifecycleForFrames(60 * 5) {
			camera.update(CameraMode3D.ORBITAL)

			shader.viewEye = camera.position

			Canvas.draw {
				setBackgroundColor(Color.WHITE)

				camera3D(camera) {
					material.setMapColor(MaterialMap.Texture.ALBEDO, Color.RED)
					drawMesh(cube, material, 5, 0, 0)
					material.setMapColor(MaterialMap.Texture.ALBEDO, Color.BLUE)
					drawMesh(knot, material, 0, 0, 0)
					material.setMapColor(MaterialMap.Texture.ALBEDO, Color.GREEN)
					drawMesh(cone, material, -5, 0, 0)

					grid() // not visible with shader
				}
			}
		}

		material.unload()
		cube.unload()
		knot.unload()
		cone.unload()
	}

	@Test
	fun testRaytracing() {
		if (Window.isHeadless) return
		assertFalse { Window.isHeadless }

		Window.open(800, 600, "Test Raytracing")

		val shader = RAYTRACING_SHADER
		shader.ambientOcclusionMapEnabled = true
		shader.roughnessMapEnabled = true
		shader.addPointLight(0F, 5F, 0F, Color.WHITE, 10F)

		val m1 = Material.default(shader) {
			setMapColor(MaterialMap.Texture.ALBEDO, Color.RED)
			setMapColor(MaterialMap.Texture.AO, Color.RED)
			setMapColor(MaterialMap.Texture.ROUGHNESS , Color.BLACK)
		}

		val m2 = Material.default(shader) {
			setMapColor(MaterialMap.Texture.ALBEDO, Color.BLUE)
			setMapColor(MaterialMap.Texture.AO, Color.BLUE)
			setMapColor(MaterialMap.Texture.ROUGHNESS , Color.DARK_GRAY)
		}

		val m3 = Material.default(shader) {
			setMapColor(MaterialMap.Texture.ALBEDO, Color.GREEN)
			setMapColor(MaterialMap.Texture.AO, Color.GREEN)
			setMapColor(MaterialMap.Texture.ROUGHNESS , Color.LIGHT_GRAY)
		}

		val m4 = Material.default(shader) {
			setMapColor(MaterialMap.Texture.ALBEDO, Color.YELLOW)
			setMapColor(MaterialMap.Texture.AO, Color.YELLOW)
			setMapColor(MaterialMap.Texture.ROUGHNESS , Color.GRAY)
		}

		val m5 = Material.default(shader) {
			setMapColor(MaterialMap.Texture.ALBEDO, Color.CYAN)
			setMapColor(MaterialMap.Texture.AO, Color.CYAN)
			setMapColor(MaterialMap.Texture.ROUGHNESS , Color.WHITE)
		}

		val sphere = Mesh.sphere(2F, 32, 32)
		val cube = Mesh.cube(3F)
		val cone = Mesh.cone(2F, 5F)
		val prism = Mesh.rectPrism(2F, 3F, 4F)
		val torus = Mesh.torus(2.5F, 3F, 16, 16)

		val camera = Camera3D(
			20f to 30f to 20f,
			0f to 0f to 0f,
			0f to 1f to 0f,
			45f,
			CameraProjection3D.PERSPECTIVE
		)

		Window.fps = 60
		Window.lifecycleForFrames(60 * 5) {
			camera.update(CameraMode3D.ORBITAL)

			shader.viewPos = camera.position
			Canvas.draw {
				setBackgroundColor(Color.SKY_BLUE)

				camera3D(camera) {
					drawMesh(sphere, m1)
					drawMesh(cube, m2, 5, 0, 0)
					drawMesh(cone, m3, -5, 0, 0)
					drawMesh(prism, m4, 0, 0, 5)
					drawMesh(torus, m5, 0, 0, -5)
				}
			}
		}

		sphere.unload()
		cube.unload()
		cone.unload()
		prism.unload()
		torus.unload()
	}

}
