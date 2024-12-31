package fr.enssat.singwithme.Imane_Perrine

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistFetcher
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistCache
import fr.enssat.singwithme.Imane_Perrine.data.Track
import fr.enssat.singwithme.Imane_Perrine.Player.PlayerScreen
import fr.enssat.singwithme.Imane_Perrine.ui.theme.SingWithMeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SingWithMeTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val playlistFetcher = remember { PlaylistFetcher(context) }
    val playlistCache = remember { PlaylistCache(context) }

    var tracks by remember { mutableStateOf<List<Track>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Fetch cached or online playlist
        try {
            val cachedTracks = playlistCache.getPlaylist()
            if (cachedTracks != null) {
                tracks = cachedTracks
                Log.d("MainActivity", "Loaded cached playlist: ${cachedTracks.size}")
            }

            val fetchedTracks = withContext(Dispatchers.IO) {
                playlistFetcher.fetchPlaylistFromUrl("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json")
            }
            if (fetchedTracks != null) {
                tracks = fetchedTracks
                playlistCache.savePlaylist(fetchedTracks)
            } else {
                errorMessage = "Failed to fetch playlist."
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToPlaylist = { navController.navigate("playlist") })
        }
        composable("playlist") {
            if (tracks != null) {
                PlaylistScreen(tracks!!, onTrackClick = { track ->
                    navController.navigate("player/${track.name}")
                })
            } else if (errorMessage != null) {
                ErrorScreen(errorMessage!!)
            } else {
                LoadingScreen()
            }
        }
        composable(
            "player/{trackName}",
            arguments = listOf(navArgument("trackName") { type = NavType.StringType })
        ) { backStackEntry ->
            val trackName = backStackEntry.arguments?.getString("trackName")
            val selectedTrack = tracks?.find { it.name == trackName }
            if (selectedTrack != null) {
                PlayerScreen(track = selectedTrack)
            } else {
                ErrorScreen("Track not found.")
            }
        }
    }

}

@Composable
fun PlaylistScreen(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Add the background image
        Image(
            painter = painterResource(id = R.drawable.karaoke_icon), // Use the same image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(tracks) { track ->
                val textColor = if (track.locked) Color.Black else Color.White // Change text color based on locked state
                Button(
                    onClick = { onTrackClick(track) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !track.locked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor  = if (!track.locked) Color(0xFF6200EE) else Color.Transparent // Purple for enabled, transparent for disabled
                    )
                ) {
                    Text(
                        text = "${track.name} - ${track.artist}",
                        color = textColor // Dynamically set text color
                    )
                }
            }
        }
    }
}
@Composable
fun SplashScreen(onNavigateToPlaylist: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter // Align content towards the top
    ) {
        // Image de fond
        Image(
            painter = painterResource(id = R.drawable.karaoke_icon), // Remplacez par votre image
            contentDescription = "Karaoke Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Contenu de la page
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp) // Adjust the position by increasing/decreasing this value
        ) {
            // Texte du titre
            Text(
                text = "Sing With Me",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp) // Add space below the title
            )

            // Texte "Are you ready?"
            Text(
                text = "Are you ready?",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6200EE), // Purple color
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }

    // Redirection automatique après 3 secondes
    LaunchedEffect(Unit) {
        delay(3000L)
        onNavigateToPlaylist()
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

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
