# 🎮 JAVA ROYAL - QUICK START GUIDE

## ✅ Ce qui a été Implémenté

### 1. **Modèle Utilisateur Enrichi** ✓
```java
User.java
├── id, username, email
├── currentLevel (1-5+)
├── currentXp (progression)
├── totalXp (statistique)
├── getNextLevelThreshold() → Level × 100
└── getXpProgressPercentage() → 0.0 à 1.0
```

### 2. **Service d'Authentification** ✓
```java
UserService.java
├── authenticate(username, password) → User complet avec stats
├── getUserById(id) → Données complètes
├── getUserByUsername(username)
└── updateUserXp(userId, newXp, newLevel)
```

### 3. **Gestion de Session** ✓
```java
UserSession.java (Singleton)
├── Stocke : id, username, email, level, xp
├── update(...) → Mise à jour synchrone
└── clear() → Déconnexion
```

### 4. **Page d'Introduction** ✓
```
IntroductionView.fxml + IntroductionController.java
├── Affichage: "Bienvenue dans Java Royal!"
├── Explication du concept
├── Bouton "C'est parti!"
├── Transition fluide: FadeTransition 500ms
└── Redirect → HomeView
```

### 5. **Page d'Accueil (Lobby)** ✓
```
HomeView.fxml + HomeController.java
├── TOP: Message "Bonjour [username]!"
│   ├── Badge Niveau avec bordure dorée
│   └── Barre XP: current_xp / next_threshold
├── CENTER: Fond d'arène dynamique
│   ├── ImageView récupère arena_{level}.png
│   └── Placeholder si image absente
└── BOTTOM: Boutons d'action
    ├── "Démarrer un Mini-jeu"
    ├── "Profil"
    └── "Quitter" → Logout
```

### 6. **Flux de Navigation** ✓
```
Start → [Connexion/Inscription]
  ├─ Inscription réussie (level=1)
  │  └─→ IntroductionView → HomeView
  └─ Connexion réussie
     ├─ IF level==1 → IntroductionView → HomeView
     └─ ELSE → HomeView directement
```

### 7. **Design Clash Royale** ✓
```css
styles.css
├── Couleurs: Or (#FFD700), Bleu (#1f6feb), Noir (#0d1117)
├── Gradient buttons: Doré, Vert, Gris, Rouge
├── Drop shadows: 5px, opacity 0.5
├── Border radius: 5-50px
└── Font weights: Bold pour titres/badges
```

---

## 🎯 Formule XP (Simple & Scalable)

```
Seuil XP suivant = Niveau_Actuel × 100

Exemples:
├─ Level 1: Seuil = 100 XP
├─ Level 2: Seuil = 200 XP
├─ Level 3: Seuil = 300 XP
├─ Level 4: Seuil = 400 XP
└─ Level 5: Seuil = 500 XP

Progression en %:
├─ À 45 XP, Level 1 → 45/100 = 45% ✓
├─ À 150 XP, Level 2 → 150/200 = 75% ✓
└─ À 200 XP, Level 3 → 200/300 = 67% ✓
```

---

## 📁 Structure du Projet

```
src/main/java/com/example/java_royal/
├── model/
│   └── User.java ✓ [MODIFIÉ]
├── service/
│   └── UserService.java ✓ [CRÉÉ]
├── session/
│   └── UserSession.java ✓ [MODIFIÉ]
├── controller/
│   └── HelloController.java ✓ [MODIFIÉ - LOGIN]
└── controllers/
    ├── IntroductionController.java ✓ [CRÉÉ]
    ├── HomeController.java ✓ [CRÉÉ]
    ├── RegisterController.java ✓ [MODIFIÉ]
    └── StartController.java (inchangé)

src/main/resources/com/example/java_royal/
├── introduction-view.fxml ✓ [CRÉÉ]
├── home-view.fxml ✓ [CRÉÉ]
├── start-view.fxml (inchangé)
├── hello-view.fxml (inchangé)
├── register-view.fxml (inchangé)
├── styles.css ✓ [CRÉÉ]
└── arenas/
    └── [À créer: arena_1.png, arena_2.png, ...]
```

---

## 🚀 Comment Tester

### 1. **Inscription**
```
1. Lancer l'app
2. Cliquer "S'inscrire"
3. Remplir: Username=Alice, Email=alice@test.com, Password=abc123
4. ✓ Should see IntroductionView
5. Cliquer "C'est parti!"
6. ✓ Should see HomeView with "Bienvenue Alice!"
```

### 2. **Connexion (Nouveau Compte - Level 1)**
```
1. Inscrire: Bob
2. Se déconnecter (Quitter)
3. Cliquer "Connexion"
4. Entrer: bob@test.com / abc123
5. ✓ Should see IntroductionView (car level=1)
6. ✓ Should see HomeView après "C'est parti!"
```

### 3. **Connexion (Compte Existant - Level > 1)**
```
1. Modifier en DB: UPDATE users SET current_level=3 WHERE id=1;
2. Se déconnecter et reconnecter
3. ✓ Should go directly to HomeView (pas d'intro)
4. ✓ Badge shows "3"
5. ✓ XP bar = 0 / 300
```

### 4. **HomeView Components**
```
✓ Welcome label: "Bienvenue [username]!"
✓ Level badge: Display with gold border
✓ XP bar: Shows progress (0-100%)
✓ XP text: "X / Y XP"
✓ Arena image: Try placeholder text
✓ Buttons: Try click actions (stub for now)
✓ Logout: Returns to start screen + clears session
```

---

## 💾 Vérifications Base de Données

### Schéma attendu
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
```

### Données d'exemple
```sql
-- Nouvel utilisateur
INSERT INTO users (username, email, password) 
VALUES ('alice', 'alice@test.com', '<bcrypt_hash>');
-- → level=1, current_xp=0, total_xp=0

-- Utilisateur Level 3
UPDATE users SET current_level=3, current_xp=150, total_xp=350 
WHERE username='alice';
```

---

## 🔧 Debug Tips

### 1. **Check Session Data**
```java
// In any controller
UserSession session = UserSession.getInstance();
System.out.println("Username: " + session.getUsername());
System.out.println("Level: " + session.getCurrentLevel());
System.out.println("XP: " + session.getCurrentXp());
```

### 2. **Check User from Database**
```java
try {
    User user = UserService.getUserById(1);
    System.out.println(user.getUsername() + " - Level " + 
                       user.getCurrentLevel());
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 3. **Check CSS Styles**
```java
// Add to FXML root:
// style="-fx-background-color: #0d1117;"
// style="-fx-text-fill: #FFD700;"
```

### 4. **Check ImageView Loading**
```java
try {
    Image img = new Image(
        getClass().getResourceAsStream(
            "/com/example/java_royal/arenas/arena_1.png"
        )
    );
    System.out.println("Image loaded: " + !img.isError());
} catch (Exception e) {
    e.printStackTrace();
}
```

---

## 📋 Checklist de Validation

### Avant de Lancer
- [ ] Maven compile sans erreurs
- [ ] Toutes les classes sont importées correctement
- [ ] FXML files compilent sans warnings
- [ ] CSS file is in resources/
- [ ] Database initialized (schema_sqlite.sql)

### Après le Test d'Inscription
- [ ] ✓ Utilisateur créé en DB avec level=1
- [ ] ✓ Introduction affichée
- [ ] ✓ "C'est parti!" → HomeView avec FadeTransition
- [ ] ✓ Bienvenue personnalisée visible
- [ ] ✓ Badge niveau = 1
- [ ] ✓ XP bar = 0 / 100

### Après le Test de Connexion
- [ ] ✓ Level 1 → Introduction automatique
- [ ] ✓ Level > 1 → HomeView directement
- [ ] ✓ Session stocke les données
- [ ] ✓ Logout → Start screen

### Design Verification
- [ ] ✓ Couleurs Clash Royale appliquées
- [ ] ✓ Gradients visibles sur les barres
- [ ] ✓ Bordures dorées autour du badge
- [ ] ✓ Ombres (drop shadows) présentes
- [ ] ✓ Fonts en bold pour titres

---

## 🎯 Prochaines Étapes Immédiates

1. **Créer images d'arènes**
   - arena_1.png (300×400) → Entraînement
   - arena_2.png → Forêt
   - arena_3.png → Château

2. **Implémenter bouton "Démarrer Mini-jeu"**
   - Créer `MiniGameController`
   - Créer `quiz-view.fxml`
   - Lier au système d'XP

3. **Ajouter système de montée de niveau**
   - Quand XP ≥ seuil → Level +1, XP reset
   - Afficher animation "Level Up!"
   - Débloquer nouvelle arène

4. **Page Profil**
   - `ProfileController.java` + `profile-view.fxml`
   - Afficher: Total XP, Statistiques, Historique

---

## 📚 Documentation Générale

**Document complet:** `POST_LOGIN_ARCHITECTURE.md`
- Architecture MVC détaillée
- Tous les contrôleurs expliqués
- Formules XP avec exemples
- Patterns utilisés

**Guide Mini-jeux:** `MINIGAMES_IMPLEMENTATION_GUIDE.md`
- Templates pour créer mini-jeux
- Exemple Quiz complet
- Système d'arènes
- Assets recommendations

---

## ✨ Points Forts

✅ **Architecture propre** — MVC séparation nette
✅ **Sécurité** — BCrypt hashing, Singleton pour session
✅ **Extensibilité** — Facile d'ajouter mini-jeux, arènes, etc.
✅ **UX fluide** — Transitions, FadeTransition, animations
✅ **Design moderne** — Inspiré Clash Royale, professionnel
✅ **Documentation** — Guides complets pour chaque phase

---

**Code prêt à compiler et tester ! 🎮✨**

Besoin d'aide pour les mini-jeux ou les images d'arènes?

