# Java Royal

## Configuration MySQL / MAMP

Créer un fichier `.env` à la racine du projet avec :

```dotenv
DB_URL=jdbc:mysql://127.0.0.1:8889/Java_Royal?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=root
```

## Table nécessaire

Exécuter dans MySQL/MAMP :

```sql
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);
```

## Lancement

```bash
./mvnw clean javafx:run
```

## Flux

- Écran d'accueil: choisir `Se connecter` ou `S'inscrire`
- Inscription: crée un utilisateur dans MySQL
- Connexion: redirige vers `Accueil`
- Accueil: affiche `Bonjour [Username]`
