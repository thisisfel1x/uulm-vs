package de.uulm.vs.sheet03.ex02;

import de.uulm.vs.sheet03.ex01.RemoteKVStore;

import java.rmi.RemoteException;

public interface SubscribeKVStore extends RemoteKVStore {

    void subscribe(String key, Subscriber subscriber) throws RemoteException;

    void unsubscribe(String key, Subscriber subscriber) throws RemoteException;

}
