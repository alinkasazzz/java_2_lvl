package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;
    private  AuthService authService;
    public AuthService getAuthService(){
        return authService;
    }

    public Server(){
        clients = new Vector<>();
        authService = new SimpleAuthService();
        Socket client = null;
        ServerSocket server = null;
        int PORT = 222;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            while (true){
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

    public void broadcastMsg(String msg, String nick){
        for (ClientHandler c : clients) {
            c.send(nick + ": "+ msg);
        }
    }
    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }
    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }
}

