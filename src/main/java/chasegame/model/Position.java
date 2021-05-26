package chasegame.model;

/**
 * Simple position class for handling moves and current position.
 * Uses {@code Java 16 Record class}.
 */
public record Position(int row, int col) {

    public Position moveTo(Direction direction) {
        return new Position(row + direction.getRowChange(), col + direction.getColChange());
    }

    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}