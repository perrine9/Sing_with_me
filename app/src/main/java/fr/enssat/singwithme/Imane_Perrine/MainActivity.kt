package fr.enssat.singwithme.Imane_Perrine

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistFetcher
import fr.enssat.singwithme.Imane_Perrine.data.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import fr.enssat.singwithme.Imane_Perrine.ui.theme.SingWithMeTheme
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistCache
import fr.enssat.singwithme.Imane_Perrine.ui.theme.AppTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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

    // Variables d'état pour la playlist
    var tracks by remember { mutableStateOf<List<Track>?>(null) }
    var isCacheMode by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Vérification de la connexion réseau dans un LaunchedEffect
    LaunchedEffect(Unit) {
        isConnected = isNetworkAvailable(context)
    }

    // Chargement des données depuis le cache ou Internet
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            // Si offline, charger le cache
            val cachedTracks = playlistCache.getPlaylist()
            if (cachedTracks != null) {
                tracks = cachedTracks
                isCacheMode = true // Indique que nous utilisons les données du cache
            } else {
                errorMessage = "Aucune playlist en cache"
            }
        } else {

            try {
                val url = "https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/playlist.json"
                val playlistFetcher = PlaylistFetcher(context)
                val fetchedTracks = withContext(Dispatchers.IO) {
                    playlistFetcher.fetchPlaylistFromUrl(url)
                }
                if (fetchedTracks != null) {
                    tracks = fetchedTracks
                    isCacheMode = false // Nous avons des données en ligne
                    playlistCache.savePlaylist(fetchedTracks) // Met à jour le cache
                } else {
                    errorMessage = "Erreur lors de la récupération des données"
                }
            } catch (e: Exception) {
                // Gérer les erreurs réseau
                Log.e("MainScreen", "Error fetching playlist", e)
                errorMessage = "Erreur réseau, veuillez réessayer."
            }
        }
    }

    // Affichage de l'écran
    if (tracks != null) {
        PlaylistScreen(
            tracks = tracks!!,
            isCacheMode = isCacheMode,
            isOnline = isConnected,
            onRefreshClick = {
                // Rafraîchir le cache
                playlistCache.refreshCache(context, playlistCache, { fetchedTracks ->
                    tracks = fetchedTracks
                    isCacheMode = false
                    Toast.makeText(context, "Cache mis à jour!", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(context, "Erreur lors de la mise à jour du cache!", Toast.LENGTH_SHORT).show()
                })
            }
        )
    } else {
        if (errorMessage != null) {
            // Afficher un message d'erreur si un problème est survenu
            ErrorScreen(errorMessage!!)
        } else {
            LoadingScreen()
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
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
    onRefreshClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }
    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    // Boîte de dialogue de confirmation
    if (showDialog) {
        KaraokeConfirmationDialog(
            onConfirm = {
                showDialog = false
                showCountdown = true
            },
            onDismiss = {
                showDialog = false
            }
        )
    }

    // Écran de décompte
    if (showCountdown) {
        CountdownScreen(onCountdownFinished = {
            showCountdown = false
            // Lancer la session de karaoké (action personnalisée)
            selectedTrack?.let {
                Log.d("PlaylistScreen", "Starting karaoke for track: ${it.name}")
            }
        })
    }

    // Contenu principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Bouton de rafraîchissement en haut
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier.size(48.dp) // Taille ajustable
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh, // Icône refresh
                    contentDescription = "Rafraîchir",
                    tint = MaterialTheme.colorScheme.primary // Couleur de l'icône
                )
            }
        }

        // Affichage d'un message d'avertissement si hors ligne
        if (!isOnline) {
            Text(
                text = "Mode hors ligne - Certaines fonctionnalités peuvent être limitées",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Liste des pistes
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tracks) { track ->
                Button(
                    onClick = {
                        selectedTrack = track
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
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

@Composable
fun KaraokeConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Lancer le karaoké ?")
        },
        text = {
            Text("Souhaitez-vous démarrer une partie de karaoké ?")
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Annuler")
            }
        }
    )
}


@Composable
fun CountdownScreen(onCountdownFinished: () -> Unit) {
    var countdown by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            kotlinx.coroutines.delay(1000)
            countdown--
        }
        onCountdownFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 1f)), // Fond blanc
        contentAlignment = Alignment.Center
    ) {
        // Texte centré avec une taille de police très grande
        Text(
            text = "$countdown",
            style = AppTypography.displayLarge.copy(fontSize = 72.sp), // Texte plus grand
            color = Color.Black // Texte en noir pour bien se voir sur le fond blanc
        )
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
