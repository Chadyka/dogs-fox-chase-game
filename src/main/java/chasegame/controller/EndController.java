package chasegame.controller;

import chasegame.results.GameResult;
import chasegame.results.GameResultDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class EndController {

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private GameResultDao gameResultDao;

    @FXML
    private TableView<GameResult> highScoreTable;

    @FXML
    private TableColumn<GameResult, String> player;

    @FXML
    private TableColumn<GameResult, Integer> rounds;

    @FXML
    private TableColumn<GameResult, Duration> duration;

    @FXML
    private TableColumn<GameResult, String> winnerType;

    @FXML
    private void initialize() {
        Logger.debug("Loading high scores...");
        List<GameResult> highScoreList = gameResultDao.findBest(10);

        player.setCellValueFactory(new PropertyValueFactory<>("player"));
        rounds.setCellValueFactory(new PropertyValueFactory<>("rounds"));
        duration.setCellValueFactory(new PropertyValueFactory<>("duration"));

        duration.setCellFactory(column -> {
            TableCell<GameResult, Duration> cell = new TableCell<GameResult, Duration>() {
                @Override
                protected void updateItem(Duration item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(DurationFormatUtils.formatDuration(item.toMillis(), "H:mm:ss"));
                    }
                }
            };
            return cell;
        });

        ObservableList<GameResult> observableResult = FXCollections.observableArrayList();
        observableResult.addAll(highScoreList);

        highScoreTable.setItems(observableResult);
    }

    /**
     * Defines action for the back button on the high score scene.
     * @param actionEvent event fired when the button is clicked
     * @throws IOException occurs if the {@link FXMLLoader} can't find a file.
     */

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        fxmlLoader.setLocation(getClass().getResource("/fxml/game.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
