package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;

    private String nick;
    private String login;
    private String sender;
    private String receiver;

    public String getNick(){
        return nick;
    }

    public ClientHandler(Socket client, Server server) {


        try {
            this.client = client;
            this.server = server;
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            new Thread(() -> {
                try {
                    while (true){
                        String str = in.readUTF();
                        if (str.startsWith("/auth ")){
                            String[] token = str.split(" ");
                            String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);
                            if (newNick != null){
                                send("/authok "+newNick);
                                nick = newNick;
                                login = token[1];
                                server.subscribe(this);
                                System.out.println("Клиент "+ nick +" прошел аутентификацию");
                                break;
                            }else {
                                send("Неверный логин / пароль");
                            }


                        }

                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/end");
                            break;
                        }
                        if (str.startsWith("/w")){
                            String[] token = str.split(" ", 3);
                            if(token.length ==3) {
                                server.privateMsg(nick, token[1], token[2]);
                            }
                        }
                        else {
                            server.broadcastMsg(str, nick);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Клиент отключился");
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

