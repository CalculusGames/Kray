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

	@Test
	fun testBytes() {
		val text = "Security is important! " +
			"Keep your data safe and secure. " +
			"With proper encryption and best practices, " +
			"you can protect sensitive information from unauthorized access."

		val bytes = text.encodeToByteArray()
		val ubytes = UByteArray(64) { (it * 2).toUByte() } +
			UByteArray(64) { (it * 3).toUByte() } +
			UByteArray(64) { (it * 4).toUByte() } +
			UByteArray(64)

		// Compression
		val compressed1 = bytes.compress()
		assertFalse("compressed1 is empty") { compressed1.isEmpty() }
		assertTrue(
			"compressed1 size is not less than bytes size: " +
				"${compressed1.size} >= ${bytes.size}"
		) { compressed1.size < bytes.size }
		val decompressed1 = compressed1.decompress()
		assertEquals(bytes.size, decompressed1.size,
			"decompressed1 size ${decompressed1.size} does not match original size ${bytes.size}"
		)
		assertTrue(
			"decompressed1 data does not match original data:\n" +
				"${bytes.contentToString()} != ${decompressed1.contentToString()}"
		) { bytes.contentEquals(decompressed1) }

		val compressed2 = ubytes.compress()
		assertFalse("compressed2 is empty") { compressed2.isEmpty() }
		assertTrue(
			"compressed2 size is not less than ubytes size: " +
				"${compressed2.size} >= ${ubytes.size}"
		) { compressed2.size < ubytes.size }
		val decompressed2 = compressed2.decompress()
		assertEquals(ubytes.size, decompressed2.size,
			"decompressed2 size ${decompressed2.size} does not match original size ${ubytes.size}"
		)
		assertTrue(
			"decompressed2 data does not match original data:\n" +
				"${ubytes.contentToString()} != ${decompressed2.contentToString()}"
		) { ubytes.contentEquals(decompressed2) }

		// TODO submit issues for Base64 and checksums on various platforms

//		// Base64
//		val base641 = bytes.toBase64()
//		assertNotNull(base641, "base641 is null")
//		assertFalse("base641 is empty") { base641.isEmpty() }
//		val decoded1 = base641.fromBase64()
//		assertEquals(bytes.size, decoded1.size,
//			"decoded1 size ${decoded1.size} does not match original size ${bytes.size}:\n" +
//				"${bytes.contentToString()} != ${decoded1.contentToString()}"
//		)
//		assertTrue(
//			"decoded1 data does not match original data:\n" +
//				"${bytes.contentToString()} != ${decoded1.contentToString()}"
//		) { bytes.contentEquals(decoded1) }
//
//		val base642 = ubytes.toBase64()
//		assertNotNull(base642, "base642 is null")
//		assertFalse("base642 is empty") { base642.isEmpty() }
//		val decoded2 = base642.fromBase64U()
//		assertEquals(ubytes.size, decoded2.size,
//			"decoded2 size ${decoded2.size} does not match original size ${ubytes.size}:\n" +
//				"${ubytes.contentToString()} != ${decoded2.contentToString()}"
//		)
//		assertTrue(
//			"decoded2 data does not match original data:\n" +
//				"${ubytes.contentToString()} != ${decoded2.contentToString()}"
//		) { ubytes.contentEquals(decoded2) }
//
//		// Checksums
//		val crc1 = bytes.crc32()
//		assertEquals(0xA271149Du, crc1, "crc1 does not match expected value")
//		val crc2 = ubytes.crc32()
//		assertEquals(0x60555F71u, crc2, "crc2 does not match expected value")
//
//		val md51 = bytes.md5()
//		assertEquals("0f617fb820484ae7ad0e53ec17b7cf32", md51, "md51 does not match expected value")
//		val md52 = ubytes.md5()
//		assertEquals("2d01fd2427dbb5d746ca2acc5c4b3cdf", md52, "md52 does not match expected value")

//		https://github.com/raysan5/raylib/pull/5397
//		val sha11 = bytes.sha1()
//		assertEquals("2c0f22ce3a9ee65c7f0ccca1ecb5e5b53c8fa6bb", sha11)
//		val sha12 = ubytes.sha1()
//		assertEquals("9a4026e4e27148c6f7177f9001f3630e7eb291f1", sha12)
	}

}
