package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class Controller {
    @FXML
    public Button buttonSend;
    @FXML
    public TextField textField;
    @FXML
    public TextArea textArea;


    public void send() {

        textArea.appendText(textField.getText()+"\n");
        textField.clear();
        textField.requestFocus();
    }

    public void enter() {
        textField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode()== KeyCode.ENTER){
                send();
            }
        });
    }
}
