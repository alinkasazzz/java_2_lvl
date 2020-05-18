package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegController {
    Controller controller;

    @FXML
    public TextField nickField;
    @FXML
    public PasswordField passwordField2;
    @FXML
    public PasswordField passwordField1;
    @FXML
    public TextField loginField;

    public void trytoReg() {
        String login = loginField.getText().trim();
        String password1 = passwordField1.getText().trim();
        String password2 = passwordField2.getText().trim();
        String nickname = nickField.getText().trim();
        if (!password1.equals(password2)){
            controller.textArea.setText("Пароли не совпадают");
        }
        controller.tryRegistration(login, password1, nickname);

        ((Stage) loginField.getScene().getWindow()).close();
    }
}
