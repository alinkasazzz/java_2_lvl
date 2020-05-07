import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        //подключения
        ServerSocket server = null;
        Socket client = null;
        int PORT = 222;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            client = server.accept();
            System.out.println("Клиент подключен");

            Scanner sc = new Scanner(client.getInputStream());
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            while (true){
                String str = sc.nextLine();
                if(str.equals("/end")){
                    System.out.println("Клиент отключился");
                    break;
                }
                System.out.println("Клиент:"  +str);
                out.println("echo: " + str);

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
}
