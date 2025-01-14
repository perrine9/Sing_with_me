# Documentation du Projet - MusicPlayer

## Description
La classe `MusicPlayer` est responsable de la lecture audio dans l'application de karaoké. Elle utilise **ExoPlayer** pour gérer les fichiers audio et fournit des fonctionnalités essentielles comme la lecture, la pause, et le suivi du progrès de la lecture.

## Structure des classes

### MusicPlayer
Classe principale permettant la lecture audio.

**Propriétés :**
- `context` : Contexte Android nécessaire pour initialiser ExoPlayer.
- `_playbackProgress` : Variable interne pour suivre la progression de la lecture.
- `playbackProgress` : `LiveData` exposant la progression de lecture.

**Méthodes :**
- `initializePlayer(uri: Uri, onPlaybackReady: () -> Unit)` :
    - Initialise ExoPlayer avec un fichier audio.
    - **Paramètres :**
        - `uri` : URI du fichier audio.
        - `onPlaybackReady` : Callback appelé lorsque le lecteur est prêt.
- `play()` :
    - Démarre la lecture.
- `pause()` :
    - Met en pause la lecture.
- `stop()` :
    - Arrête la lecture.
- `release()` :
    - Libère les ressources associées à ExoPlayer.
- `trackProgress(onProgressUpdate: (Long) -> Unit)` :
    - Suit la progression de la lecture en temps réel.
    - **Paramètre :**
        - `onProgressUpdate` : Callback appelé avec la position actuelle.

## Exemple d'utilisation

### Initialisation
```kotlin
val musicPlayer = MusicPlayer(context)
musicPlayer.initializePlayer(uri) {
    // Callback lorsque le lecteur est prêt
    println("Player is ready!")
}
```

### Contrôle de la lecture
```kotlin
musicPlayer.play()  // Démarrer la lecture
musicPlayer.pause() // Mettre en pause
musicPlayer.stop()  // Arrêter
musicPlayer.release() // Libérer les ressources
```

### Suivi du progrès
```kotlin
musicPlayer.trackProgress { progress ->
    println("Progression actuelle : $progress ms")
}
```

## Utilisation dans le projet
Le `MusicPlayer` est un composant clé pour synchroniser l'audio avec les paroles affichées à l'écran dans l'application de karaoké.