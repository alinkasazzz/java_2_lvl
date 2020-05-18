package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();
        Socket client = null;
        ServerSocket server = null;
        int PORT = 222;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            while (true) {
                client = server.accept();
                System.out.println("Клиент подключен");

                new ClientHandler(client, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ]: %s", sender.getNick(), receiver, msg);
        if (sender.getNick().equals(receiver)){
            sender.send(msg);
            return;
        }

        for (ClientHandler client : clients) {
            if (client.getNick().equals(receiver)) {
                sender.send(message);
                client.send(message);
                return;
            }
        }
        sender.send("Получатель не найден" + receiver);
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public boolean isLoginAuthorized(String login) {
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastMsg(String msg, String nick) {
        for (ClientHandler c : clients) {
            c.send(nick + ": " + msg);
        }
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist ");
        for (ClientHandler c : clients) {
            sb.append(c.getNick() + " ");
        }
        String msg = sb.toString();
        for (ClientHandler client : clients) {
            client.send(msg);
        }
    }
}

