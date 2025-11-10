package de.uulm.vs.sheet03.ex02;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class CachedRMIClient extends UnicastRemoteObject implements Subscriber {

    private final HashMap<String, String> localCache;
    private final SubscribeKVStore serverStub;
    private final String clientName;

    public CachedRMIClient(String name) throws RemoteException {
        super();
        this.localCache = new HashMap<>();
        this.clientName = name;
        System.out.println("Starting RMI Client " + name);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", SubRMIKVStore.RMI_PORT);
            this.serverStub = (SubscribeKVStore) registry.lookup(SubRMIKVStore.SERVICE_NAME);

            System.out.println("RMI Client " + name + " started");
        } catch (Exception ignored) {
            throw new RemoteException("RMI Client " + name + " not connected");
        }
    }

    public void write(String key, String value) {
        System.out.println("Writing " + key + " to " + value);

        try {
            serverStub.writeRemote(key, value);
            localCache.put(key, value);
        } catch (RemoteException ignored) {
            System.out.println("RMIKVStore write failed");
        }
    }

    public void remove(String key) {
        System.out.println("Removing " + key);

        try {
            serverStub.deleteRemote(key);
            localCache.remove(key);
        } catch (RemoteException e) {
            System.out.println("RMIKVStore remove failed");
        }
    }

    public String read(String key) {
        if (localCache.containsKey(key)) {
            System.out.println("Reading " + key + " from " + localCache.get(key) + " (HIT)");
            return localCache.get(key);
        }

        System.out.println("Reading " + key + " from " + localCache.get(key) + " (MISS)");
        try {
            String value = serverStub.readRemote(key);

            localCache.put(key, value);
            serverStub.subscribe(key, this);

            System.out.println("Value read from " + key + " from " + localCache.get(key) + " cached");
            return value;

        } catch (RemoteException ignored) {
            System.out.println("Could not read " + key);
            return null;
        }
    }

    @Override
    public void updateEntry(String key, String value) throws RemoteException {
        if (!localCache.containsKey(key)) {
            throw new RemoteException("Key " + key + " not found in cache");
        }

        System.out.println("Updating cache " + key + " from " + localCache.get(key) + " to " + value);
        localCache.put(key, value);
    }

    @Override
    public void removeEntry(String key) throws RemoteException {
        if (localCache.remove(key) == null) {
            throw new RemoteException("Key " + key + " not found in cache from " + localCache.get(key));
        }

        System.out.println("Key was removed externally from " + localCache.get(key));
    }

    public void printLocalCache() {
        System.out.println(clientName + " - Current cache size: " + localCache);
    }
}
