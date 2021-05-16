package game;

import chasegame.model.DogDirection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DogDirectionTest {
    @Test
    void testOf() {
        assertEquals(DogDirection.UP_LEFT, DogDirection.of(-1, -1));
        assertEquals(DogDirection.UP_RIGHT, DogDirection.of(-1, 1));
    }

    @Test
    void testOfIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DogDirection.of(1, 2));
        assertThrows(IllegalArgumentException.class, () -> DogDirection.of(1, -1));
        assertThrows(IllegalArgumentException.class, () -> DogDirection.of(1, 1));
    }
}