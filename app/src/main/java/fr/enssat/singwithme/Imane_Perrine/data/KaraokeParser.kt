package fr.enssat.singwithme.Imane_Perrine.data

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream

data class KaraokeLine(val startTime: Float, val endTime: Float, val text: String)

class KaraokeParser {
    fun parse(inputStream: InputStream): List<KaraokeLine> {
        val lines = mutableListOf<KaraokeLine>()
        val reader = BufferedReader(inputStream.reader())
        val rawLines = reader.lineSequence().toList()

        val regex = Regex("""\{\s*(\d+):(\d+(?:\.\d+)?)\s*\}(.*?)(?:\{\s*(\d+):(\d+(?:\.\d+)?)\s*\})?""")

        rawLines.forEach { line ->
            val match = regex.find(line)
            if (match != null) {
                val startTime = match.groupValues[1].toInt() * 60 + match.groupValues[2].toFloat()
                val text = match.groupValues[3].trim()
                val endTime = if (match.groupValues[4].isNotEmpty() && match.groupValues[5].isNotEmpty()) {
                    match.groupValues[4].toInt() * 60 + match.groupValues[5].toFloat()
                } else {
                    startTime + 5 // Default duration of 5 seconds
                }

                lines.add(KaraokeLine(startTime, endTime, text))
            } else {
                Log.w("KaraokeParser", "No match for line: $line")
            }
        }
        return lines
    }
}
