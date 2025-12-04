package raylib

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestIO {

	@Test
	fun testFile() {
		val file1 = File("data.txt")
		file1.writeText("Hello World")
		assertTrue { file1.exists() }
		assertTrue { file1.isFile }
		assertFalse { file1.isDirectory }
		assertTrue { file1.absolutePath.endsWith("data.txt") }
		assertEquals("Hello World", file1.readText())
		assertFalse { file1.readBytes().isEmpty() }
		assertTrue { file1.files().isEmpty() }
		assertTrue { file1.delete() }
		assertFalse { file1.exists() }

		val file2 = File("testdir")
		file2.mkdir()
		assertTrue { file2.exists() }
		assertFalse { file2.isFile }
		assertTrue { file2.isDirectory }
		assertTrue { file2.files().isEmpty() }
		assertTrue { file2.delete() }
		assertFalse { file2.exists() }

		val file3 = File("testdir2").apply { mkdir() }
		assertTrue { file3.exists() }
		assertTrue { file3.isDirectory }
		File(file3, "a.txt").apply {
			writeText("Hello World")
			assertTrue { exists() }
			assertEquals("Hello World", readText())
			assertFalse { readBytes().isEmpty() }
		}
		File(file3, "b.txt").apply {
			writeText("Hello World, again")
			assertTrue { exists() }
			assertEquals("Hello World, again", readText())
			assertFalse { readBytes().isEmpty() }
		}
		assertEquals(2, file3.files().size)
		assertTrue { file3.deleteRecursively() }
		assertFalse { file3.exists() }

		val file4 = File("calcugames.png")
		assertTrue { file4.exists() }
		assertTrue { file4.isFile }
		assertFalse { file4.readBytes().isEmpty() }
		File("calcugames.h").apply {
			assertTrue { file4.writeCode(this) }
			assertTrue { exists() }
			assertNotNull(readText())
			assertTrue { delete() }
		}
	}

}
