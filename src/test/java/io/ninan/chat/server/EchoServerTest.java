package io.ninan.chat.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class EchoServerTest {
    private static final int PORT = 4444;
    private EchoServer server;
    private EchoClient client;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        server = new EchoServer();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                server.start(PORT);
            } catch (IOException e) {
            }
        });

        Thread.sleep(150);

        client = new EchoClient();
        client.startConnection("127.0.0.1", PORT);
    }

    @AfterEach
    public void tearDown() throws IOException {
        client.stopConnection();
    }

    @Test
    public void givenClientServerEchoes_Correct() throws IOException {
        String msg1 = client.sendMessage("hello");
        String msg2 = client.sendMessage("world");
        String msg3 = client.sendMessage("!");
        String msg4 = client.sendMessage(".");

        assertEquals("hello", msg1);
        assertEquals("world", msg2);
        assertEquals("!", msg3);
        assertEquals("good bye", msg4);
    }
}