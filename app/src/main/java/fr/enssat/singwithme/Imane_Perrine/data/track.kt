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
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(urlString).build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
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
        } catch (e: Exception) {
            Log.e("PlaylistFetcher", "Error fetching playlist: ${e.message}", e)
            null
        }
    }

    fun normalizeFileName(fileName: String): String {
        return fileName.replace(" ", "")
    }

    private fun readTrack(reader: JsonReader): Track {
        var name = ""
        var artist = ""
        var locked: Boolean? = null
        var path: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> name = reader.nextString()
                "artist" -> artist = reader.nextString()
                "locked" -> locked = reader.nextBoolean()
                "path" -> path = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        val isLocked = locked ?: false
        var mp3Path: String? = null

        if (!isLocked && path != null) {
            // Construct URLs for lyrics and mp3
            val lyricsUrl = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/$path"
            mp3Path = path.replace(".md", ".mp3")
            val mp3Url = lyricsUrl.replace(".md", ".mp3")

            // Download files
            downloadFile(lyricsUrl, path)
            downloadFile(mp3Url, mp3Path)
        }

        return Track(name, artist, isLocked, path, mp3Path)
    }

    fun readLyrics(path: String): String {
        val file = File(context.filesDir, "downloads/$path")
        return if (file.exists()) {
            file.readText()
        } else {
            Log.w("PlaylistFetcher", "File not found: $path")
            "Paroles introuvables."
        }
    }

    fun parseLyrics(fileContent: String): List<KaraokeLine> {
        val regex = Regex("""\{\s*(\d+):(\d+)\s*\}(.*?)(?=\{\s*\d+:\d+\s*\}|$)""")
        val lines = mutableListOf<KaraokeLine>()
        var previousEndTime: Float? = null

        fileContent.lines().forEachIndexed { index, line ->
            if (line.startsWith("#") || line.isBlank()) {
                Log.d("parseLyrics", "Ignoring metadata or empty line: $line")
                return@forEachIndexed
            }

            val matches = regex.findAll(line)
            matches.forEachIndexed { matchIndex, match ->
                val groups = match.groupValues
                val minutes = groups[1].toIntOrNull()
                val seconds = groups[2].toIntOrNull()
                val text = groups[3].trim()

                if (minutes != null && seconds != null) {
                    val startTime = (minutes * 60 + seconds).toFloat()

                    // Determine endTime: use next match's startTime or default to 5 seconds
                    val endTime = if (matchIndex + 1 < matches.count()) {
                        val nextMatch = matches.elementAt(matchIndex + 1)
                        val nextMinutes = nextMatch.groupValues[1].toIntOrNull()
                        val nextSeconds = nextMatch.groupValues[2].toIntOrNull()
                        if (nextMinutes != null && nextSeconds != null) {
                            (nextMinutes * 60 + nextSeconds).toFloat()
                        } else {
                            startTime + 5f
                        }
                    } else if (index + 1 < fileContent.lines().size) {
                        val nextLine = fileContent.lines()[index + 1]
                        val nextLineMatch = regex.find(nextLine)
                        if (nextLineMatch != null) {
                            val nextMinutes = nextLineMatch.groupValues[1].toIntOrNull()
                            val nextSeconds = nextLineMatch.groupValues[2].toIntOrNull()
                            if (nextMinutes != null && nextSeconds != null) {
                                (nextMinutes * 60 + nextSeconds).toFloat()
                            } else {
                                startTime + 5f
                            }
                        } else {
                            startTime + 5f
                        }
                    } else {
                        startTime + 5f
                    }

                    if (endTime > startTime) {
                        lines.add(KaraokeLine(startTime, endTime, text))
                        previousEndTime = endTime
                    } else {
                        Log.w("parseLyrics", "Skipping invalid segment with startTime=$startTime and endTime=$endTime: $line")
                    }
                } else {
                    Log.w("parseLyrics", "Failed to parse timestamp: $line")
                }
            }
        }

        return lines
    }

    fun downloadFile(url: String, path: String) {
        val downloadsDir = File(context.filesDir, "downloads")
        val file = File(downloadsDir, path)

        Log.d("DownloadFile", "downloadsDir path: ${downloadsDir.absolutePath}")
        Log.d("DownloadFile", "File path: ${file.absolutePath}")

        try {
            // Vérifiez si le fichier existe déjà
            if (file.exists()) {
                Log.d("DownloadFile", "File already exists")
                return
            }

            // Créez les dossiers nécessaires
            file.parentFile?.mkdirs()
            Log.d("DownloadFile", "Parent directories created")

            // Téléchargez le fichier
            OkHttpClient().newCall(Request.Builder().url(url).build()).execute().use { response ->
                response.body?.byteStream()?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            Log.d("DownloadFile", "File successfully downloaded")
        } catch (e: Exception) {
            Log.e("DownloadFile", "Error downloading file", e)
        }
    }

}

