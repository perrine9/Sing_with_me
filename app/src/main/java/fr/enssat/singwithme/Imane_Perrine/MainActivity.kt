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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistFetcher
import fr.enssat.singwithme.Imane_Perrine.data.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import fr.enssat.singwithme.Imane_Perrine.ui.theme.SingWithMeTheme

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
    // Variable d'état pour la playlist
    var tracks by remember { mutableStateOf<List<Track>?>(null) }

    // Utilisation d'une Coroutine pour effectuer l'appel réseau dans un thread de fond
    LaunchedEffect(Unit) {
        // Utilisation de withContext pour exécuter l'appel réseau sur le thread IO
        tracks = withContext(Dispatchers.IO) {
            val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json"
            val playlistFetcher = PlaylistFetcher()
            playlistFetcher.fetchPlaylistFromUrl(url)
        }
    }

    // Si la playlist est chargée, afficher l'écran avec les boutons
    if (tracks != null) {
        PlaylistScreen(tracks = tracks!!)
    } else {
        // Affichage d'un message ou d'un indicateur de chargement tant que les données sont nulles
        LoadingScreen()
    }
}

@Composable
fun PlaylistScreen(tracks: List<Track>) {
    // Affichage d'une liste de titres sous forme de boutons
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(tracks) { track ->
            // Chaque titre est un bouton
            Button(
                onClick = {
                    // Action à effectuer lorsque le bouton est cliqué
                    Log.d("MainActivity", "Track clicked: ${track.name}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = track.name)
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
