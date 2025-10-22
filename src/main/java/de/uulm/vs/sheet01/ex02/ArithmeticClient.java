package de.uulm.vs.sheet01.ex02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ArithmeticClient {

    private final static String HOST = "vs.lxd-vs.uni-ulm.de";
    private final static int PORT = 5678;
    private final static String REQUEST = "Please provide a new expression!";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            System.out.println("Connected to " + HOST + ":" + PORT);

            System.out.println("Sending request...");

            out.println(REQUEST);

            String expression = in.readLine();
            if (expression == null) {
                System.out.println("An error occurred");
                return;
            }

            System.out.println("Received expression: " + expression);

            long solution = evaluateExpression(expression);

            System.out.printf("Calculated solution: %d%n", solution);

            String solutionString = expression + "=" + solution;

            out.println(solutionString);

            String response = in.readLine();
            System.out.println("Final response: " + response);

        } catch (IOException ignored) {
            System.out.println("An error occurred");
        }
    }

    private static long evaluateExpression(String expression) {
        // Split: 6*5+9+3 -> 6*5, 9, 3
        String[] terms = expression.split("\\+");

        long totalSum = 0;

        for (String term : terms) {
            // Split: e.g. 6*5 -> 6, 5
            String[] factors = term.split("\\*");

            long termValue = 1;

            for (String factor : factors) {
                termValue *= Long.parseLong(factor.trim());
            }

            // Combine
            totalSum += termValue;
        }

        return totalSum;
    }

}
