package ar.edu.itba.pod.services;

import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.models.ReorderFlightsResponseDTO;
import ar.edu.itba.pod.models.RunwayCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagementService extends Remote {
    /**
     * Given a name and a category, it creates a new Runaway and it's queue of flights.
     * Throws exception if a runway with the same name exists.
     * @param runwayName
     * @param category
     * @throws RemoteException
     * @throws DuplicateRunwayException
     */
    void addRunway(String runwayName, RunwayCategory category) throws RemoteException, DuplicateRunwayException;

    /**
     * If the runway exists, will return the current status
     * @param runwayName
     * @return
     * @throws RemoteException
     */
    boolean getRunwayStatus(String runwayName) throws RemoteException;

    /**
     * Opens the runway in case it's closed
     * @param runwayName
     * @return
     * @throws RemoteException
     */
    boolean openRunway(String runwayName) throws RemoteException;

    /**
     * Closes the runaway in case it's open
     * @param runwayName
     * @return
     * @throws RemoteException
     */
    boolean closeRunway(String runwayName) throws RemoteException;

    /**
     * Dispatchs a flight on each open runway
     * @throws RemoteException
     */
    void takeOff() throws RemoteException;

    /**
     * Empties all the flight queues and
     * reorders the flights among all open runways
     * @return
     * @throws RemoteException
     */
    ReorderFlightsResponseDTO reorderFlights() throws RemoteException;
}
