package de.uulm.vs.sheet03.ex02;

import de.uulm.vs.sheet03.ex01.RMIKVStore;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SubRMIKVStore extends UnicastRemoteObject implements SubscribeKVStore {

    private HashMap<String, String> store;

    private ConcurrentHashMap<String, List<Subscriber>> subscribers;

    public static final String SERVICE_NAME = "SubKVStoreService";
    public static final int RMI_PORT = 1099;

    public SubRMIKVStore() throws RemoteException {
        super();

        this.store = new HashMap<>();
        this.subscribers = new ConcurrentHashMap<>();

        try {
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(SERVICE_NAME, this);

            System.out.println("SubRMIKVStore created");
        } catch (Exception ignored) {
            throw new RemoteException("SubRMIKVStore creation failed");
        }
    }

    public static void main(String[] args) {
        try {
            new SubRMIKVStore();
        } catch (RemoteException ignored) {
            System.out.println("RMIKVStore creation failed");
        }
    }

    @Override
    public synchronized String readRemote(String key) throws RemoteException {
        System.out.println("SubRMIKVStore reading " + key);
        if (!this.store.containsKey(key)) {
            throw new RemoteException("Key " + key + " not found");
        }
        return this.store.get(key);
    }

    @Override
    public synchronized void writeRemote(String key, String value) throws RemoteException {
        System.out.println("SubRMIKVStore writing " + key);

        this.store.put(key, value);

        System.out.println("Notifying subscribers for key: " + key);
        notifySubscribers(key, value);

    }

    @Override
    public synchronized void deleteRemote(String key) throws RemoteException {
        System.out.println("SubRMIKVStore deleting " + key);

        if (!this.store.containsKey(key)) {
            throw new RemoteException("Key " + key + " not found");
        }

        System.out.println("Notifying subscribers for key: " + key);
        notifySubscribersOnRemoval(key);

        this.store.remove(key);
        this.subscribers.remove(key);
    }

    @Override
    public void subscribe(String key, Subscriber subscriber) throws RemoteException {
        if (!this.store.containsKey(key)) {
            throw new RemoteException("Key " + key + " not found in store");
        }

        List<Subscriber> subs = this.subscribers.computeIfAbsent(key, k -> new ArrayList<>());

        if (!subs.contains(subscriber)) {
            subs.add(subscriber);
            System.out.println("New subscriber " + subscriber + " added to subscribers for key: " + key);
        }
    }

    @Override
    public void unsubscribe(String key, Subscriber subscriber) throws RemoteException {
        if (!this.store.containsKey(key)) {
            throw new RemoteException("Key " + key + " not found in store");
        }

        List<Subscriber> subs = this.subscribers.get(key);
        if (subs == null) return;

        subs.remove(subscriber);
        System.out.println("Unsubscribing subscriber " + subscriber + " for key: " + key);

        if (subs.isEmpty()) this.subscribers.remove(key);
    }

    private void notifySubscribers(String key, String value) throws RemoteException {
        List<Subscriber> subs  = this.subscribers.get(key);
        if (subs == null) return;

        List<Subscriber> deadSubscribers = new ArrayList<>();
        for (Subscriber sub : subs) {
            try {
                sub.updateEntry(key, value);
            } catch (RemoteException ignored) {
                System.out.println("Error notifying subscriber " + sub + " for key: " + key);
                System.out.println("Removing dead subscriber " + sub + " for key: " + key);

                deadSubscribers.add(sub);
            }
        }

        subs.removeAll(deadSubscribers);
    }

    private void notifySubscribersOnRemoval(String key) throws RemoteException {
        List<Subscriber> subs = this.subscribers.get(key);
        if (subs == null) return;

        List<Subscriber> deadSubscribers = new ArrayList<>();
        for (Subscriber sub : subs) {
            try {
                sub.removeEntry(key);
            } catch (RemoteException ignored) {
                System.out.println("Error notifying subscriber " + sub + " for key: " + key);
                deadSubscribers.add(sub);
            }
        }

        subs.removeAll(deadSubscribers);
    }
}
