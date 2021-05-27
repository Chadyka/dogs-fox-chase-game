package chasegame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.IOException;

public class StartController {
    @Inject
    private FXMLLoader fxmlLoader;

    @FXML
    private TextField inputField;

    @FXML
    private TextField inputField1;

    public void startGame(ActionEvent actionEvent) throws IOException {
        if (!inputField.getText().isEmpty() && !inputField1.getText().isEmpty()) {
            fxmlLoader.setLocation(getClass().getResource("/fxml/game.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.<GameController>getController().setPlayerName(inputField.getText());
            fxmlLoader.<GameController>getController().setPlayerName1(inputField1.getText());
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            Logger.debug("The player one name is set to {}, player two name is {}, loading game scene", inputField.getText(), inputField1.getText());
        }
    }
}