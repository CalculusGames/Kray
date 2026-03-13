@file:OptIn(ExperimentalForeignApi::class)

package raylib

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.set
import kotlinx.cinterop.staticCFunction
import kray.toUByteArray
import raylib.internal.*

/**
 * Represents the Audio controller by raylib, which is used to manage audio devices and settings.
 */
object Audio {

	/**
	 * Whether the audio device is ready to be used.
	 */
	val ready: Boolean
		get() = IsAudioDeviceReady()

	/**
	 * Initializes the audio device.
	 */
	fun start() = InitAudioDevice()

	/**
	 * Stops the audio device.
	 */
	fun stop() = CloseAudioDevice()

	/**
	 * Plays a playable audio format, which can be a [Sound], [MusicStream], or [AudioStream]. If the audio is already playing, it will be restarted from the beginning.
	 * @param playable The playable audio format to play.
	 */
	fun play(playable: Playable) = playable.play()

	/**
	 * Stops a playable audio format, which can be a [Sound], [MusicStream], or [AudioStream]. If the audio is not playing, this function does nothing. After calling this function, the audio will be reset to the beginning and can be played again with [play].
	 * @param playable The playable audio format to stop.
	 */
	fun stop(playable: Playable) = playable.stop()

	/**
	 * Pauses a playable audio format, which can be a [Sound], [MusicStream], or [AudioStream]. If the audio is not playing, this function does nothing. After calling this function, the audio will be paused at its current position and can be resumed with [resume].
	 * @param playable The playable audio format to pause.
	 */
	fun pause(playable: Playable) = playable.pause()

	/**
	 * Resumes a playable audio format, which can be a [Sound], [MusicStream], or [AudioStream]. If the audio is not paused, this function does nothing. After calling this function, the audio will continue playing from its current position.
	 * @param playable The playable audio format to resume.
	 */
	fun resume(playable: Playable) = playable.resume()

	/**
	 * The master volume of the audio device, which can be set between 0.0 and 1.0.
	 */
	var masterVolume: Float
		get() = GetMasterVolume()
		set(value) {
			if (value !in 0.0f..1.0f) {
				throw IllegalArgumentException("Master volume must be between 0.0 and 1.0")
			}

			SetMasterVolume(value)
		}
}

/**
 * Represents a playable audio format in raylib.
 */
interface Playable {

	/**
	 * Whether the audio is valid and can be played.
	 */
	val valid: Boolean

	/**
	 * Whether the audio is currently playing.
	 */
	val playing: Boolean

	/**
	 * Plays the audio. If the audio is already playing, it will be restarted from the beginning.
	 */
	fun play()

	/**
	 * Stops the audio. If the audio is not playing, this function does nothing. After calling this function, the audio will be reset to the beginning and can be played again with [play].
	 */
	fun stop()

	/**
	 * Pauses the audio. If the audio is not playing, this function does nothing. After calling this function, the audio will be paused at its current position and can be resumed with [resume].
	 */
	fun pause()

	/**
	 * Resumes the audio. If the audio is not paused, this function does nothing. After calling this function, the audio will continue playing from its current position.
	 */
	fun resume()

	/**
	 * Sets the volume of the audio, which can be set between 0.0 and 1.0.
	 * @param volume The volume value to set, which must be between 0.0 and 1.0.
	 * @throws IllegalArgumentException if the volume value is not between 0.0 and
	 */
	fun setVolume(volume: Float)

	/**
	 * Sets the pitch of the audio, which can be set between 0.5 and 2.0.
	 * A pitch of 1.0 means normal speed, less than 1.0 means slower, and greater than 1.0 means faster.
	 * @param pitch The pitch value to set, which must be between 0.5 and 2.0.
	 * @throws IllegalArgumentException if the pitch value is not between 0.5 and 2.0.
	 */
	fun setPitch(pitch: Float)

	/**
	 * Sets the pan of the audio, which can be set between 0.0 and 1.0.
	 * A pan of 0.0 means the audio is fully left, 1.0 means the audio is fully right, and 0.5 means the audio is centered (default).
	 * @param pan The pan value to set, which must be between 0.0 and 1.0.
	 * @throws IllegalArgumentException if the pan value is not between 0.0 and 1.0.
	 */
	fun setPan(pan: Float)

}

/**
 * Represents a wave in raylib, which is a raw audio data structure that can be loaded from a file or generated procedurally.
 */
class Wave(internal val raw: CPointer<raylib.internal.Wave>) {

	/**
	 * The frame count of the wave, which can be set to any positive integer value.
	 *
	 * Frame count is the total number of audio frames in the wave. An audio frame is a single sample for each channel in the audio data.
	 * For example, if a wave has a sample size of 2 (stereo) and a frame count of 44100, it means that there are 44100 frames, and each frame contains 2 samples (one for the left channel and one for the right channel), resulting in a total of 88200 samples in the wave.
	 */
	var frameCount: Int
		get() = raw.pointed.frameCount.toInt()
		set(value) {
			if (value <= 0) {
				throw IllegalArgumentException("Frame count must be a positive integer")
			}

			raw.pointed.frameCount = value.toUInt()
		}

	/**
	 * The sample rate of the wave, which can be set to any positive integer value.
	 *
	 * Sample rate is the number of samples played per second.
	 * For example, a sample rate of 44100 means that 44100 samples are played per second (44.1 kHz), which is a common sample rate for audio. Higher sample rates can provide better audio quality but also require more processing power and storage space.
	 */
	var sampleRate: Int
		get() = raw.pointed.sampleRate.toInt()
		set(value) {
			if (value <= 0) {
				throw IllegalArgumentException("Sample rate must be a positive integer")
			}

			raw.pointed.sampleRate = value.toUInt()
		}

	/**
	 * The sample size of the wave, which can be set to any positive integer value.
	 *
	 * Sample size in a wave is the number of bit samples per audio frame, which is determined by the number of channels in the wave. For example, a wave with 1 channel (mono) has a sample size of 1, while a wave with 2 channels (stereo) has a sample size of 2.
	 * More channels can be used for surround sound or other multichannel audio formats, which would increase the sample size accordingly.
	 */
	var sampleSize: Int
		get() = raw.pointed.sampleSize.toInt()
		set(value) {
			if (value <= 0) {
				throw IllegalArgumentException("Sample size must be a positive integer")
			}

			raw.pointed.sampleSize = value.toUInt()
		}

	/**
	 * The number of channels in the wave, which can be set to any positive integer value.
	 *
	 * Channels refer to the number of separate audio tracks in the wave. For example, a wave with 1 channel is mono, while a wave with 2 channels is stereo. More channels can be used for surround sound or other multichannel audio formats.
	 */
	var channels: Int
		get() = raw.pointed.channels.toInt()
		set(value) {
			if (value <= 0) {
				throw IllegalArgumentException("Channels must be a positive integer")
			}

			raw.pointed.channels = value.toUInt()
		}

	/**
	 * The raw audio data of the wave, which can be set to any byte array of the appropriate size.
	 */
	var data: UByteArray
		get() {
			val size = frameCount * channels * (sampleSize / 8)
			return raw.pointed.data?.toUByteArray(size) ?: UByteArray(0)
		}
		set(value) {
			val size = frameCount * channels * (sampleSize / 8)
			if (value.size != size) {
				throw IllegalArgumentException("Data size must be equal to frameCount * channels * (sampleSize / 8) = $size")
			}

			val ptr = raw.pointed.data?.reinterpret()
				?: run {
					val alloc = nativeHeap.allocArray<UByteVar>(size).reinterpret<UByteVar>()
					raw.pointed.data = alloc.reinterpret()
					alloc
				}

			for (i in value.indices)
				ptr[i] = value[i]
		}

	/**
	 * Crops this wave to the specified start and end frame indices,
	 * which can be set to any positive integer values between 0 and frameCount - 1.
	 *
	 * After calling this function, the wave will be modified to contain only the audio data between the specified start and end frame indices.
	 * @param start The starting frame index to crop from, which must be between `0` and `frameCount - 1`.
	 * @param end The ending frame index to crop to, which must be between `0` and `frameCount - 1`, and must be greater than the start index.
	 * @throws IllegalArgumentException if the start or end indices are not between `0` and `frameCount - 1`, or if the start index is not less than the end index.
	 */
	fun crop(start: Int, end: Int) {
		if (start < 0 || end < 0 || start >= frameCount || end >= frameCount) {
			throw IllegalArgumentException("Start and end must be between 0 and frameCount - 1")
		}

		if (start >= end) {
			throw IllegalArgumentException("Start must be less than end")
		}

		WaveCrop(raw, start, end)
	}

	/**
	 * Formats this wave with the specified sample rate, sample size, and number of channels, which can be set to any positive integer values.
	 *
	 * After calling this function, the wave will be modified to have the specified sample rate, sample size, and number of channels. The audio data of the wave will be resampled and reformatted accordingly to match the new format.
	 * @param sampleRate The sample rate to format the wave with, which must be a positive integer.
	 * @param sampleSize The sample size to format the wave with, which must be a positive integer.
	 * @param channels The number of channels to format the wave with, which must be a positive integer.
	 * @throws IllegalArgumentException if the sample rate, sample size, or channels are not positive integers.
	 */
	fun format(sampleRate: Int, sampleSize: Int, channels: Int) {
		if (sampleRate <= 0 || sampleSize <= 0 || channels <= 0) {
			throw IllegalArgumentException("Sample rate, sample size, and channels must be positive integers")
		}

		WaveFormat(raw, sampleRate, sampleSize, channels)
	}

	/**
	 * Loads the samples of the wave into a float array, which can be used for audio processing or analysis.
	 * The size of the returned float array will be equal to the frame count of the wave multiplied by the number of channels in the wave.
	 *
	 * After calling this function, the samples of the wave will be loaded into a float array, where each sample is represented as a float value between -1.0 and 1.0. The order of the samples in the array will be interleaved by channel, meaning that for a stereo wave, the first sample will be for the left channel, the second sample will be for the right channel, and so on.
	 * @return A float array containing the samples of the wave, where each sample is a float value between -1.0 and 1.0, and the order of the samples is interleaved by channel.
	 */
	fun loadSamples(): FloatArray {
		val ptr = LoadWaveSamples(raw.pointed.readValue())
		val size = frameCount * channels

		val array = FloatArray(size) { i -> ptr?.get(i) ?: 0f }
		UnloadWaveSamples(ptr)

		return array
	}

	/**
	 * Unloads the wave from memory.
	 * This should be called when the wave is no longer needed to free up resources.
	 */
	fun unload() = UnloadWave(raw.pointed.readValue())

	companion object {

		/**
		 * Loads a wave from memory, which can be in WAV, OGG, or MP3 format. The loaded wave should be unloaded with [Wave.unload] when it is no longer needed.
		 * @param type The type of the wave data, which can be "WAV", "OGG", or "MP3".
		 * @param data The raw audio data of the wave.
		 * @return The loaded wave.
		 */
		fun loadFromMemory(type: String, data: UByteArray): Wave {
			val size = data.size
			val ptr = nativeHeap.allocArray<UByteVar>(size)
			for (i in data.indices)
				ptr[i] = data[i]

			val wave = LoadWaveFromMemory(type, ptr.reinterpret(), size)
			val wavePtr = nativeHeap.alloc<raylib.internal.Wave>()
			wave.place(wavePtr.ptr)

			return Wave(wavePtr.ptr)
		}

		/**
		 * Loads a wave from a file, which can be in WAV, OGG, or MP3 format. The loaded wave should be unloaded with [Wave.unload] when it is no longer needed.
		 * @param fileName The name of the file to load the wave from.
		 * @return The loaded wave.
		 */
		fun load(fileName: String): Wave {
			val wave = LoadWave(fileName)
			val ptr = nativeHeap.alloc<raylib.internal.Wave>()
			wave.place(ptr.ptr)

			return Wave(ptr.ptr)
		}

	}

}

/**
 * Represents an audio stream in raylib, which is a dynamic audio data structure that can be used for streaming audio data.
 * An audio stream should be unloaded when it is no longer needed.
 */
class AudioStream(internal val raw: CPointer<raylib.internal.AudioStream>) : Playable {

	override val valid: Boolean
		get() = IsAudioStreamValid(raw.pointed.readValue())

	override val playing: Boolean
		get() = IsAudioStreamPlaying(raw.pointed.readValue())

	override fun play() = PlayAudioStream(raw.pointed.readValue())
	override fun stop() = StopAudioStream(raw.pointed.readValue())
	override fun pause() = PauseAudioStream(raw.pointed.readValue())
	override fun resume() = ResumeAudioStream(raw.pointed.readValue())
	override fun setVolume(volume: Float) = SetAudioStreamVolume(raw.pointed.readValue(), volume)
	override fun setPitch(pitch: Float) = SetAudioStreamPitch(raw.pointed.readValue(), pitch)
	override fun setPan(pan: Float) = SetAudioStreamPan(raw.pointed.readValue(), pan)

	/**
	 * Sets a callback function to be called when the audio stream needs more data.
	 * The callback function should take a byte array as input, which will be filled with the audio data to be streamed.
	 * The size of the byte array will be equal to the number of frames requested by the audio stream multiplied by the sample size and number of channels of the audio stream.
	 *
	 * **Only one stream callback can be set at a time across all AudioStream instances. Setting a new stream callback will replace the previous one.**
	 * @param callback The callback function to set, which will be called when the audio stream needs more data. The callback function should take a byte array as input, which will be filled with the audio data to be streamed. The size of the byte array will be equal to the number of frames requested by the audio stream multiplied by the sample size and number of channels of the audio stream.
	 */
	fun setCallback(callback: (UByteArray) -> Unit) {
		audioCallback = callback
		SetAudioStreamCallback(raw.pointed.readValue(), audioCallbackFn)
	}

	/**
	 * Attaches a processor function to the audio stream, which will be called with the audio data before it is played.
	 * The processor function can be used to modify the audio data in real-time, such as applying effects or filters.
	 * The processor function should take a byte array as input, which will contain the audio data to be processed. The size of the byte array will be equal to the number of frames requested by the audio stream multiplied by the sample size and number of channels of the audio stream.
	 *
	 * **Only one processor callback can be attached at a time across all AudioStream instances. Attaching a new processor callback will replace the previous one.**
	 * @param callback The callback function to attach, which will be called with the audio data before it is played. The processor function can be used to modify the audio data in real-time, such as applying effects or filters. The processor function should take a byte array as input, which will contain the audio data to be processed. The size of the byte array will be equal to the number of frames requested by the audio stream multiplied by the sample size and number of channels of the audio stream.
	 */
	fun attachProcessor(callback: (UByteArray) -> Unit) {
		processorCallback = callback
		AttachAudioStreamProcessor(raw.pointed.readValue(), processorCallbackFn)
	}

	/**
	 * Detaches the processor function from the audio stream, which will stop calling the processor function with the audio data before it is played.
	 * After calling this function, the audio stream will no longer call the processor function and will play the audio data without any modifications.
	 */
	fun detachProcessor() {
		AttachAudioStreamProcessor(raw.pointed.readValue(), processorCallbackFn)
		processorCallback = null
	}

	/**
	 * Unloads the audio stream from memory.
	 * This should be called when the audio stream is no longer needed to free up resources.
	 */
	fun unload() = UnloadAudioStream(raw.pointed.readValue())

	companion object {

		// kotlin/native does not yet support capturing lambdas in C function pointers, so we have to store the callbacks in variables and use static C functions to call them
		// this limits us to only one stream callback and one processor callback across all AudioStream instances,
		// but it is a limitation of the current state of kotlin/native and should be sufficient for most use cases

		private var audioCallback: ((UByteArray) -> Unit)? = null
		private var audioCallbackFn = staticCFunction { buffer: COpaquePointer?, frames: UInt ->
			val size = frames.toInt()
			val bytes = buffer?.toUByteArray(size) ?: UByteArray(0)
			audioCallback!!(bytes)
		}

		private var processorCallback: ((UByteArray) -> Unit)? = null
		private var processorCallbackFn = staticCFunction { buffer: COpaquePointer?, frames: UInt ->
			val size = frames.toInt()
			val bytes = buffer?.toUByteArray(size) ?: UByteArray(0)
			processorCallback!!(bytes)
		}

		/**
		 * Creates an audio stream with the specified sample rate, sample size, and number of channels. The created audio stream should be unloaded with [AudioStream.unload] when it is no longer needed.
		 * @param sampleRate The sample rate of the audio stream, which can be set to any positive integer value.
		 * @param sampleSize The sample size of the audio stream, which can be set to any positive integer value.
		 * @param channels The number of channels in the audio stream, which can be set to any positive integer value.
		 * @return The created audio stream.
		 */
		fun create(sampleRate: Int, sampleSize: Int, channels: Int): AudioStream {
			if (sampleRate <= 0 || sampleSize <= 0 || channels <= 0) {
				throw IllegalArgumentException("Sample rate, sample size, and channels must be positive integers")
			}

			val stream = LoadAudioStream(sampleRate.toUInt(), sampleSize.toUInt(), channels.toUInt())
			val ptr = nativeHeap.alloc<raylib.internal.AudioStream>()
			stream.place(ptr.ptr)

			return AudioStream(ptr.ptr)
		}

		/**
		 * Sets the default buffer size for audio streams, which can be set to any positive integer value.
		 *
		 * The default buffer size is the number of frames that the audio stream will request from the callback function when it needs more data. A larger buffer size can reduce the chances of audio glitches or dropouts, but it can also increase latency. A smaller buffer size can reduce latency, but it can also increase the chances of audio glitches or dropouts. The optimal buffer size depends on the specific use case and hardware capabilities.
		 * @param size The default buffer size to set, which must be a positive integer.
		 * @throws IllegalArgumentException if the buffer size is not a positive integer.
		 */
		fun setDefaultBufferSize(size: Int) {
			if (size <= 0) {
				throw IllegalArgumentException("Default buffer size must be a positive integer")
			}

			SetAudioStreamBufferSizeDefault(size)
		}

		private var globalMixedProcessorCallback: ((UByteArray) -> Unit)? = null
		private var globalMixedProcessorCallbackFn = staticCFunction { buffer: COpaquePointer?, frames: UInt ->
			val size = frames.toInt()
			val bytes = buffer?.toUByteArray(size) ?: UByteArray(0)
			globalMixedProcessorCallback!!(bytes)
		}

		/**
		 * Attaches a processor function to the audio mixer, which will be called with the mixed audio data before it is played.
		 * The processor function can be used to modify the mixed audio data in real-time, such as applying effects or filters to the final output.
		 * The processor function should take a byte array as input, which will contain the mixed audio data to be processed. The size of the byte array will be equal to the number of frames requested by the audio mixer multiplied by the sample size and number of channels of the audio mixer.
		 *
		 * **Only one mixed processor callback can be attached at a time. Attaching a new mixed processor callback will replace the previous one.**
		 * @param callback The callback function to attach, which will be called with the mixed audio data before it is played. The processor function can be used to modify the mixed audio data in real-time, such as applying effects or filters to the final output. The processor function should take a byte array as input, which will contain the mixed audio data to be processed. The size of the byte array will be equal to the number of frames requested by the audio mixer multiplied by the sample size and number of channels of the audio mixer.
		 * @throws IllegalStateException if a mixed processor callback is already attached. Only one mixed processor callback can be attached at a time.
		 */
		fun attachMixedProcessor(callback: (UByteArray) -> Unit) {
			globalMixedProcessorCallback = callback
			AttachAudioMixedProcessor(globalMixedProcessorCallbackFn)
		}

		/**
		 * Detaches the processor function from the audio mixer, which will stop calling the processor function with the mixed audio data before it is played.
		 * After calling this function, the audio mixer will no longer call the processor function and will play the mixed audio data without any modifications.
		 */
		fun detachMixedProcessor() {
			AttachAudioMixedProcessor(globalMixedProcessorCallbackFn)
			globalMixedProcessorCallback = null
		}
	}
}

/**
 * Represents a sound in raylib, which is a processed audio data structure that can be played, paused, or stopped. A sound is typically loaded from a wave and should be unloaded when it is no longer needed.
 */
class Sound(internal val raw: CPointer<raylib.internal.Sound>) : Playable {

	override val valid: Boolean
		get() = IsSoundValid(raw.pointed.readValue())

	override val playing: Boolean
		get() = IsSoundPlaying(raw.pointed.readValue())

	override fun play() = PlaySound(raw.pointed.readValue())
	override fun stop() = StopSound(raw.pointed.readValue())
	override fun pause() = PauseSound(raw.pointed.readValue())
	override fun resume() = ResumeSound(raw.pointed.readValue())
	override fun setVolume(volume: Float) = SetSoundVolume(raw.pointed.readValue(), volume)
	override fun setPitch(pitch: Float) = SetSoundPitch(raw.pointed.readValue(), pitch)
	override fun setPan(pan: Float) = SetSoundPan(raw.pointed.readValue(), pan)

	/**
	 * Unloads the sound from memory.
	 * This should be called when the sound is no longer needed to free up resources.
	 */
	fun unload() = UnloadSound(raw.pointed.readValue())

	companion object {

		/**
		 * Loads a sound from a wave, which can be generated procedurally or loaded from a file. The loaded sound should be unloaded with [Sound.unload] when it is no longer needed.
		 * @param wave The wave to load the sound from.
		 * @return The loaded sound.
		 */
		fun loadFromWave(wave: Wave): Sound {
			val sound = raylib.internal.LoadSoundFromWave(wave.raw.pointed.readValue())
			val ptr = nativeHeap.alloc<raylib.internal.Sound>()
			sound.place(ptr.ptr)

			return Sound(ptr.ptr)
		}

		/**
		 * Loads a sound from a file, which can be in WAV, OGG, or MP3 format. The loaded sound should be unloaded with [Sound.unload] when it is no longer needed.
		 * @param fileName The name of the file to load the sound from.
		 * @return The loaded sound.
		 */
		fun load(fileName: String): Sound {
			val sound = LoadSound(fileName)
			val ptr = nativeHeap.alloc<raylib.internal.Sound>()
			sound.place(ptr.ptr)

			return Sound(ptr.ptr)
		}

	}

}

/**
 * Represents a music stream in raylib, which is a processed audio data structure that can be played, paused, or stopped. A music stream is typically loaded from a file and should be unloaded when it is no longer needed.
 */
class MusicStream(internal val raw: CPointer<Music>) : Playable {

	override val valid: Boolean
		get() = IsMusicValid(raw.pointed.readValue())

	override val playing: Boolean
		get() = IsMusicStreamPlaying(raw.pointed.readValue())

	override fun play() = PlayMusicStream(raw.pointed.readValue())
	override fun stop() = StopMusicStream(raw.pointed.readValue())
	override fun pause() = PauseMusicStream(raw.pointed.readValue())
	override fun resume() = ResumeMusicStream(raw.pointed.readValue())
	override fun setVolume(volume: Float) = SetMusicVolume(raw.pointed.readValue(), volume)
	override fun setPitch(pitch: Float) = SetMusicPitch(raw.pointed.readValue(), pitch)
	override fun setPan(pan: Float) = SetMusicPan(raw.pointed.readValue(), pan)

	/**
	 * Seeks the music stream to a specific position, which can be set to any positive float value between 0.0 and the total length of the music stream, in seconds.
	 * After calling this function, the music stream will be positioned at the specified time and can be played from that point with [play].
	 * @param position The position to seek to, which must be between 0.0 and the total length of the music stream, in seconds.
	 */
	fun seek(position: Float) = SeekMusicStream(raw.pointed.readValue(), position)

	/**
	 * The total length of the music stream in seconds, which is a positive float value.
	 */
	val length: Float
		get() = GetMusicTimeLength(raw.pointed.readValue())

	/**
	 * The current time played of the music stream in seconds, which is a positive float value between 0.0 and the total length of the music stream.
	 */
	val timePlayed: Float
		get() = GetMusicTimePlayed(raw.pointed.readValue())

	/**
	 * Unloads the music stream from memory.
	 * This should be called when the music stream is no longer needed to free up resources.
	 */
	fun unload() = UnloadMusicStream(raw.pointed.readValue())

	companion object {

		/**
		 * Loads a music stream from memory, which can be in WAV, OGG, or MP3 format. The loaded music stream should be unloaded with [MusicStream.unload] when it is no longer needed.
		 * @param type The type of the music stream data, which can be "WAV", "OGG", or "MP3".
		 * @param data The raw audio data of the music stream.
		 * @return The loaded music stream.
		 */
		fun loadFromMemory(type: String, data: UByteArray): MusicStream {
			val size = data.size
			val ptr = nativeHeap.allocArray<UByteVar>(size)
			for (i in data.indices)
				ptr[i] = data[i]

			val music = LoadMusicStreamFromMemory(type, ptr.reinterpret(), size)
			val musicPtr = nativeHeap.alloc<Music>()
			music.place(musicPtr.ptr)

			return MusicStream(musicPtr.ptr)
		}

		/**
		 * Loads a music stream from a file, which can be in WAV, OGG, or MP3 format. The loaded music stream should be unloaded with [MusicStream.unload] when it is no longer needed.
		 * @param fileName The name of the file to load the music stream from.
		 * @return The loaded music stream.
		 */
		fun load(fileName: String): MusicStream {
			val music = LoadMusicStream(fileName)
			val ptr = nativeHeap.alloc<Music>()
			music.place(ptr.ptr)

			return MusicStream(ptr.ptr)
		}

	}

}
