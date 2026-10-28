package io.ninan.chat.server;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {

    private final Socket socket;
    private final ServerMain serverMain;
    private final PrintWriter out;
    private int clientNr;

    public ServerThread(Socket socket, ServerMain main) throws IOException {
        this.socket = socket;
        this.serverMain = main;
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {
        this.clientNr = serverMain.getClientNr();
        System.out.println("Client #" + clientNr + " connected.");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("Welcome! You are Client #" + clientNr + ". Type /quit to leave.");
            serverMain.broadcast("[Server]: Client #" + clientNr + " has joined the room.", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                serverMain.broadcast("Client #" + clientNr + ": " + message, this);
            }

        } catch (IOException e) {
            System.err.println("Connection dropped with Client #" + clientNr);
        } finally {
            serverMain.removeClient(this);
            serverMain.broadcast("[Server]: Client #" + clientNr + " has left.", this);
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println("Client #" + clientNr + " disconnected.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}