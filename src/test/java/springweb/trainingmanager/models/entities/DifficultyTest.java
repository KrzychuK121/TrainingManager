package springweb.trainingmanager.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DifficultyTest {

    @Test
    void getEnumDescShouldReturnBrak() {
        Difficulty difficulty = null;
        assertEquals("brak", Difficulty.getEnumDesc(difficulty));
    }

    @Test
    void getEnumDescShouldReturnDlaZaawansowanych() {
        Difficulty difficulty = Difficulty.ADVANCED;
        assertEquals("dla zaawansowanych", Difficulty.getEnumDesc(difficulty));
    }

    @Test
    void getEnumDescShouldReturnDlaPoczatkujacych() {
        Difficulty difficulty = Difficulty.FOR_BEGINNERS;
        assertEquals("dla początkujących", Difficulty.getEnumDesc(difficulty));
    }

    @Test
    void getEnumDescShouldReturnDlaSredniozaawansowanych() {
        Difficulty difficulty = Difficulty.MEDIUM;
        assertEquals("dla średniozaawansowanych", Difficulty.getEnumDesc(difficulty));
    }
}