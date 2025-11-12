package de.uulm.vs.sheet03.ex02;

import java.rmi.RemoteException;

public class CachedTest {

    public static void main(String[] args) {
        try {
            // Creating clients
            CachedRMIClient client1 = new CachedRMIClient("Client-1");
            CachedRMIClient client2 = new CachedRMIClient("Client-2");

            Thread.sleep(500);

            System.out.println("Test 1: C1 WRITE keyA");
            client1.write("keyA", "Wert_123");
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("Test 2: C2 READ keyA -> MISS & SUBSCRIBE");
            String val = client2.read("keyA");
            System.out.println("C2 hat gelesen: " + val);
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("Test 3: C1 OVERWRITE keyA");
            client1.write("keyA", "Wert_456_NEU");

            Thread.sleep(1000);

            System.out.println("Checking caches...");
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("Test 4: C1 DELETE keyA");
            client1.remove("keyA");

            Thread.sleep(1000);

            System.out.println("Checking caches...");
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("Test 5: C1 READ Key (already deleted)");
            val = client1.read("keyA");
            System.out.println("C1 has read: " + val);

            System.exit(0);

        } catch (RemoteException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

}
