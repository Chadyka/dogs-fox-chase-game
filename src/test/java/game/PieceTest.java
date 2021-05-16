package game;

import chasegame.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class PieceTest {

    Piece fox = new Piece(PieceType.GREY, new Position(0, 2));
    Piece dog = new Piece(PieceType.GREY, new Position(7, 7));

    @Test
    public void pieceMoveToTest() {
        fox.moveTo(FoxDirection.of(1,1));
        assertEquals(fox.getPosition(), new Position(1, 3));

        dog.moveTo(DogDirection.of(-1,-1));
        assertEquals(dog.getPosition(), new Position(6, 6));
    }
}
