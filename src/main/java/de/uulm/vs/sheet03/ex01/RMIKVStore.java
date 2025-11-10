package de.uulm.vs.sheet03.ex01;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RMIKVStore extends UnicastRemoteObject implements RemoteKVStore {

    private static final String SERVICE_NAME = "KVStoreService";
    private static final int RMI_PORT = 1099;
    private final HashMap<String, String> store;

    public RMIKVStore() throws RemoteException {
        super();

        this.store = new HashMap<>();

        try {
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(SERVICE_NAME, this);

            System.out.println("RMIKVStore created");
        } catch (Exception ignored) {
            throw new RemoteException("RMIKVStore creation failed");
        }
    }

    public static void main(String[] args) {
        try {
            new RMIKVStore();
        } catch (RemoteException ignored) {
            ignored.printStackTrace();
            System.out.println("RMIKVStore creation failed");
        }
    }

    @Override
    public synchronized String readRemote(String key) throws RemoteException {
        System.out.println("RMIKVStore reading " + key);
        if (!this.store.containsKey(key)) {
            throw new RemoteException("Key " + key + " not found");
        }
        return this.store.get(key);
    }

    @Override
    public synchronized void writeRemote(String key, String value) {
        System.out.println("RMIKVStore writing " + key + ":" + value);
        this.store.put(key, value);
    }

    @Override
    public void deleteRemote(String key) throws RemoteException {
        System.out.println("RMIKVStore deleting " + key);
        if (this.store.remove(key) == null) {
            throw new RemoteException("Key " + key + " not found");
        }
    }
}
