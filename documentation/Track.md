# PlaylistFetcher - README

La classe `PlaylistFetcher` offre des fonctionnalités pour récupérer des playlists depuis une URL, télécharger les fichiers associés (comme les MP3 et les paroles), et analyser le contenu des paroles pour une lecture au format karaoké. Elle permet de récupérer, gérer et manipuler des morceaux de musique, y compris le téléchargement des fichiers associés et l'analyse des paroles avec des minutages pour une lecture synchronisée.

## Fonctionnalités

- **Récupérer la Playlist depuis une URL** : La classe permet de récupérer une playlist au format JSON depuis une URL donnée. Chaque morceau de la playlist contient des informations comme le nom du morceau, l'artiste, et les chemins d'accès aux fichiers des paroles et MP3.
- **Télécharger les fichiers MP3 et Paroles** : Les fichiers MP3 et de paroles associés sont automatiquement téléchargés dans le stockage local lors de la récupération de la playlist.
- **Normaliser les noms de fichiers** : Les noms de fichiers sont normalisés en supprimant les espaces pour garantir un bon traitement des fichiers.
- **Analyser les paroles pour la lecture au format Karaoké** : Les paroles sont analysées et formatées avec des minutages pour afficher les paroles synchronisées avec la musique, avec la possibilité d'ignorer les lignes de métadonnées ou vides.
- **Lire les paroles** : Les paroles téléchargées peuvent être lues à partir du stockage local si elles sont disponibles.
- **Journalisation des erreurs** : Les erreurs pendant des opérations comme le téléchargement des fichiers ou la récupération de la playlist sont enregistrées pour le dépannage.

## Composants

### 1. Classe de données `Track`

La classe de données `Track` représente un morceau de musique et contient les propriétés suivantes:

- `name` : Le nom de la chanson.
- `artist` : L'artiste de la chanson.
- `locked` : Une valeur booléenne indiquant si le morceau est verrouillé.
- `lyricsPath` : Le chemin d'accès au fichier des paroles (si disponible).
- `mp3Path` : Le chemin d'accès au fichier MP3 (si disponible).

### 2. Classe `PlaylistFetcher`

La classe `PlaylistFetcher` est responsable de la récupération de la playlist, du téléchargement des fichiers et de l'analyse des paroles. Elle contient les méthodes suivantes:

- `fetchPlaylistFromUrl(urlString: String)` : Récupère une playlist depuis une URL donnée et renvoie une liste d'objets `Track`.
- `normalizeFileName(fileName: String)` : Supprime les espaces du nom de fichier pour le rendre adapté au stockage local.
- `readTrack(reader: JsonReader)` : Lit un morceau depuis une réponse JSON et renvoie un objet `Track`.
- `readLyrics(path: String)` : Lit le contenu d'un fichier de paroles stocké localement et renvoie le texte.
- `parseLyrics(fileContent: String)` : Analyse le contenu des paroles pour extraire les lignes avec minutages pour la lecture au format karaoké.
- `downloadFile(url: String, path: String)` : Télécharge un fichier (MP3 ou paroles) depuis une URL vers le stockage local.

## Gestion des erreurs

La gestion des erreurs dans `PlaylistFetcher` repose principalement sur la journalisation des erreurs dans les logs afin de faciliter le diagnostic des problèmes. Voici les principaux types d'erreurs traitées:

- **Échec de récupération de la playlist** : Si la requête HTTP échoue ou retourne un statut d'erreur, un message d'erreur est enregistré avec le code de réponse HTTP.
- **Erreur lors du téléchargement de fichiers** : Si un fichier ne peut pas être téléchargé (MP3 ou paroles), une exception est capturée et un message d'erreur est enregistré avec les détails de l'exception.
- **Fichiers manquants** : Lorsque des fichiers de paroles sont demandés mais non trouvés dans le stockage local, un message d'avertissement est affiché pour informer l'utilisateur.
- **Échec de l'analyse des paroles** : Si le format des paroles est incorrect ou que des informations essentielles (comme le minutage) ne peuvent pas être extraites, un avertissement est émis et la ligne concernée est ignorée.

Ces messages d'erreur et d'avertissement sont affichés dans le journal Android pour une analyse ultérieure. Cela permet de suivre et corriger les erreurs en cours d'exécution.




