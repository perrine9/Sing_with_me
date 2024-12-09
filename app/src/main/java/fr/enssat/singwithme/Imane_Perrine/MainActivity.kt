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

        // Appel setContent pour l'interface Compose
        setContent {
            SingWithMeTheme {
                // Appel de la fonction Composable qui gère l'UI
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val playlistCache = remember { PlaylistCache(context) }

    // Variable d'état pour la playlist
    var tracks by remember { mutableStateOf<List<Track>?>(null) }
    var isOffline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Charge d'abord les données depuis le cache
        val cachedTracks = playlistCache.getPlaylist()
        if (cachedTracks != null) {
            tracks = cachedTracks
            isOffline = true // Indique que nous utilisons des données hors-ligne
        }

        // Ensuite, essayez de charger depuis Internet
        try {
            val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json"
            val playlistFetcher = PlaylistFetcher()
            val fetchedTracks = withContext(Dispatchers.IO) {
                playlistFetcher.fetchPlaylistFromUrl(url)
            }
            if (fetchedTracks != null) {
                tracks = fetchedTracks
                isOffline = false // Nous avons des données en ligne
                playlistCache.savePlaylist(fetchedTracks) // Met à jour le cache
            }
        } catch (e: Exception) {
            // Gérer les erreurs réseau, les logs suffisent pour ce cas
            Log.e("MainScreen", "Error fetching playlist", e)
        }
    }

    // Affiche les données ou un indicateur de chargement
    if (tracks != null) {
        PlaylistScreen(tracks = tracks!!, isOffline = isOffline)
    } else {
        LoadingScreen()
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
    // Afficher un message ou une animation de chargement
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    SingWithMeTheme {
        MainScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingScreen() {
    LoadingScreen()
}
