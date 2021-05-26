package chasegame.controller;

import chasegame.model.*;
import chasegame.results.GameResult;
import chasegame.results.GameResultDao;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the flow of the game, selection and game over database actions.
 */
public class GameController {

    private String playerName;
    private Instant startTime;
    private IntegerProperty rounds = new SimpleIntegerProperty();
    private StringProperty turnText = new SimpleStringProperty("Dog's turn!");
    private int roundCounter = 1;

    public void setPlayerName(String text) {
        this.playerName = text;
    }

    public String getPlayerName() {
        return playerName;
    }

    /**
     * Switches movement phases.
     */
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

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private GameResultDao gameResultDao;

    @FXML
    private GridPane board;

    @FXML
    private Label roundsLabel;

    @FXML
    private Label turnLabel;

    @FXML
    private void initialize() {
        createBoard();
        createPieces();
        startTime = Instant.now();
        roundsLabel.textProperty().bind(rounds.asString());
        turnLabel.textProperty().bind(turnText);
        setSelectablePositions();
        showSelectablePositions();
    }

    /**
     * Creates squares on the board.
     */
    private void createBoard() {
        for (int i = 0; i < GameModel.BOARD_SIZE; i++) {
            for (int j = 0; j < GameModel.BOARD_SIZE; j++) {
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

    /**
     * Creates a {@link StackPane} square on the {@link GridPane}.
     *
     * @return the square that was created.
     */
    private StackPane createSquare() {
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    /**
     * Creates board pieces (Fox and Dogs)
     */
    private void createPieces() {
        for (int i = 0; i < model.getPieceCount(); i++) {
            model.positionProperty(i).addListener(this::piecePositionChange);
            var piece = createPiece(Color.valueOf(model.getPieceType(i).name()));
            getSquare(model.getPiecePosition(i)).getChildren().add(piece);
        }
    }

    /**
     * Creates circle that represents the given piece.
     * @param color the color of the piece.
     * @return the {@link Circle} instance representing the piece.
     */
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

    /**
     * Handles click on any square of the board.
     * @param position the square's position that was clicked
     */
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
                        turnText.set("Fox turn!");
                        direction = DogDirection.of(position.row() - selected.row(), position.col() - selected.col());

                    } else if (model.getTurnOrder() == GameModel.TurnOrder.FOX) {
                        roundCounter++;
                        rounds.set(roundCounter);
                        turnText.set("Dog turn!");
                        direction = FoxDirection.of(position.row() - selected.row(), position.col() - selected.col());
                    }

                    Logger.debug("Moving piece {} {}", pieceNumber, direction);
                    model.move(pieceNumber, direction);
                    deselectSelectedPosition();
                    model.changeTurnOrder();
                    Logger.debug("{} Turn now!", model.getTurnOrder());
                    alterSelectionPhase();
                    if (isFoxWin()) {
                        Logger.debug("Fox Wins!");
                        gameResultDao.persist(createGameResult());
                    }
                }
            }
        }
    }

    /**
     * Handles win condition for the Fox.
     * @return true if the Fox won the game.
     */
    private boolean isFoxWin() {
        List<Position> positions = model.getAllPiecesPositions();
        int counter = 0;
        for (int i = 1; i < positions.size(); i++) {
            if (positions.get(0).row() > positions.get(i).row()) {
                counter++;
            }
        }
        if (positions.get(0).row() == 7) {
            return true;
        }
        return counter > 3;
    }

    /**
     * Changes phases of selection.
     */
    private void alterSelectionPhase() {
        selectionPhase = selectionPhase.alter();
        hideSelectablePositions();
        setSelectablePositions();
        showSelectablePositions();
    }

    /**
     * Selects an available position.
     * @param position position that could be selected.
     */
    private void selectPosition(Position position) {
        selected = position;
        showSelectedPosition();
    }

    /**
     * Adds border to selected position.
     */
    private void showSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().add("selected");
    }

    /**
     * Deselects a position that was previously selected.
     */
    private void deselectSelectedPosition() {
        hideSelectedPosition();
        selected = null;
    }

    /**
     * Removes highlight from square.
     */
    private void hideSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().remove("selected");
    }

    /**
     * Sets list of positions that are available.
     */
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
                        gameResultDao.persist(createGameResult());
                    }
                }
            }
        }

    }

    /**
     * Shows available moves.
     */
    private void showSelectablePositions() {
        for (var selectablePosition : selectablePositions) {
            var square = getSquare(selectablePosition);
            square.getStyleClass().add("selectable");
        }
    }

    /**
     * Hides available moves.
     */
    private void hideSelectablePositions() {
        for (var selectablePosition : selectablePositions) {
            var square = getSquare(selectablePosition);
            square.getStyleClass().remove("selectable");
        }
    }

    /**
     * Getter for {@link StackPane} square instance.
     * @param position position of the square.
     * @return the square instance.
     */
    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    /**
     * Changes the {@link Position} of the given piece.
     * @param observable changeable position of the piece.
     * @param oldPosition position before the move.
     * @param newPosition desired position after moving.
     */
    private void piecePositionChange(ObservableValue<? extends Position> observable, Position oldPosition, Position newPosition) {
        Logger.debug("Move: {} -> {}", oldPosition, newPosition);
        StackPane oldSquare = getSquare(oldPosition);
        StackPane newSquare = getSquare(newPosition);
        newSquare.getChildren().addAll(oldSquare.getChildren());
        oldSquare.getChildren().clear();
    }

    /**
     * Loads HighScore scene.
     * @param actionEvent click event from the highscore button.
     * @throws IOException occurs when {@link FXMLLoader} can't find a file.
     */
    public void seeHighScores(ActionEvent actionEvent) throws IOException {
        Logger.info("Loading high scores scene...");
        fxmlLoader.setLocation(getClass().getResource("/fxml/highscore.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Creates database build of the Game result.
     * @return the object used for the database insert.
     */
    private GameResult createGameResult() {
        return GameResult.builder()
                .player(playerName)
                .duration(Duration.between(startTime, Instant.now()))
                .rounds(rounds.get())
                .build();
    }
}
