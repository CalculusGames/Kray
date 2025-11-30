package raylib

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestWindow {

	@Test
	fun testWindow() {
		if (Window.isHeadless) return

		Window.open(800, 600, "Test Window")
		assertTrue { Window.ready }

		Window.close()
		assertFalse { Window.ready }
	}

}
