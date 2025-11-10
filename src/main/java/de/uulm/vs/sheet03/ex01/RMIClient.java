package de.uulm.vs.sheet03.ex01;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

    private static final String SERVICE_NAME = "KVStoreService";
    private static final int RMI_PORT = 1099;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, RMI_PORT);
            RemoteKVStore remoteKVStore = (RemoteKVStore) registry.lookup(SERVICE_NAME);

            System.out.println("RMIKVStore created");

            // Test functions
            remoteKVStore.writeRemote("username", "Alice");
            String value = remoteKVStore.readRemote("username");
            System.out.println("Username is: " + value);

            remoteKVStore.writeRemote("username", "Bob");
            value = remoteKVStore.readRemote("username");
            System.out.println("Username is: " + value);

            remoteKVStore.deleteRemote("username");

            try {
                remoteKVStore.readRemote("username");
            } catch (RemoteException ignored) {
                System.out.println("Expected exception for missing kv entry");
            }

        } catch (Exception ignored) {
            System.out.println("RMIKVStore creation failed");
        }
    }

}
