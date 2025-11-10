package de.uulm.vs.sheet03.ex02;

import java.rmi.RemoteException;

public class CachedTest {

    public static void main(String[] args) {
        try {
            System.out.println("--- Erzeuge Clients ---");
            CachedRMIClient client1 = new CachedRMIClient("Client-1");
            CachedRMIClient client2 = new CachedRMIClient("Client-2");

            Thread.sleep(500);

            System.out.println("\n--- Test 1: C1 schreibt 'keyA' ---");
            client1.write("keyA", "Wert_123");
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("\n--- Test 2: C2 liest 'keyA' (Cache-Miss & Subscribe) ---");
            String val = client2.read("keyA");
            System.out.println("C2 hat gelesen: " + val);
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("\n--- Test 3: C1 überschreibt 'keyA' ---");
            client1.write("keyA", "Wert_456_NEU");

            Thread.sleep(1000);

            System.out.println("\n--- Test 4: Caches prüfen nach Update ---");
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("\n--- Test 5: C1 löscht 'keyA' ---");
            client1.remove("keyA");

            Thread.sleep(1000);

            System.out.println("\n--- Test 6: Caches prüfen nach Löschen ---");
            client1.printLocalCache();
            client2.printLocalCache();

            System.out.println("\n--- Test 7: C1 liest gelöschten Key ---");
            val = client1.read("keyA");
            System.out.println("C1 hat gelesen: " + val);

            System.out.println("\n--- Demo abgeschlossen ---");

            System.exit(0);

        } catch (RemoteException | InterruptedException e) {
            System.err.println("Fehler im CacheTest: " + e);
            e.printStackTrace();
        }
    }

}
