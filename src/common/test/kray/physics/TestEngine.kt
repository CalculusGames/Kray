package kray.physics

import kotlinx.coroutines.test.runTest
import kray.Kray
import kray.formatAs
import kray.sprites.Sprite2D
import kray.sprites.addSprite
import raylib.*
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class TestEngine {

	@Test
	fun testEngine2D() = runTest(timeout = 90.seconds) {
		Kray {
			engineEnabled = true

			val s1 = Sprite2D.from {
				val image = Image.fromColor(20, 20, Color.RED)
				Texture2D.loadFree(image)
			}
			s1.y = window.screenHeight.toFloat() - s1.height
			s1.restitutionCoefficient = 0.5
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

				drawing(-1) {
					val coords = camera2D?.screenToWorld(20, 20) ?: (0f to 0f)

					val x = s1.x.formatAs("%,.2f")
					val y = s1.y.formatAs("%,.2f")
					val vx = s1.vx.formatAs("%,.2f")
					val vy = s1.vy.formatAs("%,.2f")
					val ax = s1.ax.formatAs("%,.2f")
					val ay = s1.ay.formatAs("%,.2f")

					drawText(coords, "${Window.fps} FPS | x: $x, y: $y, vx: $vx, vy: $vy, ax: $ax, ay: $ay", Color.BLACK, 18)
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

}
