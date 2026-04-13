#!/bin/bash

# Créer les images d'arènes en tant que fichiers PNG 1x1 de base
# Ceci est juste un placeholder - tu peux remplacer avec tes vraies images

BACKGROUNDS_DIR="/Users/mayel/Documents/Ynov/Java/Java_Royal/src/main/resources/com/example/java_royal/assets/backgrounds"

echo "Création des images d'arènes placeholder..."

# Créer 5 images PNG minimalistes avec ImageMagick ou directement
# Pour simplicité, on créera des fichiers vides que tu remplaceras par les vraies images

for i in {1..5}; do
    # Si tu as ImageMagick installé, décommente la ligne suivante:
    # convert -size 1920x1080 xc:blue "$BACKGROUNDS_DIR/arene$i.jpg"

    # Pour maintenant, créer un fichier texte comme placeholder
    echo "Placeholder pour arene$i" > "$BACKGROUNDS_DIR/arene$i.jpg"
    echo "✅ Fichier arene$i.jpg créé"
done

echo "✅ Toutes les images placeholder créées!"
echo "📝 NOTE: Remplace ces fichiers par tes vraies images PNG/JPG"

