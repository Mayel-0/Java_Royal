# Guide Pratique - Implémentation des Arènes et Mini-jeux

## 🎮 Architecture Recommandée pour Phase 2+

### Structure des Dossiers à Créer

```
src/main/java/com/example/java_royal/
├── minigame/
│   ├── MiniGame.java              ← Interface abstraite
│   ├── MiniGameController.java     ← Contrôleur générique
│   └── games/
│       ├── QuizGame.java          ← Mini-jeu Quiz
│       ├── MemoryGame.java        ← Mini-jeu Mémoire
│       └── ReflexGame.java        ← Mini-jeu Réflexes
├── arena/
│   ├── ArenaManager.java          ← Gère déblocage arènes
│   └── ArenaData.java             ← Données des arènes
└── stats/
    ├── StatsService.java          ← Gère XP et niveaux
    └── AchievementService.java    ← Gère achievements

src/main/resources/com/example/java_royal/
├── arenas/
│   ├── arena_1.png    (300x400) → Arène d'entraînement
│   ├── arena_2.png    (300x400) → Forêt enchantée
│   └── ...
├── minigames/
│   ├── quiz-view.fxml
│   ├── memory-view.fxml
│   └── reflex-view.fxml
```

---

## 🎯 Implémentation des Arènes

### 1. Données des Arènes

```java
// src/main/java/com/example/java_royal/arena/ArenaData.java

public enum ArenaData {
    ARENA_1(1, "Arène d'Entraînement", "Commencez votre aventure", 1, 0),
    ARENA_2(2, "Forêt Enchantée", "Explorez la magie", 2, 100),
    ARENA_3(3, "Château du Roi", "Affrontez le défi", 3, 300),
    ARENA_4(4, "Montagne Gelée", "Survivez au froid", 4, 600),
    ARENA_5(5, "Volcan Souterrain", "Résistez à la chaleur", 5, 1000);

    private final int level;
    private final String name;
    private final String description;
    private final int minPlayerLevel;
    private final int xpReward;

    ArenaData(int level, String name, String description, 
              int minPlayerLevel, int xpReward) {
        this.level = level;
        this.name = name;
        this.description = description;
        this.minPlayerLevel = minPlayerLevel;
        this.xpReward = xpReward;
    }

    // Getters
    public static ArenaData getArenaForLevel(int level) {
        for (ArenaData arena : values()) {
            if (arena.level == level) return arena;
        }
        return ARENA_1;
    }

    public boolean isUnlockedFor(int playerLevel) {
        return playerLevel >= minPlayerLevel;
    }
}
```

### 2. Gestionnaire d'Arènes

```java
// src/main/java/com/example/java_royal/arena/ArenaManager.java

public class ArenaManager {
    
    /**
     * Vérifie si une arène est débloquée pour le joueur
     */
    public static boolean isArenaUnlocked(int playerLevel, int arenaLevel) {
        ArenaData arena = ArenaData.getArenaForLevel(arenaLevel);
        return playerLevel >= arena.getMinPlayerLevel();
    }

    /**
     * Récupère les arènes disponibles pour le joueur
     */
    public static List<ArenaData> getAvailableArenas(int playerLevel) {
        return Arrays.stream(ArenaData.values())
            .filter(arena -> isArenaUnlocked(playerLevel, arena.getLevel()))
            .collect(Collectors.toList());
    }

    /**
     * Lance un mini-jeu dans une arène
     */
    public static int playMiniGame(int arenaLevel, MiniGame game) {
        // Logique d'exécution du mini-jeu
        int result = game.play(); // 0 = Défaite, 1+ = Points
        return result > 0 ? awardXp(arenaLevel, result) : 0;
    }

    private static int awardXp(int arenaLevel, int performance) {
        ArenaData arena = ArenaData.getArenaForLevel(arenaLevel);
        // XP = Base + Bonus performance
        return arena.getXpReward() + (performance * 10);
    }
}
```

---

## 🎮 Interface Générique pour Mini-jeux

```java
// src/main/java/com/example/java_royal/minigame/MiniGame.java

public interface MiniGame {
    
    /**
     * Lance le mini-jeu et retourne le score/résultat
     */
    int play();

    /**
     * Obtient le nom du mini-jeu
     */
    String getName();

    /**
     * Obtient la description
     */
    String getDescription();

    /**
     * Durée du jeu en secondes
     */
    int getDurationSeconds();
}
```

---

## 🧠 Exemple: Mini-jeu Quiz

```java
// src/main/java/com/example/java_royal/minigame/games/QuizGame.java

public class QuizGame implements MiniGame {
    private static final int DURATION = 30; // secondes
    private int score = 0;
    private List<Question> questions;

    public QuizGame(int arenaLevel) {
        this.questions = generateQuestions(arenaLevel);
    }

    @Override
    public int play() {
        // Retourne le nombre de bonnes réponses
        return score;
    }

    @Override
    public String getName() {
        return "Quiz Royal";
    }

    @Override
    public String getDescription() {
        return "Répondez à 5 questions Java en moins de 30 secondes !";
    }

    @Override
    public int getDurationSeconds() {
        return DURATION;
    }

    private List<Question> generateQuestions(int level) {
        // Questions basées sur le niveau
        List<Question> list = new ArrayList<>();
        
        if (level >= 1) {
            list.add(new Question(
                "Qu'est-ce qu'une classe en Java ?",
                new String[]{"Un type de base", "Un modèle d'objet", "Une variable"},
                1
            ));
        }
        
        if (level >= 2) {
            list.add(new Question(
                "À quoi sert l'héritage ?",
                new String[]{"Gagner de l'argent", "Réutiliser du code", "Créer des bugs"},
                1
            ));
        }
        
        return list;
    }

    public void answerQuestion(int questionIndex, int answerIndex) {
        if (questions.get(questionIndex).isCorrect(answerIndex)) {
            score++;
        }
    }
}

class Question {
    private String question;
    private String[] options;
    private int correctIndex;

    public Question(String question, String[] options, int correctIndex) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public boolean isCorrect(int index) {
        return index == correctIndex;
    }

    // Getters...
}
```

---

## 🎮 Vue du Mini-jeu Quiz (FXML)

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>

<BorderPane xmlns:fx="http://javafx.com/fxml" xmlns="http://javafx.com/javafx/21"
            fx:controller="com.example.java_royal.minigame.MiniGameController"
            style="-fx-background-color: #0d1117;">

    <!-- Top: Timer et Score -->
    <top>
        <HBox spacing="50" alignment="CENTER" 
              style="-fx-padding: 20; -fx-background-color: linear-gradient(180deg, #1f6feb 0%, #1a4d99 100%); -fx-border-color: #FFD700; -fx-border-width: 0 0 3 0;">
            
            <Label fx:id="timerLabel" text="30s"
                   style="-fx-text-fill: #FFD700; -fx-font-size: 28; -fx-font-weight: bold;"/>
            
            <Label fx:id="scoreLabel" text="Score: 0/5"
                   style="-fx-text-fill: #FFFFFF; -fx-font-size: 20;"/>
        </HBox>
    </top>

    <!-- Center: Contenu du Quiz -->
    <center>
        <VBox spacing="30" alignment="CENTER" style="-fx-padding: 40;">
            
            <!-- Question -->
            <Label fx:id="questionLabel" wrapText="true"
                   text="Qu'est-ce qu'une classe en Java ?"
                   style="-fx-text-fill: #FFD700; -fx-font-size: 24; -fx-font-weight: bold;"/>

            <!-- Réponses (Boutons) -->
            <VBox spacing="15" alignment="CENTER">
                <Button fx:id="answer1Button" prefWidth="300" prefHeight="50"
                        text="Un type de base"
                        style="-fx-font-size: 14; -fx-text-fill: white; -fx-background-color: #4a90e2; -fx-border-radius: 5; -fx-background-radius: 5;"
                        onAction="#handleAnswer"/>
                
                <Button fx:id="answer2Button" prefWidth="300" prefHeight="50"
                        text="Un modèle d'objet"
                        style="-fx-font-size: 14; -fx-text-fill: white; -fx-background-color: #4a90e2; -fx-border-radius: 5; -fx-background-radius: 5;"
                        onAction="#handleAnswer"/>
                
                <Button fx:id="answer3Button" prefWidth="300" prefHeight="50"
                        text="Une variable"
                        style="-fx-font-size: 14; -fx-text-fill: white; -fx-background-color: #4a90e2; -fx-border-radius: 5; -fx-background-radius: 5;"
                        onAction="#handleAnswer"/>
            </VBox>
        </VBox>
    </center>

</BorderPane>
```

---

## 🎮 Contrôleur du Mini-jeu

```java
// src/main/java/com/example/java_royal/minigame/MiniGameController.java

public class MiniGameController {
    
    @FXML private Label timerLabel;
    @FXML private Label scoreLabel;
    @FXML private Label questionLabel;
    @FXML private Button answer1Button, answer2Button, answer3Button;

    private QuizGame game;
    private int currentQuestion = 0;
    private Timeline timer;

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        game = new QuizGame(session.getCurrentLevel());
        
        displayQuestion(0);
        startTimer();
    }

    private void displayQuestion(int index) {
        if (index < game.getQuestions().size()) {
            Question q = game.getQuestions().get(index);
            questionLabel.setText(q.getQuestion());
            
            answer1Button.setText(q.getOptions()[0]);
            answer2Button.setText(q.getOptions()[1]);
            answer3Button.setText(q.getOptions()[2]);
        }
    }

    @FXML
    private void handleAnswer(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();
        
        // Détermine l'index de la réponse
        int answerIndex = 0;
        if (clicked == answer2Button) answerIndex = 1;
        else if (clicked == answer3Button) answerIndex = 2;

        game.answerQuestion(currentQuestion, answerIndex);
        
        currentQuestion++;
        if (currentQuestion < game.getQuestions().size()) {
            displayQuestion(currentQuestion);
        } else {
            endGame();
        }
    }

    private void startTimer() {
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                int remaining = Integer.parseInt(timerLabel.getText().split("s")[0]) - 1;
                if (remaining >= 0) {
                    timerLabel.setText(remaining + "s");
                } else {
                    endGame();
                }
            })
        );
        timer.setCycleCount(game.getDurationSeconds());
        timer.play();
    }

    private void endGame() {
        timer.stop();
        
        int xpGained = game.play();
        awardXp(xpGained);
        
        // Affiche résultat et revient à Home
        showResultDialog(xpGained);
    }

    private void awardXp(int xp) {
        try {
            UserSession session = UserSession.getInstance();
            User currentUser = UserService.getUserById(session.getId());
            
            int newXp = currentUser.getCurrentXp() + xp;
            int newLevel = currentUser.getCurrentLevel();
            int threshold = currentUser.getNextLevelThreshold();
            
            // Montée de niveau si dépassement du seuil
            while (newXp >= threshold) {
                newXp -= threshold;
                newLevel++;
                threshold = newLevel * 100;
            }
            
            UserService.updateUserXp(session.getId(), newXp, newLevel);
            session.setCurrentXp(newXp);
            session.setCurrentLevel(newLevel);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showResultDialog(int xpGained) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Résultat du Jeu");
        alert.setHeaderText("Victoire !");
        alert.setContentText("Vous avez gagné " + xpGained + " XP !");
        alert.showAndWait();
        
        // Revient à Home
        goBackToHome();
    }

    private void goBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/java_royal/home-view.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) timerLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 📊 Service de Statistiques

```java
// src/main/java/com/example/java_royal/stats/StatsService.java

public class StatsService {
    
    /**
     * Augmente l'XP d'un joueur et gère la montée de niveau
     */
    public static void addXp(long userId, int xpAmount) throws SQLException {
        User user = UserService.getUserById(userId);
        
        int newXp = user.getCurrentXp() + xpAmount;
        int newLevel = user.getCurrentLevel();
        int totalXp = user.getTotalXp() + xpAmount;
        
        // Boucle de montée de niveau
        int threshold = newLevel * 100;
        while (newXp >= threshold) {
            newXp -= threshold;
            newLevel++;
            threshold = newLevel * 100;
            
            // Événement : Level Up
            onLevelUp(userId, newLevel);
        }
        
        UserService.updateUserXp(userId, newXp, newLevel);
    }

    /**
     * Gère l'événement de montée de niveau
     */
    private static void onLevelUp(long userId, int newLevel) {
        System.out.println("✨ Level Up! Niveau " + newLevel + " atteint !");
        
        // Déverrouille les arènes supérieures
        // Déclenche des notifications
        // Augmente les récompenses
    }

    /**
     * Obtient la progression globale d'un joueur
     */
    public static Map<String, Object> getPlayerProgress(long userId) throws SQLException {
        User user = UserService.getUserById(userId);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("level", user.getCurrentLevel());
        progress.put("currentXp", user.getCurrentXp());
        progress.put("totalXp", user.getTotalXp());
        progress.put("xpToNextLevel", user.getNextLevelThreshold() - user.getCurrentXp());
        progress.put("progression", user.getXpProgressPercentage());
        
        return progress;
    }
}
```

---

## 🎨 Création des Images d'Arènes

### Dimensions recommandées
- **300×400** pixels pour les aperçus
- **Format PNG** avec transparence
- **DPI 72** pour écran

### Outils gratuits
- **GIMP** — Éditeur raster complet
- **Aseprite** — Spécialisé pixel art
- **Krita** — Peinture digitale

### Palette Clash Royale recommandée
```
#1F1F2E - Noir profond
#2D5A7B - Bleu foncé
#E8B03F - Or
#FF6B35 - Orange-rouge
#2CD26C - Vert émeraude
#8B4513 - Marron
```

---

## 🚀 Checklist d'Implémentation

### Phase 2: Mini-jeux
- [ ] Créer interface `MiniGame`
- [ ] Implémenter `QuizGame`
- [ ] Créer `quiz-view.fxml`
- [ ] Implémenter `MiniGameController`
- [ ] Ajouter bouton "Démarrer Mini-jeu" à Home
- [ ] Tester cycle complet: Home → Quiz → Victoire → +XP → Home
- [ ] Implémenter `MemoryGame` (optionnel)
- [ ] Implémenter `ReflexGame` (optionnel)

### Phase 3: Arènes Dynamiques
- [ ] Créer `ArenaManager`
- [ ] Créer `ArenaData` enum
- [ ] Générer images arena_1.png à arena_5.png
- [ ] Tester déblocage progressif des arènes
- [ ] Ajouter animations de transition
- [ ] Implémenter sons d'arène

### Phase 4: Système de Progression
- [ ] Créer `StatsService`
- [ ] Implémenter système de montée de niveau automatique
- [ ] Ajouter événements Level Up
- [ ] Créer table `achievements`
- [ ] Implémenter système de trophées

### Phase 5: Polish & Qualité
- [ ] Ajouter animations (particle effects, pop-ups XP)
- [ ] Créer leaderboard
- [ ] Ajouter musiques et effets sonores
- [ ] Optimiser performances
- [ ] Tester cross-platform

---

**Bonne chance pour l'implémentation des mini-jeux ! 🎮🚀**

