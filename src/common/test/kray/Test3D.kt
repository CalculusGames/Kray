package kray

import kray.shaders.LIGHTING_SHADER
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
	}

}
