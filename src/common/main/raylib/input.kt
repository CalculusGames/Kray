@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import raylib.internal.*

/**
 * The current mouse and cursor state.
 */
object Mouse {

	/**
	 * Whether the cursor is enabled on the screen.
	 */
	var isCursorEnabled: Boolean
		get() = IsCursorOnScreen()
		set(value) {
			if (value) EnableCursor() else DisableCursor()
		}

	/**
	 * Whether the cursor is hidden.
	 */
	var isCursorHidden: Boolean
		get() = IsCursorHidden()
		set(value) {
			if (value) HideCursor() else ShowCursor()
		}

	/**
	 * Indicates whether the cursor is currently on the screen.
	 */
	val isCursorOnScreen: Boolean
		get() = IsCursorOnScreen()

	/**
	 * The current X position of the mouse.
	 */
	var mouseX: Int
		get() = GetMouseX()
		set(value) = SetMousePosition(value, mouseY)

	/**
	 * The current Y position of the mouse.
	 */
	var mouseY: Int
		get() = GetMouseY()
		set(value) = SetMousePosition(mouseX, value)

	/**
	 * The mouse movement along the X axis between frames.
	 */
	val deltaX: Float
		get() = GetMouseDelta().useContents { x }

	/**
	 * The mouse movement along the Y axis between frames.
	 */
	val deltaY: Float
		get() = GetMouseDelta().useContents { y }

	/**
	 * Represents a mouse button that can be pressed.
	 */
	value class Button private constructor(internal val value: UInt) {
		companion object {
			/**
			 * The left mouse button.
			 */
			val LEFT: Button by lazy { Button(MOUSE_BUTTON_LEFT) }

			/**
			 * The right mouse button.
			 */
			val RIGHT: Button by lazy { Button(MOUSE_BUTTON_RIGHT) }

			/**
			 * The middle mouse button.
			 *
			 * Called when the wheel is pressed.
			 */
			val MIDDLE: Button by lazy { Button(MOUSE_BUTTON_MIDDLE) }

			/**
			 * The side mouse button.
			 *
			 * Usually only available on advanced mice.
			 */
			val SIDE: Button by lazy { Button(MOUSE_BUTTON_SIDE) }

			/**
			 * The extra mouse button.
			 *
			 * Usually only available on advanced mice.
			 */
			val EXTRA: Button by lazy { Button(MOUSE_BUTTON_EXTRA) }

			/**
			 * The forward mouse button.
			 *
			 * Usually only available on advanced mice.
			 */
			val FORWARD: Button by lazy { Button(MOUSE_BUTTON_FORWARD) }

			/**
			 * The back mouse button.
			 *
			 * Usually only available on advanced mice.
			 */
			val BACK: Button by lazy { Button(MOUSE_BUTTON_BACK) }

			private val allButtons by lazy {
				listOf(LEFT, RIGHT, MIDDLE, SIDE, EXTRA, FORWARD, BACK)
			}

			internal fun fromValue(value: Int): Button? {
				return allButtons.find { it.value.toInt() == value }
			}
		}
	}

	/**
	 * Whether a mouse button was pressed once.
	 * @param button The mouse button to check.
	 * @return true if pressed, false otherwise
	 */
	fun isPressed(button: Button)
		= IsMouseButtonPressed(button.value.toInt())

	/**
	 * Whether a mouse button was released once.
	 * @param button The mouse button to check.
	 * @return true if released, false otherwise
	 */
	fun isReleased(button: Button)
		= IsMouseButtonReleased(button.value.toInt())

	/**
	 * Whether a mouse button is currently in up state, or is not being pressed.
	 * @param button The mouse button to check.
	 * @return true if up, false otherwise
	 */
	fun isUp(button: Button)
		= IsMouseButtonUp(button.value.toInt())

	/**
	 * Whether a mouse button is currently in down state, or being pressed.
	 * @param button The mouse button to check.
	 * @return true if down, false otherwise
	 */
	fun isDown(button: Button)
		= IsMouseButtonDown(button.value.toInt())

	/**
	 * The X movement for the mouse wheel.
	 */
	val wheelX: Float
		get() = GetMouseWheelMoveV().useContents { x }

	/**
	 * The Y movement for the mouse wheel.
	 */
	val wheelY: Float
		get() = GetMouseWheelMoveV().useContents { y }

	/**
	 * The currently displayed icon over the cursor.
	 */
	value class Cursor private constructor(internal val value: UInt) {
		companion object {
			/**
			 * The default mouse cursor for the computer.
			 */
			val DEFAULT: Cursor by lazy { Cursor(MOUSE_CURSOR_DEFAULT) }

			/**
			 * The mouse is shaped like an arrow.
			 */
			val ARROW: Cursor by lazy { Cursor(MOUSE_CURSOR_ARROW) }

			/**
			 * The mouse is writing text.
			 */
			val IBEAM: Cursor by lazy { Cursor(MOUSE_CURSOR_IBEAM) }

			/**
			 * The mouse has a crosshair on it.
			 */
			val CROSSHAIR: Cursor by lazy { Cursor(MOUSE_CURSOR_CROSSHAIR) }

			/**
			 * The mouse is shaped like a pointing hand.
			 */
			val POINTING_HAND: Cursor by lazy { Cursor(MOUSE_CURSOR_POINTING_HAND) }

			/**
			 * The mouse is resizing an element horizontally.
			 */
			val HORIZONTAL_RESIZE: Cursor by lazy { Cursor(MOUSE_CURSOR_RESIZE_EW) }

			/**
			 * The mouse is resizing an element vertically.
			 */
			val VERTICAL_RESIZE: Cursor by lazy { Cursor(MOUSE_CURSOR_RESIZE_NS) }

			/**
			 * The mouse is resizing an element diagonally, starting from top-left.
			 */
			val DIAGONAL_RESIZE_LEFT: Cursor by lazy { Cursor(MOUSE_CURSOR_RESIZE_NWSE) }

			/**
			 * The mouse is resizing an element diagonally, starting from top-right.
			 */
			val DIAGONAL_RESIZE_RIGHT: Cursor by lazy { Cursor(MOUSE_CURSOR_RESIZE_NESW) }

			/**
			 * The mouse is using the omnidirectional resize icon.
			 */
			val RESIZE_ALL: Cursor by lazy { Cursor(MOUSE_CURSOR_RESIZE_ALL) }

			/**
			 * The mouse action is not allowed.
			 */
			val BLOCKED: Cursor by lazy { Cursor(MOUSE_CURSOR_NOT_ALLOWED) }
		}
	}

	/**
	 * Sets the icon of the mouse.
	 * @param cursor The cursor icon to set.
	 */
	fun setCursor(cursor: Cursor) = SetMouseCursor(cursor.value.toInt())

	/**
	 * Sets the size scale of the mouse pointer.
	 * @param x The X scale to use.
	 * @param y The Y scale to use.
	 */
	fun setScale(x: Float, y: Float) = SetMouseScale(x, y)
}

/**
 * Represents a keyboard key.
 * @property chars The (lowercase) characters associated with the key, if applicable.
 */
enum class Key(
	internal val value0: UInt,
	vararg val chars: Char
) {

	//<editor-fold desc="Keys" defaultState=collapsed>

	/**
	 * The null key, used for no key pressed.
	 */
	NULL(KEY_NULL),

	/**
	 * A
	 */
	A(KEY_A, 'a'),

	/**
	 * B
	 */
	B(KEY_B, 'b'),

	/**
	 * C
	 */
	C(KEY_C, 'c'),

	/**
	 * D
	 */
	D(KEY_D, 'd'),

	/**
	 * E
	 */
	E(KEY_E, 'e'),

	/**
	 * F
	 */
	F(KEY_F, 'f'),

	/**
	 * G
	 */
	G(KEY_G, 'g'),

	/**
	 * H
	 */
	H(KEY_H, 'h'),

	/**
	 * I
	 */
	I(KEY_I, 'i'),

	/**
	 * J
	 */
	J(KEY_J, 'j'),

	/**
	 * K
	 */
	K(KEY_K, 'k'),

	/**
	 * L
	 */
	L(KEY_L, 'l'),

	/**
	 * M
	 */
	M(KEY_M, 'm'),

	/**
	 * N
	 */
	N(KEY_N, 'n'),

	/**
	 * O
	 */
	O(KEY_O, 'o'),

	/**
	 * P
	 */
	P(KEY_P, 'p'),

	/**
	 * Q
	 */
	Q(KEY_Q, 'q'),

	/**
	 * R
	 */
	R(KEY_R, 'r'),

	/**
	 * S
	 */
	S(KEY_S, 's'),

	/**
	 * T
	 */
	T(KEY_T, 't'),

	/**
	 * U
	 */
	U(KEY_U, 'u'),

	/**
	 * V
	 */
	V(KEY_V, 'v'),

	/**
	 * W
	 */
	W(KEY_W, 'w'),

	/**
	 * X
	 */
	X(KEY_X, 'x'),

	/**
	 * Y
	 */
	Y(KEY_Y, 'y'),

	/**
	 * Z
	 */
	Z(KEY_Z, 'z'),

	/**
	 * 1 or !
	 */
	ONE(KEY_ONE, '1', '!'),

	/**
	 * 2 or @
	 */
	TWO(KEY_TWO, '2', '@'),

	/**
	 * 3 or #
	 */
	THREE(KEY_THREE, '3', '#'),

	/**
	 * 4 or $
	 */
	FOUR(KEY_FOUR, '4', '$'),

	/**
	 * 5 or %
	 */
	FIVE(KEY_FIVE, '5', '%'),

	/**
	 * 6 or ^
	 */
	SIX(KEY_SIX, '6', '^'),

	/**
	 * 7 or &
	 */
	SEVEN(KEY_SEVEN, '7', '&'),

	/**
	 * 8 or *
	 */
	EIGHT(KEY_EIGHT, '8', '*'),

	/**
	 * 9 or (
	 */
	NINE(KEY_NINE, '9', '('),

	/**
	 * 0 or )
	 */
	ZERO(KEY_ZERO, '0', ')'),

	/**
	 * ' or "
	 */
	APOSTROPHE(KEY_APOSTROPHE, '\'', '"'),

	/**
	 * , or <
	 */
	COMMA(KEY_COMMA, ',', '<'),

	/**
	 * - or _
	 */
	MINUS(KEY_MINUS, '-', '_'),

	/**
	 * . or >
	 */
	PERIOD(KEY_PERIOD, '.', '>'),

	/**
	 * / or ?
	 */
	SLASH(KEY_SLASH, '/', '?'),

	/**
	 * ; or :
	 */
	SEMICOLON(KEY_SEMICOLON, ';', ':'),

	/**
	 * = or +
	 */
	EQUAL(KEY_EQUAL, '=', '+'),

	/**
	 * [ or {
	 */
	LEFT_BRACKET(KEY_LEFT_BRACKET, '[', '{'),

	/**
	 * ] or }
	 */
	RIGHT_BRACKET(KEY_RIGHT_BRACKET, ']', '}'),

	/**
	 * ` or ~
	 */
	GRAVE(KEY_GRAVE, '`', '~'),

	/**
	 * `' '`
	 */
	SPACE(KEY_SPACE, ' '),

	/**
	 * Escape
	 */
	ESC(KEY_ESCAPE),

	/**
	 * Enter/Return
	 */
	ENTER(KEY_ENTER),

	/**
	 * `'	'`
	 */
	TAB(KEY_TAB, '	'),

	/**
	 * Backspace/Delete
	 */
	BACKSPACE(KEY_BACKSPACE),

	/**
	 * Insert
	 */
	INSERT(KEY_INSERT),

	/**
	 * Delete
	 *
	 * Different from [BACKSPACE] as some smaller keyboards may not have this button.
	 */
	DELETE(KEY_DELETE),

	/**
	 * Right
	 */
	RIGHT_ARROW(KEY_RIGHT),

	/**
	 * Left
	 */
	LEFT_ARROW(KEY_LEFT),

	/**
	 * Up
	 */
	UP_ARROW(KEY_UP),

	/**
	 * Down
	 */
	DOWN_ARROW(KEY_DOWN),

	/**
	 * Page Up
	 */
	PAGE_UP(KEY_PAGE_UP),

	/**
	 * Page Down
	 */
	PAGE_DOWN(KEY_PAGE_DOWN),

	/**
	 * Home
	 */
	HOME(KEY_HOME),

	/**
	 * End
	 */
	END(KEY_END),

	/**
	 * Caps Lock
	 */
	CAPS_LOCK(KEY_CAPS_LOCK),

	/**
	 * Scroll Lock
	 */
	SCROLL_LOCK(KEY_SCROLL_LOCK),

	/**
	 * Numpad Lock
	 */
	NUM_LOCK(KEY_NUM_LOCK),

	/**
	 * Print Screen
	 */
	PRINT_SCREEN(KEY_PRINT_SCREEN),

	/**
	 * Pause
	 */
	PAUSE(KEY_PAUSE),

	/**
	 * Function Key 1
	 */
	F1(KEY_F1),

	/**
	 * Function Key 2
	 */
	F2(KEY_F2),

	/**
	 * Function Key 3
	 */
	F3(KEY_F3),

	/**
	 * Function Key 4
	 */
	F4(KEY_F4),

	/**
	 * Function Key 5
	 */
	F5(KEY_F5),

	/**
	 * Function Key 6
	 */
	F6(KEY_F6),

	/**
	 * Function Key 7
	 */
	F7(KEY_F7),

	/**
	 * Function Key 8
	 */
	F8(KEY_F8),

	/**
	 * Function Key 9
	 */
	F9(KEY_F9),

	/**
	 * Function Key 10
	 */
	F10(KEY_F10),

	/**
	 * Function Key 11
	 */
	F11(KEY_F11),

	/**
	 * Function Key 12
	 */
	F12(KEY_F12),

	/**
	 * Left Shift
	 */
	LSHIFT(KEY_LEFT_SHIFT),

	/**
	 * Left Control
	 */
	LCTRL(KEY_LEFT_CONTROL),

	/**
	 * Left Alt
	 */
	LALT(KEY_LEFT_ALT),

	/**
	 * Left Super
	 *
	 * Command (macOS) or Windows Key
	 */
	LSUPER(KEY_LEFT_SUPER),

	/**
	 * Right Shift
	 */
	RSHIFT(KEY_RIGHT_SHIFT),

	/**
	 * Right Control
	 */
	RCTRL(KEY_RIGHT_CONTROL),

	/**
	 * Right Alt
	 */
	RALT(KEY_RIGHT_ALT),

	/**
	 * Right Super
	 *
	 * Command (macOS) or Windows Key
	 */
	RSUPER(KEY_RIGHT_SUPER),

	/**
	 * Menu
	 */
	MENU(KEY_KB_MENU),

	/**
	 * Keypad 0
	 */
	KP_0(KEY_KP_0, '0'),

	/**
	 * Keypad 1
	 */
	KP_1(KEY_KP_1, '1'),

	/**
	 * Keypad 2
	 */
	KP_2(KEY_KP_2, '2'),

	/**
	 * Keypad 3
	 */
	KP_3(KEY_KP_3, '3'),

	/**
	 * Keypad 4
	 */
	KP_4(KEY_KP_4, '4'),

	/**
	 * Keypad 5
	 */
	KP_5(KEY_KP_5, '5'),

	/**
	 * Keypad 6
	 */
	KP_6(KEY_KP_6, '6'),

	/**
	 * Keypad 7
	 */
	KP_7(KEY_KP_7, '7'),

	/**
	 * Keypad 8
	 */
	KP_8(KEY_KP_8, '8'),

	/**
	 * Keypad 9
	 */
	KP_9(KEY_KP_9, '9'),

	/**
	 * Keypad .
	 */
	KP_DECIMAL(KEY_KP_DECIMAL, '.'),

	/**
	 * Keypad /
	 */
	KP_DIVIDE(KEY_KP_DIVIDE, '/'),

	/**
	 * Keypad *
	 */
	KP_MULTIPLY(KEY_KP_MULTIPLY, '*'),

	/**
	 * Keypad +
	 */
	KP_ADD(KEY_KP_ADD, '+'),

	/**
	 * Keypad Enter
	 */
	KP_ENTER(KEY_KP_ENTER),

	/**
	 * Keypad Equal
	 */
	KP_EQUAL(KEY_KP_EQUAL, '='),

	//</editor-fold>

	;

	val value: Int
		get() = value0.toInt()

	companion object {
		/**
		 * Tries to find a key based on its input code.
		 * @param value The value to find
		 * @return the key found, or null if not found
		 */
		fun find(value: Int) = entries.find { it.value == value }

		/**
		 * Tries to find the first key based on its associated characters.
		 * @param char The character to find
		 * @return the primary key found, or null if not found
		 */
		fun find(char: Char) = entries.find { char in it.chars }

		/**
		 * Tries to find all keys based on their associated characters.
		 * @param char The character to find
		 * @return all keys associated with this character
		 */
		fun findAll(char: Char) = entries.filter { char in it.chars }
	}
}

/**
 * The current keyboard state.
 */
object Keyboard {

	/**
	 * Whether the key was pressed once.
	 * @param key The key to check
	 * @return true if pressed, false otherwise
	 */
	fun isPressed(key: Key) = IsKeyPressed(key.value)

	/**
	 * Whether a character was pressed once.
	 * @param char The character to check
	 * @return true if pressed, false otherwise
	 */
	fun isPressed(char: Char) = Key.findAll(char).map { isPressed(it) }.first { it }

	/**
	 * Whether the key was released once.
	 * @param key The key to check
	 * @return true if released, false otherwise
	 */
	fun isReleased(key: Key) = IsKeyReleased(key.value)

	/**
	 * Whether a character was released once.
	 * @param char The character to check
	 * @return true if released, false otherwise
	 */
	fun isReleased(char: Char) = Key.findAll(char).map { isReleased(it) }.first { it }

	/**
	 * Whether the key is in down state, or being pressed.
	 * @param key The key to check
	 * @return true if down, false otherwise
	 */
	fun isDown(key: Key) = IsKeyDown(key.value)

	/**
	 * Whether a character is in down state, or being pressed.
	 * @param char The character to check
	 * @return true if down, false otherwise
	 */
	fun isDown(char: Char) = Key.findAll(char).map { isDown(it) }.first { it }

	/**
	 * Whether the key is in up state, or not being pressed.
	 * @param key The key to check
	 * @return true if up, false otherwise
	 */
	fun isUp(key: Key) = IsKeyUp(key.value)

	/**
	 * Whether a character is in up state, or not being pressed.
	 * @param char The character to check
	 * @return true if up, false otherwise
	 */
	fun isUp(char: Char) = Key.findAll(char).map { isUp(it) }.first { it }

	/**
	 * Whether the control key is currently down and being pressed.
	 */
	val isCtrlDown: Boolean
		get() = isDown(Key.LCTRL) || isDown(Key.RCTRL)

	/**
	 * Whether the shift key is currently down and being pressed.
	 */
	val isShiftDown: Boolean
		get() = isDown(Key.LSHIFT) || isDown(Key.RSHIFT)

	/**
	 * Whether the alt key is currently down and being pressed.
	 */
	val isAltDown: Boolean
		get() = isDown(Key.LALT) || isDown(Key.RALT)

	/**
	 * Whether the super key is currently down and being pressed.
	 */
	val isSuperDown: Boolean
		get() = isDown(Key.LSUPER) || isDown(Key.RSUPER)

	/**
	 * The last key pressed, or the next one in the pressing queue.
	 */
	val keyPressed: Key
		get() = Key.find(GetKeyPressed()) ?: Key.NULL

	/**
	 * The last character pressed, or the next one in the pressing queue.
	 */
	val charPressed: Char
		get() = GetCharPressed().toChar()

	/**
	 * Sets the exit key for the program. Default is [Key.ESC].
	 * @param key The key to set.
	 */
	fun setExitKey(key: Key) = SetExitKey(key.value)

}

/**
 * The current gamepad state, if one or multiple are connected.
 *
 * Up to four gamepads can be connected at one time. The `index` parameter
 * refers to the current index of which gamepad to read from, such as `0`
 * for the first gamepad (Player 1), `1` for the second gamepad (Player 2),
 * `2` for the third gamepad (Player 3), and `3` for the fourth gamepad
 * (Player 4).
 */
object GamePad {

	/**
	 * Whether the gamepad is available.
	 * @param index The index of the connected gamepad.
	 * @return true if the gamepad is available, false otherwise
	 */
	fun isAvailable(index: Int = 0) = IsGamepadAvailable(index)

	/**
	 * Gets the internal gamepad name.
	 * @param index The index of the connected gamepad.
	 * @return the name of the gamepad, or null if not found
	 */
	fun getName(index: Int = 0) = GetGamepadName(index)?.toKString()

	/**
	 * A specific gamepad button.
	 */
	value class Button private constructor(internal val value: UInt) {
		companion object {
			/**
			 * An unknown button was pressed.
			 */
			val UNKNOWN: Button by lazy { Button(GAMEPAD_BUTTON_UNKNOWN) }

			/**
			 * The up botton on the directional pad or left face.
			 */
			val DPAD_UP: Button by lazy { Button(GAMEPAD_BUTTON_LEFT_FACE_UP) }

			/**
			 * The down button on the directional pad or left face.
			 */
			val DPAD_DOWN: Button by lazy { Button(GAMEPAD_BUTTON_LEFT_FACE_DOWN) }

			/**
			 * The left button on the directional pad or left face.
			 */
			val DPAD_LEFT: Button by lazy { Button(GAMEPAD_BUTTON_LEFT_FACE_LEFT) }

			/**
			 * The right button on the directional pad or left face.
			 */
			val DPAD_RIGHT: Button by lazy { Button(GAMEPAD_BUTTON_RIGHT_FACE_LEFT) }

			/**
			 * The up button on the right face.
			 *
			 * `△` on PlayStation / `Y` for Xbox / `X` for Switch
			 */
			val UP: Button by lazy { Button(GAMEPAD_BUTTON_RIGHT_FACE_UP) }

			/**
			 * The down button on the right face.
			 *
			 * `✕` on PlayStation / `A` for Xbox / `B` for Switch
			 */
			val DOWN: Button by lazy { Button(GAMEPAD_BUTTON_RIGHT_FACE_DOWN) }

			/**
			 * The left button on the right face.
			 *
			 * `□` on PlayStation / `X` for Xbox / `Y` for Switch
			 */
			val LEFT: Button by lazy { Button(GAMEPAD_BUTTON_LEFT_FACE_LEFT) }

			/**
			 * The right button on the right face.
			 *
			 * `○` on PlayStation / `B` for Xbox / `A` for Switch
			 */
			val RIGHT: Button by lazy { Button(GAMEPAD_BUTTON_RIGHT_FACE_LEFT) }

			/**
			 * The left button.
			 */
			val LB: Button by lazy { Button(GAMEPAD_BUTTON_LEFT_TRIGGER_1) }

			/**
			 * The left trigger.
			 */
			val LT: Button by lazy { Button(GAMEPAD_BUTTON_LEFT_TRIGGER_2) }

			/**
			 * The right button.
			 */
			val RB: Button by lazy { Button(GAMEPAD_BUTTON_RIGHT_TRIGGER_1) }

			/**
			 * The right trigger.
			 */
			val RT: Button by lazy { Button(GAMEPAD_BUTTON_RIGHT_TRIGGER_2) }

			/**
			 * The middle button.
			 *
			 * PS on PlayStation / Xbox Logo on Xbox / Home on Switch
			 */
			val MIDDLE: Button by lazy { Button(GAMEPAD_BUTTON_MIDDLE) }

			/**
			 * The middle left button.
			 *
			 * Select or Share on PlayStation / Back or Share on Xbox / View on Switch
			 */
			val MIDDLE_LEFT: Button by lazy { Button(GAMEPAD_BUTTON_MIDDLE_LEFT) }

			/**
			 * The middle right button.
			 *
			 * Start on PlayStation / Options on Xbox
			 */
			val MIDDLE_RIGHT: Button by lazy { Button(GAMEPAD_BUTTON_MIDDLE_RIGHT) }

			private val allButtons by lazy {
				listOf(
					UNKNOWN, DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
					UP, DOWN, LEFT, RIGHT,
					LB, LT, RB, RT,
					MIDDLE, MIDDLE_LEFT, MIDDLE_RIGHT
				)
			}

			internal fun fromValue(value: Int): Button? {
				return allButtons.find { it.value.toInt() == value }
			}
		}
	}

	/**
	 * Whether the current button was pressed once.
	 * @param button The button to check.
	 * @param index The index of the connected gamepad.
	 * @return true if pressed, false otherwise
	 */
	fun isPressed(button: Button, index: Int = 0)
		= IsGamepadButtonPressed(index, button.value.toInt())

	/**
	 * Whether the current button was released once.
	 * @param button The button to check.
	 * @param index The index of the connected gamepad.
	 * @return true if released, false otherwise
	 */
	fun isreleased(button: Button, index: Int = 0)
		= IsGamepadButtonReleased(index, button.value.toInt())

	/**
	 * Whether the current button is currently in down state, or being pressed.
	 * @param button The button to check.
	 * @param index The index of the connected gamepad.
	 * @return true if down, false otherwise
	 */
	fun isDown(button: Button, index: Int = 0)
		= IsGamepadButtonDown(index, button.value.toInt())

	/**
	 * Whether the current button is currently in up state, or not being pressed.
	 * @param button The button to check.
	 * @param index The index of the connected gamepad.
	 * @return true if down, false otherwise
	 */
	fun isUp(button: Button, index: Int = 0)
		= IsGamepadButtonUp(index, button.value.toInt())

	/**
	 * The last pressed button on any connected gamepad.
	 */
	val lastPressed: Button?
		get() {
			val last = GetGamepadButtonPressed()
			return Button.fromValue(last)
		}

	/**
	 * A specific gamepad axis movement.
	 */
	value class Axis private constructor(internal val value: UInt) {
		companion object {
			/**
			 * The left joystick's X axis.
			 */
			val LEFT_X: Axis by lazy { Axis(GAMEPAD_AXIS_LEFT_X) }

			/**
			 * The left joystick's Y axis.
			 */
			val LEFT_Y: Axis by lazy { Axis(GAMEPAD_AXIS_LEFT_Y) }

			/**
			 * The right joystick's X axis.
			 */
			val RIGHT_X: Axis by lazy { Axis(GAMEPAD_AXIS_RIGHT_X) }

			/**
			 * The right joystick's Y axis.
			 */
			val RIGHT_Y: Axis by lazy { Axis(GAMEPAD_AXIS_RIGHT_Y) }

			/**
			 * The left trigger's axis.
			 *
			 * This is useful to determine how much the trigger is pressed down.
			 */
			val LT: Axis by lazy { Axis(GAMEPAD_AXIS_LEFT_TRIGGER) }

			/**
			 * The right trigger's axis.
			 *
			 * This is useful to determine how much the trigger is pressed down.
			 */
			val RT: Axis by lazy { Axis(GAMEPAD_AXIS_RIGHT_TRIGGER) }
		}
	}

	/**
	 * Gets the axi available to the gamepad.
	 * @param index The index of the currently connected gamepad.
	 * @return the number of axi available on the gamepad
	 */
	fun getAxisCount(index: Int = 0) = GetGamepadAxisCount(index)

	/**
	 * Gets the axis movement on the specified gamepad.
	 *
	 * Values will be in between `0.0` and `1.0`.
	 * @param axis The axis movement to check.
	 * @param index The index of the currently connected gamepad.
	 * @return the moved axis value between `0.0` and `1.0`
	 */
	fun getAxisMovement(axis: Axis, index: Int = 0)
		= GetGamepadAxisMovement(index, axis.value.toInt())

	/**
	 * Vibrates the controller.
	 * @param left The percentage power for the left motor (0.0 to 1.0)
	 * @param right The percentage power for the right motor (0.0 to 1.0)
	 * @param time The time of vibration, in seconds.
	 * @param index The index of the currently connected controller.
	 */
	fun vibrate(left: Float, right: Float, time: Float, index: Int = 0)
		= SetGamepadVibration(index, left, right, time)

}
