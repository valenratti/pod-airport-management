package ar.edu.itba.pod.server;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.models.RunwayCategory;
import ar.edu.itba.pod.server.model.Flight;
import ar.edu.itba.pod.models.FlightDetailsDTO;
import ar.edu.itba.pod.server.model.Runway;

import java.util.*;
import java.util.stream.Collectors;

public class AirportDataManagement {
    private Map<Runway, Queue<Flight>> runwayQueueMap;
    private Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions;
    private List<FlightDetailsDTO> flightDetailsDTOS;
    private static AirportDataManagement singletonInstance;

    private AirportDataManagement(Map<Runway, Queue<Flight>> runwayQueueMap, Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions, List<FlightDetailsDTO> flightDetailsDTOS) {
        this.runwayQueueMap = runwayQueueMap;
        this.flightSubscriptions = flightSubscriptions;
        this.flightDetailsDTOS = flightDetailsDTOS;
    }

    public static AirportDataManagement getInstance(){
        if(singletonInstance == null)
            singletonInstance = new AirportDataManagement(new HashMap<>(), new HashMap<>(), new ArrayList<>());

        return singletonInstance;
    }

    public boolean assignFlightToRunwayIfPossible(Flight flight) {
        //TODO: Implement
        return false;
    }

    public void dispatchFlights(){
        for(Runway runway : runwayQueueMap.keySet()) {
            Queue<Flight> runwayQueue = runwayQueueMap.get(runway);
            Flight dispatched = runwayQueue.poll();
            if(dispatched != null){
                flightDetailsDTOS.add(new FlightDetailsDTO(dispatched.getId(), dispatched.getDestinationAirportCode(), dispatched.getAirlineName(), dispatched.getCategory(), dispatched.getTakeOffCounter(), runway.getName(), runway.getCategory(), runway.isOpen()));
            }
            for(Flight flightInQueue : runwayQueue){
                flightInQueue.setTakeOffCounter(flightInQueue.getTakeOffCounter() + 1);
            }
        }
    }

    public void reorderFlights(){
        //TODO: Implement
    }

    public void addRunway(String runwayName, RunwayCategory runwayCategory){
        Runway runway = new Runway(runwayName, runwayCategory);
        if(runwayQueueMap.containsKey(runway))
            throw new DuplicateRunwayException(runwayName);

        runwayQueueMap.put(runway, new LinkedList<>());
    }

    public boolean openRunway(String runwayName){
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

    public boolean closeRunway(String runwayName){
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

    public boolean getRunwayStatus(String runwayName){
        Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
        if(optionalRunway.isPresent()){
            return optionalRunway.get().isOpen();
        }else{
            throw new NoSuchElementException("The runway expected does not exist!");
        }
    }

    public List<FlightDetailsDTO> getAllFlightsDepartures(){
        return flightDetailsDTOS;
    }

    public List<FlightDetailsDTO> getAllFlightsDeparturesByRunway(String runwayName){
        return flightDetailsDTOS.stream().filter((dto) -> dto.getRunwayName().equals(runwayName))
                .collect(Collectors.toList());
    }

    public List<FlightDetailsDTO> getAllFlightsDeparturesByAirline(String airlineName){
        return flightDetailsDTOS.stream().filter((dto) -> dto.getAirlineName().equals(airlineName))
                .collect(Collectors.toList());
    }


}
