package io.ninan.chat.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientConnectionTest {

    @Test
    void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
        ClientConnection client = new ClientConnection();
        client.startConnection("127.0.0.1", 6666);
        assertEquals("hello client", client.sendMessage("hello server"));
        client.stopConnection();
    }
}