package kray.physics

import kotlinx.coroutines.test.runTest
import kray.Kray
import kray.formatAs
import kray.sprites.Sprite2D
import kray.sprites.addSprite
import raylib.*
import kotlin.test.Test

class TestEngine {

	@Test
	fun testEngine2D() = runTest {
		Kray {
			engineEnabled = true

			val sprite = Sprite2D.from {
				val image = Image.fromColor(20, 20, Color.RED)
				Texture2D.load(image)
			}
			sprite.y = window.screenHeight.toFloat() - sprite.height
			addSprite(sprite)

			camera2D = Camera2D.center(sprite)
			loop(10 * 60) {
				if (frameCount == 30) {
					sprite.setVelocity(425.0, 335.0)
				}

				canvas.setBackgroundColor(Color.WHITE)
				drawing {
					line(
						camera2D!!.bottomLeft.first,
						window.screenHeight - 1f,
						camera2D!!.bottomRight.first,
						window.screenHeight - 1f
					)

					val coords = camera2D?.screenToWorld(20, 20) ?: (0f to 0f)

					val x = sprite.x.formatAs("%,.2f")
					val y = sprite.y.formatAs("%,.2f")
					val vx = sprite.vx.formatAs("%,.2f")
					val vy = sprite.vy.formatAs("%,.2f")
					val ax = sprite.ax.formatAs("%,.2f")
					val ay = sprite.ay.formatAs("%,.2f")

					drawText(coords, "x: $x, y: $y, vx: $vx, vy: $vy, ax: $ax, ay: $ay", Color.BLACK, 18)
				}

				camera2D?.followPlayerBoundsPush(sprite)
			}
		}
	}

}
