#!/usr/bin/env zsh

# Script d'installation et vérification du Leaderboard
# Usage: source leaderboard_setup.sh

PROJECT_ROOT="/Users/mayel/Documents/Ynov/Java/Java_Royal"

echo "🚀 Configuration du système de Classement (Leaderboard)"
echo "======================================================"
echo ""

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# Vérifier que le projet existe
if [ ! -d "$PROJECT_ROOT" ]; then
    echo -e "${RED}❌ Erreur: Répertoire du projet non trouvé: $PROJECT_ROOT${NC}"
    exit 1
fi

cd "$PROJECT_ROOT"

echo -e "${BLUE}📁 Répertoire du projet: $PROJECT_ROOT${NC}"
echo ""

# Étape 1: Vérifier les fichiers critiques
echo -e "${YELLOW}[1/4] Vérification des fichiers...${NC}"
FILES_TO_CHECK=(
    "src/main/java/com/example/java_royal/controllers/LeaderboardController.java"
    "src/main/resources/com/example/java_royal/leaderboard-view.fxml"
    "src/main/java/com/example/java_royal/service/UserService.java"
    "src/main/java/com/example/java_royal/controllers/HomeController.java"
)

for file in "${FILES_TO_CHECK[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✅${NC} $file"
    else
        echo -e "${RED}❌${NC} $file"
    fi
done

echo ""

# Étape 2: Vérifier les méthodes essentielles
echo -e "${YELLOW}[2/4] Vérification des méthodes...${NC}"

if grep -q "getTopPlayers" src/main/java/com/example/java_royal/service/UserService.java; then
    echo -e "${GREEN}✅${NC} UserService.getTopPlayers() présente"
else
    echo -e "${RED}❌${NC} UserService.getTopPlayers() manquante"
fi

if grep -q "handleLeaderboard" src/main/java/com/example/java_royal/controllers/HomeController.java; then
    echo -e "${GREEN}✅${NC} HomeController.handleLeaderboard() présente"
else
    echo -e "${RED}❌${NC} HomeController.handleLeaderboard() manquante"
fi

if grep -q "loadLeaderboard" src/main/java/com/example/java_royal/controllers/LeaderboardController.java; then
    echo -e "${GREEN}✅${NC} LeaderboardController.loadLeaderboard() présente"
else
    echo -e "${RED}❌${NC} LeaderboardController.loadLeaderboard() manquante"
fi

echo ""

# Étape 3: Compiler le projet (optionnel)
echo -e "${YELLOW}[3/4] Compilation du projet (optionnel)${NC}"
echo -e "${BLUE}Pour compiler: mvn clean compile${NC}"
echo ""

# Étape 4: Afficher le guide d'utilisation
echo -e "${YELLOW}[4/4] Guide d'utilisation${NC}"
echo -e "${GREEN}"
echo "📖 GUIDE D'UTILISATION:"
echo "===================="
echo ""
echo "1. Lancer l'application Java Royal"
echo "2. Se connecter avec vos identifiants"
echo "3. Cliquer sur le bouton 'Classement' (doré)"
echo "4. Voir les 10 meilleurs joueurs"
echo "5. Cliquer '← Retour' pour revenir à l'accueil"
echo ""
echo "🎨 STYLES APPLIQUÉS:"
echo "==================="
echo "• 🥇 1ère place : Or (#FFD700)"
echo "• 🥈 2ème place : Argent (#C0C0C0)"
echo "• 🥉 3ème place : Bronze (#CD7F32)"
echo "• Autres : Alternance bleu foncé/clair"
echo ""
echo "📊 AFFICHAGE:"
echo "============="
echo "Colonne 1: Rang (avec médailles)"
echo "Colonne 2: Pseudo"
echo "Colonne 3: Niveau"
echo "Colonne 4: XP Totale"
echo ""
echo -e "${BLUE}📚 Pour plus de détails, consultez:${NC}"
echo "   - LEADERBOARD_GUIDE.md"
echo "   - LEADERBOARD_SUMMARY.md"
echo ""
echo -e "${NC}${GREEN}✅ Configuration terminée!${NC}"
echo ""

