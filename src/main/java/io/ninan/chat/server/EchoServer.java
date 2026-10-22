package io.ninan.chat.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.*;
import java.io.*;

public class EchoServer
{

    public void start(int port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while((inputLine = in.readLine()) != null)
        {
            if(".".equals(inputLine))
            {
                out.println("good bye");
                break;
            }
            out.println(inputLine);
        }
    }
}