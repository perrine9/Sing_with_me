# PlayerScreen - README

La fonction `PlayerScreen` permet de créer une interface utilisateur pour la lecture d'une chanson avec synchronisation des paroles en format karaoké. Elle utilise ExoPlayer pour lire le fichier audio.

### Fonction `PlayerScreen`

La fonction `PlayerScreen` crée une interface utilisateur avec les éléments suivants :

- **Lecture Audio** : Utilisation de `ExoPlayer` pour lire le fichier MP3 de la chanson.
- **Paroles Synchronisées** : Chargement des paroles depuis un fichier et synchronisation de leur affichage avec la progression de la lecture audio.
- **Affichage de la Progression** : Affichage d'un indicateur de progression pour la chanson en fonction du temps écoulé et des paroles actuellement affichées.
- **Contrôles** : Boutons pour jouer/pause la musique et retourner à l'écran d'accueil.

### Méthodes Utilisées

- **`exoPlayer`** : Instancie un lecteur ExoPlayer pour gérer la lecture audio.
- **`LaunchedEffect`** : Utilisé pour charger et analyser les paroles dans un thread séparé tout en maintenant l'interface réactive.
- **`DisposableEffect`** : Ajoute un listener pour gérer l'état de la lecture audio et nettoyer les ressources lorsque le composant est supprimé.
- **`LinearProgressIndicator`** : Affiche la barre de progression de la chanson.

## Gestion des erreurs

La gestion des erreurs repose sur la journalisation des événements dans les logs afin de faciliter le diagnostic des problèmes. Les erreurs suivantes sont enregistrées :

- **Échec de lecture du fichier audio** : Si le fichier MP3 spécifié dans `mp3Path` est introuvable ou si une erreur se produit pendant la lecture, un message d'erreur est enregistré.
- **Problème avec les paroles** : Si le fichier de paroles est manquant ou contient des erreurs, un avertissement est enregistré.
- **Erreur de lecture audio** : En cas d'erreur de lecture, l'erreur est capturée par `Player.Listener` et un message est loggé.

Ces erreurs sont affichées dans le journal Android pour une analyse plus approfondie et une résolution rapide.
