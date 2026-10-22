package io.ninan.chat;
import io.ninan.chat.server.MultiServer;

import java.io.IOException;

public class Main {
    static void main() throws IOException {
        MultiServer server = new MultiServer();
        server.start(6666);
    }

}
