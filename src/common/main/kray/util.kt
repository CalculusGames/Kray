@file:OptIn(ExperimentalForeignApi::class)

package kray

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import raylib.internal.Vector2
import raylib.internal.Vector3

// cinterop

/**
 * Converts a [COpaquePointer] (aka `void *`) to an unsigned byte array.
 * @param size The size of the dynamic array.
 * @return An unsigned byte array from the pointer
 */
fun COpaquePointer.toByteArray(size: Int): ByteArray {
	val ptr = reinterpret<ByteVar>()
	return ByteArray(size) { i -> ptr[i] }
}

// pairs & triples

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
 * @param third The third parameter i	n a triple.
 * @return a triple based on the pair with the third item
 */
infix fun <A, B, C> Pair<A, B>.to(third: C): Triple<A, B, C> = Triple(first, second, third)
