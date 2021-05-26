package chasegame.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Class for abstracting pieces.
 */
public class Piece {

    private final PieceType type;
    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();

    public Piece(PieceType type, Position position) {
        this.type = type;
        this.position.set(position);
    }

    public PieceType getType() {
        return type;
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
        return type.toString() + position.get().toString();
    }
}
