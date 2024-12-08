package fr.enssat.singwithme.Imane_Perrine.data

import com.google.gson.stream.JsonReader
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import okhttp3.Cache
import java.io.File

data class Track(val name: String, val artist: String, val locked: Boolean)

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
        var locked = false  // Modifié pour utiliser un booléen

        reader.beginObject()  // Commencer à lire chaque objet dans le tableau

        while (reader.hasNext()) {
            val fieldName = reader.nextName()
            when (fieldName) {
                "name" -> name = reader.nextString()
                "artist" -> artist = reader.nextString()
                "locked" -> locked = reader.nextBoolean()  // Utilisation de nextBoolean() au lieu de nextString()
                else -> reader.skipValue()  // Ignorer les autres clés
            }
        }

        reader.endObject()  // Fin de l'objet "track"
        return Track(name, artist, locked)
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