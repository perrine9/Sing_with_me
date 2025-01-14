# Documentation du Projet - KaraokeText

## Description
Le fichier `KaraokeText` contient une fonction Composable utilisée pour afficher une liste de paroles synchronisée avec un temps donné. Chaque ligne de texte est mise en évidence si elle correspond à la ligne actuellement lue.

## Structure des fonctions

### KaraokeText
Fonction Composable principale pour afficher une liste de paroles de karaoké.

**Paramètres :**
- `lyrics` : Une liste d'objets `KaraokeLine`, représentant les paroles avec leurs timestamps de début et de fin.
- `currentTime` : Le temps actuel (en secondes) pour déterminer quelle ligne de texte est mise en évidence.

**Fonctionnement :**
- Utilisation de `LazyColumn` pour afficher les paroles sous forme de liste verticale défilante.
- Chaque ligne est rendue avec :
    - **Texte vert** : Si la ligne correspond au temps actuel.
    - **Texte gris** : Pour les lignes non actives.

## Fonctionnalités principales
1. **Détection de la ligne actuelle :**
    - Une ligne est considérée comme actuelle si :
      
      currentTime >= line.startTime && currentTime < line.endTime
      

2. **Affichage des paroles :**
    - Utilisation de `Text` pour afficher le texte de chaque ligne.
    - Application de styles dynamiques (couleurs et marges) pour distinguer la ligne active.



