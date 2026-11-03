package io.ninan.chat.server;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {

    private final Socket socket;
    private final ServerMain serverMain;
    private final PrintWriter out;
    private int clientNr;
    private volatile String nickname;

    public ServerThread(Socket socket, ServerMain main) throws IOException {
        this.socket = socket;
        this.serverMain = main;
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {
        this.clientNr = serverMain.getClientNr();
        this.nickname = "Client #" + clientNr;
        System.out.println(nickname + " connected.");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("Welcome! You are " + nickname + ". Type /quit to leave.");
            serverMain.broadcast("[Server]: " + nickname + " has joined the room.", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                if (message.startsWith("/") && message.toLowerCase().startsWith("/nick")) {
                    handleNickCommand(message);
                    continue;
                }
                if (message.startsWith("/")) {
                    sendMessage("Unknown command.");
                    continue;
                }
                serverMain.broadcast(nickname + ": " + message, this);
            }

        } catch (IOException e) {
            System.err.println("Connection dropped with" + nickname);
        } finally {
            serverMain.removeClient(this);
            serverMain.releaseNickname(nickname);
            serverMain.broadcast("[Server]: " + nickname + " has left.", this);
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println(nickname + clientNr + " disconnected.");
        }
    }

    private void handleNickCommand(String message)
    {
        String[] parts = message.split("\\s+", 2);
        if(parts.length < 2 || parts[1].trim().isEmpty())
        {
            sendMessage("Usage: /nick <name>");
            return;
        }
        String newNick = parts[1].trim();

        if(newNick.length() < 2)
        {
            sendMessage("The nickname is too short (minimum 2 characters)");
            return;
        }
        if(newNick.length() > 16)
        {
            sendMessage("The nickname is too long (max 16 characters)");
            return;
        }
        if(!newNick.matches("^[a-zA-Z0-9]+$"))
        {
            sendMessage("Nickname should only contain letters or numbers");
            return;
        }

        if(!serverMain.tryClaimNickname(newNick, this))
        {
            sendMessage("The nickname is already taken, pick a new one.");
            return;
        }

        String oldNick = this.nickname;
        serverMain.releaseNickname(oldNick);
        this.nickname = newNick;

        serverMain.broadcast("[Server]: " + oldNick + " is now known as " + newNick, this);
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}