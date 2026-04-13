# 🚀 DÉMARRAGE RAPIDE - Commandes Essentielles

## ⚡ En 30 Secondes

```bash
cd /Users/mayel/Documents/Ynov/Java/Java_Royal
mvn clean compile
mvn javafx:run
```

---

## ✅ Votre Correction en 3 Points

### 1️⃣ Fichier FXML
**Suppression** : `style="-fx-background-color: #0d1117;"` du rootPane

### 2️⃣ Fichier Java (2 endroits)
**Ajout** : `rootPane.setStyle("");` dans `initialize()` et `updateBackground()`

### 3️⃣ Résultat
**L'image d'arène s'affiche correctement** ✅

---

## 📖 Guide par Cas d'Usage

| Situation | Fichier à Lire |
|-----------|----------------|
| "Je veux voir avant/après" | VISUAL_SUMMARY.md |
| "Je veux comprendre pourquoi" | TECHNICAL_EXPLANATION.md |
| "Je veux tester rapidement" | QUICK_TEST.md |
| "Je veux vérifier tout" | VERIFICATION_CHECKLIST.md |
| "Je veux d'autres exemples" | JAVAFX_BACKGROUND_SNIPPETS.md |
| "Je veux la doc complète" | BACKGROUND_FIX_GUIDE.md |
| "Je suis perdu" | INDEX.md |

---

## 🧪 Test Instantané (1 ligne)

```bash
cd /Users/mayel/Documents/Ynov/Java/Java_Royal && mvn -q clean compile && echo "✅ BUILD OK" || echo "❌ BUILD FAIL"
```

---

## 🎯 Résultat Attendu

Après `mvn javafx:run`, vous devriez voir :

**Logs** :
```
✅ [ArenaImageCache] ✅ Image Arene1 chargée avec succès
✅ [HomeController] ✅ Background arene1 appliqué avec succès!
```

**Écran** :
```
🏟️ Image d'arène visible (pas d'écran noir)
```

---

## 🔑 Clé du Succès

> **En JavaFX, le CSS prime sur le Background JavaFX**
> 
> Solution : Vider le CSS avant d'appliquer l'image
> ```java
> pane.setStyle("");              // ← Important!
> pane.setBackground(image);      // ← Puis appliquer
> ```

---

## 📞 Questions Rapides

**Q: Ça va casser quelque chose?**  
A: Non, juste la page d'accueil

**Q: Dois-je faire `mvn clean`?**  
A: Oui, au moins une fois

**Q: Comment savoir si ça marche?**  
A: L'image d'arène sera visible à l'écran

---

**Status** : ✅ Tous les changements appliqués  
**Prochaine étape** : Lancer l'application avec `mvn javafx:run`

