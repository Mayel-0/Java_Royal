# Système de Classement (Leaderboard) - Java Royal

## 📋 Vue d'ensemble

Un système de classement complet a été intégré à l'application **Java Royal**. Ce système affiche les 10 meilleurs joueurs classés par **expérience totale (total_xp)**.

---

## 🎯 Fonctionnalités principales

### 1️⃣ **Récupération des données** (UserService)
- **Méthode**: `getTopPlayers(int limit)`
- **SQL**: `SELECT username, current_level, total_xp FROM users ORDER BY total_xp DESC LIMIT ?`
- **Retour**: `List<LeaderboardEntry>` contenant les N meilleurs joueurs

### 2️⃣ **Affichage du classement** (LeaderboardController)
- **TableView** avec 4 colonnes:
  - 🏅 **Rang**: Affiché avec médailles (🥇 1er, 🥈 2ème, 🥉 3ème)
  - 👤 **Pseudo**: Nom d'utilisateur
  - ⭐ **Niveau**: Niveau actuel du joueur
  - 💾 **XP Totale**: Points d'expérience accumulés

### 3️⃣ **Styles personnalisés**
- **Top 3 joueurs**:
  - 🥇 1er: **Or** (#FFD700) - Fond doré clair
  - 🥈 2ème: **Argent** (#C0C0C0) - Fond gris argenté
  - 🥉 3ème: **Bronze** (#CD7F32) - Fond bronze

- **Autres joueurs**:
  - Lignes alternées: Bleu foncé (#2a2a4e) et Bleu légèrement plus clair (#353555)
  - Texte gris (#AAAAAA, #CCCCCC)

---

## 📂 Fichiers créés/modifiés

### ✨ Nouveaux fichiers

1. **LeaderboardController.java**
   - Chemin: `src/main/java/com/example/java_royal/controllers/`
   - Gère la logique du classement
   - Peuple la TableView avec les données
   - Applique les styles aux 3 premiers joueurs

2. **leaderboard-view.fxml**
   - Chemin: `src/main/resources/com/example/java_royal/`
   - Interface graphique du classement
   - Contient une TableView avec colonnes préconfigurées
   - Bouton "Retour à l'accueil"

3. **leaderboard.css** (optionnel)
   - Chemin: `src/main/resources/com/example/java_royal/`
   - Styles CSS supplémentaires pour le tableau

### 🔄 Fichiers modifiés

1. **UserService.java**
   - ➕ Ajout de la méthode `getTopPlayers(int limit)`
   - ➕ Classe interne `LeaderboardEntry` pour représenter une ligne du classement

2. **HomeController.java**
   - ➕ Ajout de la méthode `handleLeaderboard()`
   - Navigue vers le classement avec transition fluide

3. **home-view.fxml**
   - ➕ Ajout du bouton "Classement" 
   - Style doré (#FFD700/#FFA500)
   - Intégration dans la barre d'actions

---

## 🚀 Navigation

### Accès au Classement
1. Page d'accueil (HomeView)
2. Clic sur le bouton **"Classement"** (bouton doré)
3. Affichage du classement avec les 10 meilleurs joueurs
4. Clic sur **"← Retour à l'accueil"** pour revenir

---

## 📊 Modèle de données

### LeaderboardEntry (UserService)
```java
public static class LeaderboardEntry {
    private final String username;      // Nom du joueur
    private final int level;            // Niveau actuel
    private final int totalXp;          // XP totale accumulée
}
```

### LeaderboardRowData (LeaderboardController)
```java
public static class LeaderboardRowData {
    private final int rank;             // Rang (1, 2, 3, ...)
    private final String username;      // Nom du joueur
    private final int level;            // Niveau
    private final int totalXp;          // XP totale
}
```

---

## 🎨 Style Clash Royale

### Couleurs utilisées
- **Doré**: #FFD700 (Titres, top 3, bordures)
- **Orange**: #FFA500 (Boutons au survol)
- **Bleu Royal**: #1F6FEB (Barres supérieure/inférieure)
- **Bleu sombre**: #2a2a4e, #353555 (Alternance des lignes)
- **Bronze**: #CD7F32 (3ème place)
- **Argent**: #C0C0C0 (2ème place)

### Effets appliqués
- ✨ Drop shadows pour la profondeur
- 🎭 Bordures dorées (#FFD700, 2px)
- 📊 Alternance de couleurs pour lisibilité
- 🏅 Médailles emoji pour les rangs
- ⭐ Icônes stars pour les niveaux
- 💾 Formatage des XP (ex: "1,234 XP")

---

## 🔌 Intégration à la base de données

### Table `users`
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    current_level INTEGER DEFAULT 1,
    current_xp INTEGER DEFAULT 0,
    total_xp INTEGER DEFAULT 0,         -- ← Utilisé pour le classement
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
```

### Requête SQL
```sql
SELECT username, current_level, total_xp 
FROM users 
ORDER BY total_xp DESC 
LIMIT 10
```

---

## 🧪 Test du système

### Création de données de test
Pour tester le classement avec plusieurs joueurs:

```sql
-- Insérer des joueurs de test
INSERT INTO users (username, email, password, current_level, current_xp, total_xp) VALUES
('mayel', 'mayel@example.com', 'hashed_password', 5, 50, 450),
('player2', 'player2@example.com', 'hashed_password', 4, 75, 350),
('player3', 'player3@example.com', 'hashed_password', 3, 25, 250),
('player4', 'player4@example.com', 'hashed_password', 2, 0, 150);
```

---

## 📝 Exemple d'exécution

### Requête
```java
List<UserService.LeaderboardEntry> topPlayers = UserService.getTopPlayers(10);
```

### Résultat affiché
```
👑 CLASSEMENT 👑
┌─────────────────────────────────────────────────┐
│ 🥇 1er │ mayel      │ ⭐5  │ 450 XP         │
│ 🥈 2ème│ player2    │ ⭐4  │ 350 XP         │
│ 🥉 3ème│ player3    │ ⭐3  │ 250 XP         │
│ #4    │ player4    │ ⭐2  │ 150 XP         │
└─────────────────────────────────────────────────┘
```

---

## 🎯 Améliorations possibles

- ✅ Classement global par XP totale
- 📅 Ajouter un classement hebdomadaire/mensuel
- 🎖️ Système de récompenses pour top 3
- 📈 Afficher les amis dans le classement
- 🔄 Mise à jour en temps réel
- 🏆 Trophées et achievements
- 📊 Graphiques de progression

---

## ✅ Vérification de la compilation

Pour compiler le projet:
```bash
cd /Users/mayel/Documents/Ynov/Java/Java_Royal
mvn clean compile
mvn clean package
```

---

## 📞 Support

En cas de problème:
1. Vérifier que les fichiers FXML et Java sont bien placés
2. Vérifier la base de données SQLite (`java_royal.db`)
3. Vérifier les imports dans les contrôleurs
4. Vérifier les chemins des ressources FXML

---

**Système de Classement complètement intégré! 🎉**

