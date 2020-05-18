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

    public String getNick() {
        return nick;
    }

    public String getLogin() {
        return login;
    }

    public ClientHandler(Socket client, Server server) {


        try {
            this.client = client;
            this.server = server;
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/reg ")){
                            String[] token = str.split(" ");
                            boolean reg = server.getAuthService().registration(token[1], token[2], token[3]);
                            if(reg){
                                send("Регистрация прошла успешно");
                            }else {
                                send("Логин или ник уже занят");
                            }
                        }
                        if (str.equals("/end")) {
                            throw new RuntimeException("Клиент отключился крестиком");
                        }
                            if (str.startsWith("/auth ")) {
                                String[] token = str.split(" ");
                                String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);

                                login = token[1];

                                if (newNick != null) {
                                    if (!server.isLoginAuthorized(login)) {
                                        send("/authok " + newNick);
                                        nick = newNick;
                                        server.subscribe(this);
                                        System.out.println("Клиент " + nick + " прошел аутентификацию");
                                        break;
                                    } else {
                                        send("С этим логином уже авторизовались");
                                    }
                                } else {
                                    send("Неверный логин / пароль");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    out.writeUTF("/end");
                                    break;
                                }
                                if (str.startsWith("/w")) {
                                    String[] token = str.split(" ", 3);
                                    if (token.length == 3) {
                                        server.privateMsg(this, token[1], token[2]);
                                    }
                                }
                            } else {
                                server.broadcastMsg(str, nick);
                            }
                        }
                } catch(RuntimeException e){
                    System.out.println(e.getMessage());
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
    public void send(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }


