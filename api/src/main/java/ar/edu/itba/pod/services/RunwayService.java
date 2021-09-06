package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.RunawayCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RunwayService extends Remote {
    void requireRunway(int flightCode, String destinationAirport, String airlineName, RunawayCategory minCategory) throws RemoteException;
}
