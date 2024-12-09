package fr.enssat.singwithme.Imane_Perrine.data

import com.google.gson.stream.JsonReader
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import okhttp3.Cache
import java.io.File
import java.io.FileOutputStream

data class Track(
    val name: String,
    val artist: String,
    val locked: Boolean,
    val lyricsPath: String?,
    val mp3Path: String?
)

class PlaylistFetcher {


    fun fetchPlaylistFromUrl(urlString: String): List<Track>? {
        val client = OkHttpClient()
        val request = Request.Builder().url(urlString).build()

        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                val reader = JsonReader(InputStreamReader(response.body?.byteStream()))
                reader.beginArray()  // Commencer à lire un tableau directement

                val tracks = mutableListOf<Track>()

                while (reader.hasNext()) {
                    tracks.add(readTrack(reader))  // Lire chaque élément du tableau
                }

                reader.endArray()  // Fin du tableau
                tracks
            } else {
                null  // Retourner null si la requête échoue
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

        val isLocked = locked ?: false


        reader.endObject()  // Fin de l'objet "track"
        if (!isLocked) {
            path?.let {
                val lyricsPath = it // Utilise "path" pour les paroles
                mp3path = it.removeSuffix(".md") + ".mp3" // Génère le chemin pour le fichier MP3

                // Télécharger les fichiers
                downloadFile("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/$lyricsPath", "$name-lyrics.md")
                downloadFile("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/$mp3path", "$name.mp3")
            }
        }
        return Track(name, artist, isLocked, path, mp3path)
    }


    fun downloadFile(url: String, fileName: String) {

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val file = File("downloads", fileName) //
                file.parentFile?.mkdirs()

                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                println("Downloaded: $fileName")
            } else {
                println("Failed to download: $url")
            }
        }
    }

}


fun main() {
    val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json" // URL à remplacer si besoin
    val playlistFetcher = PlaylistFetcher()
    val tracks = playlistFetcher.fetchPlaylistFromUrl(url)

    tracks?.forEach { track ->
        println("Track: ${track.name}, Artist: ${track.artist}, Locked: ${track.locked}")
        println("Lyrics Path: ${track.lyricsPath}")
        println("MP3 Path: ${track.mp3Path}")
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