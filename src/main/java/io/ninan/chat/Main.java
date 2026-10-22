package io.ninan.chat;
import io.ninan.chat.server.ClientConnection;
import io.ninan.chat.server.Server;

import java.net.*;
import java.io.*;

import java.io.IOException;
import java.net.Socket;

public class Main {
    static void main() throws IOException {
        Server server = new Server();
        server.start(6666);
    }

}
