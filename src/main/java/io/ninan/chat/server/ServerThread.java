package io.ninan.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerThread implements Runnable {

    private final Socket socket;
    private final ServerMain serverMain;
    private final PrintWriter out;
    private final int clientNr;
    private volatile String nickname;
    private final BlockingQueue<String> outgoing = new LinkedBlockingQueue<>(100);


    public ServerThread(Socket socket, ServerMain main) throws IOException {
        this.socket = socket;
        this.serverMain = main;
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        this.clientNr = main.getClientNr();
        this.nickname = "Client #" + clientNr;
    }

    @Override
    public void run() {
        System.out.println(nickname + " connected.");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("Welcome! You are " + nickname + ". Commands: /nick <name>, /who, /quit");
            serverMain.broadcast("[Server]: " + nickname + " has joined the room.", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                if (message.isBlank()) {
                    continue;
                }
                if (message.startsWith("/")) {
                    handleCommand(message);
                    continue;
                }
                serverMain.broadcast(nickname + ": " + message, this);
            }

        } catch (IOException e) {
            System.err.println("Connection dropped with " + nickname);
        } finally {
            serverMain.removeClient(this);
            serverMain.releaseNickname(nickname, this);
            serverMain.broadcast("[Server]: " + nickname + " has left.", this);
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println(nickname + " disconnected.");
        }
    }

    private void handleCommand(String message) {
        String[] parts = message.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "/nick":
                handleNickCommand(argument);
                break;
            case "/who":
                handleWhoCommand();
                break;
            default:
                sendMessage("Unknown command.");
        }
    }

    private void handleNickCommand(String newNick) {
        if (newNick.isEmpty()) {
            sendMessage("Usage: /nick <name>");
            return;
        }
        if (newNick.length() < 2) {
            sendMessage("The nickname is too short (minimum 2 characters).");
            return;
        }
        if (newNick.length() > 16) {
            sendMessage("The nickname is too long (maximum 16 characters).");
            return;
        }
        if (!newNick.matches("[a-zA-Z0-9]+")) {
            sendMessage("Nickname should only contain letters or numbers.");
            return;
        }

        String oldNick = this.nickname;

        if (newNick.equalsIgnoreCase(oldNick)) {
            if (newNick.equals(oldNick)) {
                sendMessage("That is already your nickname.");
            } else {
                this.nickname = newNick;
                serverMain.broadcast("[Server]: " + oldNick + " is now known as " + newNick, this);
            }
            return;
        }

        if (!serverMain.tryClaimNickname(newNick, this)) {
            sendMessage("The nickname is already taken, pick a new one.");
            return;
        }

        serverMain.releaseNickname(oldNick, this);
        this.nickname = newNick;
        serverMain.broadcast("[Server]: " + oldNick + " is now known as " + newNick, this);
    }

    private void handleWhoCommand() {
        List<String> names = serverMain.getNicknames();
        sendMessage("Online (" + names.size() + "): " + String.join(", ", names));
    }

    public String getNickname() {
        return nickname;
    }

    public boolean sendMessage(String message)
    {
        boolean doesItFit = outgoing.offer(message);
        return doesItFit;
    }

    public void disconnect()
    {
        try
        {
            socket.close();
        }
        catch(IOException e){}
        
    }
}