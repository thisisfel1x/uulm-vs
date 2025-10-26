package de.uulm.vs.sheet02.ex02;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Controller {

    private static final String PUBLISHER_ADDR = "tcp://vs.lxd-vs.uni-ulm.de:27378";
    private static final String REPLY_ADDR = "tcp://vs.lxd-vs.uni-ulm.de:27379";

    private static final String WORKER_TASK_ADDR = "tcp://localhost:5555";
    private static final String WORKER_RESULT_ADDR = "tcp://localhost:5556";

    public static void main(String[] args) {
        System.out.println("Controller starting...");

        try (ZContext context = new ZContext()) {
            ZMQ.Socket subSocket = context.createSocket(SocketType.SUB);
            subSocket.connect(PUBLISHER_ADDR);
            subSocket.subscribe(ZMQ.SUBSCRIPTION_ALL);

            ZMQ.Socket reqSocket = context.createSocket(SocketType.REQ);
            reqSocket.connect(REPLY_ADDR);

            ZMQ.Socket taskPusher = context.createSocket(SocketType.PUSH);
            taskPusher.bind(WORKER_TASK_ADDR);

            ZMQ.Socket resultPuller = context.createSocket(SocketType.PULL);
            resultPuller.bind(WORKER_RESULT_ADDR);

            ZMQ.Poller poller = context.createPoller(2);
            poller.register(subSocket, ZMQ.Poller.POLLIN);
            poller.register(resultPuller, ZMQ.Poller.POLLIN);

            System.out.println("Everything connected!");

            while (!Thread.currentThread().isInterrupted()) {
                poller.poll();

                if (poller.pollin(0)) {
                    String n_str = subSocket.recvStr(ZMQ.DONTWAIT).trim();
                    System.out.println("Controller: Received challenge: " + n_str);

                    taskPusher.send(n_str);
                }

                if (poller.pollin(1)) {
                    String result = resultPuller.recvStr(ZMQ.DONTWAIT).trim();
                    System.out.println("Controller: Received solution from worker: " + result);

                    reqSocket.send(result);

                    String serverResponse = reqSocket.recvStr(0);
                    System.out.println("Controller: Server response: " + serverResponse);
                }
            }
        }
    }
}
