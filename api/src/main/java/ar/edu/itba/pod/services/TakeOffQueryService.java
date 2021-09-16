package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.FlightDetailsDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public interface TakeOffQueryService extends Remote {
    /**
     * Returns all historical departures
     * @return
     */
    List<FlightDetailsDTO> getAllTakeoffs() throws RemoteException;

    /**
     * Returns all historical departures in a certain runway
     * @param runway
     * @return
     */
    List<FlightDetailsDTO> getTakeoffsByRunway(String runway) throws RemoteException;

    /**
     * Returns all historical departures in a certain airline
     * @param airline
     * @return
     */
    List<FlightDetailsDTO> getTakeoffsByAirline(String airline) throws RemoteException;


    /**
     * Returns the quantity of runways created (open and closed)
     * @return
     */
    long getRunwaysQuantity();

    /**
     * Returns the queantity of flights created
     * @return
     */
    long getFlightsQuantity();

    /**
     * Returns the quantity of registers for a particular flight
     * @param flightCode
     * @return
     */
    long getRegisterQuantityForFlight(int flightCode);
}
