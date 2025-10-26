package de.uulm.vs.sheet02.ex01;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberGuessingGame {

    private final static String HOST = "vs.lxd-vs.uni-ulm.de";
    private final static int PORT = 27401;

    private static final Pattern GAME_ID_PATTERN = Pattern.compile(".* (\\d+)$");
    private static final Pattern ATTEMPT_PATTERN = Pattern.compile("Attempt: (\\d+) - .*");

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://" + HOST + ":" + PORT);

            System.out.println("Connected to " + HOST + ":" + PORT);

            long gameID = getInitialGameID(socket);
            System.out.println("Game ID: " + gameID);

            long low = 1;
            long high = Long.MAX_VALUE;
            long lastNumAttempts = 0;

            while (!Thread.currentThread().isInterrupted()) {

                if (low > high) {
                    System.err.println("Search range [" + low + ", " + high + "] invalid. Resetting game...");
                    gameID = getInitialGameID(socket);
                    low = 1;
                    high = Long.MAX_VALUE;
                    lastNumAttempts = 0;
                    continue;
                }

                long t0_guess = low + (high - low) / 2;

                long guess = t0_guess + lastNumAttempts;

                String response = sendRequest(socket, gameID, guess);

                if (response.startsWith("Correct guess")) {
                    System.out.println("\n>>> WE WON! " + response + " <<<\n");
                    gameID = parseGameID(response);
                    low = 1;
                    high = Long.MAX_VALUE;
                    lastNumAttempts = 0;

                } else if (response.startsWith("This number has") || response.startsWith("GameID unknown")) {
                    System.out.println("--- Game Over or Reset. Starting new game. ---");
                    gameID = parseGameID(response);
                    low = 1;
                    high = Long.MAX_VALUE;
                    lastNumAttempts = 0;

                } else if (response.startsWith("Attempt:")) {
                    long N_prime = parseNumAttempts(response);
                    long N = N_prime - 1;

                    lastNumAttempts = N_prime;

                    if (response.endsWith("too small")) {
                        // T0 > G - N
                        low = (guess - N) + 1;
                    } else if (response.endsWith("too large")) {
                        // T0 < G - N
                        high = (guess - N) - 1;
                    }

                } else if (response.startsWith("Invalid request")) {
                    System.err.println("FATAL: Server reported invalid request format. Stopping.");
                    break;

                } else {
                    System.err.println("Unknown response: " + response);
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            System.out.println("An error occurred.");
        }
    }

    private static String sendRequest(ZMQ.Socket socket, long gameID, long guess) {
        String request = String.format("%d:%d\n", gameID, guess);
        socket.send(request.getBytes(ZMQ.CHARSET));

        byte[] reply = socket.recv();
        String response = new String(reply, ZMQ.CHARSET).trim();

        System.out.printf("Sent: %-45s | Received: %s\n", request.trim(), response);
        return response;
    }

    private static long getInitialGameID(ZMQ.Socket socket) {
        String response = sendRequest(socket, 0, 0);
        return parseGameID(response);
    }

    private static long parseGameID(String response) {
        Matcher matcher = GAME_ID_PATTERN.matcher(response);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                System.err.println("Could not parse gameID from: " + response);
                return 0;
            }
        }
        System.err.println("Could not find gameID in: " + response);
        return 0; // Fallback
    }

    private static long parseNumAttempts(String response) {
        Matcher matcher = ATTEMPT_PATTERN.matcher(response);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                System.err.println("Could not parse numAttempts from: " + response);
                return 0;
            }
        }
        System.err.println("Could not find numAttempts in: " + response);
        return 0; // Fallback
    }

}
