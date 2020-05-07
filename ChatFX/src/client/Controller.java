package client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public Button buttonSend;
    @FXML
    public TextField textField;
    @FXML
    public TextArea textArea;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox msgPanel;

    Socket client;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 222;

    final String CHAT_TITLE_EMPTY = "Chat 2020";

    private boolean authenticated;
    private String nickname;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
        if (!authenticated) {
            nickname = "";
            setTitle(CHAT_TITLE_EMPTY);
        }
        textArea.clear();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authenticated = false;
    }

    public void connect() {
        try {
            client = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            new Thread(() -> {
                try {
                    while (true){
                        String str = in.readUTF();
                        if (str.startsWith("/authok")){
                            try {
                                nickname = str.split(" ")[1];
                            }catch (ArrayIndexOutOfBoundsException e){
                                e.printStackTrace();
                            }

                            setAuthenticated(true);
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }

                    setTitle(CHAT_TITLE_EMPTY + " : " + nickname);

                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            System.out.println("Клиент отключился");
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthenticated(false);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void send() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void enter() {
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                send();
            }
        });
    }

    public void tryToAuth() {
        if(client == null || client.isClosed()){
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText().trim() +" "+ passwordField.getText().trim() );
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void setTitle(String title) {
        Platform.runLater(() -> {
            ((Stage) textArea.getScene().getWindow()).setTitle(title);
        });
    }
}
