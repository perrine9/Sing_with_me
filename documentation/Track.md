# Documentation du Projet - PlaylistFetcher

## Description

Ce projet permet de gérer une playlist de musique en téléchargeant des fichiers MP3 et des paroles, tout en permettant de lire et d'analyser ces fichiers. Il inclut une classe principale `PlaylistFetcher`, qui permet de récupérer une playlist depuis une URL, télécharger des fichiers associés, et analyser les paroles pour une lecture de type karaoké.

## Structure des classes

### `Track`

Représente une chanson dans la playlist.

#### Propriétés :
- **`name`** : Nom de la chanson.
- **`artist`** : Nom de l'artiste.
- **`locked`** : Indique si la chanson est verrouillée.
- **`lyricsPath`** : Chemin du fichier de paroles.
- **`mp3Path`** : Chemin du fichier MP3.

### `PlaylistFetcher`

Classe principale permettant de récupérer la playlist, de télécharger les fichiers, et d'analyser les paroles.

#### Propriété :
- **`context`** : Contexte Android nécessaire pour les opérations de fichiers.

#### Méthodes :

- **`fetchPlaylistFromUrl(urlString: String): List<Track>?`** : 
  - Récupère la playlist depuis l'URL fournie et renvoie une liste d'objets `Track`.
  - Paramètre : **`urlString: String`** , L'URL du fichier JSON contenant les informations de la playlist. Ce fichier doit suivre un format spécifique où chaque chanson est représentée par un objet avec les propriétés `name`, `artist`, `locked` et `path`.
  - Retour : une liste de `Track` (`List<Track>`) si la récupération de la playlist est réussie, ou `null` en cas d'échec (par exemple, si l'URL est invalide ou si la réponse du serveur est erronée).
  - Détails : 
    1. **Création de la requête HTTP**
    La fonction utilise la bibliothèque **OkHttp** pour envoyer une requête HTTP GET à l'URL spécifiée (`urlString`). La réponse est ensuite traitée.
    2. **Lecture du JSON**
    Si la requête est réussie (code de statut HTTP 200), la fonction parse le corps de la réponse en utilisant un `JsonReader`. Elle attend un tableau JSON contenant des objets représentant des chansons.
    3. **Conversion des données**
    Chaque objet dans le tableau JSON est converti en un objet `Track` à l'aide de la fonction interne `readTrack`.
       - Pour chaque chanson, les informations suivantes sont extraites :
           - **`name`** : Nom de la chanson
           - **`artist`** : Nom de l'artiste
           - **`locked`** : Indique si la chanson est verrouillée
             - **`path`** : Chemin du fichier de paroles au format `.md`
       Si la chanson n'est pas verrouillée (`locked = false`), les fichiers MP3 et les paroles sont téléchargés.
    4. **Téléchargement des fichiers**
    La fonction génère deux URL basées sur le chemin de la chanson pour récupérer les fichiers :
       - **Fichier de paroles** : URL construite avec le `path` de la chanson.
         - **Fichier MP3** : URL construite en remplaçant `.md` par `.mp3` dans le `path`.
    Les fichiers sont ensuite téléchargés et enregistrés localement via la fonction `downloadFile`.
    5. **Gestion des erreurs**
    En cas d'erreur pendant la requête ou le parsing du JSON, la fonction capture l'exception et retourne `null`. Des messages d'erreur sont enregistrés dans les logs pour faciliter le débogage.
    6. **Retour de la playlist**
    Si tout se passe bien, une liste d'objets `Track` est retournée, représentant toutes les chansons de la playlist. Si une erreur se produit à n'importe quelle étape, `null` est retourné.

- **`normalizeFileName(fileName: String): String`** : Normalise le nom du fichier en supprimant les espaces.
  - La fonction `normalizeFileName` prend en entrée un nom de fichier sous forme de chaîne de caractères et renvoie une version normalisée de ce nom de fichier où tous les espaces ont été supprimés. Cette fonction est particulièrement utile pour garantir que les noms de fichiers respectent une convention ou pour faciliter le traitement des fichiers dans un environnement de stockage où les espaces peuvent poser problème.
  - Paramètre : **`fileName: String`** , le nom du fichier à normaliser, sous forme de chaîne de caractères.
  - Retour : une nouvelle chaîne de caractères représentant le nom du fichier avec tous les espaces supprimés.
  - Détails de l'implémentation :
    1. La fonction utilise la méthode `replace` de la classe `String` pour supprimer tous les espaces dans le nom du fichier. Cela permet de garantir que les noms de fichiers sont cohérents et peuvent être utilisés sans ambiguïté dans les systèmes qui n'acceptent pas les espaces dans les noms de fichiers.
    2. La fonction remplace chaque espace par une chaîne vide, ce qui supprime effectivement tous les espaces dans le nom du fichier.

- **`readTrack(reader: JsonReader): Track`** : 
  - Lit un objet `Track` à partir d'un `JsonReader`.
  - Paramètres : **`fileName: String`**, Le nom du fichier à normaliser, sous forme de chaîne de caractères.


- **`readLyrics(path: String): String`** : Lit les paroles depuis un fichier stocké localement et retourne le contenu.
- **`parseLyrics(fileContent: String): List<KaraokeLine>`** : Parse les paroles pour en extraire les lignes de karaoké avec leurs temps de début et de fin.
- **`downloadFile(url: String, path: String)`** : Télécharge un fichier à partir de l'URL spécifiée et le sauvegarde sous le chemin spécifié.




