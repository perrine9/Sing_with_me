package fr.enssat.singwithme.Imane_Perrine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistFetcher
import fr.enssat.singwithme.Imane_Perrine.data.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import fr.enssat.singwithme.Imane_Perrine.ui.theme.SingWithMeTheme
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistCache

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SingWithMeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val playlistCache = remember { PlaylistCache(context) }

    // Mutable states for tracks, offline mode, and errors
    var tracks by remember { mutableStateOf<List<Track>?>(null) }
    var isOffline by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            // Load cached tracks
            val cachedTracks = playlistCache.getPlaylist()
            if (cachedTracks != null) {
                tracks = cachedTracks
                isOffline = true
                Log.d("MainScreen", "Loaded cached tracks: ${cachedTracks.size}")
            }

            // Fetch tracks from API
            val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json"
            val playlistFetcher = PlaylistFetcher(context) // Pass context
            val fetchedTracks = withContext(Dispatchers.IO) {
                playlistFetcher.fetchPlaylistFromUrl(url)
            }
            if (fetchedTracks != null) {
                tracks = fetchedTracks
                isOffline = false
                playlistCache.savePlaylist(fetchedTracks)
                Log.d("MainScreen", "Fetched tracks: ${fetchedTracks.size}")
            } else {
                errorMessage = "Failed to fetch playlist."
            }
        } catch (e: Exception) {
            errorMessage = "Error fetching playlist: ${e.message}"
            Log.e("MainScreen", "Error fetching playlist", e)
        }
    }

    when {
        tracks != null -> PlaylistScreen(tracks = tracks!!, isOffline = isOffline)
        errorMessage != null -> ErrorScreen(errorMessage!!)
        else -> LoadingScreen()
    }
}

@Composable
fun PlaylistScreen(tracks: List<Track>, isOffline: Boolean) {
    Column {
        if (isOffline) {
            Text(
                text = "Mode hors ligne - Certaines fonctionnalités peuvent être limitées",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(tracks) { track ->
                Button(
                    onClick = {
                        Log.d("MainActivity", "Track clicked: ${track.name}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !track.locked
                ) {
                    Text(text = track.name)
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}
