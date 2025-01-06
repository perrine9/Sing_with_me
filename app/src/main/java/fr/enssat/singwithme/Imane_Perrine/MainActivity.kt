package fr.enssat.singwithme.Imane_Perrine

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var isCacheMode by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isConnected = isNetworkAvailable(context)
    }

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            val cachedTracks = playlistCache.getPlaylist()
            if (cachedTracks != null) {
                tracks = cachedTracks
                isCacheMode = true
            } else {
                errorMessage = "No playlist in cache."
            }
        } else {
            try {
                val fetchedTracks = withContext(Dispatchers.IO) {
                    playlistFetcher.fetchPlaylistFromUrl("https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json")
                }
                if (fetchedTracks != null) {
                    tracks = fetchedTracks
                    isCacheMode = false
                    playlistCache.savePlaylist(fetchedTracks)
                } else {
                    errorMessage = "Failed to fetch playlist."
                }
            } catch (e: Exception) {
                Log.e("AppNavigation", "Error fetching playlist", e)
                errorMessage = "Network error. Please try again."
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToPlaylist = { navController.navigate("playlist") })
        }
        composable("playlist") {
            if (tracks != null) {
                PlaylistScreen(
                    tracks = tracks!!,
                    isCacheMode = isCacheMode,
                    isOnline = isConnected,
                    onRefreshClick = {
                        playlistCache.refreshCache(context, playlistCache, { refreshedTracks ->
                            tracks = refreshedTracks
                            isCacheMode = false
                            Toast.makeText(context, "Cache updated!", Toast.LENGTH_SHORT).show()
                        }, {
                            Toast.makeText(context, "Cache update failed!", Toast.LENGTH_SHORT).show()
                        })
                    },
                    onTrackClick = { track ->
                        navController.navigate("player/${track.name}")
                    }
                )
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

@SuppressLint("ServiceCast")
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

@Composable
fun PlaylistScreen(
    tracks: List<Track>,
    isCacheMode: Boolean,
    isOnline: Boolean,
    onRefreshClick: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onRefreshClick, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        if (!isOnline) {
            Text(
                text = "Offline mode - Some features may be limited",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tracks) { track ->
                Button(
                    onClick = { onTrackClick(track) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    enabled = !track.locked
                ) {
                    Text(text = "${track.name} - ${track.artist}")
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onNavigateToPlaylist: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("Sing With Me", style = MaterialTheme.typography.headlineLarge, color = Color.White)
    }
    LaunchedEffect(Unit) {
        delay(3000L)
        onNavigateToPlaylist()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
