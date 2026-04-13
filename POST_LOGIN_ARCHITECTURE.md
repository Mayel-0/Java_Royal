# Architecture Post-Connexion - Java Royal

## 📋 Résumé des Changements

J'ai implémenté une architecture MVC complète pour gérer la logique post-connexion avec les fonctionnalités suivantes :

### 1️⃣ **Gestion du Premier Lancement (Introduction View)**

#### Vue : `introduction-view.fxml`
- Affiche un message de bienvenue stylisé « Java Royal »
- Explique le concept : *"Gravissez les arènes en remportant des mini-jeux pour gagner de l'XP et débloquer de nouveaux mondes"*
- Bouton « C'est parti ! » qui déclenche une transition FadeTransition fluide

#### Contrôleur : `IntroductionController.java`
```java
- goToHome() : Navigue vers l'accueil avec une transition fluide (fade out/fade in)
- Utilise FadeTransition avec durée de 500ms pour un effet professionnel
```

**Logique d'Affichage :**
- Affichée UNIQUEMENT si `current_level == 1` après connexion
- Après inscription, niveau par défaut = 1 → Introduction automatique
- Après le premier "C'est parti!", utilisateur → Home

---

### 2️⃣ **Interface d'Accueil (Home View / Lobby)**

#### Vue : `home-view.fxml`
Structure avec **BorderPane** :

**TOP - Barre d'Information :**
```
┌─────────────────────────────────────────────────┐
│  Bienvenue [username] !                          │
│  ┌──────────┐  Progression XP                    │
│  │ Niveau   │  ▓▓▓▓▓░░░░ (45 / 100 XP)          │
│  │    1     │                                     │
│  └──────────┘                                     │
└─────────────────────────────────────────────────┘
```

- **Message personnalisé :** "Bienvenue [username] !"
- **Badge Niveau :** Affiche le niveau actuel avec bordure dorée (style Clash Royale)
- **Barre de Progression XP :** 
  - Affiche `current_xp / next_level_threshold`
  - Formule : `Seuil_Suivant = Niveau_Actuel × 100`
  - Exemple : Niveau 1 → seuil 100 XP, Niveau 2 → seuil 200 XP

**CENTER - Fond d'Arène Dynamique :**
```
         Arène 1 - Entraînement
         (ImageView placeholder)
```

- Récupère l'image selon le niveau du joueur
- Chemin : `/resources/arenas/arena_{level}.png`
- Fallback sur texte si image introuvable

**BOTTOM - Boutons d'Action :**
```
┌─────────────────────────────────────────────────┐
│  [Démarrer Mini-jeu]  [Profil]  [Quitter]      │
└─────────────────────────────────────────────────┘
```

#### Contrôleur : `HomeController.java`
```java
public class HomeController {
    // Composants FXML
    @FXML private Label welcomeLabel;
    @FXML private Label levelBadge;
    @FXML private ProgressBar xpProgressBar;
    @FXML private Label xpLabel;
    @FXML private ImageView arenaImageView;
    @FXML private Label arenaPlaceholder;

    @FXML
    public void initialize() {
        loadPlayerData();          // Charge depuis la session/DB
        updateArenaBackground();    // Met à jour le fond
    }

    private void loadPlayerData() throws SQLException {
        // Récupère les données du joueur : username, level, xp
        User user = UserService.getUserById(session.getId());
        
        // Met à jour l'UI
        welcomeLabel.setText("Bienvenue " + user.getUsername() + " !");
        levelBadge.setText(String.valueOf(user.getCurrentLevel()));
        
        // Configure la barre XP
        xpProgressBar.setProgress(user.getXpProgressPercentage());
        xpLabel.setText(user.getCurrentXp() + " / " + 
                       user.getNextLevelThreshold() + " XP");
    }

    private void updateArenaBackground() {
        // Niveau 1 = Arène d'entraînement
        // Niveau 2 = Arène 2, etc.
        String imagePath = "/com/example/java_royal/arenas/arena_" 
                          + level + ".png";
        
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            arenaImageView.setImage(image);
        } catch (Exception e) {
            // Affiche placeholder si image introuvable
            arenaPlaceholder.setText("Arène " + level + " - Entraînement");
        }
    }

    @FXML
    private void handleLogout() {
        // Efface la session et revient à l'écran de connexion
        UserSession.getInstance().clear();
    }
}
```

---

### 3️⃣ **Architecture MVC - Séparation des Responsabilités**

#### **Model Layer**
```
src/main/java/com/example/java_royal/model/
├── User.java               ← Représente un utilisateur avec ses stats
│   ├── id, username, email
│   ├── currentLevel, currentXp, totalXp
│   ├── getNextLevelThreshold()  ← Calcul XP: Level × 100
│   └── getXpProgressPercentage() ← Pourcentage de progression
```

#### **Service Layer (Business Logic)**
```
src/main/java/com/example/java_royal/service/
└── UserService.java        ← Gère toute la logique SQL et métier
    ├── authenticate(username, password) → User
    ├── getUserById(id) → User
    ├── getUserByUsername(username) → User
    ├── updateUserXp(userId, newXp, newLevel)
    └── updateUserTutorialStatus(userId, completed)
```

#### **Session Layer**
```
src/main/java/com/example/java_royal/session/
└── UserSession.java        ← Singleton pour stocker l'utilisateur actif
    ├── id, username, email
    ├── currentLevel, currentXp
    └── update(...) / clear()
```

#### **Controller/View Layer**
```
src/main/java/com/example/java_royal/controllers/
├── HelloController.java         ← Login, redirige vers Introduction ou Home
├── IntroductionController.java  ← Vue d'intro avec FadeTransition
├── HomeController.java          ← Accueil/Lobby principal
├── RegisterController.java      ← Inscription, redirige vers Introduction
└── StartController.java         ← Écran de démarrage (Connexion/Inscription)

src/main/resources/com/example/java_royal/
├── introduction-view.fxml
├── home-view.fxml
├── hello-view.fxml (Login)
├── register-view.fxml
├── start-view.fxml
└── styles.css               ← Thème Clash Royale
```

---

### 4️⃣ **Flux de Navigation**

```
START
  ↓
[start-view.fxml - StartController]  ← Choix : Connexion ou Inscription
  ├─→ goToLogin()
  │    ↓
  │  [hello-view.fxml - HelloController - LOGIN]
  │    ├─ Authentification échouée → Affiche erreur
  │    └─ Authentification réussie
  │         ├─ IF level == 1 → goToIntroduction()
  │         │    ↓
  │         │  [introduction-view.fxml - IntroductionController]
  │         │    └─ C'est parti! → FadeTransition → goToHome()
  │         │
  │         └─ ELSE → goToHome()
  │
  └─→ goToRegister()
       ↓
     [register-view.fxml - RegisterController - INSCRIPTION]
       └─ Inscription réussie (level=1 par défaut)
            ↓
          [introduction-view.fxml - IntroductionController]
            └─ C'est parti! → FadeTransition → goToHome()

HOME
  ↓
[home-view.fxml - HomeController - LOBBY PRINCIPAL]
  ├─ Affiche : username, level, xp, fond d'arène
  ├─ Boutons : Mini-jeu, Profil, Quitter
  └─ Quitter → handleLogout() → Retour à start-view
```

---

### 5️⃣ **Système de Calcul XP**

```java
// Formule simple et évolutive
public int getNextLevelThreshold() {
    return currentLevel * 100;
}

// Exemple :
// Niveau 1 : Seuil = 1 × 100 = 100 XP
// Niveau 2 : Seuil = 2 × 100 = 200 XP
// Niveau 3 : Seuil = 3 × 100 = 300 XP
// ...

// Progression en %
public double getXpProgressPercentage() {
    int threshold = getNextLevelThreshold();
    return Math.min(1.0, (double) currentXp / threshold);
}

// Exemple :
// currentXp = 45, level = 1, threshold = 100
// Progression = 45 / 100 = 0.45 = 45% ✅
```

---

### 6️⃣ **Design Thème Clash Royale**

#### Fichier : `styles.css`
```css
/* Couleurs Clash Royale */
.root                      → Fond noir #0d1117
.header-bar / .footer-bar  → Gradient bleu #1f6feb → #1a4d99
.level-badge               → Bordure dorée #FFD700, fond transparent
.text-gold                 → Couleur dorée #FFD700 avec dropshadow

/* Boutons */
.button-primary            → Gradient doré (Start, Continue)
.button-success            → Gradient vert (Démarrer Mini-jeu)
.button-secondary          → Gradient gris (Profil)
.button-danger             → Gradient rouge (Quitter)

/* Effets */
- dropshadow(gaussian, #000000, 5, 0.5, 0, 2) → Ombres professionnelles
- FadeTransition(500ms)  → Transitions fluides
```

**Exemple d'application :**
```xml
<Label text="Bienvenue Alice !"
       style="-fx-text-fill: #FFD700; 
               -fx-font-size: 24; 
               -fx-font-weight: bold; 
               -fx-effect: dropshadow(gaussian, #000000, 3, 0.5, 0, 2);"/>

<VBox style="-fx-border-color: #FFD700; 
             -fx-border-width: 2; 
             -fx-border-radius: 10; 
             -fx-background-color: rgba(255, 215, 0, 0.1); 
             -fx-padding: 10 20;">
    <!-- Badge de niveau -->
</VBox>
```

---

### 7️⃣ **Gestion du Background d'Arène Dynamique**

```java
private void updateArenaBackground() {
    int level = session.getCurrentLevel();
    
    // Construction du chemin d'image
    String imagePath = "/com/example/java_royal/arenas/arena_" + level + ".png";
    
    try {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        if (image.isError()) {
            // Si image introuvable, affiche placeholder
            arenaPlaceholder.setText("Arène " + level + " - Entraînement");
            arenaImageView.setVisible(false);
        } else {
            arenaImageView.setImage(image);
            arenaImageView.setVisible(true);
        }
    } catch (Exception e) {
        arenaPlaceholder.setText("Arène " + level + " - Entraînement");
    }
}

// À implémenter pour chaque niveau :
// src/main/resources/com/example/java_royal/arenas/
// ├── arena_1.png  (Arène d'entraînement)
// ├── arena_2.png  (Arène 2)
// ├── arena_3.png  (Arène 3)
// └── ...
```

---

## 📝 Fichiers Créés/Modifiés

### ✅ **Fichiers Créés**
```
src/main/java/com/example/java_royal/
├── service/
│   └── UserService.java                    [NEW]
└── controllers/
    ├── IntroductionController.java         [NEW]
    └── HomeController.java                 [NEW]

src/main/resources/com/example/java_royal/
├── introduction-view.fxml                  [NEW]
├── home-view.fxml                          [NEW]
└── styles.css                              [NEW]
```

### 🔄 **Fichiers Modifiés**
```
src/main/java/com/example/java_royal/
├── model/
│   └── User.java                           [MODIFIED - Ajout level, xp, calculs]
├── session/
│   └── UserSession.java                    [MODIFIED - Ajout level, xp]
├── controller/
│   └── HelloController.java                [MODIFIED - Logique post-login]
└── controllers/
    └── RegisterController.java             [MODIFIED - Redirection intro]
```

---

## 🚀 Comment Utiliser

### 1. Inscription
```
1. Cliquer "S'inscrire" sur start-view
2. Remplir username, email, password
3. Level = 1 (défaut) → Introduction automatique
4. Voir le message de bienvenue
5. Cliquer "C'est parti!" → FadeTransition → Home
```

### 2. Connexion
```
1. Cliquer "Connexion" sur start-view
2. Entrer credentials
3. IF level == 1 → Introduction
   ELSE → Home directement
```

### 3. Accueil (Home)
```
Affiche :
- Nom d'utilisateur personnalisé
- Niveau actuel (badge dorée)
- Barre XP avec progression en %
- Image de fond d'arène dynamique selon le niveau

Boutons :
- "Démarrer un Mini-jeu" → [À implémenter]
- "Profil" → [À implémenter]
- "Quitter" → Retour à connexion, efface session
```

---

## 🔧 Prochaines Étapes

### Phase 2 : Mini-jeux et Progression
- [ ] Créer les vues de mini-jeux
- [ ] Implémenter la logique de victoire → +XP
- [ ] Système de montée de niveau automatique

### Phase 3 : Profil Utilisateur
- [ ] Vue de profil avec stats complètes
- [ ] Historique des victoires
- [ ] Achievements/Trophées

### Phase 4 : Mondes et Arènes
- [ ] Déblocage progressif des arènes selon le niveau
- [ ] Assets graphiques pour chaque arène (arena_1.png, arena_2.png, etc.)

### Phase 5 : Système de Classement
- [ ] Leaderboard global
- [ ] Classement par amis

---

## 💾 Schéma de Base de Données

```sql
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT NOT NULL UNIQUE,
  email TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  current_level INTEGER DEFAULT 1,
  current_xp INTEGER DEFAULT 0,
  total_xp INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Exemple de données :
-- id=1, username=alice, level=1, current_xp=0, total_xp=0
-- id=2, username=bob, level=3, current_xp=150, total_xp=450
```

---

## 📚 Références Architecturales

### MVC Pattern
- **Model** : `User.java` — Données et calculs
- **Service** : `UserService.java` — Logique SQL et métier
- **Controller** : `*Controller.java` — Gestion des événements
- **View** : `*.fxml` — Interface utilisateur

### Design Patterns Utilisés
- **Singleton** : `UserSession.getInstance()`
- **Factory** : `FXMLLoader.load()` pour créer les vues
- **Observer** : `@FXML` annotations (JavaFX binding)
- **Transition** : `FadeTransition` pour animations fluides

---

## ✨ Points Forts de cette Implémentation

✅ **Architecture MVC propre** — Séparation nette des responsabilités  
✅ **Formule XP simple** — Niveau × 100 pour progression linéaire  
✅ **Design inspiré Clash Royale** — Bordures dorées, gradients, ombres  
✅ **Transitions fluides** — FadeTransition 500ms professionnel  
✅ **Gestion session robuste** — Singleton thread-safe  
✅ **Système modulaire** — Facile à étendre (mini-jeux, profil, etc.)  
✅ **Code documenté** — Commentaires JavaDoc complets  

---

**Prêt à lancer l'application ? 🎮**

