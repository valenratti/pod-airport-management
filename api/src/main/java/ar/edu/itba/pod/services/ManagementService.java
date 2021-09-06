package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.RunwayCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagementService extends Remote {
    void addRunway(String runwayName, RunwayCategory category) throws RemoteException;
    boolean getRunwayStatus(String runwayName) throws RemoteException;
    boolean openRunway(String runwayName) throws RemoteException;
    boolean closeRunway(String runwayName) throws RemoteException;
    void takeOff() throws RemoteException;
    void reorderFlights() throws RemoteException;
}
