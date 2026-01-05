@file:OptIn(ExperimentalForeignApi::class)

package kray

import kotlinx.cinterop.*
import raylib.internal.Vector2
import raylib.internal.Vector3

// cinterop

/**
 * Converts a [COpaquePointer] (aka `void *`) to an unsigned byte array.
 * @param size The size of the dynamic array.
 * @return An unsigned byte array from the pointer
 */
fun COpaquePointer.toUByteArray(size: Int): UByteArray = readBytes(size).toUByteArray()

/**
 * Converts a [COpaquePointer] (aka `void *`) to an integer array.
 * @param size The size of the dynamic array.
 * @return An integer array from the pointer
 */
fun COpaquePointer.toIntArray(size: Int): IntArray {
	val ptr = reinterpret<IntVar>()
	return IntArray(size) { i -> ptr[i] }
}

/**
 * Converts a [COpaquePointer] (aka `void *`) to an unsigned integer array.
 * @param size The size of the dynamic array.
 * @return An unsigned integer array from the pointer
 */
fun COpaquePointer.toUIntArray(size: Int): UIntArray {
	val ptr = reinterpret<IntVar>()
	return UIntArray(size) { i -> ptr[i].toUInt() }
}

// pairs, triples and quadruples

/**
 * Converts a pair of integers to a raw Vector2.
 * @return The C structure with a Vector2.
 */
fun Pair<Int, Int>.toVector2(): CValue<Vector2> = cValue<Vector2>{
	x = first.toFloat()
	y = second.toFloat()
}

/**
 * Converts a pair of floats to a raw Vector2.
 * @return The C structure with a Vector2.
 */
fun Pair<Float, Float>.toVector2(): CValue<Vector2> = cValue<Vector2>{
	x = first
	y = second
}

/**
 * Converts a triplet of integers to a raw Vector3.
 * @return The C structure with a Vector3.
 */
fun Triple<Int, Int, Int>.toVector3(): CValue<Vector3> = cValue<Vector3>{
	x = first.toFloat()
	y = second.toFloat()
	z = third.toFloat()
}

/**
 * Converts a triplet of floats to a raw Vector3.
 * @return The C structure with a Vector3.
 */
fun Triple<Float, Float, Float>.toVector3(): CValue<Vector3> = cValue<Vector3>{
	x = first
	y = second
	z = third
}

/**
 * Creates a triplet using the [kotlin.to] paradigm.
 * @param A type of the first parameter in a triple.
 * @param B type of the second parameter in a triple.
 * @param C type of the third parameter in a triple.
 * @param third The third parameter i	n a triple.
 * @return a triple based on the pair with the third item
 */
infix fun <A, B, C> Pair<A, B>.to(third: C): Triple<A, B, C> = Triple(first, second, third)

/**
 * A data class that represents a quadruple of values.
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quadruple exhibits value semantics, i.e. two quadruples are equal if all four components are equal..
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 */
data class Quadruple<out A, out B, out C, out D>(
	val first: A,
	val second: B,
	val third: C,
	val fourth: D
) {
	/**
	 * Returns string representation of the [Quadruple] including its [first], [second], [third] and [fourth] values.
	 */
	override fun toString(): String = "($first, $second, $third, $fourth)"
}

/**
 * Creates a quadruple using the [kotlin.to] paradigm.
 * @param A type of the first parameter in a quadruple.
 * @param B type of the second parameter in a quadruple.
 * @param C type of the third parameter in a quadruple.
 * @param D type of the fourth parameter in a quadruple.
 * @param fourth The fourth parameter in a quadruple.
 * @return a quadruple based on the triple with the fourth item
 */
infix fun <A, B, C, D> Triple<A, B, C>.to(fourth: D): Quadruple<A, B, C, D> =
	Quadruple(first, second, third, fourth)

// collections

/**
 * Allocates an array of integers in the given [NativePlacement] and initializes it with the provided [elements].
 * @return A pointer to the allocated array of integers.
 */
fun NativePlacement.allocArrayOf(vararg elements: Int): CArrayPointer<IntVar> {
	val res = allocArray<IntVar>(elements.size)
	var index = 0
	while (index < elements.size) {
		res[index] = elements[index]
		++index
	}
	return res
}

/**
 * Allocates an array of unsigned integers in the given [NativePlacement] and initializes it with the provided [elements].
 * @return A pointer to the allocated array of integers.
 */
fun NativePlacement.allocArrayOf(vararg elements: UInt): CArrayPointer<IntVar> {
	val res = allocArray<IntVar>(elements.size)
	var index = 0
	while (index < elements.size) {
		res[index] = elements[index].toInt()
		++index
	}
	return res
}

/**
 * Converts a collection of integers to a dynamically allocated array.
 *
 * **Important:** The caller is responsible for freeing this memory using `nativeHeap.free(array.rawValue)`
 * when the array is no longer needed to prevent memory leaks.
 *
 * @return A pointer to a dynamically allocated array of integers.
 */
fun Collection<Int>.toDynamicArray(): CPointer<IntVarOf<Int>> {
	return nativeHeap.allocArray<IntVarOf<Int>>(size) { i ->
		value = this@toDynamicArray.elementAt(i)
	}
}

// raylib internals

/**
 * Creates a Rectangle C structure with the given parameters.
 * @param x The x-coordinate of the rectangle.
 * @param y The y-coordinate of the rectangle.
 * @param width The width of the rectangle.
 * @param height The height of the rectangle.
 * @return A CValue representing the Rectangle.
 */
fun rectangle(x: Float, y: Float, width: Float, height: Float): CValue<raylib.internal.Rectangle> =
	cValue<raylib.internal.Rectangle> {
		this.x = x
		this.y = y
		this.width = width
		this.height = height
	}
