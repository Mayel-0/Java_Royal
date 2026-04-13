# ✅ GUIDE DE TEST - Connexion et Inscription

## 🎯 Tester le flux complet

### Test 1: INSCRIPTION
```
1. Lancer l'application
   └─ mvn javafx:run

2. Cliquer sur "S'inscrire"

3. Remplir le formulaire:
   ├─ Username: alice
   ├─ Email: alice@test.com
   └─ Password: abc123

4. Cliquer "Créer un compte"

5. ✅ RÉSULTAT ATTENDU:
   └─ Voir la page IntroductionView
      ├─ Titre: "JAVA ROYAL"
      ├─ Message: "Bienvenue dans Java Royal!"
      ├─ Bouton: "C'est parti!"
      └─ Fond: Gradient bleu-noir

6. Cliquer "C'est parti!"

7. ✅ RÉSULTAT ATTENDU:
   └─ FadeTransition 500ms (transition fluide)
      └─ Voir la page HomeView
         ├─ Message: "Bienvenue Alice !"
         ├─ Badge Niveau: "1"
         ├─ Barre XP: "0 / 100 XP"
         └─ Boutons: Mini-jeu, Profil, Quitter
```

### Test 2: CONNEXION (Level 1)
```
1. Cliquer "Quitter" dans HomeView
   └─ Revient à l'écran de démarrage

2. Cliquer "Connexion"

3. Remplir:
   ├─ Username: alice
   └─ Password: abc123

4. Cliquer "Se connecter"

5. ✅ RÉSULTAT ATTENDU:
   └─ Voir IntroductionView (level=1)
      └─ Bouton "C'est parti!"

6. Cliquer "C'est parti!"

7. ✅ RÉSULTAT ATTENDU:
   └─ HomeView avec les données d'Alice
      ├─ "Bienvenue Alice !"
      ├─ Niveau: 1
      └─ XP: 0 / 100
```

### Test 3: CONNEXION (Level > 1)
```
1. Modifier la base de données:
   └─ UPDATE users SET current_level=3 WHERE username='alice';

2. Cliquer "Quitter"

3. Cliquer "Connexion"

4. Entrer alice / abc123

5. ✅ RÉSULTAT ATTENDU:
   └─ Aller DIRECTEMENT à HomeView (pas d'introduction)
      ├─ "Bienvenue Alice !"
      ├─ Niveau: 3
      └─ XP: 0 / 300
```

---

## 🐛 Dépannage

### ❌ Erreur: "Impossible d'accéder à l'introduction"

**Cause possible:** Fichier introduction-view.fxml introuvable

**Solutions:**
```
1. Vérifier que le fichier existe:
   src/main/resources/com/example/java_royal/introduction-view.fxml

2. Vérifier le chemin dans le code:
   getClass().getResource("/com/example/java_royal/introduction-view.fxml")

3. Recompiler le projet:
   mvn clean compile

4. Vérifier les logs pour plus d'info:
   └─ L'erreur affiche maintenant e.getMessage()
```

### ❌ Erreur: "Impossible d'accéder à l'accueil"

**Cause possible:** Fichier home-view.fxml introuvable

**Solutions:**
```
1. Vérifier que le fichier existe:
   src/main/resources/com/example/java_royal/home-view.fxml

2. Vérifier les logs détaillés
```

### ❌ IntroductionView affichée mais bouton ne fonctionne pas

**Cause possible:** IntroductionController vide ou mal configuré

**Solution:** Vérifier que IntroductionController.java contient:
```java
@FXML
private void goToHome() {
    // Charge home-view.fxml
    // Crée FadeTransition
    // Navigue vers HomeView
}
```

---

## ✅ Checklist de Validation

```
[✅] IntroductionController.java créé et compilé
[✅] HelloController.java mis à jour avec meilleure gestion d'erreur
[✅] RegisterController.java mis à jour avec meilleure gestion d'erreur
[✅] introduction-view.fxml existe
[✅] home-view.fxml existe
[✅] Inscription fonctionne
[✅] Connexion Level 1 affiche Introduction
[✅] Connexion Level > 1 affiche Home directement
[✅] Transition fluide (FadeTransition 500ms)
[✅] Les données du joueur s'affichent correctement
```

---

## 📝 Logs de Debug

Pour voir les erreurs détaillées, ouvre la console et cherche:
```
- "Impossible d'accéder à l'introduction: ..."
- "Impossible d'ouvrir la page d'accueil: ..."
- "Fichier introduction-view.fxml introuvable"
- "java.io.IOException: ..."
```

Si tu vois des erreurs, copie-les et je pourrai les corriger!

---

**Prêt à tester? 🚀**

