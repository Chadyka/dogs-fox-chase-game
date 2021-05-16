package chasegame.model;
/*
* Simple Direction interface for {@link FoxDirection} and {@link DogDirection} classes.
* */
public interface Direction {
    int getRowChange();
    int getColChange();
}
