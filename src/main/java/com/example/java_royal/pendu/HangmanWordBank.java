package com.example.java_royal.pendu;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class HangmanWordBank {

    private static final Map<HangmanDifficulty, List<HangmanWord>> WORDS = Map.of(
            HangmanDifficulty.FACILE, List.of(
                    new HangmanWord("PRINCE", "Clash", "Un combattant melee avec une lance"),
                    new HangmanWord("GEANT", "Clash", "Un tank tres lent"),
                    new HangmanWord("SORCIER", "Clash", "Il lance des projectiles magiques"),
                    new HangmanWord("ARENE", "Jeu", "Lieu des affrontements"),
                    new HangmanWord("COFFRE", "Jeu", "Il contient des recompenses"),
                    new HangmanWord("CARTE", "Jeu", "Unite ou sort a poser"),
                    new HangmanWord("DRAGON", "Creature", "Il vole et souffle des flammes"),
                    new HangmanWord("ROI", "Univers", "Il defend une tour principale"),
                    new HangmanWord("TOUR", "Univers", "Structure defensive"),
                    new HangmanWord("BATAILLE", "Action", "Affrontement en temps reel")
            ),
            HangmanDifficulty.NORMAL, List.of(
                    new HangmanWord("MORTIER", "Clash", "Batiment qui tire de loin"),
                    new HangmanWord("MOUSQUETAIRE", "Clash", "Unite a distance precise"),
                    new HangmanWord("MIRROIR", "Strategie", "Duplique la carte precedente"),
                    new HangmanWord("CHEVALIER", "Clash", "Un guerrier peu couteux"),
                    new HangmanWord("BARBARES", "Clash", "Groupe de combattants robustes"),
                    new HangmanWord("ARCHERS", "Clash", "Duo d'unites a distance"),
                    new HangmanWord("ELIXIR", "Ressource", "Permet de jouer des cartes"),
                    new HangmanWord("TORNADE", "Sort", "Regroupe les unites ennemies"),
                    new HangmanWord("DEMOLITION", "Action", "Destruction complete d'une cible"),
                    new HangmanWord("CHAMPION", "Classe", "Unite avec pouvoir special")
            ),
            HangmanDifficulty.EXPERT, List.of(
                    new HangmanWord("SEISME", "Sort", "Affecte batiments et troupes au sol"),
                    new HangmanWord("SQUELETTE", "Creature", "Unite tres fragile mais rapide"),
                    new HangmanWord("CATAPULTE", "Arme", "Machine de siege a trajectoire courbe"),
                    new HangmanWord("LABYRINTHE", "Concept", "Chemin complexe avec impasses"),
                    new HangmanWord("STRATEGIQUE", "Concept", "Base sur l'anticipation"),
                    new HangmanWord("ELECTROCUTION", "Effet", "Impact electrique violent"),
                    new HangmanWord("PRECIPITATION", "Concept", "Action trop rapide"),
                    new HangmanWord("METAMORPHOSE", "Concept", "Changement de forme"),
                    new HangmanWord("CONCENTRATION", "Concept", "Etat de focus maximal"),
                    new HangmanWord("SYNCHRONISATION", "Concept", "Coordination parfaite")
            )
    );

    public HangmanWord pickRandomWord(HangmanDifficulty difficulty, Random random) {
        List<HangmanWord> pool = WORDS.getOrDefault(difficulty, WORDS.get(HangmanDifficulty.NORMAL));
        int index = random.nextInt(pool.size());
        return pool.get(index);
    }
}
