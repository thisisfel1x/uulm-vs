package de.uulm.vs.sheet02.ex02;

public class Test {

    public static void main(String[] args) {
        System.out.println("Starting!");

        System.out.println("Starting Worker Thread...");
        new Thread(() -> {
            Worker.main(new String[0]);
        }).start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Starting Controller Thread...");
        new Thread(() -> {
            Controller.main(new String[0]);
        }).start();

        System.out.println("Controller and Worker are now running");
    }
}
