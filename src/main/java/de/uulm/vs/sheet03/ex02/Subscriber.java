package de.uulm.vs.sheet03.ex02;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Subscriber extends Remote {

    void updateEntry(String key, String value) throws RemoteException;

    void removeEntry(String key) throws RemoteException;

}
