@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kray.to
import kray.toVector2
import raylib.internal.*

internal fun Canvas.ensureDrawing() {
	if (!inDrawingState) throw IllegalStateException("Cannot draw if not in drawing state. Use start() or draw() first")
}

// Draw Functions

/**
 * Draws a pixel on the canvas.
 * @param x The X coord to draw at
 * @param y The Y coord to draw at
 * @param color The color of the pixel
 */
fun Canvas.draw(x: Int, y: Int, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawPixel(x, y, color.raw())
}

/**
 * Draws a line on the canvas.
 * @param x1 The X coordinate of the first point
 * @param y1 The Y coordinate of the second point
 * @param x2 The X coordinate of the first point
 * @param y2 The Y coordinate of the second point
 * @param color The color of the line
 */
fun Canvas.line(x1: Int, y1: Int, x2: Int, y2: Int, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawLine(x1, y1, x2, y2, color.raw())
}

/**
 * Draws a line on the canvas using triangles and quads.
 * @param x1 The X coordinate of the first point
 * @param y1 The Y coordinate of the second point
 * @param x2 The X coordinate of the first point
 * @param y2 The Y coordinate of the second point
 * @param thick The thickness of the line.
 * @param color The color of the line
 */
fun Canvas.line(x1: Int, y1: Int, x2: Int, y2: Int, thick: Float, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawLineEx(
		cValue {
			x = x1.toFloat()
			y = y1.toFloat()
		},
		cValue {
			x = x2.toFloat()
			y = y2.toFloat()
		},
		thick, color.raw()
	)
}

/**
 * Draws multiple lines on the canvas.
 * @param color The color of the line strip
 * @param points The points to draw the line strip through
 */
fun Canvas.lineStrip(color: Color = Color.BLACK, points: List<Pair<Int, Int>>) {
	if (points.isEmpty()) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawLineStrip(array, points.size, color.raw())
}

/**
 * Draws multiple lines on the canvas.
 * @param color The color of the line strip
 * @param points The points to draw the line strip through
 */
fun Canvas.lineStrip(color: Color = Color.BLACK, vararg points: Pair<Int, Int>) {
	lineStrip(color, points.toList())
}

/**
 * Draws a cubic bezier line on the canvas.
 * @param x1 The X coordinate of the first point
 * @param y1 The Y coordinate of the second point
 * @param x2 The X coordinate of the first point
 * @param y2 The Y coordinate of the second point
 * @param thick The thickness of the line.
 * @param color The color of the line
 */
fun Canvas.lineBezier(x1: Int, y1: Int, x2: Int, y2: Int, thick: Float, color: Color = Color.BLACK) {
	ensureDrawing()

	DrawLineBezier(
		cValue {
			x = x1.toFloat()
			y = y1.toFloat()
		},
		cValue {
			x = x2.toFloat()
			y = y2.toFloat()
		},
		thick, color.raw()
	)
}

/**
 * Draws a circle outline on the canvas.
 * @param cx The X coordinate of the circle center
 * @param cy The Y coordinate of the circle center
 * @param radius The radius of the circle
 * @param color The color of the circle
 */
fun Canvas.circle(cx: Int, cy: Int, radius: Float, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawCircleLines(cx, cy, radius, color.raw())
}

/**
 * Draws a filled circle on the canvas.
 * @param cx The X coordinate of the circle center
 * @param cy The Y coordinate of the circle center
 * @param radius The radius of the circle
 * @param color The color of the circle
 */
fun Canvas.fillCircle(cx: Int, cy: Int, radius: Float, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawCircle(cx, cy, radius, color.raw())
}

/**
 * Draws a filled circle with a radial gradient on the canvas.
 * @param cx The X coordinate of the circle center
 * @param cy The Y coordinate of the circle center
 * @param radius The radius of the circle
 * @param inner The inner color of the gradient
 * @param outer The outer color of the gradient
 */
fun Canvas.fillCircleGradient(cx: Int, cy: Int, radius: Float, inner: Color, outer: Color) {
	ensureDrawing()
	DrawCircleGradient(cx, cy, radius, inner.raw(), outer.raw())
}

/**
 * Draws an arc outline on the canvas.
 * @param cx The X coordinate of the arc center
 * @param cy The Y coordinate of the arc center
 * @param radius The radius of the arc
 * @param startAngle The starting angle of the arc in degrees
 * @param endAngle The ending angle of the arc in degrees
 * @param segments The number of segments to use for drawing the arc (more segments = smoother arc, typically >= 6)
 * @param color The color of the arc
 */
fun Canvas.arc(cx: Int, cy: Int, radius: Float, startAngle: Float, endAngle: Float, segments: Int = 6, color: Color = Color.BLACK) {
	ensureDrawing()

	DrawCircleSectorLines(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		radius, startAngle, endAngle, segments, color.raw()
	)
}

/**
 * Draws an arc on the canvas as a filled sector.
 * @param cx The X coordinate of the arc center
 * @param cy The Y coordinate of the arc center
 * @param radius The radius of the arc
 * @param startAngle The starting angle of the arc in degrees
 * @param endAngle The ending angle of the arc in degrees
 * @param segments The number of segments to use for drawing the arc (more segments = smoother arc, typically >= 6)
 * @param color The color of the arc
 */
fun Canvas.fillArc(cx: Int, cy: Int, radius: Float, startAngle: Float, endAngle: Float, segments: Int = 6, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawCircleSector(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		radius, startAngle, endAngle, segments, color.raw()
	)
}

/**
 * Draws an ellipse outline on the canvas.
 * @param cx The X coordinate of the ellipse center
 * @param cy The Y coordinate of the ellipse center
 * @param hradius The horizontal radius of the ellipse
 * @param vradius The vertical radius of the ellipse
 * @param color The color of the ellipse
 */
fun Canvas.ellipse(cx: Int, cy: Int, hradius: Float, vradius: Float, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawEllipseLines(cx, cy, hradius, vradius, color.raw())
}

/**
 * Draws a filled ellipse on the canvas.
 * @param cx The X coordinate of the ellipse center
 * @param cy The Y coordinate of the ellipse center
 * @param hradius The horizontal radius of the ellipse
 * @param vradius The vertical radius of the ellipse
 * @param color The color of the ellipse
 */
fun Canvas.fillEllipse(cx: Int, cy: Int, hradius: Float, vradius: Float, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawEllipse(cx, cy, hradius, vradius, color.raw())
}

/**
 * Draws a ring outline on the canvas.
 * @param cx The X coordinate of the ring center
 * @param cy The Y coordinate of the ring center
 * @param iradius The inner radius of the ring
 * @param oradius The outer radius of the ring
 * @param startAngle The starting angle of the ring in degrees
 * @param endAngle The ending angle of the ring in degrees
 * @param segments The number of segments to use for drawing the ring (more segments = smoother ring, typically >= 6)
 * @param color The color of the ring
 */
fun Canvas.ring(
	cx: Int,
	cy: Int,
	iradius: Float,
	oradius: Float,
	startAngle: Float,
	endAngle: Float,
	segments: Int = 6,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawRingLines(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		iradius, oradius, startAngle, endAngle, segments, color.raw()
	)
}

/**
 * Draws a filled ring on the canvas, in between two radii.
 * @param cx The X coordinate of the ring center
 * @param cy The Y coordinate of the ring center
 * @param iradius The inner radius of the ring
 * @param oradius The outer radius of the ring
 * @param startAngle The starting angle of the ring in degrees
 * @param endAngle The ending angle of the ring in degrees
 * @param segments The number of segments to use for drawing the ring (more segments = smoother ring, typically >= 6)
 * @param color The color of the ring
 */
fun Canvas.fillRing(
	cx: Int,
	cy: Int,
	iradius: Float,
	oradius: Float,
	startAngle: Float,
	endAngle: Float,
	segments: Int = 6,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawRing(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		iradius, oradius, startAngle, endAngle, segments, color.raw()
	)
}

/**
 * Draws a rectangle outline on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param color The color of the rectangle
 */
fun Canvas.rect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawRectangleLines(x, y, width, height, color.raw())
}

/**
 * Draws a rectangle outline on the canvas with specified line thickness.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param lineThick The thickness of the rectangle lines
 * @param color The color of the rectangle
 */
fun Canvas.rect(x: Int, y: Int, width: Int, height: Int, lineThick: Int, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawRectangleLinesEx(
		cValue {
			this.x = x.toFloat()
			this.y = y.toFloat()
			this.width = width.toFloat()
			this.height = height.toFloat()
		},
		lineThick.toFloat(),
		color.raw()
	)
}

/**
 * Draws a filled rectangle on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param color The color of the rectangle
 */
fun Canvas.fillRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawRectangle(x, y, width, height, color.raw())
}

/**
 * Draws a rotated filled rectangle on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param rotation The rotation of the rectangle in degrees
 * @param color The color of the rectangle
 */
fun Canvas.fillRect(x: Int, y: Int, width: Int, height: Int, rotation: Float, color: Color = Color.BLACK) {
	ensureDrawing()
	DrawRectanglePro(
		cValue {
			this.x = x.toFloat()
			this.y = y.toFloat()
			this.width = width.toFloat()
			this.height = height.toFloat()
		},
		cValue {
			this.x = (width / 2).toFloat()
			this.y = (height / 2).toFloat()
		},
		rotation,
		color.raw()
	)
}

/**
 * Draws a filled rectangle with a gradient on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param col1 The first color of the gradient
 * @param col2 The second color of the gradient
 * @param vertical Whether the gradient is vertical (true) or horizontal (false)
 */
fun Canvas.fillRectGradient(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	col1: Color,
	col2: Color,
	vertical: Boolean = true
) {
	ensureDrawing()
	if (vertical) {
		DrawRectangleGradientV(x, y, width, height, col1.raw(), col2.raw())
	} else {
		DrawRectangleGradientH(x, y, width, height, col1.raw(), col2.raw())
	}
}

/**
 * Draws a filled rectangle with a four-corner gradient on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param topLeft The color of the top-left corner
 * @param bottomLeft The color of the bottom-left corner
 * @param topRight The color of the top-right corner
 * @param bottomRight The color of the bottom-right corner
 */
fun Canvas.fillRectGradient(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	topLeft: Color,
	bottomLeft: Color,
	topRight: Color,
	bottomRight: Color
) {
	ensureDrawing()
	DrawRectangleGradientEx(
		cValue {
			this.x = x.toFloat()
			this.y = y.toFloat()
			this.width = width.toFloat()
			this.height = height.toFloat()
		},
		topLeft.raw(),
		bottomLeft.raw(),
		topRight.raw(),
		bottomRight.raw()
	)
}

/**
 * Draws a rounded rectangle outline on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param roundness The roundness of the rectangle corners as a percentage (0.0 - 1.0)
 * @param segments The number of segments to use for drawing the rounded corners
 * @param color The color of the rectangle
 */
fun Canvas.roundRect(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	roundness: Float,
	segments: Int = 0,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawRectangleRoundedLines(
		cValue {
			this.x = x.toFloat()
			this.y = y.toFloat()
			this.width = width.toFloat()
			this.height = height.toFloat()
		},
		roundness, segments, color.raw()
	)
}

/**
 * Draws a rounded rectangle outline on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param roundness The roundness of the rectangle corners as a percentage (0.0 - 1.0)
 * @param lineThick The thickness of the rectangle lines
 * @param segments The number of segments to use for drawing the rounded corners
 * @param color The color of the rectangle
 */
fun Canvas.roundRect(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	roundness: Float,
	lineThick: Int,
	segments: Int = 0,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawRectangleRoundedLinesEx(
		cValue {
			this.x = x.toFloat()
			this.y = y.toFloat()
			this.width = width.toFloat()
			this.height = height.toFloat()
		},
		roundness, segments, lineThick.toFloat(), color.raw()
	)
}

/**
 * Draws a filled rounded rectangle on the canvas.
 * @param x The X coordinate of the rectangle top-left corner
 * @param y The Y coordinate of the rectangle top-left corner
 * @param width The width of the rectangle
 * @param height The height of the rectangle
 * @param roundness The roundness of the rectangle corners as a percentage (0.0 - 1.0)
 * @param segments The number of segments to use for drawing the rounded
 * @param color The color of the rectangle
 */
fun Canvas.fillRoundRect(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	roundness: Float,
	segments: Int = 0,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawRectangleRounded(
		cValue {
			this.x = x.toFloat()
			this.y = y.toFloat()
			this.width = width.toFloat()
			this.height = height.toFloat()
		},
		roundness, segments, color.raw()
	)
}

/**
 * Draws a triangle outline on the canvas.
 *
 * Note that the vertexes are specified and will be drawn in counter-clockwise order.
 * @param x1 The X coordinate of the first vertex
 * @param y1 The Y coordinate of the first vertex
 * @param x2 The X coordinate of the second vertex
 * @param y2 The Y coordinate of the second vertex
 * @param x3 The X coordinate of the third vertex
 * @param y3 The Y coordinate of the third vertex
 * @param color The color of the triangle
 */
fun Canvas.triangle(
	x1: Int,
	y1: Int,
	x2: Int,
	y2: Int,
	x3: Int,
	y3: Int,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawTriangleLines(
		cValue {
			this.x = x1.toFloat()
			this.y = y1.toFloat()
		},
		cValue {
			this.x = x2.toFloat()
			this.y = y2.toFloat()
		},
		cValue {
			this.x = x3.toFloat()
			this.y = y3.toFloat()
		},
		color.raw()
	)
}

/**
 * Draws a filled triangle on the canvas.
 * @param x1 The X coordinate of the first vertex
 * @param y1 The Y coordinate of the first vertex
 * @param x2 The X coordinate of the second vertex
 * @param y2 The Y coordinate of the second vertex
 * @param x3 The X coordinate of the third vertex
 * @param y3 The Y coordinate of the third vertex
 * @param color The color of the triangle
 */
fun Canvas.fillTriangle(
	x1: Int,
	y1: Int,
	x2: Int,
	y2: Int,
	x3: Int,
	y3: Int,
	color: Color = Color.BLACK
) {
	ensureDrawing()
	DrawTriangle(
		cValue {
			this.x = x1.toFloat()
			this.y = y1.toFloat()
		},
		cValue {
			this.x = x2.toFloat()
			this.y = y2.toFloat()
		},
		cValue {
			this.x = x3.toFloat()
			this.y = y3.toFloat()
		},
		color.raw()
	)
}

/**
 * Draws a triangle fan on the canvas.
 * @param color The color of the triangle fan
 * @param points The points to draw the triangle fan through
 */
fun Canvas.triangleFan(color: Color = Color.BLACK, points: List<Pair<Int, Int>>) {
	if (points.size < 3) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawTriangleFan(array, points.size, color.raw())
}

/**
 * Draws a triangle fan on the canvas.
 * @param color The color of the triangle fan
 * @param points The points to draw the triangle fan through
 */
fun Canvas.triangleFan(color: Color = Color.BLACK, vararg points: Pair<Int, Int>) {
	triangleFan(color, points.toList())
}

/**
 * Draws a triangle strip on the canvas.
 * @param color The color of the triangle strip
 * @param points The points to draw the triangle strip through
 */
fun Canvas.triangleStrip(color: Color = Color.BLACK, points: List<Pair<Int, Int>>) {
	if (points.size < 3) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawTriangleStrip(array, points.size, color.raw())
}

/**
 * Draws a triangle strip on the canvas.
 * @param color The color of the triangle strip
 * @param points The points to draw the triangle strip through
 */
fun Canvas.triangleStrip(color: Color = Color.BLACK, vararg points: Pair<Int, Int>) {
	triangleStrip(color, points.toList())
}

/**
 * Draws a polygon outline on the canvas.
 * @param cx The X coordinate of the polygon center
 * @param cy The Y coordinate of the polygon center
 * @param sides The number of sides of the polygon (minimum 3)
 * @param radius The radius of the polygon
 * @param rotation The rotation of the polygon in degrees
 * @param color The color of the polygon
 */
fun Canvas.polygon(
	cx: Int,
	cy: Int,
	sides: Int,
	radius: Float,
	rotation: Float = 0f,
	color: Color = Color.BLACK
) {
	if (sides < 3) return
	ensureDrawing()
	DrawPolyLines(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		sides, radius, rotation, color.raw()
	)
}

/**
 * Draws a polygon outline on the canvas.
 * @param cx The X coordinate of the polygon center
 * @param cy The Y coordinate of the polygon center
 * @param sides The number of sides of the polygon (minimum 3)
 * @param radius The radius of the polygon
 * @param rotation The rotation of the polygon in degrees
 * @param lineThick The thickness of the polygon lines
 * @param color The color of the polygon
 */
fun Canvas.polygon(
	cx: Int,
	cy: Int,
	sides: Int,
	radius: Float,
	rotation: Float = 0f,
	lineThick: Int,
	color: Color = Color.BLACK
) {
	if (sides < 3) return
	ensureDrawing()
	DrawPolyLines(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		sides, radius, rotation, color.raw()
	)
}

/**
 * Draws a filled polygon on the canvas.
 * @param cx The X coordinate of the polygon center
 * @param cy The Y coordinate of the polygon center
 * @param sides The number of sides of the polygon (minimum 3)
 * @param radius The radius of the polygon
 * @param rotation The rotation of the polygon in degrees
 * @param color The color of the polygon
 */
fun Canvas.fillPolygon(
	cx: Int,
	cy: Int,
	sides: Int,
	radius: Float,
	rotation: Float = 0f,
	color: Color = Color.BLACK
) {
	if (sides < 3) return
	ensureDrawing()
	DrawPoly(
		cValue {
			x = cx.toFloat()
			y = cy.toFloat()
		},
		sides, radius, rotation, color.raw()
	)
}

/**
 * Draws a linear spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 2)
 */
fun Canvas.linearSpline(color: Color = Color.BLACK, thick: Float, points: List<Pair<Int, Int>>) {
	if (points.size < 2) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawSplineLinear(array, points.size, thick, color.raw())
}

/**
 * Draws a linear spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 2)
 */
fun Canvas.linearSpline(color: Color = Color.BLACK, thick: Float, vararg points: Pair<Int, Int>) {
	linearSpline(color, thick, points.toList())
}

/**
 * Draws a basis spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 4)
 */
fun Canvas.basisSpline(color: Color = Color.BLACK, thick: Float, points: List<Pair<Int, Int>>) {
	if (points.size < 4) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawSplineBasis(array, points.size, thick, color.raw())
}

/**
 * Draws a basis spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 4)
 */
fun Canvas.basisSpline(color: Color = Color.BLACK, thick: Float, vararg points: Pair<Int, Int>) {
	basisSpline(color, thick, points.toList())
}

/**
 * Draws a Catmull-Rom spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 4)
 */
fun Canvas.catmullRomSpline(color: Color = Color.BLACK, thick: Float, points: List<Pair<Int, Int>>) {
	if (points.size < 4) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawSplineCatmullRom(array, points.size, thick, color.raw())
}

/**
 * Draws a Catmull-Rom spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 4)
 */
fun Canvas.catmullRomSpline(color: Color = Color.BLACK, thick: Float, vararg points: Pair<Int, Int>) {
	catmullRomSpline(color, thick, points.toList())
}

/**
 * Draws a quadratic bezier spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 3)
 */
fun Canvas.quadraticBezierSpline(
	color: Color = Color.BLACK,
	thick: Float,
	points: List<Pair<Int, Int>>
) {
	if (points.size < 3) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawSplineBezierQuadratic(array, points.size, thick, color.raw())
}

/**
 * Draws a quadratic bezier spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 3)
 */
fun Canvas.quadraticBezierSpline(
	color: Color = Color.BLACK,
	thick: Float,
	vararg points: Pair<Int, Int>
) {
	quadraticBezierSpline(color, thick, points.toList())
}

/**
 * Draws a cubic bezier spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 4)
 */
fun Canvas.cubicBezierSpline(
	color: Color = Color.BLACK,
	thick: Float,
	points: List<Pair<Int, Int>>
) {
	if (points.size < 4) return
	ensureDrawing()

	val array = memScoped {
		allocArray<Vector2>(points.size) { i -> points[i].toVector2() }
	}

	DrawSplineBezierCubic(array, points.size, thick, color.raw())
}

/**
 * Draws a cubic bezier spline on the canvas.
 * @param color The color of the spline
 * @param thick The thickness of the spline
 * @param points The points to draw the spline through (must be at least 4)
 */
fun Canvas.cubicBezierSpline(
	color: Color = Color.BLACK,
	thick: Float,
	vararg points: Pair<Int, Int>
) {
	cubicBezierSpline(color, thick, points.toList())
}
