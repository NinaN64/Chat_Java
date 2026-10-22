package io.ninan.chat.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiServerTest {

    private static final int PORT = 5555;
    private static MultiServer server;

    @BeforeAll
    static void startServer() throws Exception {
        server = new MultiServer();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                server.start(PORT);
            } catch (IOException e) {
            }
        });
        Thread.sleep(200);
    }

    @AfterAll
    static void stopServer() throws IOException {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void givenClient1ServerRes_Correct() throws IOException {
        EchoClient client = new EchoClient();
        client.startConnection("127.0.0.1", PORT);

        String msg1 = client.sendMessage("hello");
        String msg2 = client.sendMessage("world");
        String terminate = client.sendMessage(".");

        assertEquals("hello", msg1);
        assertEquals("world", msg2);
        assertEquals("bye", terminate);

        client.stopConnection();
    }

    @Test
    void givenClient2_whenServerResponds_thenCorrect() throws IOException {
        EchoClient client = new EchoClient();
        client.startConnection("127.0.0.1", PORT);

        String msg = client.sendMessage("testing client 2");
        String terminate = client.sendMessage(".");

        assertEquals("testing client 2", msg);
        assertEquals("bye", terminate);

        client.stopConnection();
    }
}