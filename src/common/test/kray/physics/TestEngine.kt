package kray.physics

import kotlinx.coroutines.test.runTest
import kray.Kray
import kray.formatAs
import kray.to
import kray.sprites.Sprite2D
import kray.sprites.Sprite3D
import kray.sprites.addSprite
import raylib.*
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class TestEngine {

	@Test
	fun testEngine2D() = runTest(timeout = 90.seconds) {
		if (Window.isHeadless) return@runTest
		assertFalse { Window.isHeadless }

		Kray {
			engineEnabled = true

			val s1 = Sprite2D.from {
				val image = Image.fromColor(20, 20, Color.RED)
				Texture2D.loadFree(image)
			}
			s1.y = window.screenHeight.toFloat() - s1.height
			s1.restitutionCoefficient = 0.3
			s1.spinFactor = 0.5f
			addSprite(s1)

			val s2 = Sprite2D.from {
				val image = Image.fromColor(100, 100, Color.BLUE)
				Texture2D.loadFree(image)
			}
			s2.x = window.screenWidth.toFloat() / 2 - s2.width / 2
			s2.y = window.screenHeight.toFloat() / 2 - s2.height / 2
			s2.static = true

			addSprite(s2)

			minX = 0.0f
			maxX = 1000.0f

			camera2D = Camera2D.center(s1)
			loop(20 * 60) {
				if (frameCount == 30) {
					s1.setVelocity(825.0, 535.0)
				}

				canvas.setBackgroundColor(Color.WHITE)

				drawing(-1, inCamera = false) {
					val x = s1.x.formatAs("%,.2f")
					val y = s1.y.formatAs("%,.2f")
					val vx = s1.vx.formatAs("%,.2f")
					val vy = s1.vy.formatAs("%,.2f")
					val ax = s1.ax.formatAs("%,.2f")
					val ay = s1.ay.formatAs("%,.2f")

					drawText(20, 20, "${Window.fps} FPS | x: $x, y: $y, vx: $vx, vy: $vy, ax: $ax, ay: $ay", Color.BLACK, 18)
				}

				drawing {
					line(
						camera2D!!.bottomLeft.first,
						window.screenHeight - 1f,
						camera2D!!.bottomRight.first,
						window.screenHeight - 1f
					)

					line(minX, window.screenHeight - 1f, minX, 0.0f)
					line(maxX, window.screenHeight - 1f, maxX, 0.0f)
				}

				camera2D?.followPlayerBoundsPush(s1)
			}

			camera2D?.unload()
		}
	}

	@Test
	fun testEngine3D() = runTest(timeout = 90.seconds) {
		if (Window.isHeadless) return@runTest
		assertFalse { Window.isHeadless }

		Kray {
			engineEnabled = true
			groundY = 0f

			val material = Material.default()
			material.setMapColor(MaterialMap.Texture.ALBEDO, Color.RED)

			val s1 = Sprite3D.from {
				val mesh = Mesh.cube(5f)
				Model.fromMesh(mesh, material)
			}
			s1.restitutionCoefficient = 0.3
			s1.spinFactor = 0.5f
			addSprite(s1)

			camera3D = Camera3D.on(s1, distance = 50f, rotation = 0f to 0f to 0f)
			loop(20 * 60) {
				if (frameCount == 30) {
					s1.setVelocity(265.0, 235.0, 135.0)
				}

				canvas.setBackgroundColor(Color.WHITE)

				drawing(-1, inCamera = false) {
					val x = s1.x.formatAs("%,.2f")
					val y = s1.y.formatAs("%,.2f")
					val z = s1.z.formatAs("%,.2f")
					val vx = s1.vx.formatAs("%,.2f")
					val vy = s1.vy.formatAs("%,.2f")
					val vz = s1.vz.formatAs("%,.2f")
					val ax = s1.ax.formatAs("%,.2f")
					val ay = s1.ay.formatAs("%,.2f")
					val az = s1.az.formatAs("%,.2f")

					val (storedPitch, storedYaw, storedRoll) = s1.storedRotation
					val storedPitchDeg = storedPitch * 180f / kotlin.math.PI.toFloat()
					val storedYawDeg = storedYaw * 180f / kotlin.math.PI.toFloat()
					val storedRollDeg = storedRoll * 180f / kotlin.math.PI.toFloat()

					val extractedPitch = s1.transform.pitch * 180f / kotlin.math.PI.toFloat()
					val extractedYaw = s1.transform.yaw * 180f / kotlin.math.PI.toFloat()
					val extractedRoll = s1.transform.roll * 180f / kotlin.math.PI.toFloat()

					drawText(20, 20, "${Window.fps} FPS\nx: $x, y: $y, z: $z\nvx: $vx, vy: $vy, vz: $vz\nax: $ax, ay: $ay, az: $az\nStored Rot: ${storedPitchDeg.formatAs("%.2f")}°, ${storedYawDeg.formatAs("%.2f")}°, ${storedRollDeg.formatAs("%.2f")}°\nExtracted Rot: ${extractedPitch.formatAs("%.2f")}°, ${extractedYaw.formatAs("%.2f")}°, ${extractedRoll.formatAs("%.2f")}°", Color.BLACK, 18)
				}

				drawing {
					grid(10000)
				}

				camera3D?.updateWith(CameraMode3D.THIRD_PERSON, s1)
			}

			// Assertions after the loop
			val (finalPitch, finalYaw, finalRoll) = s1.storedRotation
			val finalPitchDeg = finalPitch * 180f / kotlin.math.PI.toFloat()
			val finalYawDeg = finalYaw * 180f / kotlin.math.PI.toFloat()
			val finalRollDeg = finalRoll * 180f / kotlin.math.PI.toFloat()

			println("Final stored rotation: pitch=${finalPitchDeg}°, yaw=${finalYawDeg}°, roll=${finalRollDeg}°")
			println("Final velocity: vx=${s1.vx}, vy=${s1.vy}, vz=${s1.vz}")
			println("On ground: ${s1.isBelow(groundY + 0.3f)}")

			// The sprite should have returned to near-flat orientation (within 5 degrees)
			// For a cube with targetRotation (180, 360, 180), it should snap to 0° or 180° on pitch/roll
			val threshold = 5.0f

			// Normalize angles to [0, 360) for easier comparison
			fun normalizeAngle(degrees: Float): Float {
				var norm = degrees % 360f
				if (norm < 0) norm += 360f
				return norm
			}

			val normPitch = normalizeAngle(finalPitchDeg)
			val normRoll = normalizeAngle(finalRollDeg)

			val pitchNearFlat = (normPitch < threshold || normPitch > 360f - threshold ||
			                     abs(normPitch - 180f) < threshold)
			val rollNearFlat = (normRoll < threshold || normRoll > 360f - threshold ||
			                    abs(normRoll - 180f) < threshold)

			assertTrue(pitchNearFlat, "Pitch should be near 0° or 180° (was ${finalPitchDeg}°, normalized: ${normPitch}°)")
			assertTrue(rollNearFlat, "Roll should be near 0° or 180° (was ${finalRollDeg}°, normalized: ${normRoll}°)")
			assertTrue(abs(s1.vx) < 1.0, "Horizontal velocity X should be near zero (was ${s1.vx})")
			assertTrue(abs(s1.vz) < 1.0, "Horizontal velocity Z should be near zero (was ${s1.vz})")

			material.unload()
			camera3D?.unload()
		}
	}

}
