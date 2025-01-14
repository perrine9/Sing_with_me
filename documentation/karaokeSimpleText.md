# Documentation du Projet - KaraokeSimpleText

## Description
Le fichier `KaraokeSimpleText` contient une fonction Composable utilisée pour afficher du texte synchronisé avec une progression, typique pour un affichage de type karaoké. Cette fonction est destinée à créer une expérience visuelle où les parties lues et non lues du texte sont clairement différenciées.

## Structure des fonctions

### KaraokeSimpleText
Fonction Composable principale pour afficher du texte synchronisé.

**Paramètres :**
- `text` : Le texte à afficher.
- `progress` : Progression de la lecture, représentée par une valeur flottante entre 0.0 (début) et 1.0 (fin).

**Fonctionnement :**
- Le texte est affiché en deux couches :
    - **Texte complet** : Affiché en rouge, représentant tout le texte.
    - **Texte lu** : Superposé en noir, représentant la portion déjà lue, calculée à partir de la progression.

## Fonctionnalités principales
1. **Affichage superposé :**
    - Utilisation de `Box` pour superposer deux couches de texte.
    - La première couche est le texte complet en rouge.
    - La seconde couche est le texte lu en noir, masquée dynamiquement en fonction de la progression.

2. **Calcul de la largeur du texte lu :**
    - Utilisation de `Modifier.onSizeChanged` pour obtenir la largeur totale du texte.
    - Application de `Modifier.drawWithContent` pour découper dynamiquement la portion visible en fonction de `progress`.

## Exemple d'utilisation
KaraokeSimpleText(text = "When you were here before", progress = 0.5f)
Dans cet exemple, la moitié du texte sera affichée en noir pour représenter une progression de 50%.

## Utilisation dans le projet
La fonction `KaraokeSimpleText` est essentielle pour créer une expérience visuelle immersive et dynamique dans une application de karaoké. Elle permet de synchroniser visuellement le texte avec l'audio pour offrir une interaction fluide.
