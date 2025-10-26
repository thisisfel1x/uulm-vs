package de.uulm.vs.sheet02.ex02;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.math.BigInteger;

public class Worker {

    private static final String CONTROLLER_TASK_ADDRESS = "tcp://localhost:5555";
    private static final String CONTROLLER_RESULT_ADDRESS = "tcp://localhost:5556";

    public static void main(String[] args) {
        System.out.println("Starting Worker...");

        try (ZContext context = new ZContext()) {
            ZMQ.Socket taskPuller = context.createSocket(SocketType.PULL);
            taskPuller.connect(CONTROLLER_TASK_ADDRESS);

            ZMQ.Socket resultPusher = context.createSocket(SocketType.PUSH);
            resultPusher.connect(CONTROLLER_RESULT_ADDRESS);

            System.out.println("Connected PULL and PUSH Worker");

            while (!Thread.currentThread().isInterrupted()) {
                String newSTring = taskPuller.recvStr(0).trim();
                BigInteger newBigInteger = new BigInteger(newSTring);
                BigInteger[] factors = Fermat.fermatFactorization(newBigInteger);

                BigInteger p = factors[0];
                BigInteger q = factors[1];

                String result = String.format("%s:%s:%s", newBigInteger, p.toString(), q.toString());

                resultPusher.send(result);
                System.out.println("Sent result: " + result);
            }
        }
    }

}
