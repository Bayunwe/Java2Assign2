package application;

import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class Main0 {
    public static void main(String[] args) {
        // Server
        try {
            ServerSocket serverSocket = new ServerSocket(9000);
            while (true) {
                Socket socket = serverSocket.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF("Waiting for player2");


                Socket socket1 = serverSocket.accept();
                DataOutputStream dataOutputStream1 = new DataOutputStream(socket1.getOutputStream());
                dataOutputStream1.writeUTF("Waiting for player1");

                ClientHandler clientHandler = new ClientHandler(socket, socket1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class ClientHandler extends Thread {
        Socket socket;
        Socket socket1;
        DataInputStream dataInputStream;
        DataInputStream dataInputStream1;
        DataOutputStream dataOutputStream;
        DataOutputStream dataOutputStream1;
        String meg = "", meg1 = "";

        public ClientHandler(Socket socket, Socket socket1) {
            this.socket = socket;
            this.socket1 = socket1;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataInputStream1 = new DataInputStream(socket1.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream1 = new DataOutputStream(socket1.getOutputStream());
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                System.out.println("玩家匹配成功");
                dataOutputStream.writeUTF("You are player 1, matching successful");
                dataOutputStream1.writeUTF("You are player 2, matching successful");
                while (!meg.equals("stop")) {
                    meg = dataInputStream.readUTF();
                    System.out.println("Client says: " + meg);
                    dataOutputStream1.writeUTF(meg);
                    dataOutputStream1.flush();

                    meg1 = dataInputStream1.readUTF();
                    System.out.println("Client1 says: " + meg1);
                    dataOutputStream.writeUTF(meg1);
                    dataOutputStream.flush();
                }
                dataInputStream.close();
                dataInputStream1.close();
                socket.close();
                socket1.close();
            } catch (Exception e) {
                try {
                    if (socket.getInputStream()==null||socket1.getInputStream()==null){
                        socket.getInputStream().close();
                        socket1.getInputStream().close();
                }
                    if (socket1!=null){
                        System.out.println("有玩家退出游戏！游戏结束");
                        socket1.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
