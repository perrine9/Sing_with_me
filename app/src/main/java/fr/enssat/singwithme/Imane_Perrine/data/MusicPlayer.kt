import android.content.Context
import android.media.MediaPlayer

class MusicPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playTrack(resourceId: Int) {
        stopTrack() // Arrête le track précédent
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.start()
    }

    fun stopTrack() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
