#!/bin/bash

# 🚀 QUICK START - Après correction du Merge Hell
# Commandes rapides pour valider et exécuter le projet

echo "=========================================="
echo "🎯 JAVA ROYAL - POST MERGE HELL"
echo "=========================================="
echo ""

PROJECT_DIR="/Users/mayel/Documents/Ynov/Java/Java_Royal"
cd "$PROJECT_DIR" || exit 1

# 1. Vérification initiale
echo "1️⃣  Vérification des marqueurs de merge..."
if grep -r "<<<<<<\|======\|>>>>>>" src/main/java/ 2>/dev/null | grep -v "ProgressBar\|BackgroundSize\|BaseClass"; then
    echo "❌ ERREUR: Marqueurs de merge trouvés!"
    exit 1
else
    echo "✅ Aucun marqueur de merge détecté"
fi
echo ""

# 2. Compilation
echo "2️⃣  Compilation du projet..."
if [ -f "./mvnw" ]; then
    echo "Utilisation de Maven wrapper (./mvnw)..."
    ./mvnw clean compile -q
    COMPILE_RESULT=$?
elif command -v mvn &> /dev/null; then
    echo "Utilisation de Maven système (mvn)..."
    mvn clean compile -q
    COMPILE_RESULT=$?
else
    echo "⚠️  Maven non trouvé. Veuillez installer Maven ou utiliser Maven wrapper."
    echo "Télécharger: https://maven.apache.org/download.cgi"
    exit 1
fi

if [ $COMPILE_RESULT -eq 0 ]; then
    echo "✅ Compilation réussie!"
else
    echo "❌ ERREUR de compilation!"
    exit 1
fi
echo ""

# 3. Vérification de la structure
echo "3️⃣  Vérification de la structure du projet..."
JAVA_FILES=$(find src/main/java -name "*.java" | wc -l)
echo "📊 Fichiers Java trouvés: $JAVA_FILES"

if [ "$JAVA_FILES" -lt 15 ]; then
    echo "⚠️  Moins de fichiers que prévu"
else
    echo "✅ Structure correcte"
fi
echo ""

# 4. Vérification des dépendances
echo "4️⃣  Vérification des dépendances..."
if grep -q "jbcrypt\|javafx\|sqlite" pom.xml; then
    echo "✅ Dépendances clés présentes"
else
    echo "⚠️  Vérifier les dépendances dans pom.xml"
fi
echo ""

# 5. État final
echo "=========================================="
echo "✅ PROJET PRÊT!"
echo "=========================================="
echo ""
echo "🚀 Pour exécuter l'application:"
echo ""
if [ -f "./mvnw" ]; then
    echo "   ./mvnw clean javafx:run"
else
    echo "   mvn clean javafx:run"
fi
echo ""
echo "📋 Fichiers importants:"
echo "   - CONFIRMATION.md (validation complète)"
echo "   - MERGE_HELL_FIXED.md (détails corrections)"
echo "   - CHECKLIST_FINAL.md (checklist exhaustive)"
echo ""
echo "📚 Documentation:"
echo "   - README.md"
echo "   - QUICK_START.md"
echo ""
echo "🎮 Application démarrera sur:"
echo "   http://localhost:8080 (si développé)"
echo ""

