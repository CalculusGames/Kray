package raylib

import kray.to
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestWindow {

	@Test
	fun testWindow() {
		if (Window.isHeadless) return
		assertFalse { Window.isHeadless }

		Window.open(800, 600, "Test Window")
		assertTrue { Window.ready }
		assertEquals("Test Window", Window.currentTitle)

		// Clipboard
		Window.clipboardText = "Kray"
		assertEquals("Kray", Window.clipboardText)

		// FPS & Time
		println("${Window.fps} FPS")
		println("${Window.time} seconds passed")

		Window.close()
		assertFalse { Window.ready }
	}

	@Test
	fun testCanvas() {
		if (Window.isHeadless) return
		assertFalse { Window.isHeadless }

		Window.open(800, 600, "Test Canvas 2D")

		Canvas.draw {
			setBackgroundColor(Color.WHITE)

			// Test basic drawing
			draw(10, 10, Color.RED)
			draw(20, 20, Color.BLUE)

			// Test lines
			line(50, 50, 100, 100, Color.GREEN)
			line(50, 100, 100, 50, 2.5f, Color.ORANGE)
			lineBezier(150, 50, 200, 100, 3.0f, Color.PURPLE)
			lineStrip(Color.MAGENTA, 250 to 50, 270 to 70, 290 to 50, 310 to 80)

			// Test circles
			circle(100, 200, 30f, Color.RED)
			fillCircle(200, 200, 30f, Color.BLUE)
			fillCircleGradient(300, 200, 30f, Color.YELLOW, Color.ORANGE)

			// Test arcs
			arc(100, 300, 40f, 0f, 180f, 10, Color.GREEN)
			fillArc(200, 300, 40f, 0f, 270f, 12, Color.CYAN)

			// Test ellipse
			ellipse(100, 400, 50f, 30f, Color.PINK)
			fillEllipse(200, 400, 50f, 30f, Color.LIME)

			// Test rings
			ring(100, 500, 20f, 40f, 0f, 360f, 16, Color.BROWN)
			fillRing(200, 500, 20f, 40f, 0f, 270f, 16, Color.VIOLET)

			// Test rectangles
			rect(400, 50, 80, 60, Color.BLACK)
			rect(500, 50, 80, 60, 3, Color.BLUE)
			fillRect(400, 150, 80, 60, Color.MAROON)
			fillRect(500, 150, 80, 60, 45f, Color.GREEN)
			fillRectGradient(400, 250, 80, 60, Color.RED, Color.YELLOW, Color.ORANGE, Color.BLUE)

			// Test rounded rectangles
			fillRoundRect(400, 350, 80, 60, 0.3f, 10, Color.GOLD)

			// Test triangles
			triangle(50, 550, 100, 550, 75, 520, Color.RED)
			fillTriangle(150, 550, 200, 550, 175, 520, Color.BLUE)
			triangleStrip(Color.GREEN, 250 to 550, 270 to 520, 290 to 550, 310 to 520, 330 to 550)

			// Test polygon
			polygon(450, 500, 6, 40f, 0f, Color.PURPLE)
			fillPolygon(550, 500, 5, 40f, 0f, Color.ORANGE)
		}

		// Hold for a moment to view
		Window.delay(3000)

		Window.close()
	}

}
