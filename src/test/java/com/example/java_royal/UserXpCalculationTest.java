package com.example.java_royal.test;

import com.example.java_royal.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le modèle User et la logique XP
 */
public class UserXpCalculationTest {

    @Test
    public void testNextLevelThreshold() {
        // Level 1 → Threshold should be 100
        User user1 = new User(1, "alice", "alice@test.com", 1, 0, 0);
        assertEquals(100, user1.getNextLevelThreshold());

        // Level 2 → Threshold should be 200
        User user2 = new User(2, "bob", "bob@test.com", 2, 0, 0);
        assertEquals(200, user2.getNextLevelThreshold());

        // Level 5 → Threshold should be 500
        User user5 = new User(5, "charlie", "charlie@test.com", 5, 0, 0);
        assertEquals(500, user5.getNextLevelThreshold());
    }

    @Test
    public void testXpProgressPercentage() {
        // Level 1: 0 XP → 0%
        User user1 = new User(1, "alice", "alice@test.com", 1, 0, 0);
        assertEquals(0.0, user1.getXpProgressPercentage());

        // Level 1: 50 XP → 50% (50/100)
        User user2 = new User(2, "bob", "bob@test.com", 1, 50, 0);
        assertEquals(0.5, user2.getXpProgressPercentage());

        // Level 1: 100 XP → 100% (100/100, capped at 1.0)
        User user3 = new User(3, "charlie", "charlie@test.com", 1, 100, 0);
        assertEquals(1.0, user3.getXpProgressPercentage());

        // Level 1: 150 XP → 100% (capped at 1.0, should already be level 2)
        User user4 = new User(4, "dave", "dave@test.com", 1, 150, 0);
        assertTrue(user4.getXpProgressPercentage() <= 1.0);

        // Level 2: 100 XP → 50% (100/200)
        User user5 = new User(5, "eve", "eve@test.com", 2, 100, 0);
        assertEquals(0.5, user5.getXpProgressPercentage());

        // Level 2: 150 XP → 75% (150/200)
        User user6 = new User(6, "frank", "frank@test.com", 2, 150, 0);
        assertEquals(0.75, user6.getXpProgressPercentage());
    }

    @Test
    public void testXpProgressionScenario() {
        // Scénario: Un joueur passe de Level 1 à Level 3

        // Initial: Level 1, 0 XP
        User user = new User(1, "alice", "alice@test.com", 1, 0, 0);
        assertEquals(1, user.getCurrentLevel());
        assertEquals(0, user.getCurrentXp());
        assertEquals(0.0, user.getXpProgressPercentage());

        // Gains 50 XP
        user = new User(1, "alice", "alice@test.com", 1, 50, 50);
        assertEquals(1, user.getCurrentLevel());
        assertEquals(50, user.getCurrentXp());
        assertEquals(0.5, user.getXpProgressPercentage());

        // Gains 50 XP → Atteint 100 (devrait level up)
        // Mais le level est toujours 1 car pas auto-leveled dans le model
        user = new User(1, "alice", "alice@test.com", 1, 100, 100);
        assertEquals(1.0, user.getXpProgressPercentage());

        // Après level up (simulé par le controller):
        // XP = 100 - 100 (threshold) = 0, Level = 2
        user = new User(1, "alice", "alice@test.com", 2, 0, 100);
        assertEquals(2, user.getCurrentLevel());
        assertEquals(0, user.getCurrentXp());
        assertEquals(0.0, user.getXpProgressPercentage());
        assertEquals(200, user.getNextLevelThreshold());

        // Gains 100 XP
        user = new User(1, "alice", "alice@test.com", 2, 100, 200);
        assertEquals(0.5, user.getXpProgressPercentage());

        // Gains 100 XP → Atteint 200 (devrait level up)
        user = new User(1, "alice", "alice@test.com", 2, 200, 300);
        assertEquals(1.0, user.getXpProgressPercentage());

        // Après level up:
        // XP = 200 - 200 (threshold) = 0, Level = 3
        user = new User(1, "alice", "alice@test.com", 3, 0, 300);
        assertEquals(3, user.getCurrentLevel());
        assertEquals(0, user.getCurrentXp());
        assertEquals(300, user.getNextLevelThreshold());
    }

    @Test
    public void testProgressPercentageWithVariousXp() {
        // Test avec différentes combinaisons Level/XP

        // Level 1: 1-99 XP
        for (int xp = 1; xp < 100; xp++) {
            User user = new User(1, "test", "test@test.com", 1, xp, xp);
            double percentage = user.getXpProgressPercentage();
            assertEquals(xp / 100.0, percentage);
            assertTrue(percentage > 0 && percentage < 1.0);
        }

        // Level 2: 1-199 XP
        for (int xp = 1; xp < 200; xp++) {
            User user = new User(1, "test", "test@test.com", 2, xp, xp);
            double percentage = user.getXpProgressPercentage();
            assertEquals(xp / 200.0, percentage);
            assertTrue(percentage > 0 && percentage < 1.0);
        }
    }

    @Test
    public void testGettersAndSetters() {
        User user = new User(123, "alice", "alice@example.com", 2, 50, 150);

        assertEquals(123, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(2, user.getCurrentLevel());
        assertEquals(50, user.getCurrentXp());
        assertEquals(150, user.getTotalXp());
    }

    @Test
    public void testEdgeCases() {
        // Level 0 (invalid but should handle)
        User user0 = new User(1, "test", "test@test.com", 0, 0, 0);
        assertEquals(0, user0.getNextLevelThreshold());
        assertEquals(0.0, user0.getXpProgressPercentage());

        // Negative XP (shouldn't happen but defensive)
        User userNeg = new User(1, "test", "test@test.com", 1, -50, 0);
        assertTrue(userNeg.getXpProgressPercentage() < 0);

        // XP > Threshold (shouldn't happen but should cap at 1.0)
        User userOver = new User(1, "test", "test@test.com", 1, 500, 0);
        assertEquals(1.0, userOver.getXpProgressPercentage());
    }

    @Test
    public void testXpFormulasConsistency() {
        // Test que les formules sont cohérentes entre niveaux

        for (int level = 1; level <= 10; level++) {
            User user = new User(level, "test", "test@test.com", level, 0, 0);

            // Threshold doit être level * 100
            assertEquals(level * 100, user.getNextLevelThreshold());

            // À 0 XP, progress doit être 0%
            assertEquals(0.0, user.getXpProgressPercentage());

            // À threshold XP, progress doit être 100% (capped)
            User userMax = new User(level, "test", "test@test.com", level,
                                   level * 100, 0);
            assertEquals(1.0, userMax.getXpProgressPercentage());
        }
    }
}

