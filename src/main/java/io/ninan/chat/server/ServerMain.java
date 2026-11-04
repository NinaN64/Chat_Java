package io.ninan.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain {

    private final AtomicInteger clientCounter = new AtomicInteger(1);
    private final List<ServerThread> clients = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, ServerThread> takenNicknames = new ConcurrentHashMap<>();

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

    public boolean tryClaimNickname(String newNick, ServerThread thread) {
        return takenNicknames.putIfAbsent(newNick.toLowerCase(), thread) == null;
    }

    public void releaseNickname(String nick, ServerThread owner) {
        if (nick != null) {
            takenNicknames.remove(nick.toLowerCase(), owner);
        }
    }

    public int getClientNr() {
        return clientCounter.getAndIncrement();
    }

    public void broadcast(String message, ServerThread sender)
    {
        for (ServerThread client : clients) {
            if(!client.sendMessage(message))
            {
                System.out.println(client.getNickname() + " is too slow, disconnecting.");
                removeClient(client);
                client.disconnect();
            }
        }
    }

    public void removeClient(ServerThread client) {
        clients.remove(client);
    }

    public List<String> getNicknames() {
        List<String> names = new ArrayList<>();
        for (ServerThread client : clients) {
            names.add(client.getNickname());
        }
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    public static void main(String[] args) {
        new ServerMain();
    }
}