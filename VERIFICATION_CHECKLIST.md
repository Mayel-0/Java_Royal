# ✅ Checklist de Vérification - Affichage des Backgrounds

## 📋 Avant de Lancer l'Application

### 🔍 Vérifications du Système de Fichiers
- [ ] Dossier `src/main/resources/assets background/` existe
- [ ] Les 5 images présentes : `Arene1.jpg` à `Arene5.jpg`
- [ ] Dossier `target/classes/assets background/` existe après `mvn clean compile`
- [ ] Les 5 images présentes dans `target/classes/assets background/`

**Commande de vérification** :
```bash
# Vérifier les sources
ls -la src/main/resources/assets\ background/

# Vérifier après compilation
ls -la target/classes/assets\ background/
```

### 🔍 Vérifications du Code Java

- [ ] `HomeController.java` contient `rootPane.setStyle("")` dans `initialize()`
- [ ] `HomeController.java` contient `rootPane.setStyle("")` dans `updateBackground()`
- [ ] `BackgroundSize` utilise `false` pour le 5ème paramètre (contain mode)
- [ ] Pas de `try/catch` qui avalerait les erreurs sans les afficher

**Commande de vérification** :
```bash
grep -n "rootPane.setStyle" src/main/java/com/example/java_royal/controllers/HomeController.java
```

### 🔍 Vérifications du Fichier FXML

- [ ] `home-view.fxml` : Le `<BorderPane>` n'a PAS de `style="-fx-background-color: ..."`
- [ ] `home-view.fxml` : Les éléments `<top>`, `<center>`, `<bottom>` ont les bons styles

**Commande de vérification** :
```bash
head -20 src/main/resources/com/example/java_royal/home-view.fxml | grep -i "borderPane\|background"
```

### ✅ Compilation

- [ ] `mvn clean compile` s'exécute sans erreurs
- [ ] Pas de warnings CSS ou autres avertissements importants
- [ ] Les fichiers `.class` sont dans `target/classes/`

**Commande de vérification** :
```bash
cd /Users/mayel/Documents/Ynov/Java/Java_Royal
mvn clean compile
echo "Résultat: $?"  # 0 = succès, 1 = erreur
```

---

## 🎬 Pendant l'Exécution de l'Application

### 📊 Logs à Vérifier

Recherchez dans la console **TOUTES** ces lignes :

```
[ArenaImageCache] Tentative de chargement: /assets background/Arene1.jpg
[ArenaImageCache] Tentative de chargement: /assets background/Arene2.jpg
[ArenaImageCache] Tentative de chargement: /assets background/Arene3.jpg
[ArenaImageCache] Tentative de chargement: /assets background/Arene4.jpg
[ArenaImageCache] Tentative de chargement: /assets background/Arene5.jpg

[ArenaImageCache] ✅ Image Arene1 chargée avec succès
[ArenaImageCache] ✅ Image Arene2 chargée avec succès
[ArenaImageCache] ✅ Image Arene3 chargée avec succès
[ArenaImageCache] ✅ Image Arene4 chargée avec succès
[ArenaImageCache] ✅ Image Arene5 chargée avec succès

[ArenaImageCache] ✅ Cache initialisé avec 5 images

[HomeController] updateBackground() - Level: 1 (ou votre niveau)
[HomeController] arenaImage retrieved: NOT NULL
[HomeController] Image width: XXXX, height: YYYY
[HomeController] ✅ Background arene1 appliqué avec succès!
[HomeController] rootPane style cleared: 
```

### ❌ Logs d'Erreur à Éviter

- ❌ `[ArenaImageCache] ❌ NPE: Fichier non trouvé`
- ❌ `[ArenaImageCache] ❌ InputStream null`
- ❌ `[HomeController] ⚠️ Image pour niveau 1 est NULL`
- ❌ Erreurs CSS : `Error parsing: Expected '<color>'`

---

## 👁️ Visuels à Vérifier

### ✅ Ce Qu'Vous Devriez Voir

1. **Écran de connexion/registration** : Interface normale (sans changement)

2. **Page d'accueil (HomeView)** :
   - ✅ Arrière-plan : Image de l'arène correspondant au niveau
   - ✅ En haut : Barre bleue avec "Bienvenue [Username]", niveau, et XP
   - ✅ Au centre : Texte semi-transparent "Arène"
   - ✅ En bas : 3 boutons (Démarrer, Profil, Quitter) sur fond bleu

3. **Changement de niveau** : L'image d'arène change si vous montez de niveau

### ❌ Ce Qu'Vous NE Devriez PAS Voir

- ❌ Écran complètement noir ou gris
- ❌ Pas de distinction entre les arènes (toutes identiques)
- ❌ Les boutons cachés ou invisibles
- ❌ Textes invisibles (trop sombres)

---

## 🧪 Tests Spécifiques

### Test 1 : Image Chargée
```
✅ Objectif: Confirmer que les images sont en mémoire
✅ Vérifier: Logs [ArenaImageCache] ✅ Image Arene1 chargée avec succès
✅ Résultat: Oui/Non
```

### Test 2 : Background Appliqué
```
✅ Objectif: Confirmer que le background est appliqué au rootPane
✅ Vérifier: Logs [HomeController] ✅ Background arene1 appliqué avec succès!
✅ Résultat: Oui/Non
```

### Test 3 : Image Affichée Visuellement
```
✅ Objectif: L'image d'arène est visible à l'écran
✅ Vérifier: Arrière-plan de la HomeView n'est pas noir/gris pur
✅ Résultat: Oui/Non
```

### Test 4 : Niveau 1 vs Niveau 5
```
✅ Objectif: Les images changent avec le niveau
✅ Procédure: 
   1. Créer un utilisateur niveau 1 → Vérifier image
   2. Créer un utilisateur niveau 5 → Vérifier image différente
✅ Résultat: Images différentes selon le niveau Oui/Non
```

---

## 🔧 Dépannage Rapide

### Problème : "L'image ne s'affiche toujours pas"

**Étape 1 : Vérifier les ressources**
```bash
ls -la target/classes/assets\ background/
# Si vide → faire: mvn clean compile
```

**Étape 2 : Vérifier les logs de chargement**
- Les images sont-elles en cache ? Cherchez `✅ Image Arene1 chargée`
- Si non → Vérifier `ArenaImageCache.java` chemin d'accès

**Étape 3 : Vérifier les styles CSS**
- Ouvrez `home-view.fxml`
- S'il y a `style="-fx-background-color: ..."` sur le BorderPane → Supprimer

**Étape 4 : Forcer un refresh**
- Nettoyez Maven : `mvn clean`
- Recompillez : `mvn compile`
- Redémarrez l'application

### Problème : "Erreurs CSS dans la console"

**Cause** : Styles CSS invalides  
**Solution** : Les erreurs CSS ne doivent pas empêcher le chargement des images. Elles sont généralement ignorables, mais si vous voulez les corriger, vérifiez que tous les styles CSS utilisent la bonne syntaxe.

### Problème : "L'image est noire/complètement sombre"

**Cause** : La couleur CSS prime encore  
**Solution** :
```java
// Dans HomeController.initialize()
rootPane.setStyle(""); // ← TRÈS IMPORTANT

// Dans HomeController.updateBackground()
rootPane.setStyle(""); // ← Aussi ici
```

### Problème : "L'image est coupée aux bords"

**Cause** : Configuration BackgroundSize incorrecte  
**Solution** : Vérifier que `BackgroundSize` a `false` pour le 5ème paramètre (contain mode)

```java
new BackgroundSize(
    100, 100, true, true,
    false,  // ← Doit être FALSE (contain mode)
    true
)
```

---

## 📊 Résumé des Changements

| Fichier | Changement | Statut |
|---------|-----------|--------|
| `home-view.fxml` | Suppression `style="-fx-background-color: #0d1117;"` | ✅ |
| `HomeController.java` | Ajout `rootPane.setStyle("")` dans `initialize()` | ✅ |
| `HomeController.java` | Amélioration `BackgroundSize` (false pour cover) | ✅ |
| `HomeController.java` | Ajout `rootPane.setStyle("")` dans `updateBackground()` | ✅ |

---

## 📞 Si Ça Coince Toujours...

1. **Vérifiez les logs complets** : Copiez tous les logs de la console
2. **Vérifiez le niveau de l'utilisateur** : Il faut être connecté avec un niveau valide
3. **Vérifiez `target/classes/`** : Les images doivent y être après `mvn compile`
4. **Testez une image simple** : Créez une petite image de test pour écliminer les problèmes de fichier

---

## ✨ Résultat Attendu

```
📱 Écran d'accueil (HomeView):
┌─────────────────────────────────────┐
│  Bienvenue [Username] !  [Niveau 1] │  ← Barre bleue
├─────────────────────────────────────┤
│                                     │
│        🏟️ IMAGE ARENE 1 🏟️         │  ← Fond avec image
│           (visible)                 │
│         Texte "Arène"               │
│                                     │
├─────────────────────────────────────┤
│ [Démarrer] [Profil] [Quitter]      │  ← Boutons visibles
└─────────────────────────────────────┘
```

---

**Création** : 13 avril 2026  
**Dernier update** : 13 avril 2026  
**Statut** : ✅ À jour et testé

