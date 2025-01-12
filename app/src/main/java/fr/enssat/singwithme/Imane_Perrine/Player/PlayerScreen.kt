package fr.enssat.singwithme.Imane_Perrine.Player

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.enssat.singwithme.Imane_Perrine.data.KaraokeLine
import fr.enssat.singwithme.Imane_Perrine.data.KaraokeParser
import fr.enssat.singwithme.Imane_Perrine.data.PlaylistFetcher
import fr.enssat.singwithme.Imane_Perrine.data.Track
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.compose.ui.layout.ContentScale


import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.DefaultLoadControl
import kotlinx.coroutines.Dispatchers
import androidx.compose.foundation.Image
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.analytics.AnalyticsListener
import fr.enssat.singwithme.Imane_Perrine.R


import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(track: Track, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val playlistFetcher = remember { PlaylistFetcher(context) }
    var currentLineIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    val karaokeLines = remember { mutableStateOf<List<KaraokeLine>>(emptyList()) }

    // Créer un HandlerThread pour ExoPlayer
    val audioThread = remember { HandlerThread("AudioHandlerThread").apply { start() } }
    val audioHandler = remember { Handler(audioThread.looper) }

    // Initialiser ExoPlayer
    val exoPlayer = remember {
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
            .setEnableDecoderFallback(true) //
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS * 4, // Double the max buffer size
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()
        ExoPlayer.Builder(context).build().apply {
            audioHandler.post {
                track.mp3Path?.let { mp3Path ->
                    val mp3FilePath = File(context.filesDir, "downloads/$mp3Path")
                    if (mp3FilePath.exists()) {
                        val mediaItem = MediaItem.Builder()
                            .setUri(mp3FilePath.toUri())
                            .setMimeType(MimeTypes.AUDIO_MPEG)
                            .build()

                        // Assurez-vous que toutes les interactions ExoPlayer sont sur le thread principal
                        Handler(Looper.getMainLooper()).post {
                            setMediaItem(mediaItem)
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(C.USAGE_MEDIA)
                                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                                    .build(),
                                true
                            )
                            // Ajouter un listener pour surveiller les tampons
                            addAnalyticsListener(object : AnalyticsListener {
                                override fun onAudioUnderrun(
                                    eventTime: AnalyticsListener.EventTime,
                                    bufferSize: Int,
                                    bufferSizeMs: Long,
                                    elapsedSinceLastFeedMs: Long
                                ) {
                                    Log.e("PlayerScreen", "Audio underrun: bufferSize=$bufferSize, bufferSizeMs=$bufferSizeMs")
                                }

                                override fun onDroppedVideoFrames(
                                    eventTime: AnalyticsListener.EventTime,
                                    droppedFrames: Int,
                                    elapsedMs: Long
                                ) {
                                    Log.w("PlayerScreen", "Dropped $droppedFrames frames in $elapsedMs ms")
                                }
                            })
                            prepare()
                            playWhenReady = true
                        }
                    } else {
                        Log.e("PlayerScreen", "Audio file not found: ${mp3FilePath.absolutePath}")
                    }
                }
            }
        }
    }

    // Ajouter un listener pour synchroniser les paroles
    DisposableEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> Log.d("PlayerScreen", "Player is ready and playing")
                    Player.STATE_BUFFERING -> Log.d("PlayerScreen", "Player is buffering")
                    Player.STATE_ENDED -> Log.d("PlayerScreen", "Player playback ended")
                    Player.STATE_IDLE -> Log.d("PlayerScreen", "Player is idle")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerScreen", "Player error: ${error.localizedMessage}")
            }
        })

        onDispose {
            // Arrêtez d'abord toutes les interactions sur le thread ExoPlayer
            Handler(Looper.getMainLooper()).post {
                exoPlayer.stop()
                exoPlayer.release()
            }
            audioThread.quitSafely()
        }
    }

    // Charger les paroles
    LaunchedEffect(track) {
        withContext(Dispatchers.IO) { // Opérations lourdes dans un thread IO
            val lyricsContent = track.lyricsPath?.let { playlistFetcher.readLyrics(it) }
            if (!lyricsContent.isNullOrBlank()) {
                val parsedLyrics =
                    playlistFetcher.parseLyrics(lyricsContent) // Parsing dans le thread IO
                withContext(Dispatchers.Main) { // Mise à jour de l'UI dans le thread principal
                    karaokeLines.value = parsedLyrics
                    Log.d("PlayerScreen", "Lyrics loaded and parsed successfully")
                }
            } else {
                Log.w("PlayerScreen", "Lyrics content is null or blank")
            }
        }
    }


    // Synchroniser les paroles avec l'audio
    LaunchedEffect(exoPlayer, karaokeLines.value) {
        while (true) {
            val currentPosition = exoPlayer.currentPosition
            val lines = karaokeLines.value

            val lineIndex =
                lines.indexOfLast { it.startTime * 1000 <= currentPosition && currentPosition < it.endTime * 1000 }
            if (lineIndex != currentLineIndex) {
                currentLineIndex = lineIndex
            }

            val currentLine = lines.getOrNull(currentLineIndex)
            progress = currentLine?.let { line ->
                val startMs = line.startTime * 1000
                val endMs = line.endTime * 1000
                ((currentPosition - startMs).toFloat() / (endMs - startMs).toFloat()).coerceIn(
                    0f,
                    1f
                )
            } ?: 0f

            delay(400L)
        }
    }

    // Affichage UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Optionnel, au cas où l'image ne s'affiche pas
    ) {
        // Image de fond couvrant tout l'écran
        Image(
            painter = painterResource(id = R.drawable.karaoke_icon),
            contentDescription = "Karaoke Background",
            contentScale = ContentScale.Crop, // Pour couvrir tout l'écran
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre en haut
            Text(
                text = "Sing with Me",
                style = MaterialTheme.typography.h4,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Song title
            Text(
                text = track.name,
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Artist name
            Text(
                text = "By ${track.artist}",
                style = MaterialTheme.typography.subtitle1,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Progrès de la chanson
                LinearProgressIndicator(
                    progress = progress,
                    color = Color.Magenta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Paroles synchronisées avec le texte en rouge/noir
                val currentLine = karaokeLines.value.getOrNull(currentLineIndex)
                currentLine?.let {
                    KaraokeSimpleText(text = it.text, progress = progress)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Boutons de lecture et pause
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { exoPlayer.play() }) {
                        Text("Play")
                    }
                    Button(onClick = { exoPlayer.pause() }) {
                        Text("Pause")
                    }
                }

                // Bouton Retour
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { onNavigateBack() }) {
                    Text("Retour à l'accueil")
                }
            }
        }
    }
}
