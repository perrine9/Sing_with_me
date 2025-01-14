# PlaylistCache - README

La classe `PlaylistCache` permet de gérer le cache d'une playlist, en offrant des fonctionnalités pour sauvegarder, récupérer et vider le cache de la playlist. Elle permet également de rafraîchir le cache en récupérant une nouvelle playlist depuis une URL distante, tout en nettoyant les anciens fichiers associés.

### Classe `PlaylistCache`

La classe `PlaylistCache` est responsable de la gestion du cache de playlist, de la sauvegarde des playlists, de leur récupération et de leur suppression. Elle contient les méthodes suivantes :

- `savePlaylist(tracks: List<Track>)` : Sauvegarde la playlist sous forme de JSON dans les `SharedPreferences`.
- `getPlaylist()` : Récupère la playlist depuis les `SharedPreferences` et la désérialise en une liste d'objets `Track`.
- `clearCache()` : Vide le cache en supprimant toutes les données stockées dans les `SharedPreferences`.
- `refreshCache(context: Context, playlistCache: PlaylistCache, onSuccess: (List<Track>) -> Unit, onError: () -> Unit)` : Rafraîchit le cache en récupérant la playlist depuis une URL distante et en sauvegardant la nouvelle version dans le cache.
- `clearExistingFiles(tracks: List<Track>)` : Supprime les anciens fichiers MP3 et de paroles associés aux morceaux avant de sauvegarder la nouvelle playlist.

## Gestion des erreurs

La gestion des erreurs dans `PlaylistCache` repose principalement sur la journalisation des erreurs dans les logs afin de faciliter le diagnostic des problèmes. Voici les principaux types d'erreurs traitées :

- **Échec de récupération de la playlist** : Si une erreur se produit lors de la récupération de la playlist depuis l'URL distante, un message d'erreur est enregistré.
- **Erreur lors de la suppression des fichiers** : Si un fichier MP3 ou de paroles ne peut pas être supprimé lors du nettoyage, un message d'erreur est enregistré pour chaque fichier non supprimé.
- **Échec de la mise à jour du cache** : Si le cache ne peut pas être mis à jour ou si une exception est lancée lors de la récupération de la playlist, un message d'erreur est enregistré.

Ces erreurs sont enregistrées dans les logs pour permettre une analyse et une résolution des problèmes en cours d'exécution.
