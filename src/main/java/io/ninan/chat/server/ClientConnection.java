package io.ninan.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection {

    public ClientConnection(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
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

            while (keyboard.hasNextLine()) {
                String input = keyboard.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not connect to " + host + ":" + port + " (" + e.getMessage() + ")");
        }
    }

    public static void main(String[] args) {
        String host = args.length >= 1 ? args[0] : "localhost";
        int port = 2020;

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Port must be a number.");
                return;
            }
        }

        new ClientConnection(host, port);
    }
}