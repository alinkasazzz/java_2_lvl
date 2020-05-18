package client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button buttonSend;
    @FXML
    private TextField textField;
    @FXML
    public TextArea textArea;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox authPanel;
    @FXML
    private HBox msgPanel;
    @FXML
    private ListView<String> clientList;

    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 222;

    private final String CHAT_TITLE_EMPTY = "Chat 2020";

    private boolean authenticated;
    private String nickname;

    private Stage regStage;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        if (!authenticated) {
            nickname = "";
            setTitle(CHAT_TITLE_EMPTY);
        }
        textArea.clear();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authenticated = false;
        Platform.runLater(()->{
            Stage stage = (Stage) textArea.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("bue");
                    if(client != null && !client.isClosed()){
                        try {
                            out.writeUTF("/end");
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    public void connect() {
        try {
            client = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok ")) {
                            try {
                                nickname = str.split(" ")[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
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
                        if (str.startsWith("/")){
                            if (str.equals("/end")) {
                                System.out.println("Клиент отключился");

                                break;
                            }
                            if (str.startsWith("/clientlist ")){
                                String[] token = str.split(" ");
                                Platform.runLater(() ->{
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }
                        }
                       else {
                            textArea.appendText(str + "\n");
                        }

                    }
                }catch (EOFException e){
                    System.out.println(e.getMessage());
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

    public void clickClientList(MouseEvent mouseEvent) {
        System.out.println(clientList.getSelectionModel().getSelectedItem());textField.requestFocus();
        textField.setText("/w " +clientList.getSelectionModel().getSelectedItem());
        textField.appendText(" ");

    }
    private Stage createRegWindow(){
        Stage stage = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reg.fxml"));
            Parent root = fxmlLoader.load();
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            RegController regController = fxmlLoader.getController();
            regController.controller = this;

            stage.setTitle("Регистрация");
            stage.setScene(new Scene(root, 500, 300));
        } catch (IOException e) {
            e.printStackTrace();
        }
       return stage;
    }

    public void tryToReg(ActionEvent actionEvent) {
        if (regStage == null){
            regStage =  createRegWindow();
        }
        regStage.show();
    }
    public void tryRegistration(String login, String password, String nickname){
        String msg = String.format("/reg %s %s %s", login, password, nickname);

        if(client == null || client.isClosed()){
            connect();
        }
        try {
            out.writeUTF(msg);
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
