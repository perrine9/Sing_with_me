package fr.enssat.singwithme.Imane_Perrine.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class PlaylistCache(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PlaylistCache", Context.MODE_PRIVATE)

    // Sauvegarde la playlist sous forme de JSON
    fun savePlaylist(tracks: List<Track>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tracks)
        editor.putString("cached_playlist", json)
        editor.apply()
    }

    // Récupère la playlist depuis le cache
    fun getPlaylist(): List<Track>? {
        val json = sharedPreferences.getString("cached_playlist", null) ?: return null
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Vide le cache
    fun clearCache() {
        sharedPreferences.edit().clear().apply()
    }

    @OptIn(UnstableApi::class)
    fun refreshCache(
        context: Context,
        playlistCache: PlaylistCache,
        onSuccess: (List<Track>) -> Unit,
        onError: () -> Unit
    ) {
        // Lancer une coroutine pour effectuer les tâches réseau
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json"
            val playlistFetcher = PlaylistFetcher(context)
            try {
                // Récupérer la playlist depuis Internet
                val fetchedTracks = playlistFetcher.fetchPlaylistFromUrl(url)
                if (fetchedTracks != null) {
                    clearExistingFiles(fetchedTracks) // Supprimer les anciens fichiers

                    // Sauvegarder la nouvelle playlist dans le cache
                    playlistCache.savePlaylist(fetchedTracks)

                    // Mettre à jour les données sur le thread principal
                    withContext(Dispatchers.Main) {
                        onSuccess(fetchedTracks)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainScreen", "Error refreshing cache", e)
                withContext(Dispatchers.Main) {
                    onError()
                }
            }
        }
    }

    fun clearExistingFiles(tracks: List<Track>) {
        tracks.forEach { track ->
            track.lyricsPath?.let {
                // Supprimer le fichier de paroles s'il existe
                val lyricsFile = File("downloads", "${track.name}-lyrics.md")
                if (lyricsFile.exists()) {
                    lyricsFile.delete()
                    android.util.Log.d("MainScreen", "Deleted: ${lyricsFile.absolutePath}")
                }
            }

            track.mp3Path?.let {
                // Supprimer le fichier MP3 s'il existe
                val mp3File = File("downloads", "${track.name}.mp3")
                if (mp3File.exists()) {
                    mp3File.delete()
                    android.util.Log.d("MainScreen", "Deleted: ${mp3File.absolutePath}")
                }
            }
        }
    }





}
