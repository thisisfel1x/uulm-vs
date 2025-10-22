package de.uulm.vs.sheet01.ex01;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class EchoClientUDP {

    private final static String HOST = "vs.lxd-vs.uni-ulm.de";
    private final static int PORT = 3211;
    private final static String ECHO_MESSAGE = "Hello, world!";

    public static void main(String[] args) {
        try (
                DatagramSocket datagramSocket = new DatagramSocket();
        ) {

            InetAddress inetAddress = InetAddress.getByName(HOST);
            byte[] buffer = ECHO_MESSAGE.getBytes();

            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, PORT);

            System.out.println("Sending Echo Request");

            datagramSocket.send(datagramPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            datagramSocket.receive(receivePacket);

            String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);

            System.out.println("Received echo: " + receivedMessage);


        } catch (IOException ignored) {
            System.out.println("An error occurred");
        }

    }

}
