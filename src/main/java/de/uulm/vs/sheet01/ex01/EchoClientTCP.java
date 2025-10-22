package de.uulm.vs.sheet01.ex01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClientTCP {

    private final static String HOST = "vs.lxd-vs.uni-ulm.de";
    private final static int PORT = 3211;
    private final static String ECHO_MESSAGE = "Hello, world!";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            System.out.println("Connected to " + HOST + ":" + PORT);
            System.out.println("Sending ECHO to " + socket.getRemoteSocketAddress());

            out.println(ECHO_MESSAGE);

            String echo = in.readLine();

            System.out.println("Received ECHO: " + echo);

        } catch (IOException ignored) {
            System.out.println("An error occurred");
        }
    }

}
