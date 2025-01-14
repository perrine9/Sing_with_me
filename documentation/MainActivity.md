## Structure de l'application

L'application "Sing With Me" suit une architecture simple où les différentes parties de l'interface utilisateur sont séparées en plusieurs composants. Voici une description des composants principaux :

### 1. **Classe `MainActivity`**

La classe `MainActivity` est l'entrée principale de l'application. Elle hérite de `ComponentActivity` et charge l'interface utilisateur à l'aide de Jetpack Compose.

- **Méthode `onCreate`** :
  - La méthode `onCreate` est appelée lors de la création de l'activité. Elle utilise `setContent` pour définir l'interface utilisateur.
  - Le composant `AppNavigation` est chargé dans `setContent`, ce qui initialise la navigation dans l'application.

### 2. **Navigation avec `NavController`**

Le composant `AppNavigation` gère la navigation entre les différents écrans de l'application en utilisant `NavController` de la bibliothèque Jetpack Compose Navigation.

- **Définition des destinations de navigation** :
  - **"splash"** : L'écran d'accueil initial (SplashScreen).
  - **"playlist"** : L'écran de la liste des chansons (PlaylistScreen).
  - **"player/{trackName}"** : L'écran de lecture de la chanson (PlayerScreen) où `{trackName}` est un argument dynamique pour passer le nom de la chanson.

- **Navigation conditionnelle** :
  - La navigation prend en compte l'état du réseau et le cache. Si l'utilisateur est hors ligne, il verra un message d'erreur ou les chansons disponibles en mode hors ligne.
  - Si l'utilisateur est en ligne, la playlist est récupérée via une requête réseau et la cache est mise à jour.

### 3. **Écrans Principaux de l'application**

L'application se compose de trois écrans principaux qui sont rendus conditionnellement en fonction de l'état de l'application.

#### 1. **SplashScreen**

- **Fonction** : Affiche un écran de démarrage avec le texte "Sing With Me" et une transition après un délai de 3 secondes vers l'écran de playlist.
- **Utilisation de `LaunchedEffect`** : Ce délai est géré avec `LaunchedEffect` pour exécuter une action après un délai.

#### 2. **PlaylistScreen**

- **Fonction** : Affiche la liste des chansons disponibles à lire. Si l'utilisateur est hors ligne, un message d'avertissement est affiché.
- **Comportement** :
  - Si l'utilisateur est en ligne, la liste des chansons est récupérée depuis un fichier JSON via un réseau.
  - Si l'utilisateur est hors ligne, les chansons sont lues depuis un cache local.
  - L'utilisateur peut cliquer sur une chanson pour la sélectionner, ce qui lance la navigation vers l'écran de lecture de la chanson.
  - Une barre de recherche est disponible pour filtrer les chansons par nom ou artiste. Cette barre de recherche met à jour dynamiquement la liste affichée selon les critères saisis par l'utilisateur.

#### 3. **PlayerScreen**

- **Fonction** : Permet de lire une chanson sélectionnée. Affiche les paroles synchronisées avec la musique pour une expérience de karaoké.
- **Contrôles** :
  - **Lecture/Pause** : L'utilisateur peut contrôler la lecture de la chanson avec des boutons de lecture/pause.
  - **Retour** : Un bouton permet de revenir à l'écran de playlist.

#### 4. **ErrorScreen**

- **Fonction** : Affiche un message d'erreur, comme un problème réseau ou l'absence de playlist en cache.

#### 5. **LoadingScreen**

- **Fonction** : Affiche un indicateur de chargement lorsque l'application est en train de récupérer des données en arrière-plan.

### 4. **Gestion des État et des Effets**

L'état de l'application, comme la liste des chansons, le mode hors ligne, et la connectivité réseau, est géré via les API `remember` et `mutableStateOf` de Jetpack Compose.

- **`remember`** : Utilisé pour mémoriser des valeurs entre les recompositions.
- **`mutableStateOf`** : Utilisé pour stocker des valeurs réactives (comme l'état de la playlist, le mode hors ligne, ou les messages d'erreur).

L'effet `LaunchedEffect` est utilisé pour lancer des actions asynchrones, comme la récupération de la playlist ou le délai de transition entre les écrans.

### 5. **Fonction de Mise à Jour du Cache**

Lorsque l'application est en mode en ligne, elle récupère la playlist depuis un fichier JSON en ligne. En cas de succès, la playlist est stockée dans un cache pour une utilisation hors ligne future.

- **Mise à jour du cache** : L'utilisateur peut rafraîchir la playlist en appuyant sur le bouton de rafraîchissement, ce qui met à jour les données stockées localement. Si la mise à jour échoue, un message d'erreur est affiché.

### 6. **Fonctionnalité de Recherche**

- **Barre de recherche** : Une barre de recherche a été ajoutée dans l'écran de la playlist. Elle permet aux utilisateurs de filtrer les chansons affichées en saisissant un texte qui correspond au nom ou à l'artiste d'une chanson.
- **Mise à jour dynamique** : La liste des chansons se met à jour en temps réel en fonction du texte saisi par l'utilisateur.

### 7. **Fonction `isNetworkAvailable`**

Cette fonction permet de vérifier si une connexion réseau est disponible en utilisant les services du gestionnaire de connectivité Android (`ConnectivityManager`).

- **Retourne `true`** : Si l'utilisateur est connecté à Internet.
- **Retourne `false`** : Si l'utilisateur n'est pas connecté ou si le réseau ne prend pas en charge l'accès à Internet.

## Gestion des Erreurs

L'application inclut plusieurs mécanismes pour gérer les erreurs de manière fluide et informer l'utilisateur des problèmes potentiels :

- **Erreur de réseau** : Si une erreur se produit lors de la tentative de récupération de la playlist à partir du réseau, un message d'erreur générique est affiché avec la mention "Network error. Please try again.".
  - Cela se produit dans le bloc `try-catch` lors de l'appel à la fonction `playlistFetcher.fetchPlaylistFromUrl`.

- **Aucune playlist en cache** : Si l'utilisateur est hors ligne et qu'aucune playlist n'est disponible en cache, un message d'erreur spécifique "No playlist in cache." est affiché.

- **Erreur de mise à jour du cache** : Si la tentative de mise à jour du cache échoue (par exemple, si une erreur se produit lors de l'actualisation de la playlist), un message "Cache update failed!" est affiché.

- **Track non trouvée** : Si un utilisateur navigue vers un écran de lecture de chanson et que la chanson demandée n'est pas trouvée dans la liste des pistes, un message d'erreur "Track not found." est affiché.

Les messages d'erreur sont gérés dans l'interface avec `ErrorScreen`, qui prend un message en paramètre et l'affiche au centre de l'écran avec un style d'erreur.
