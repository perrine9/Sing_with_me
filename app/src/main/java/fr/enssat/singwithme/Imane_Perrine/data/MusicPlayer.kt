package fr.enssat.singwithme.Imane_Perrine.data

import android.content.Context
import android.net.Uri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import androidx.media3.common.MediaItem
import androidx.media3.common.AudioAttributes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.C

class MusicPlayer(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    private val _playbackProgress = MutableLiveData<Long>()
    val playbackProgress: LiveData<Long> get() = _playbackProgress

    fun initializePlayer(uri: Uri, onPlaybackReady: () -> Unit) {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC) // Correct constant
                    .setUsage(C.USAGE_MEDIA) // Correct constant
                    .build(),
                true
            )
            setMediaItem(MediaItem.fromUri(uri))
            prepare()

            addListener(object : Player.Listener { // Correct listener type
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) { // Ensure using correct Player class
                        onPlaybackReady()
                        play()
                    }
                }
            })
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun stop() {
        exoPlayer?.stop()
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun trackProgress(onProgressUpdate: (Long) -> Unit) {
        exoPlayer?.let { player ->
            Thread {
                while (player.isPlaying) {
                    onProgressUpdate(player.currentPosition)
                    Thread.sleep(100) // Update every 100ms
                }
            }.start()
        }
    }
}