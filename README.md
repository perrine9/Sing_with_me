# Sing With Me

Sing With Me est une application Android de karaoké interactive permettant de lire des chansons avec des paroles synchronisées tout en offrant une interface visuellement attrayante.

---

## Fonctionnalités principales
- 🎵 **Liste des chansons** : Affiche une liste de chansons disponibles pour le karaoké.
- 📜 **Paroles synchronisées** : Les paroles sont affichées et mises à jour en temps réel avec l'audio.
- ⏯️ **Boutons de lecture, pause et arrêt** : Contrôle complet pour jouer, mettre en pause ou arrêter la musique.
- 🎨 **Interface personnalisable** : Inclut des arrière-plans thématiques, des couleurs attrayantes et une interface réactive.
- 🚀 **Écran d'accueil animé** : Présente une introduction visuelle de l'application avant d'accéder à la liste des chansons.
- 🌐 **Mode hors ligne et cache** : Si l'appliation à déjà été utilisée en étant en ligne, il est par la suite possible de l'utiliser tout en étant hors-ligne. De plus, il est aussi possible de rafraichir le cache. 

---

## Structure du projet
Voici la structure des fichiers du projet, leur rôle et leur documentation  :

- Data : 
  - [Karaoké Parser](https://github.com/perrine9/Sing_with_me/blob/test/documentation/KaraokeParser.md) : fichier permettant de générer l'affichage des paroles.
  - [Music Player](https://github.com/perrine9/Sing_with_me/blob/test/documentation/MusicPlayer.md) : fichier permettant de gérer la musique.
  - [Playlist Cache](https://github.com/perrine9/Sing_with_me/blob/test/documentation/PlaylistCache.md ): fichier permetant de gérer la mise en cache de la playlist et de la musique. 
  - [Track](https://github.com/perrine9/Sing_with_me/blob/test/documentation/Track.md) : fichier permettant de télécharger et de lire les musiques ainsi que les paroles. 

- Player :
  - [Karaoke Simple Text](https://github.com/perrine9/Sing_with_me/blob/test/documentation/karaokeSimpleText.md) : fichier permettant d'afficher l'avancement dans les paroles en différentes couleurs.
  - [Karaoke Text](https://github.com/perrine9/Sing_with_me/blob/test/documentation/KaraokeText.md) : fichier permettant d'afficher les paroles de la musique en cours.
  - [Player Screen](https://github.com/perrine9/Sing_with_me/blob/test/documentation/PlayerScreen.md) : fichier permettant l'écran d'affiche de la musique. 
- [Main Activity](https://github.com/perrine9/Sing_with_me/blob/test/documentation/MainActivity.md) : fichier permettant de gérer les différents affichages. 

---

## Installation
1. Clonez le projet :
   ```bash
   git clone https://github.com/perrine9/Sing_with_me/
2. Ouvrez le projet dans Android Studio.
3. Synchronisez les dépendances avec Gradle.
4. Construisez et exécutez le projet sur un émulateur ou un appareil physique.

## Lien vers APK 
Voici la structure des fichiers du projet et leur rôle : https://github.com/perrine9/Sing_with_me/blob/test/apk/debug/app-debug.apk

---

## Technologies utilisées et dépendances 
- **Kotlin** : Langage principal pour le développement Android.
- **Jetpack Compose** : Framework UI moderne pour concevoir les écrans.
- **ExoPlayer** : Bibliothèque pour gérer la lecture audio.
- **Coroutines** : Gestion asynchrone pour le téléchargement et la lecture.
- **Material Design 3** : Pour une interface moderne et intuitive.

---
