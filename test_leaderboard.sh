#!/bin/bash

# Script de test du système de Classement (Leaderboard)
# Vérifie que tous les fichiers sont en place et correctement intégrés

echo "🔍 Vérification du système de Classement (Leaderboard)..."
echo "=========================================================="
echo ""

# Couleurs pour les messages
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Compteur de vérifications
PASSED=0
FAILED=0

# Fonction pour vérifier un fichier
check_file() {
    local FILE=$1
    local DESC=$2

    if [ -f "$FILE" ]; then
        echo -e "${GREEN}✅${NC} $DESC"
        ((PASSED++))
    else
        echo -e "${RED}❌${NC} $DESC - MANQUANT"
        ((FAILED++))
    fi
}

# Fonction pour vérifier une classe/méthode dans un fichier
check_method() {
    local FILE=$1
    local METHOD=$2
    local DESC=$3

    if grep -q "$METHOD" "$FILE"; then
        echo -e "${GREEN}✅${NC} $DESC"
        ((PASSED++))
    else
        echo -e "${RED}❌${NC} $DESC - NON TROUVÉ"
        ((FAILED++))
    fi
}

# Définir le répertoire racine
PROJECT_ROOT="/Users/mayel/Documents/Ynov/Java/Java_Royal"

echo "📁 Vérification des fichiers créés/modifiés:"
echo "---"

# 1. Vérifier LeaderboardController.java
check_file "$PROJECT_ROOT/src/main/java/com/example/java_royal/controllers/LeaderboardController.java" \
    "LeaderboardController.java"

# 2. Vérifier leaderboard-view.fxml
check_file "$PROJECT_ROOT/src/main/resources/com/example/java_royal/leaderboard-view.fxml" \
    "leaderboard-view.fxml"

# 3. Vérifier leaderboard.css
check_file "$PROJECT_ROOT/src/main/resources/com/example/java_royal/leaderboard.css" \
    "leaderboard.css"

# 4. Vérifier la documentation
check_file "$PROJECT_ROOT/LEADERBOARD_GUIDE.md" \
    "LEADERBOARD_GUIDE.md"

echo ""
echo "🔧 Vérification du code source:"
echo "---"

# Vérifier UserService.java
check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/service/UserService.java" \
    "getTopPlayers" \
    "Méthode getTopPlayers() dans UserService"

check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/service/UserService.java" \
    "class LeaderboardEntry" \
    "Classe LeaderboardEntry dans UserService"

# Vérifier HomeController.java
check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/controllers/HomeController.java" \
    "handleLeaderboard" \
    "Méthode handleLeaderboard() dans HomeController"

# Vérifier LeaderboardController.java
check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/controllers/LeaderboardController.java" \
    "loadLeaderboard" \
    "Méthode loadLeaderboard() dans LeaderboardController"

check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/controllers/LeaderboardController.java" \
    "setupTableColumns" \
    "Méthode setupTableColumns() dans LeaderboardController"

check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/controllers/LeaderboardController.java" \
    "getRankDisplay" \
    "Méthode getRankDisplay() dans LeaderboardController"

check_method "$PROJECT_ROOT/src/main/java/com/example/java_royal/controllers/LeaderboardController.java" \
    "goBackHome" \
    "Méthode goBackHome() dans LeaderboardController"

echo ""
echo "🎨 Vérification de l'intégration FXML:"
echo "---"

# Vérifier que le bouton Classement est dans home-view.fxml
if grep -q "handleLeaderboard" "$PROJECT_ROOT/src/main/resources/com/example/java_royal/home-view.fxml"; then
    echo -e "${GREEN}✅${NC} Bouton 'Classement' intégré dans home-view.fxml"
    ((PASSED++))
else
    echo -e "${RED}❌${NC} Bouton 'Classement' NON trouvé dans home-view.fxml"
    ((FAILED++))
fi

# Vérifier que leaderboard-view.fxml a les bonnes colonnes
if grep -q "rankColumn" "$PROJECT_ROOT/src/main/resources/com/example/java_royal/leaderboard-view.fxml"; then
    echo -e "${GREEN}✅${NC} Colonne Rang présente dans leaderboard-view.fxml"
    ((PASSED++))
else
    echo -e "${RED}❌${NC} Colonne Rang absente de leaderboard-view.fxml"
    ((FAILED++))
fi

if grep -q "usernameColumn" "$PROJECT_ROOT/src/main/resources/com/example/java_royal/leaderboard-view.fxml"; then
    echo -e "${GREEN}✅${NC} Colonne Pseudo présente dans leaderboard-view.fxml"
    ((PASSED++))
else
    echo -e "${RED}❌${NC} Colonne Pseudo absente de leaderboard-view.fxml"
    ((FAILED++))
fi

if grep -q "levelColumn" "$PROJECT_ROOT/src/main/resources/com/example/java_royal/leaderboard-view.fxml"; then
    echo -e "${GREEN}✅${NC} Colonne Niveau présente dans leaderboard-view.fxml"
    ((PASSED++))
else
    echo -e "${RED}❌${NC} Colonne Niveau absente de leaderboard-view.fxml"
    ((FAILED++))
fi

if grep -q "totalXpColumn" "$PROJECT_ROOT/src/main/resources/com/example/java_royal/leaderboard-view.fxml"; then
    echo -e "${GREEN}✅${NC} Colonne XP Totale présente dans leaderboard-view.fxml"
    ((PASSED++))
else
    echo -e "${RED}❌${NC} Colonne XP Totale absente de leaderboard-view.fxml"
    ((FAILED++))
fi

echo ""
echo "=========================================================="
echo "📊 Résultats:"
echo -e "   ${GREEN}✅ Réussis: $PASSED${NC}"
echo -e "   ${RED}❌ Échoués: $FAILED${NC}"
echo "=========================================================="
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 Tous les tests sont PASSÉS! Le système de classement est correctement intégré.${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Certains tests ont ÉCHOUÉ. Vérifiez les fichiers.${NC}"
    exit 1
fi

