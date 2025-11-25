package com.example.driftui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.* import android.view.animation.LinearInterpolator
import java.io.IOException

// =============================================================================================
// GLOBAL API: AUDIO
// =============================================================================================

fun playSound(
    file: String,
    volume: Double = 1.0,
    pitch: Double = 1.0,
    speed: Double = 1.0,
    pan: Double = 0.0,
    panEnd: Double? = null,
    startTime: Int = 0,
    endTime: Int = 0,
    fadeIn: Int = 0,
    backgroundPlay: Boolean = true,
    stopSystemAudio: Boolean = false,
    override: Boolean = false,
    loop: Boolean = false
) {
    DriftAudio.play(file, volume, pitch, speed, pan, panEnd, startTime, endTime, fadeIn, backgroundPlay, stopSystemAudio, override, loop)
}

fun stopSound(file: String) {
    DriftAudio.stop(file)
}

fun stopAllSounds() {
    DriftAudio.stopAll()
}

// =============================================================================================
// GLOBAL API: HAPTICS
// =============================================================================================

// 1. The Enum (Internal logic)
enum class Haptic {
    Selection, Light, Medium, Heavy, Success, Warning, Error
}

// 2. The Global Aliases (The "Hella Easy" Syntax)
val selection = Haptic.Selection
val heavy     = Haptic.Heavy
val success   = Haptic.Success
val warning   = Haptic.Warning
val error     = Haptic.Error

// Note: 'light' and 'medium' are used by Fonts, so we use synonyms here:
val soft      = Haptic.Light
val impact    = Haptic.Medium

/**
 * Triggers haptic feedback.
 */
fun haptic(type: Haptic) {
    DriftHaptics.perform(type)
}

// =============================================================================================
// ENGINE 1: AUDIO
// =============================================================================================

object DriftAudio {
    var context: Context? = null
    private val activePlayers = mutableMapOf<String, MutableList<MediaPlayer>>()
    private val foregroundOnlyPlayers = mutableListOf<MediaPlayer>()
    private val pausedByApp = mutableListOf<MediaPlayer>()
    private val handlers = mutableMapOf<MediaPlayer, Runnable>()

    private val focusRequests = mutableMapOf<MediaPlayer, AudioFocusRequest?>()
    private var silenceFocusRequest: AudioFocusRequest? = null
    private var silenceTrack: AudioTrack? = null

    private var isBlockingSystemAudio = false
    private val globalFocusListener = AudioManager.OnAudioFocusChangeListener { }

    fun initialize(ctx: Context) {
        if (context == null) context = ctx.applicationContext
    }

    fun requestSilence(ctx: Context) {
        isBlockingSystemAudio = true
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val res = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(globalFocusListener)
                .build()

            silenceFocusRequest = focusRequest
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(globalFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }

        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (silenceTrack == null || silenceTrack?.state == AudioTrack.STATE_UNINITIALIZED) {
                try {
                    val sampleRate = 44100
                    val bufSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
                    silenceTrack = AudioTrack.Builder()
                        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                        .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                        .setBufferSizeInBytes(bufSize).setTransferMode(AudioTrack.MODE_STATIC).build()

                    val silentData = ByteArray(bufSize)
                    silenceTrack?.write(silentData, 0, bufSize)
                    silenceTrack?.setLoopPoints(0, bufSize / 2, -1)
                } catch (e: Exception) {}
            }
            try { if (silenceTrack?.playState != AudioTrack.PLAYSTATE_PLAYING) silenceTrack?.play() } catch (e: Exception) {}
        }
    }

    fun releaseSilence(ctx: Context) {
        isBlockingSystemAudio = false
        try {
            if (silenceTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) silenceTrack?.stop()
            silenceTrack?.release()
            silenceTrack = null
        } catch (e: Exception) {}

        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            silenceFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            silenceFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(globalFocusListener)
        }
    }

    fun onAppPause() {
        foregroundOnlyPlayers.forEach { mp ->
            if (mp.isPlaying) { mp.pause(); pausedByApp.add(mp) }
        }
        if (silenceTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) silenceTrack?.pause()
    }

    fun onAppResume() {
        pausedByApp.forEach { mp -> try { mp.start() } catch (e: Exception) {} }
        pausedByApp.clear()
        if (isBlockingSystemAudio && context != null) requestSilence(context!!)
    }

    fun play(file: String, vol: Double, pitch: Double, speed: Double, panStart: Double, panEnd: Double?, start: Int, end: Int, fade: Int, bg: Boolean, sysStop: Boolean, override: Boolean, loop: Boolean) {
        val ctx = context ?: return
        if (override) stop(file)

        try {
            val d = ctx.assets.openFd(file)
            val mp = MediaPlayer()
            mp.setDataSource(d.fileDescriptor, d.startOffset, d.length)
            d.close()
            mp.prepare()

            if (sysStop) {
                val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                        .setOnAudioFocusChangeListener { }.build()
                    am.requestAudioFocus(req)
                    focusRequests[mp] = req
                } else {
                    @Suppress("DEPRECATION")
                    am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                }
            }

            if (bg) mp.setWakeMode(ctx, PowerManager.PARTIAL_WAKE_LOCK) else foregroundOnlyPlayers.add(mp)
            if (start > 0) mp.seekTo(start)

            val dur = mp.duration
            val finalEnd = if (end in 1 until dur) end else dur
            val playDur = finalEnd - start
            if (playDur <= 0) { mp.release(); return }

            if (Build.VERSION.SDK_INT >= 23) {
                val p = mp.playbackParams
                p.pitch = pitch.toFloat(); p.speed = speed.toFloat()
                mp.playbackParams = p
            }
            mp.isLooping = loop

            // Mixer
            val pStart = panStart.toFloat().coerceIn(-1f, 1f)
            val v = vol.toFloat()
            var curVol = if (fade > 0) 0f else v
            var curPan = pStart
            val targetPan = panEnd?.toFloat()?.coerceIn(-1f, 1f)

            fun updateMixer() {
                try {
                    val l = curVol * (if (curPan > 0) 1f - curPan else 1f)
                    val r = curVol * (if (curPan < 0) 1f + curPan else 1f)
                    mp.setVolume(l, r)
                } catch (e: Exception) {}
            }
            updateMixer()

            val list = activePlayers.getOrPut(file) { mutableListOf() }
            list.add(mp)

            val onFinish = Runnable {
                try {
                    if (mp.isPlaying) mp.stop()
                    mp.release()
                    if (sysStop) {
                        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            focusRequests[mp]?.let { am.abandonAudioFocusRequest(it) }
                            focusRequests.remove(mp)
                        } else {
                            @Suppress("DEPRECATION")
                            am.abandonAudioFocus(null)
                        }
                    }
                    list.remove(mp)
                    if (list.isEmpty()) activePlayers.remove(file)
                    foregroundOnlyPlayers.remove(mp)
                    pausedByApp.remove(mp)
                    handlers.remove(mp)
                } catch (e: Exception) {}
            }
            mp.setOnCompletionListener { onFinish.run() }
            mp.start()

            if (end > 0 && end < dur) {
                val adjDur = (playDur / speed).toLong()
                val t = Runnable { onFinish.run() }
                Handler(Looper.getMainLooper()).postDelayed(t, adjDur)
                handlers[mp] = t
            }

            if (fade > 0) {
                val a = ValueAnimator.ofFloat(0f, v); a.duration = fade.toLong(); a.interpolator = LinearInterpolator()
                a.addUpdateListener { curVol = it.animatedValue as Float; updateMixer() }; a.start()
            }
            if (targetPan != null && targetPan != curPan) {
                val a = ValueAnimator.ofFloat(curPan, targetPan); a.duration = (playDur / speed).toLong(); a.interpolator = LinearInterpolator()
                a.repeatCount = if (loop) ValueAnimator.INFINITE else 0; a.repeatMode = ValueAnimator.RESTART
                a.addUpdateListener { curPan = it.animatedValue as Float; updateMixer() }; a.start()
            }
        } catch (e: IOException) { e.printStackTrace() }
    }

    fun stop(file: String) { activePlayers[file]?.forEach { cleanupPlayer(it) }; activePlayers.remove(file) }
    fun stopAll() { activePlayers.values.forEach { l -> l.forEach { cleanupPlayer(it) } }; activePlayers.clear() }
    private fun cleanupPlayer(mp: MediaPlayer) {
        try {
            if (mp.isPlaying) mp.stop()
            mp.release()
            if (focusRequests.containsKey(mp)) {
                val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    focusRequests[mp]?.let { audioManager?.abandonAudioFocusRequest(it) }
                } else {
                    @Suppress("DEPRECATION")
                    audioManager?.abandonAudioFocus(null)
                }
                focusRequests.remove(mp)
            }
            handlers[mp]?.let { Handler(Looper.getMainLooper()).removeCallbacks(it) }
            handlers.remove(mp); foregroundOnlyPlayers.remove(mp); pausedByApp.remove(mp)
        } catch (e: Exception) {}
    }
}

// =============================================================================================
// ENGINE 2: HAPTICS
// =============================================================================================

object DriftHaptics {
    var context: Context? = null
    private var vibrator: Vibrator? = null

    fun initialize(ctx: Context) {
        if (context == null) {
            context = ctx.applicationContext
            vibrator = if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }

    fun perform(type: Haptic) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= 29) {
            val effect = when (type) {
                Haptic.Selection -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                Haptic.Light -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                Haptic.Medium -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                Haptic.Heavy -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                Haptic.Success -> VibrationEffect.createWaveform(longArrayOf(0, 20, 30, 25), intArrayOf(0, 200, 0, 255), -1)
                Haptic.Warning -> VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 30), intArrayOf(0, 200, 0, 200), -1)
                Haptic.Error -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50, 50, 100), intArrayOf(0, 255, 0, 200, 0, 255), -1)
            }
            v.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            when (type) {
                Haptic.Selection -> v.vibrate(15)
                Haptic.Light -> v.vibrate(20)
                Haptic.Medium -> v.vibrate(40)
                Haptic.Heavy -> v.vibrate(80)
                Haptic.Success -> v.vibrate(longArrayOf(0, 30, 50, 30), -1)
                Haptic.Warning -> v.vibrate(longArrayOf(0, 40, 60, 40), -1)
                Haptic.Error -> v.vibrate(longArrayOf(0, 50, 50, 50, 50, 80), -1)
            }
        }
    }
}