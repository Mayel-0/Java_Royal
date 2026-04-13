package com.example.java_royal.util;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire pour gérer le cache des images d'arènes.
 * Pré-charge les images une seule fois au démarrage pour optimiser les performances.
 */
public class ArenaImageCache {

    private static final Map<Integer, Image> imageCache = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initialise le cache avec toutes les images d'arènes
     * À appeler une seule fois au démarrage de l'application
     */
    public static void initialize() {
        if (initialized) return;

        // Pré-charge les 5 images d'arènes
        for (int i = 1; i <= 5; i++) {
            // Les fichiers sont dans: src/main/resources/assets background/Arene1.jpg, etc
            // Chemin CORRECT avec l'espace exactement comme dans le dossier réel
            String imagePath = "/assets background/Arene" + i + ".jpg";
            try {
                System.out.println("[ArenaImageCache] Tentative de chargement: " + imagePath);

                // Vérifier que la ressource existe
                var resourceURL = ArenaImageCache.class.getResource(imagePath);
                if (resourceURL == null) {
                    System.err.println("[ArenaImageCache] ❌ Ressource non trouvée: " + imagePath);
                    continue;
                }

                // Charger l'image depuis le flux d'entrée
                var inputStream = ArenaImageCache.class.getResourceAsStream(imagePath);
                if (inputStream == null) {
                    System.err.println("[ArenaImageCache] ❌ InputStream null pour: " + imagePath);
                    continue;
                }

                Image image = new Image(inputStream);
                if (!image.isError()) {
                    imageCache.put(i, image);
                    System.out.println("[ArenaImageCache] ✅ Image Arene" + i + " chargée avec succès");
                } else {
                    System.err.println("[ArenaImageCache] ❌ Erreur lors du chargement de Arene" + i + " (image.isError() = true)");
                }
            } catch (Exception e) {
                System.err.println("[ArenaImageCache] ❌ Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

        initialized = true;
        System.out.println("[ArenaImageCache] ✅ Cache initialisé avec " + imageCache.size() + " images");
    }

    /**
     * Récupère une image du cache selon le niveau
     * Si le niveau > 5, retourne l'image de niveau 5 par défaut
     *
     * @param level Niveau du joueur (1-5+)
     * @return Image correspondante ou null si non trouvée
     */
    public static Image getArenaImage(int level) {
        // Limite au niveau 5 si dépassé
        int actualLevel = Math.min(level, 5);

        if (actualLevel < 1) {
            actualLevel = 1;
        }

        Image image = imageCache.get(actualLevel);

        if (image == null) {
            System.err.println("[ArenaImageCache] ⚠️ Image pour niveau " + actualLevel + " non trouvée");
        }

        return image;
    }

    /**
     * Retourne le nombre d'images en cache
     */
    public static int getCacheSize() {
        return imageCache.size();
    }

    /**
     * Affiche les images disponibles en cache
     */
    public static void printCachedImages() {
        System.out.println("[ArenaImageCache] Images en cache:");
        imageCache.forEach((level, image) ->
            System.out.println("  Niveau " + level + ": " + (image != null ? "✅ Chargée" : "❌ Null"))
        );
    }
}

