package io.ninan.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection {

    public ClientConnection() {
        try {
            Socket socket = new Socket("localhost", 2020);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner keyboard = new Scanner(System.in);

            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();

            while (true) {
                String input = keyboard.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("/quit")) {
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientConnection();
    }
}