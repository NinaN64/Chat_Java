package io.ninan.chat.server;
import java.util.Collection;

interface ClientRegistry
{
    public Collection<ClientConnection> getEveryone();
    public void addClient(ClientConnection clientConnection);
    public void removeClient(ClientConnection clientConnection);
}