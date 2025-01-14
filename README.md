# Sing With Me - Imane et Perrine

Sing With Me est une application Android de karaoké interactif permettant de lire des chansons avec les paroles synchronisées.

Ce projet à été fait dans le cadre du cours d'Android d'INFO3 à l'ENSSAT. Le sujet est disponible [ici](https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/index.html).

---

## Fonctionnalités principales
- 🎵 **Liste des chansons** : Affiche une liste de chansons disponibles pour le karaoké.
- 📜 **Paroles synchronisées** : Les paroles sont affichées et mises à jour en temps réel avec l'audio.
- ⏯️ **Boutons de lecture, pause et arrêt** : Contrôle complet pour jouer, mettre en pause ou revenir à l'écran d'acceuil.
- 🌐 **Mode hors ligne et cache** : Si l'appliation à déjà été utilisée en étant en ligne, il est par la suite possible de l'utiliser tout en étant hors-ligne. De plus, il est aussi possible de rafraichir le cache. 

---

## Structure du projet
Voici la structure des fichiers du projet, leur rôle et leur [documentation](https://github.com/perrine9/Sing_with_me/blob/test/documentation) :

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
4. Effectuer un build et vous pourrez Run le projet sur votre appareil Android ou une émulateur.

## Lien vers l'APK 
Voici la structure des fichiers du projet et leur rôle : https://github.com/perrine9/Sing_with_me/blob/test/apk/debug/app-debug.apk

---
**Attention** le projet à été testé et est fonctionel avec l'émulateur par défaut d'Android Studio : Medium Phone API 35. 
---
