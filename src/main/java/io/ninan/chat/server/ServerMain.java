package io.ninan.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain {

    private final AtomicInteger clientCounter = new AtomicInteger(1);
    private final List<ServerThread> clients = new CopyOnWriteArrayList<>();

    public ServerMain() {
        try (ServerSocket serverSocket = new ServerSocket(2020)) {
            System.out.println("Server running on port 2020...");

            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    ServerThread serverThread = new ServerThread(socket, this);
                    clients.add(serverThread);
                    new Thread(serverThread).start();
                } catch (IOException e) {
                    System.err.println("Could not set up client: " + e.getMessage());
                    try {
                        socket.close();
                    } catch (IOException ignored) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getClientNr() {
        return clientCounter.getAndIncrement();
    }

    public void broadcast(String message, ServerThread sender) {
        for (ServerThread client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ServerThread client) {
        clients.remove(client);
    }

    public static void main(String[] args) {
        new ServerMain();
    }
}