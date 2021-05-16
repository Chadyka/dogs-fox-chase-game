package chasegame;

import java.util.ArrayList;
import java.util.List;

import chasegame.model.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import org.tinylog.Logger;

/**
 * Controls the flow of the game, selection and game over messages.
 */

public class GameController {

    private enum SelectionPhase {
        SELECT_FROM,
        SELECT_TO;

        public SelectionPhase alter() {
            return switch (this) {
                case SELECT_FROM -> SELECT_TO;
                case SELECT_TO -> SELECT_FROM;
            };
        }
    }

    private SelectionPhase selectionPhase = SelectionPhase.SELECT_FROM;

    private List<Position> selectablePositions = new ArrayList<>();

    private Position selected;

    private GameModel model = new GameModel();

    @FXML
    private GridPane board;

    @FXML
    private void initialize() {
        createBoard();
        createPieces();
        setSelectablePositions();
        showSelectablePositions();
    }

    private void createBoard() {
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare();
                board.add(square, j, i);
                if ((i + j) % 2 == 0) {
                    square.getStyleClass().add("white");
                } else {
                    square.getStyleClass().add("black");

                }
            }
        }
    }

    private StackPane createSquare() {
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private void createPieces() {
        for (int i = 0; i < model.getPieceCount(); i++) {
            model.positionProperty(i).addListener(this::piecePositionChange);
            var piece = createPiece(Color.valueOf(model.getPieceType(i).name()));
            getSquare(model.getPiecePosition(i)).getChildren().add(piece);
        }
    }

    private Circle createPiece(Color color) {
        var piece = new Circle(50);
        piece.setFill(color);
        return piece;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        var position = new Position(row, col);
        Logger.debug("Click on square {}", position);
        handleClickOnSquare(position);
    }

    private void handleClickOnSquare(Position position) {
        switch (selectionPhase) {
            case SELECT_FROM -> {
                if (selectablePositions.contains(position)) {
                    selectPosition(position);
                    alterSelectionPhase();
                }
            }
            case SELECT_TO -> {
                if (selectablePositions.contains(position)) {
                    var pieceNumber = model.getPieceNumber(selected).getAsInt();

                    Direction direction = null;
                    if (model.getTurnOrder() == GameModel.TurnOrder.DOG) {
                        direction = DogDirection.of(position.row() - selected.row(), position.col() - selected.col());

                    } else if (model.getTurnOrder() == GameModel.TurnOrder.FOX) {
                        direction = FoxDirection.of(position.row() - selected.row(), position.col() - selected.col());
                    }

                    Logger.debug("Moving piece {} {}", pieceNumber, direction);
                    model.move(pieceNumber, direction);
                    deselectSelectedPosition();
                    model.changeTurnOrder();
                    Logger.debug("{} Turn now!", model.getTurnOrder());
                    alterSelectionPhase();
                    if (isFoxWin()){
                        Logger.debug("Fox Wins!");
                    }
                }
            }
        }
    }

    private boolean isFoxWin() {
        List<Position> positions = model.getAllPiecesPositions();
        int counter = 0;
        for (int i = 1; i < positions.size(); i++) {
            if (positions.get(0).row() > positions.get(i).row()){
                counter++;
            }
        }
        if (positions.get(0).row() == 7){
            return true;
        }
        return counter > 3;
    }


    private void alterSelectionPhase() {
        selectionPhase = selectionPhase.alter();
        hideSelectablePositions();
        setSelectablePositions();
        showSelectablePositions();
    }

    private void selectPosition(Position position) {
        selected = position;
        showSelectedPosition();
    }

    private void showSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().add("selected");
    }

    private void deselectSelectedPosition() {
        hideSelectedPosition();
        selected = null;
    }

    private void hideSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().remove("selected");
    }

    private void setSelectablePositions() {
        selectablePositions.clear();
        switch (selectionPhase) {
            case SELECT_FROM -> selectablePositions.addAll(model.getCurrentPiecePositions());
            case SELECT_TO -> {

                if (model.getTurnOrder() == GameModel.TurnOrder.DOG) {
                    var pieceNumber = model.getPieceNumber(selected).getAsInt();
                    for (var direction : model.getValidDogMoves(pieceNumber)) {
                        selectablePositions.add(selected.moveTo(direction));
                    }
                } else if (model.getTurnOrder() == GameModel.TurnOrder.FOX) {
                    var pieceNumber = model.getPieceNumber(selected).getAsInt();
                    for (var direction : model.getValidFoxMoves(pieceNumber)) {
                        selectablePositions.add(selected.moveTo(direction));
                    }
                    if (model.getValidFoxMoves(pieceNumber).size() == 0) {
                        Logger.debug("Dogs Win!");
                    }
                }
            }
        }
    }

    private void showSelectablePositions() {
        for (var selectablePosition : selectablePositions) {
            var square = getSquare(selectablePosition);
            square.getStyleClass().add("selectable");
        }
    }

    private void hideSelectablePositions() {
        for (var selectablePosition : selectablePositions) {
            var square = getSquare(selectablePosition);
            square.getStyleClass().remove("selectable");
        }
    }

    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void piecePositionChange(ObservableValue<? extends Position> observable, Position oldPosition, Position newPosition) {
        Logger.debug("Move: {} -> {}", oldPosition, newPosition);
        StackPane oldSquare = getSquare(oldPosition);
        StackPane newSquare = getSquare(newPosition);
        newSquare.getChildren().addAll(oldSquare.getChildren());
        oldSquare.getChildren().clear();
    }
}
