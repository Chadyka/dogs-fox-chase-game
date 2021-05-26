package chasegame.model;

/**
 * Class representation of Dog pieces
 */
public enum DogDirection implements Direction {

    UP_LEFT(-1, -1),
    UP_RIGHT(-1, 1);

    private final int rowChange;
    private final int colChange;

    DogDirection(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    /**
     * Gives a direction for a desired move.
     * @param rowChange position change on the x axis.
     * @param colChange position change on the y axis.
     * @return the desired direction.
     */
    public static DogDirection of(int rowChange, int colChange) {
        for (var direction : values()) {
            if (direction.rowChange == rowChange && direction.colChange == colChange) {
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }
}