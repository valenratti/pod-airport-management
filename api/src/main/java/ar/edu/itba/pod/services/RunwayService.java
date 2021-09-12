package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.RunwayCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RunwayService extends Remote {
    /**
     * Requires runway for a certain flight
     * @param flightCode
     * @param destinationAirport
     * @param airlineName
     * @param minCategory
     * @throws RemoteException
     * @return
     */
    boolean requireRunway(int flightCode, String destinationAirport, String airlineName, RunwayCategory minCategory) throws RemoteException;
}
