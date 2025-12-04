@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kray.toByteArray
import platform.posix.remove
import raylib.internal.DirectoryExists
import raylib.internal.ExportDataAsCode
import raylib.internal.ExportImage
import raylib.internal.FileExists
import raylib.internal.GetApplicationDirectory
import raylib.internal.GetDirectoryPath
import raylib.internal.GetFileExtension
import raylib.internal.GetFileLength
import raylib.internal.GetFileName
import raylib.internal.GetFileNameWithoutExt
import raylib.internal.GetWorkingDirectory
import raylib.internal.IsPathFile
import raylib.internal.LoadDirectoryFilesEx
import raylib.internal.LoadFileData
import raylib.internal.LoadFileText
import raylib.internal.MakeDirectory
import raylib.internal.OpenURL
import raylib.internal.SaveFileData
import raylib.internal.SaveFileText
import raylib.internal.TakeScreenshot
import raylib.internal.UnloadDirectoryFiles
import raylib.internal.UnloadFileText

/**
 * Takes and saves a screenshot.
 * @param save The path to the screenshot. The file extension will determine its format.
 * ```kt
 * screenshot("path/to/screenshot.png")
 * screenshot("path/to/screenshot2.jpg")
 * screenshot("path/to/screenshot3.bmp")
 * ```
 */
fun screenshot(save: String) {
	TakeScreenshot(save)
}

/**
 * Opens a URL in the system's default browser
 * @param url The URL to open.
 */
fun openURL(url: String) {
	OpenURL(url)
}

/**
 * The current working directory.
 */
val cwd: String?
	get() = GetWorkingDirectory()?.toKString()

/**
 * The current directory of where the application is running.
 */
val appDir: String?
	get() = GetApplicationDirectory()?.toKString()

/**
 * Converts this path to an absolute path in the application directory.
 * If [appDir] is null or the path starts with [appDir], the original path is returned.
 * @receiver The absolute path to convert.
 * @return An absolute path in the application directory.
 */
fun String.inAppDir(): String {
	if (appDir == null) return this
	if (this.startsWith(appDir!!)) {
		return this
	}

	return "${appDir!!}$this"
}

/**
 * Represents a file in raylib.
 * @property path The path to the current file.
 */
class File(path: String) {

	/**
	 * The absolute path of the file.
	 */
	val absolutePath: String = path.inAppDir()

	/**
	 * The path of the parent directory.
	 */
	val parentDir: String
		get() = GetDirectoryPath(absolutePath)?.toKString() ?: ""

	/**
	 * Creates a new file with a parent.
	 * @param parent The parent file.
	 * @param subpath The subpath of the file.
	 *
	 * ```kt
	 * File(dir, "child.txt")
	 * ```
	 */
	constructor(parent: File, subpath: String) : this("${parent.absolutePath}/${subpath}")

	/**
	 * Checks if the path exists, either as a file or directory.
	 * @return true if the path exists, false otherwise
	 */
	fun exists(): Boolean {
		return FileExists(absolutePath)
	}

	/**
	 * Checks if the current path points to a file.
	 * @return true if file exists, false otherwise
	 */
	val isFile: Boolean
		get() = IsPathFile(absolutePath)

	/**
	 * Checks if the current path points to a directory.
	 * @return true if directory, false otherwise
	 */
	val isDirectory: Boolean
		get() = DirectoryExists(absolutePath)

	/**
	 * Gets the length of the file in bytes.
	 * @return the length of the file
	 */
	val length: Int
		get() = GetFileLength(absolutePath)

	/**
	 * Gets the name of the file of the current path, if it exists.
	 * @return the name of the file
	 */
	fun getFileName(): String? {
		return GetFileName(absolutePath)?.toKString()
	}

	/**
	 * Gets the name of the file of the current path, excluding the extension.
	 * @return the name of the file path without the extension
	 */
	fun getFileNameWithoutExtension(): String? {
		return GetFileNameWithoutExt(absolutePath)?.toKString()
	}

	/**
	 * Gets the file extension of the current path, if it exists.
	 * @return the file extension of the file, including the dot (e.g. `.png`)
	 */
	fun getFileExtension(): String? {
		return GetFileExtension(absolutePath)?.toKString()
	}

	/**
	 * Creates a directory at the path, with any parent directories created if not exist.
	 * @return true if successful, false otherwise
	 */
	fun mkdir(): Boolean {
		return MakeDirectory(absolutePath) == 0
	}

	/**
	 * Reads the text from the specified file.
	 * @return the text contained in the file.
	 */
	fun readText(): String? {
		val buf = LoadFileText(absolutePath)
		val str = buf?.toKString()
		UnloadFileText(buf)

		return str
	}

	/**
	 * Reads a byte array from the specified file.
	 * @return the byte array contained in the file.
	 */
	fun readBytes(): ByteArray = memScoped {
		val size = alloc(length)
		val data = LoadFileData(absolutePath, size.ptr)

		return data?.toByteArray(length) ?: ByteArray(0)
	}

	/**
	 * Writes the text from the specified file.
	 * @param text The text to save to the file.
	 * @return true if successful, false otherwise
	 */
	fun writeText(text: String): Boolean {
		return SaveFileText(absolutePath, text)
	}

	/**
	 * Writes a byte array to the specified file.
	 * @param bytes The byte array to write with.
	 * @return true if successful, false otherwise
	 */
	fun writeBytes(bytes: ByteArray): Boolean {
		return bytes.usePinned { pinned ->
			val ptr = pinned.addressOf(0)
			SaveFileData(absolutePath, ptr, bytes.size)
		}
	}

	/**
	 * Writes an unsigned byte array to the specified file.
	 * @param bytes The byte array to write with.
	 * @return true if successful, false otherwise
	 */
	fun writeBytes(bytes: UByteArray): Boolean {
		return bytes.usePinned { pinned ->
			val ptr = pinned.addressOf(0)
			SaveFileData(absolutePath, ptr, bytes.size)
		}
	}

	/**
	 * Writes this file's data to another file as a code source (`*.h`) file.
	 * @param output The output file to write to.
	 * @return true if successful, false otherwise
	 */
	fun writeCode(output: File): Boolean {
		val bytes = readBytes()
		if (bytes.isEmpty()) return false

		return bytes.usePinned { pinned ->
			val ptr = pinned.addressOf(0).reinterpret<UByteVar>()
			ExportDataAsCode(ptr, bytes.size, output.absolutePath)
		}
	}

	/**
	 * Loads all files inside [parentDir].
	 * @param deep Whether to recursively scan subdirectories as well.
	 * @param filter A file regex glob filter. Use `'DIR'` to filter for directories.
	 * @return A list of files inside [parentDir]
	 */
	fun files(deep: Boolean = false, filter: String? = null): List<File> {
		if (isFile) return emptyList()

		val list = mutableListOf<File>()
		val files = LoadDirectoryFilesEx(absolutePath, filter, deep)
		files.useContents {
			if (paths == null) return@useContents

			for (i in 0 until count.toInt()) {
				val file = paths!![i]?.toKString() ?: continue
				list.add(File(file))
			}
		}

		UnloadDirectoryFiles(files)
		return list
	}

	/**
	 * Deletes this file or directory.
	 *
	 * If the directory is not empty this will fail.
	 * @return true if successful
	 */
	fun delete(): Boolean {
		return remove(absolutePath) == 0
	}

	/**
	 * Deletes this folder and all of its contents. If it is not a folder, this will call `false`.
	 * @return true if successful, false otherwise
	 */
	fun deleteRecursively(): Boolean {
		if (isFile) return delete()

		for (file in files()) {
			if (file.isDirectory) {
				if (!file.deleteRecursively()) return false
			} else {
				if (!file.delete()) return false
			}
		}

		return delete()
	}

	override fun hashCode(): Int {
		return absolutePath.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		if (other !is File) return false
		return absolutePath == other.absolutePath
	}

	override fun toString(): String {
		return absolutePath
	}
}

/**
 * Converts a string path to a file.
 * @return [File] object
 */
fun String.toFile(): File = File(this)

/**
 * Writes this image to a file.
 * @param file The file to write to
 * @return true if succesful, false otherwise
 */
fun Image.writeTo(file: File): Boolean {
	return ExportImage(raw, file.absolutePath)
}
