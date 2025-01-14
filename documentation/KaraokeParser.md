# KaraokeParser.md

## Description
La classe `KaraokeParser` est utilisée pour analyser et convertir les fichiers karaoké textuels en une structure de données exploitable dans une application Android/Kotlin. Ces fichiers contiennent des informations sur les paroles synchronisées avec des timestamps définissant leur début et leur fin.

## Structure des données
### `KaraokeLine`
- **Attributs** :
    - `startTime`: Temps de début de la ligne (en secondes).
    - `endTime`: Temps de fin de la ligne (en secondes).
    - `text`: Texte de la ligne.
- Représente une ligne de karaoké avec ses informations de synchronisation.


#### Exemple de données karaoké :
{ 0:19 }When you were here before,{ 0:20 }
{ 0:23 }Couldn't look you in the eye{ 0:25 }

## Fonctionnalités principales
### `parse(inputStream: InputStream): List<KaraokeLine>`
- **Entrée** : Un flux d'entrée (`InputStream`) contenant les données karaoké.
- **Sortie** : Une liste d'objets `KaraokeLine` représentant les lignes analysées.
- **Méthode** :
  1. Lit le contenu ligne par ligne à partir du fichier texte.
  2. Utilise une expression régulière pour extraire les timestamps et le texte.
  3. Gère les lignes sans timestamp de fin en attribuant une durée par défaut de 5 secondes.
  4. Enregistre les avertissements pour les lignes qui ne respectent pas le format attendu.

### Expression régulière utilisée
val regex = Regex("""\{\s*(\d+):(\d+(?:\.\d+)?)\s*\}(.*?)(?:\{\s*(\d+):(\d+(?:\.\d+)?)\s*\})?""")
-Groupe 1 et 2 : Temps de début (minute et seconde).
- Groupe 3 : Texte de la ligne.
- Groupe 4 et 5 (optionnels) : Temps de fin (minute et seconde).

### Gestion des erreurs
Si une ligne ne correspond pas au format attendu, un message d'avertissement est enregistré dans les logs Android :
Log.w("KaraokeParser", "No match for line: $line")

### Sortie
voici un exemple de sortie de cette fonction : 
[
KaraokeLine(startTime=19.0, endTime=20.0, text="When you were here before"),
KaraokeLine(startTime=23.0, endTime=25.0, text="Couldn't look you in the eye")
]
