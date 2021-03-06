package chasegame.model;

import javafx.beans.property.ObjectProperty;

import java.util.*;

/**
 * Model object for the dog-fox game. Handles game state.
 */
public class GameModel {

    public static int BOARD_SIZE = 8;

    private final Piece[] pieces;

    /**
     * Sets turn for dog or fox piece.
     */
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

    /**
     * Alternates turn order.
     */
    public void changeTurnOrder() {
        turnOrder = turnOrder.changeTurn();
    }

    public GameModel() {
        this(new Piece(Piece.PieceColor.GREY, new Position(0, 2)),
                new Piece(Piece.PieceColor.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 1)),
                new Piece(Piece.PieceColor.BLACK, new Position(BOARD_SIZE - 1, 5)),
                new Piece(Piece.PieceColor.BLACK, new Position(BOARD_SIZE - 1, 3)),
                new Piece(Piece.PieceColor.BLACK, new Position(BOARD_SIZE - 1, 1)));
    }

    public GameModel(Piece... pieces) {
        checkPieces(pieces);
        this.pieces = pieces.clone();
    }

    /**
     * Checks if pieces are on the board at game start.
     * @param pieces list of pieces available.
     */
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

    public Piece.PieceColor getPieceColor(int pieceNumber) {
        return pieces[pieceNumber].getColor();
    }

    public Position getPiecePosition(int pieceNumber) {
        return pieces[pieceNumber].getPosition();
    }

    public ObjectProperty<Position> positionProperty(int pieceNumber) {
        return pieces[pieceNumber].positionProperty();
    }

    /**
     * Checks if a move can be made on the current state.
     * @param pieceNumber id of the piece that wants to move.
     * @param direction the location change of the move.
     * @return true if the move is valid, otherwise false.
     */
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

    /**
     * Checks valid moves for Fox.
     * @param pieceNumber id of the fox piece.
     * @return validity of the move.
     */
    public Set<FoxDirection> getValidFoxMoves(int pieceNumber) {
        EnumSet<FoxDirection> validMoves = EnumSet.noneOf(FoxDirection.class);
        for (var direction : FoxDirection.values()) {
            if (isValidMove(pieceNumber, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    /**
     * Checks valid moves for Dog.
     * @param pieceNumber id of the dog piece.
     * @return validity of the move.
     */
    public Set<DogDirection> getValidDogMoves(int pieceNumber) {
        EnumSet<DogDirection> validMoves = EnumSet.noneOf(DogDirection.class);
        for (var direction : DogDirection.values()) {
            if (isValidMove(pieceNumber, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    /**
     * Initiates movement of  piece
     * @param pieceNumber id of the piece to be moved.
     * @param direction location change in the move.
     */
    public void move(int pieceNumber, Direction direction) {
        pieces[pieceNumber].moveTo(direction);
    }

    /**
     * Checks if desired location is on board or not.
     * @param position new position of the desired move.
     * @return true if move ends on board, false otherwise
     */
    public static boolean isOnBoard(Position position) {
        return 0 <= position.row() && position.row() < BOARD_SIZE
                && 0 <= position.col() && position.col() < BOARD_SIZE;
    }

    /**
     * Returns current player's piece's positions.
     * @return list of positions.
     */
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

    /**
     * Returns all piece's positions.
     * @return list of positions.
     */
    public List<Position> getAllPiecesPositions() {
        List<Position> positions = new ArrayList<>(pieces.length);
        for (var piece : pieces) {
            positions.add(piece.getPosition());
        }
        return positions;
    }

    /**
     * Returns a piece's id.
     * @param position location of the piece on the board.
     * @return id if a {@link Piece} at the specified {@link Position} exists
     */
    public OptionalInt getPieceNumber(Position position) {
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * String representation of the gamestate.
     * @return {@link String} of the state.
     */
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (var piece : pieces) {
            joiner.add(piece.toString());
        }
        return joiner.toString();
    }
}
