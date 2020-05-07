import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket client = null;
        DataInputStream in;
        DataOutputStream out;
        int PORT = 666;
        String IP = "localhost";

        try {
            client = new Socket(IP, PORT);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            Thread input = new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if(msg.equals("/end")){
                            System.out.println("Клиент отключился");
                            break;
                        }
                        System.out.println("Сервер:" +msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            Thread output = new Thread(() ->{
                try {
                    while (true) {
                        String msg = scanner.nextLine();
                        out.writeUTF(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            input.start();
            output.setDaemon(true);
            output.start();
            try {
                input.join();
                output.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
