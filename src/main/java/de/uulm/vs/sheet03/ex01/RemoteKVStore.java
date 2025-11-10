package de.uulm.vs.sheet03.ex01;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteKVStore extends Remote {

    String readRemote(String key) throws RemoteException;

    void writeRemote(String key, String value) throws RemoteException;

    void deleteRemote(String key) throws RemoteException;

}
