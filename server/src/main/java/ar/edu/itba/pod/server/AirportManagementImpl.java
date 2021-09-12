package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.exceptions.DuplicatedFlightException;
import ar.edu.itba.pod.exceptions.FlightNotFromAirlineException;
import ar.edu.itba.pod.exceptions.FlightNotInQueueException;
import ar.edu.itba.pod.models.ReorderFlightsResponseDTO;
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
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class AirportManagementImpl implements FlightTrackingService, ManagementService, RunwayService, TakeOffQueryService {
    private Map<Runway, Queue<Flight>> runwayQueueMap;
    private Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions;
    private List<FlightDetailsDTO> flightDetailsDTOS;
    private static AirportManagementImpl singletonInstance;

    public AirportManagementImpl() {
        this.runwayQueueMap = new HashMap<>();
        this.flightSubscriptions = new HashMap<>();
        this.flightDetailsDTOS = new ArrayList<>();
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
    public synchronized void addRunway(String runwayName, RunwayCategory category) throws RemoteException, DuplicateRunwayException {
        Runway runway = new Runway(runwayName, category);
        if(runwayQueueMap.containsKey(runway)) {
            throw new DuplicateRunwayException(runwayName);
        }
        runwayQueueMap.put(runway, new ConcurrentLinkedDeque<>());
    }

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
        runwayQueueMap.keySet().stream().filter(Runway::isOpen).forEach( (runway) -> {
            Queue<Flight> runwayQueue = runwayQueueMap.get(runway);
            Flight dispatched = runwayQueue.poll();
            if (dispatched != null) {
                flightDetailsDTOS.add(new FlightDetailsDTO(dispatched.getId(), dispatched.getDestinationAirportCode(), dispatched.getAirlineName(), dispatched.getCategory(), dispatched.getTakeOffCounter(), runway.getName(), runway.getCategory(), runway.isOpen()));
            }
            for (Flight flightInQueue : runwayQueue) {
                flightInQueue.setTakeOffCounter(flightInQueue.getTakeOffCounter() + 1);
            }
        });
    }

    @Override
    public ReorderFlightsResponseDTO reorderFlights() throws RemoteException {
        Queue<Flight> flightsToReorder = new LinkedList<>();
        boolean runwaysAreEmpty = false;
        while(!runwaysAreEmpty) {
            runwaysAreEmpty = true;
            for (Runway runway : runwayQueueMap.keySet()) {
                Queue<Flight> flights = runwayQueueMap.get(runway);
                //Validate that there still are flights in runway
                if(flights.size() > 0) {
                    flightsToReorder.add(flights.poll());
                    //Validate that after polling one, it still has something
                    if(flights.size() > 0)
                        runwaysAreEmpty = false;
                }
            }
        }
        ReorderFlightsResponseDTO reorderFlightsResponseDTO = new ReorderFlightsResponseDTO();
        while (flightsToReorder.size() > 0) {
            Flight flight = flightsToReorder.poll();
            if(assignFlightToRunwayIfPossible(flight)){
                reorderFlightsResponseDTO.setAssignedFlightsQty( reorderFlightsResponseDTO.getAssignedFlightsQty() + 1);
            }else{
                reorderFlightsResponseDTO.getNotAssignedFlights().add(flight.getId());
            }
        }
        return reorderFlightsResponseDTO;
    }


    /* RunwayService */

    @Override
    public void requireRunway(int flightCode, String destinationAirport, String airlineName, RunwayCategory minCategory) throws RemoteException {
        runwayQueueMap.values().forEach((queue) -> {
            queue.forEach((flight -> {
                if(flight.getId() == flightCode)
                    throw new DuplicatedFlightException((String.valueOf(Integer.valueOf(flightCode))));
            }));
        });
        final Flight flight = new Flight(flightCode, destinationAirport, airlineName, minCategory);
        assignFlightToRunwayIfPossible(flight);
    }


    private boolean assignFlightToRunwayIfPossible(Flight flight) {
        final Comparator<Runway> runwayComparator = (o1, o2) -> {
            int sizeComparation = runwayQueueMap.get(o1).size() - runwayQueueMap.get(o2).size();
            if(sizeComparation != 0){
                return sizeComparation;
            }else{
                int categoryComparation = o1.getCategory().ordinal() - o2.getCategory().ordinal();
                if(categoryComparation != 0) {
                    return categoryComparation;
                } else{
                    return o1.getName().compareTo(o2.getName());
                }
            }
        };

        Optional<Runway> toBeAddedIn = runwayQueueMap.keySet().stream()
                .filter((r) -> r.isOpen() && r.getCategory().compareTo(flight.getCategory()) >= 0)
                .min(runwayComparator);
        if(toBeAddedIn.isPresent()) {
            runwayQueueMap.get(toBeAddedIn.get()).add(flight);
            return true;
        }else{
            return false;
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

    public Queue<Flight> getQueueForRunway(String name){
        return runwayQueueMap.get(runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(name)).findFirst().orElseThrow(() -> new NoSuchElementException()));
    }

    public long getRunwaysQuantity(){
        return runwayQueueMap.values().stream().mapToLong(Collection::size).count();
    }

}
