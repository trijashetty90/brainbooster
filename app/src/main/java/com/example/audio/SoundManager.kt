package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundEffect {
    CORRECT_ANSWER,
    INCORRECT_ANSWER,
    GAME_COMPLETION,
    LEVEL_UNLOCK,
    UI_INTERACTION
}

object SoundManager {
    private const val TAG = "SoundManager"
    private const val SAMPLE_RATE = 22050

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isSfxEnabled = MutableStateFlow(true)
    val isSfxEnabled: StateFlow<Boolean> = _isSfxEnabled.asStateFlow()

    private val _isMusicEnabled = MutableStateFlow(false)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

    private var musicJob: Job? = null

    fun toggleSfx() {
        _isSfxEnabled.value = !_isSfxEnabled.value
        if (_isSfxEnabled.value) {
            playSound(SoundEffect.UI_INTERACTION)
        }
    }

    fun toggleMusic() {
        val next = !_isMusicEnabled.value
        _isMusicEnabled.value = next
        if (next) {
            startBackgroundMusic()
        } else {
            stopBackgroundMusic()
        }
    }

    fun setSfxEnabled(enabled: Boolean) {
        _isSfxEnabled.value = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        if (enabled) startBackgroundMusic() else stopBackgroundMusic()
    }

    fun playSound(effect: SoundEffect) {
        if (!_isSfxEnabled.value) return

        scope.launch {
            try {
                val pcmData = when (effect) {
                    SoundEffect.CORRECT_ANSWER -> generateCorrectChime()
                    SoundEffect.INCORRECT_ANSWER -> generateIncorrectThud()
                    SoundEffect.GAME_COMPLETION -> generateVictoryFanfare()
                    SoundEffect.LEVEL_UNLOCK -> generateLevelUnlockSparkle()
                    SoundEffect.UI_INTERACTION -> generateClickPop()
                }
                playRawPcm(pcmData)
            } catch (e: Exception) {
                Log.w(TAG, "Audio synthesis error: ${e.message}")
            }
        }
    }

    private fun startBackgroundMusic() {
        musicJob?.cancel()
        musicJob = scope.launch {
            // Calm ambient pentatonic arpeggio chords loop
            val chordProgression = listOf(
                listOf(261.63, 329.63, 392.00), // C Maj
                listOf(220.00, 261.63, 329.63), // A Min
                listOf(174.61, 220.00, 261.63), // F Maj
                listOf(196.00, 246.94, 293.66)  // G Maj
            )

            var chordIdx = 0
            while (isActive && _isMusicEnabled.value) {
                val chord = chordProgression[chordIdx % chordProgression.size]
                chordIdx++

                val durationSec = 2.2
                val numSamples = (SAMPLE_RATE * durationSec).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    // Soft envelope: attack and decay
                    val envelope = sin(PI * (i.toDouble() / numSamples)).coerceIn(0.0, 1.0) * 0.18

                    var sample = 0.0
                    for (freq in chord) {
                        sample += sin(2.0 * PI * freq * t)
                    }
                    sample /= chord.size
                    buffer[i] = (sample * envelope * Short.MAX_VALUE).toInt().toShort()
                }

                playRawPcm(buffer)
                delay(300)
            }
        }
    }

    private fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playRawPcm(buffer: ShortArray) {
        var track: AudioTrack? = null
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufSize, buffer.size * 2)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            // Wait for completion then release
            val durationMs = (buffer.size.toDouble() / SAMPLE_RATE * 1000).toLong()
            Thread.sleep(durationMs)
        } catch (e: Exception) {
            // Ignore interruption or playback errors
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }

    // --- Sound Synthesis Helpers ---

    private fun generateCorrectChime(): ShortArray {
        // C5 (523Hz) -> E5 (659Hz) -> G5 (784Hz) arpeggio
        val notes = listOf(523.25, 659.25, 783.99)
        val noteDuration = 0.09
        val numSamples = (SAMPLE_RATE * noteDuration * notes.size).toInt()
        val buffer = ShortArray(numSamples)
        val noteSamples = (SAMPLE_RATE * noteDuration).toInt()

        for (n in notes.indices) {
            val freq = notes[n]
            for (i in 0 until noteSamples) {
                val globalIdx = n * noteSamples + i
                if (globalIdx >= numSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val decay = exp(-4.0 * (i.toDouble() / noteSamples))
                val wave = sin(2.0 * PI * freq * t) + 0.3 * sin(4.0 * PI * freq * t)
                buffer[globalIdx] = (wave * decay * 0.4 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    private fun generateIncorrectThud(): ShortArray {
        // Dissonant descending buzz
        val duration = 0.22
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val freq = 180.0 - (70.0 * (i.toDouble() / numSamples))
            val decay = exp(-3.5 * t)
            val wave = sin(2.0 * PI * freq * t) + 0.6 * sin(2.0 * PI * (freq * 1.41) * t)
            buffer[i] = (wave * decay * 0.35 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateVictoryFanfare(): ShortArray {
        // Fanfare: G4 (392Hz) -> C5 (523Hz) -> E5 (659Hz) -> G5 (784Hz) triumph
        val notes = listOf(392.00, 523.25, 659.25, 783.99)
        val noteDuration = 0.12
        val totalSec = noteDuration * notes.size + 0.2
        val numSamples = (SAMPLE_RATE * totalSec).toInt()
        val buffer = ShortArray(numSamples)
        val noteSamples = (SAMPLE_RATE * noteDuration).toInt()

        for (n in notes.indices) {
            val freq = notes[n]
            val count = if (n == notes.size - 1) noteSamples * 2 else noteSamples
            for (i in 0 until count) {
                val globalIdx = n * noteSamples + i
                if (globalIdx >= numSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val decay = exp(-2.0 * (i.toDouble() / count))
                val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)
                buffer[globalIdx] = (wave * decay * 0.45 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    private fun generateLevelUnlockSparkle(): ShortArray {
        // Celestial cascading sparkle
        val freqs = listOf(880.0, 1174.66, 1396.91, 1760.0, 2093.0)
        val duration = 0.35
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(numSamples)
        val step = numSamples / freqs.size

        for (fIdx in freqs.indices) {
            val freq = freqs[fIdx]
            for (i in 0 until (step * 2).coerceAtMost(numSamples - fIdx * step)) {
                val globalIdx = fIdx * step + i
                if (globalIdx >= numSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val decay = exp(-6.0 * (i.toDouble() / step))
                val wave = sin(2.0 * PI * freq * t)
                buffer[globalIdx] = (wave * decay * 0.35 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    private fun generateClickPop(): ShortArray {
        // Crisp 18ms UI pop
        val duration = 0.02
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val freq = 1200.0 - (800.0 * (i.toDouble() / numSamples))
            val decay = exp(-12.0 * t * 50)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * decay * 0.3 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
