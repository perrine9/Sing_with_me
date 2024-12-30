package fr.enssat.singwithme.Imane_Perrine.Player

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text

import fr.enssat.singwithme.Imane_Perrine.data.KaraokeLine

@Composable
fun KaraokeText(lyrics: List<KaraokeLine>, currentTime: Float) {
    LazyColumn {
        items(lyrics) { line ->
            val isCurrent = currentTime >= line.startTime && currentTime < line.endTime
            androidx.compose.material3.Text(
                text = line.text,
                color = if (isCurrent) Color.Green else Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
