package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.exceptions.FlightNotFromAirlineException;
import ar.edu.itba.pod.exceptions.FlightNotInQueueException;
import ar.edu.itba.pod.models.RunwayCategory;
import ar.edu.itba.pod.server.model.Flight;
import ar.edu.itba.pod.models.FlightDetailsDTO;
import ar.edu.itba.pod.server.model.Runway;
import ar.edu.itba.pod.services.FlightTrackingService;
import ar.edu.itba.pod.services.ManagementService;
import ar.edu.itba.pod.services.RunwayService;
import ar.edu.itba.pod.services.TakeOffQueryService;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class AirportManagementImpl implements FlightTrackingService, ManagementService, RunwayService, TakeOffQueryService {
    private Map<Runway, Queue<Flight>> runwayQueueMap;
    private Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions;
    private List<FlightDetailsDTO> flightDetailsDTOS;
    private static AirportManagementImpl singletonInstance;

    private AirportManagementImpl(Map<Runway, Queue<Flight>> runwayQueueMap, Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions, List<FlightDetailsDTO> flightDetailsDTOS) {
        this.runwayQueueMap = runwayQueueMap;
        this.flightSubscriptions = flightSubscriptions;
        this.flightDetailsDTOS = flightDetailsDTOS;
    }

    // TODO: Se puede eliminar?
    public static AirportManagementImpl getInstance(){
        if(singletonInstance == null)
            singletonInstance = new AirportManagementImpl(new HashMap<>(), new HashMap<>(), new ArrayList<>());

        return singletonInstance;
    }

    /* FlightTrackingService */
    
    @Override
    public void registerForFlight(String airlineName, int flightCode, FlightEventsCallbackHandler callbackHandler) throws RemoteException, FlightNotFromAirlineException, FlightNotInQueueException {
        Optional<Flight> optionalFlight = flightSubscriptions.keySet().stream().filter(flight->flight.getId() == flightCode).findFirst();
        if(optionalFlight.isPresent()){
            final Flight flight = optionalFlight.get();
            if(flight.getAirlineName().equals(airlineName))
                flightSubscriptions.get(flight).add(callbackHandler);
            else
                throw new FlightNotFromAirlineException(flightCode, airlineName);
        }else
            throw new FlightNotInQueueException(flightCode);
    }


    /* ManagementService */

    @Override
    public void addRunway(String runwayName, RunwayCategory category) throws RemoteException {
        Runway runway = new Runway(runwayName, category);
        if(runwayQueueMap.containsKey(runway))
            throw new DuplicateRunwayException(runwayName);

        runwayQueueMap.put(runway, new LinkedList<>());    }

    @Override
    public boolean getRunwayStatus(String runwayName) throws RemoteException {
        Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
        if(optionalRunway.isPresent()){
            return optionalRunway.get().isOpen();
        }else{
            throw new NoSuchElementException("The runway expected does not exist!");
        }
    }

    @Override
    public boolean openRunway(String runwayName) throws RemoteException {
        Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
        if(optionalRunway.isPresent()){
            Runway runway = optionalRunway.get();
            if(runway.isOpen()){
                throw new IllegalStateException("Runway is already open!");
            }else{
                runway.setOpen(true);
                return true;
            }
        }else{
            throw new NoSuchElementException("The runway expected does not exist!");
        }
    }

    @Override
    public boolean closeRunway(String runwayName) throws RemoteException {
        Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
        if(optionalRunway.isPresent()){
            Runway runway = optionalRunway.get();
            if(!runway.isOpen()){
                throw new IllegalStateException("Runway is already closed!");
            }else{
                runway.setOpen(false);
                return true;
            }
        }else{
            throw new NoSuchElementException("The runway expected does not exist!");
        }
    }

    @Override
    public void takeOff() throws RemoteException {
        for(Runway runway : runwayQueueMap.keySet()) {
            if(runway.isOpen()) {
                Queue<Flight> runwayQueue = runwayQueueMap.get(runway);
                Flight dispatched = runwayQueue.poll();
                if (dispatched != null) {
                    flightDetailsDTOS.add(new FlightDetailsDTO(dispatched.getId(), dispatched.getDestinationAirportCode(), dispatched.getAirlineName(), dispatched.getCategory(), dispatched.getTakeOffCounter(), runway.getName(), runway.getCategory(), runway.isOpen()));
                }
                for (Flight flightInQueue : runwayQueue) {
                    flightInQueue.setTakeOffCounter(flightInQueue.getTakeOffCounter() + 1);
                }
            }
        }
    }

    @Override
    public void reorderFlights() throws RemoteException {
        //TODO faltan guardar cuales vuelos no se pudieron reordenar y contar la cantidad de reordenados.
        Queue<Flight> flightsToReorder = new LinkedList<>();
        boolean runwaysAreEmpty = false, isEmpty = true;
        while(!runwaysAreEmpty) {
            runwaysAreEmpty = true;
            for (Runway runway : runwayQueueMap.keySet()) {
                Queue<Flight> flights = runwayQueueMap.get(runway);
                if(flights.size() > 0)
                    flightsToReorder.add(flights.poll());
                if(flights.size() > 0)
                    runwaysAreEmpty = false;
            }
        }
        for(Flight flight : flightsToReorder) {
            assignFlightToRunwayIfPossible(flight);
        }
    }


    /* RunwayService */

    @Override
    public void requireRunway(int flightCode, String destinationAirport, String airlineName, RunwayCategory minCategory) throws RemoteException {
        final Flight flight = new Flight(flightCode, destinationAirport, airlineName, minCategory);
        assignFlightToRunwayIfPossible(flight);
    }


    private boolean assignFlightToRunwayIfPossible(Flight flight) {
        //TODO contar los vuelos asignados y guardar los que no se pudieron asignar
        Runway finalRunway = null;
        for(Runway runway : runwayQueueMap.keySet()) {
            if(runway.isOpen() && runway.getCategory().compareTo(flight.getCategory()) >= 0) {
                if(finalRunway != null) {
                    int runwayQueueSize = runwayQueueMap.get(runway).size();
                    int finalRunwayQueueSize = runwayQueueMap.get(finalRunway).size();
                    if(runwayQueueSize < finalRunwayQueueSize) {
                        finalRunway = runway;
                    } else if (runwayQueueSize == finalRunwayQueueSize) {
                        int comparation = runway.getCategory().compareTo(finalRunway.getCategory());
                        if(comparation < 0) {
                            finalRunway = runway;
                        } else if (comparation == 0) {
                            if(runway.getName().compareTo(finalRunway.getName()) < 0)
                                finalRunway = runway;
                        }
                    }
                } else {
                    finalRunway = runway;
                }
            }
        }

        if(finalRunway == null) {
            //TODO Lanzar un ERROR segun la consigna.
            return false;
        } else {
            runwayQueueMap.get(finalRunway).add(flight);
            return true;
        }
    }


    /* TakeOffQueryService */

    @Override
    public List<FlightDetailsDTO> getAllTakeoffs() {
        return flightDetailsDTOS;
    }

    @Override
    public List<FlightDetailsDTO> getTakeoffsByRunway(String runway) {
        return flightDetailsDTOS.stream().filter((dto) -> dto.getRunwayName().equals(runway))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDetailsDTO> getTakeoffsByAirline(String airline) {
        return flightDetailsDTOS.stream().filter((dto) -> dto.getAirlineName().equals(airline))
                .collect(Collectors.toList());
    }


}
