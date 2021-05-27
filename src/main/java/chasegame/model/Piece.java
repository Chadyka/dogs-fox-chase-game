package chasegame.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Class for abstracting pieces.
 */
public class Piece {

    private final PieceColor color;
    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();

    public enum PieceColor {
        BLACK,
        GREY
    }

    public Piece(PieceColor color, Position position) {
        this.color = color;
        this.position.set(position);
    }

    public PieceColor getColor() {
        return color;
    }

    public Position getPosition() {
        return position.get();
    }

    /**
     * Sets new position for a piece
     * @param direction desired new location for the {@link Piece}
     */
    public void moveTo(Direction direction) {
        Position newPosition = position.get().moveTo(direction);
        position.set(newPosition);
    }

    public ObjectProperty<Position> positionProperty() {
        return position;
    }

    public String toString() {
        return color.toString() + position.get().toString();
    }
}
