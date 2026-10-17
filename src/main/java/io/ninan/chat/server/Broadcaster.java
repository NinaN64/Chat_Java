package io.ninan.chat.server;
import io.ninan.chat.model.Message;

interface Broadcaster
{
    public void broadcast(Message message);
}