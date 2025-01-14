# Sing With Me

Sing With Me est une application Android de karaoké interactive permettant de lire des chansons avec des paroles synchronisées tout en offrant une interface visuellement attrayante.

---

## Fonctionnalités principales
- 🎵 **Liste des chansons** : Affiche une liste de chansons disponibles pour le karaoké.
- 📜 **Paroles synchronisées** : Les paroles sont affichées et mises à jour en temps réel avec l'audio.
- ⏯️ **Boutons de lecture, pause et arrêt** : Contrôle complet pour jouer, mettre en pause ou arrêter la musique.
- 🎨 **Interface personnalisable** : Inclut des arrière-plans thématiques, des couleurs attrayantes et une interface réactive.
- 🚀 **Écran d'accueil animé** : Présente une introduction visuelle de l'application avant d'accéder à la liste des chansons.

---

## Structure du projet
Voici la structure des fichiers du projet et leur rôle :




---

## Technologies utilisées
- **Kotlin** : Langage principal pour le développement Android.
- **Jetpack Compose** : Framework UI moderne pour concevoir les écrans.
- **ExoPlayer** : Bibliothèque pour gérer la lecture audio.
- **Coroutines** : Gestion asynchrone pour le téléchargement et la lecture.
- **Material Design 3** : Pour une interface moderne et intuitive.

---

## Installation
1. Clonez le projet :
   ```bash
   git clone https://github.com/perrine9/Sing_with_me/
2. Ouvrez le projet dans Android Studio.
3. Synchronisez les dépendances avec Gradle.
4. Construisez et exécutez le projet sur un émulateur ou un appareil physique.

## Lien vers APK 
Voici la structure des fichiers du projet et leur rôle :

## Architecture
L’application est composée de plusieurs écrans :
1. **Splash Screen** : Présente l'application avant la liste des chansons ( dure 3 secs).
2. **Playlist Screen** : Affiche une liste de chansons disponibles. ( ceux qui sont jouables en mauve)
3. **Player Screen** : Permet de lire une chanson avec les paroles synchronisées( avec des boutons de play et pause aussi).

## Fonctionnalités techniques

1. Les paroles sont synchronisées avec l'audio à l'aide d'ExoPlayer et d'un algorithme pour calculer le progrès en fonction du temps. 
2. Les fichiers de musique et les paroles sont stockés localement après téléchargement pour un accès rapide.
3. Une mise en cache efficace permet de réduire les temps de chargement.



## Lien vers la documentation complète
[Documentation complète](https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/index.html)

## Téléchargement de l'APK
[Téléchargez l'APK](https://gcpa-enssat-24-25.s3.eu-west-3.amazonaws.com/SingWithMe.apk)