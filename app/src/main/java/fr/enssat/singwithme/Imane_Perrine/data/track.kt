package fr.enssat.singwithme.Imane_Perrine.data

import android.content.Context
import android.util.Log
import com.google.gson.stream.JsonReader
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

data class Track(
    val name: String,
    val artist: String,
    val locked: Boolean,
    val lyricsPath: String?,
    val mp3Path: String?
)

class PlaylistFetcher(private val context: Context) {

    fun fetchPlaylistFromUrl(urlString: String): List<Track>? {
        val client = OkHttpClient()
        val request = Request.Builder().url(urlString).build()

        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                val reader = JsonReader(InputStreamReader(response.body?.byteStream()))
                reader.beginArray()

                val tracks = mutableListOf<Track>()
                while (reader.hasNext()) {
                    tracks.add(readTrack(reader))
                }

                reader.endArray()
                tracks
            } else {
                Log.e("PlaylistFetcher", "Failed to fetch playlist: ${response.code}")
                null
            }
        }
    }

    private fun readTrack(reader: JsonReader): Track {
        var name = ""
        var artist = ""
        var locked: Boolean? = null
        var path: String? = null
        var mp3path: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            val fieldName = reader.nextName()
            when (fieldName) {
                "name" -> name = reader.nextString()
                "artist" -> artist = reader.nextString()
                "locked" -> locked = reader.nextBoolean()
                "path" -> path = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        val isLocked = locked ?: false
        if (!isLocked) {
            path?.let {
                val lyricsPath = it
                mp3path = it.removeSuffix(".md") + ".mp3"

                // Download files
                downloadFile("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/$lyricsPath", "$name-lyrics.md")
                downloadFile("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/$mp3path", "$name.mp3")
            }
        }
        return Track(name, artist, isLocked, path, mp3path)
    }

    private fun downloadFile(url: String, fileName: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val downloadsDir = File(context.filesDir, "downloads")
                if (!downloadsDir.exists()) downloadsDir.mkdirs()

                val file = File(downloadsDir, fileName)
                if (!file.exists()) file.createNewFile()

                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                Log.d("DownloadFile", "File saved at: ${file.absolutePath}")
            } else {
                Log.e("DownloadFile", "Failed to download: $url")
            }
        }
    }
}




/*
fun main() {
    val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json"
    val playlistFetcher = PlaylistFetcher()
    val tracks = playlistFetcher.fetchPlaylistFromUrl(url)

    tracks?.forEach { track ->
        println("Track: ${track.name}, Artist: ${track.artist}, Locked: ${track.locked}")
    }
}*/