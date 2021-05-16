package chasegame.model;

import javafx.beans.property.ObjectProperty;

import java.util.*;

/**
 * Model object for the dog-fox game. Handles game state.
 */

public class GameModel {

    public static int BOARD_SIZE = 8;

    private final Piece[] pieces;

    public enum TurnOrder {
        DOG,
        FOX;

        public TurnOrder changeTurn() {
            return switch (this) {
                case DOG -> FOX;
                case FOX -> DOG;
            };
        }
    }

    private TurnOrder turnOrder = TurnOrder.DOG;

    public TurnOrder getTurnOrder() {
        return turnOrder;
    }

    public void changeTurnOrder() {
        turnOrder = turnOrder.changeTurn();
    }

    public GameModel() {
        this(new Piece(PieceType.GREY, new Position(0, 2)),
                new Piece(PieceType.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 1)),
                new Piece(PieceType.BLACK, new Position(BOARD_SIZE - 1, 5)),
                new Piece(PieceType.BLACK, new Position(BOARD_SIZE - 1, 3)),
                new Piece(PieceType.BLACK, new Position(BOARD_SIZE - 1, 1)));
    }

    public GameModel(Piece... pieces) {
        checkPieces(pieces);
        this.pieces = pieces.clone();
    }

    private void checkPieces(Piece[] pieces) {
        var seen = new HashSet<Position>();
        for (var piece : pieces) {
            if (!isOnBoard(piece.getPosition()) || seen.contains(piece.getPosition())) {
                throw new IllegalArgumentException();
            }
            seen.add(piece.getPosition());
        }
    }

    public int getPieceCount() {
        return pieces.length;
    }

    public PieceType getPieceType(int pieceNumber) {
        return pieces[pieceNumber].getType();
    }

    public Position getPiecePosition(int pieceNumber) {
        return pieces[pieceNumber].getPosition();
    }

    public ObjectProperty<Position> positionProperty(int pieceNumber) {
        return pieces[pieceNumber].positionProperty();
    }

    public boolean isValidMove(int pieceNumber, Direction direction) {
        if (pieceNumber < 0 || pieceNumber >= pieces.length) {
            throw new IllegalArgumentException();
        }
        Position newPosition = pieces[pieceNumber].getPosition().moveTo(direction);
        if (!isOnBoard(newPosition)) {
            return false;
        }
        for (var piece : pieces) {
            if (piece.getPosition().equals(newPosition)) {
                return false;
            }
        }
        return true;
    }

    public Set<FoxDirection> getValidFoxMoves(int pieceNumber) {
        EnumSet<FoxDirection> validMoves = EnumSet.noneOf(FoxDirection.class);
        for (var direction : FoxDirection.values()) {
            if (isValidMove(pieceNumber, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    public Set<DogDirection> getValidDogMoves(int pieceNumber) {
        EnumSet<DogDirection> validMoves = EnumSet.noneOf(DogDirection.class);
        for (var direction : DogDirection.values()) {
            if (isValidMove(pieceNumber, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    public void move(int pieceNumber, Direction direction) {
        pieces[pieceNumber].moveTo(direction);
    }

    public static boolean isOnBoard(Position position) {
        return 0 <= position.row() && position.row() < BOARD_SIZE
                && 0 <= position.col() && position.col() < BOARD_SIZE;
    }

    public List<Position> getCurrentPiecePositions() {
        List<Position> positions = new ArrayList<>(pieces.length);
        if (turnOrder == TurnOrder.DOG) {
            for (var piece : pieces) {
                if (piece == pieces[0]) continue;
                positions.add(piece.getPosition());
            }
        } else if (turnOrder == TurnOrder.FOX) {
            positions.add(pieces[0].getPosition());
        }
        return positions;
    }

    public List<Position> getAllPiecesPositions() {
        List<Position> positions = new ArrayList<>(pieces.length);
        for (var piece : pieces) {
            positions.add(piece.getPosition());
        }
        return positions;
    }

    public OptionalInt getPieceNumber(Position position) {
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (var piece : pieces) {
            joiner.add(piece.toString());
        }
        return joiner.toString();
    }
}
