package game;

import chasegame.model.FoxDirection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class FoxDirectionTest {
    @Test
    void testOf() {
        assertEquals(FoxDirection.DOWN_LEFT, FoxDirection.of(1, -1));
        assertEquals(FoxDirection.UP_LEFT, FoxDirection.of(-1, -1));
        assertEquals(FoxDirection.DOWN_RIGHT, FoxDirection.of(1, 1));
        assertEquals(FoxDirection.UP_RIGHT, FoxDirection.of(-1, 1));
    }

    @Test
    void testOfIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> FoxDirection.of(1, 2));
        assertThrows(IllegalArgumentException.class, () -> FoxDirection.of(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> FoxDirection.of(0, 1));
    }
}