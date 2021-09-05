package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.model.Flight;
import ar.edu.itba.pod.server.model.FlightDetailsDTO;
import ar.edu.itba.pod.server.model.Runaway;
import ar.edu.itba.pod.server.model.RunawayStatus;

import java.util.*;
import java.util.stream.Collectors;

public class AirportDataManagement {
    private Map<Runaway, Queue<Flight>> runawayQueueMap;
    private Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions;
    private List<FlightDetailsDTO> flightDetailsDTOS;
    private static AirportDataManagement singletonInstance;

    private AirportDataManagement(Map<Runaway, Queue<Flight>> runawayQueueMap, Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions, List<FlightDetailsDTO> flightDetailsDTOS) {
        this.runawayQueueMap = runawayQueueMap;
        this.flightSubscriptions = flightSubscriptions;
        this.flightDetailsDTOS = flightDetailsDTOS;
    }

    public static AirportDataManagement getInstance(){
        if(singletonInstance == null)
            return new AirportDataManagement(new HashMap<>(), new HashMap<>(), new ArrayList<>());
        else return singletonInstance;
    }

    public boolean assignFlightToRunawayIfPossible(Flight flight) {
        //TODO: Implement
    }

    public void dispatchFlights(){
        for(Runaway runaway : runawayQueueMap.keySet()) {
            Queue<Flight> runawayQueue = runawayQueueMap.get(runaway);
            Flight dispatched = runawayQueue.poll();
            if(dispatched != null){
                flightDetailsDTOS.add(new FlightDetailsDTO(dispatched.getId(), dispatched.getDestinationAirportCode(), dispatched.getAirlineName(), dispatched.getCategory(), dispatched.getTakeOffCounter(), runaway.getName(), runaway.getCategory(), runaway.isOpen()));
            }
            for(Flight flightInQueue : runawayQueue){
                flightInQueue.setTakeOffCounter(flightInQueue.getTakeOffCounter() + 1);
            }
        }
    }

    public void reorderFlights(){
        //TODO: Implement
    }

    public boolean openRunaway(String runawayName){
        Optional<Runaway> optionalRunaway = runawayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runawayName)).findFirst();
        if(optionalRunaway.isPresent()){
            Runaway runaway = optionalRunaway.get();
            if(runaway.isOpen()){
                throw new IllegalStateException("Runaway is already open!");
            }else{
                runaway.setOpen(true);
                return true;
            }
        }else{
            throw new NoSuchElementException("The runaway expected does not exist!");
        }
    }

    public boolean closeRunaway(String runawayName){
        Optional<Runaway> optionalRunaway = runawayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runawayName)).findFirst();
        if(optionalRunaway.isPresent()){
            Runaway runaway = optionalRunaway.get();
            if(!runaway.isOpen()){
                throw new IllegalStateException("Runaway is already closed!");
            }else{
                runaway.setOpen(false);
                return true;
            }
        }else{
            throw new NoSuchElementException("The runaway expected does not exist!");
        }
    }

    public RunawayStatus consultRunawayStatus(String runawayName){
        Optional<Runaway> optionalRunaway = runawayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runawayName)).findFirst();
        if(optionalRunaway.isPresent()){
            return optionalRunaway.get().isOpen() ? RunawayStatus.OPEN : RunawayStatus.CLOSED;
        }else{
            throw new NoSuchElementException("The runaway expected does not exist!");
        }
    }

    public List<FlightDetailsDTO> getAllFlightsDepartures(){
        return flightDetailsDTOS;
    }

    public List<FlightDetailsDTO> getAllFlightsDeparturesByRunaway(String runawayName){
        return flightDetailsDTOS.stream().filter((dto) -> dto.getRunawayName().equals(runawayName))
                .collect(Collectors.toList());
    }

    public List<FlightDetailsDTO> getAllFlightsDeparturesByAirline(String airlineName){
        return flightDetailsDTOS.stream().filter((dto) -> dto.getAirlineName().equals(airlineName))
                .collect(Collectors.toList());
    }


}
