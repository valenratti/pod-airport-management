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

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AirportManagementImpl implements FlightTrackingService, ManagementService, RunwayService, TakeOffQueryService {
    private final Map<Runway, Queue<Flight>> runwayQueueMap;
    private final Map<Flight, List<FlightEventsCallbackHandler>> flightSubscriptions;
    private final List<FlightDetailsDTO> flightDetailsDTOS;

    private final Comparator<Runway> runwayComparator;

    private static final Object registerLock = "resgisterLock", addRunwayLock = "addRunwayLock", takeOffLock = "takeOffLock",
            requireRunwayLock = "requireRunwayLock", assignFlightToRunwayLock = "assignFlightToRunwayLock";
    private static final ReadWriteLock reorderFlightsLock = new ReentrantReadWriteLock();

    public AirportManagementImpl() {
        this.runwayQueueMap = new HashMap<>();
        this.flightSubscriptions = new ConcurrentHashMap<>();
        this.flightDetailsDTOS = new ArrayList<>();
        this.runwayComparator = (o1, o2) -> {
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
    }

    /* FlightTrackingService */
    
    @Override
    public void registerForFlight(String airlineName, int flightCode, FlightEventsCallbackHandler callbackHandler) throws RemoteException, FlightNotFromAirlineException, FlightNotInQueueException {
        synchronized (registerLock) {
            Optional<Flight> optionalFlight = flightSubscriptions.keySet().stream().filter(flight->flight.getId() == flightCode).findFirst();
            if(optionalFlight.isPresent()){
                final Flight flight = optionalFlight.get();
                if(flight.getAirlineName().equals(airlineName)) {
                    flightSubscriptions.get(flight).add(callbackHandler);
                    this.informAssignationToSubscriber(flight, callbackHandler);
                }
                else
                    throw new FlightNotFromAirlineException(flightCode, airlineName);
            }else
                throw new FlightNotInQueueException(flightCode);
        }
    }

    private void informAssignationToSubscriber(Flight flight, FlightEventsCallbackHandler callbackHandler){
        int flightsAhead = new ArrayList<>(runwayQueueMap.get(flight.getCurrentRunway())).indexOf(flight);
        if(callbackHandler != null) {
            new Thread(() -> {
                try {
                    callbackHandler.flightAssignedToRunway(flight.getId(), flight.getCurrentRunway().getName(), flightsAhead, flight.getDestinationAirportCode());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void informDepartureFromQueueToAllSubscribers(Queue<Flight> queue) throws InterruptedException {
        for(Flight flight : queue) {
            ExecutorService pool = Executors.newFixedThreadPool(10);
            for (FlightEventsCallbackHandler callbackHandler : flightSubscriptions.get(flight)) {
                int flightsAhead = new ArrayList<>(runwayQueueMap.get(flight.getCurrentRunway())).indexOf(flight);
                pool.submit(() -> {
                    try {
                        callbackHandler.flightChangedPositionInQueue(flight.getId(), flight.getCurrentRunway().getName(), flightsAhead, flight.getDestinationAirportCode());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void informDepartureOfFlight(Flight flight){
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for(FlightEventsCallbackHandler callbackHandler : flightSubscriptions.get(flight)){
            int flightsAhead = new ArrayList<>(runwayQueueMap.get(flight.getCurrentRunway())).indexOf(flight);
            pool.submit( () -> {
                try {
                    callbackHandler.flightAssignedToRunway(flight.getId(), flight.getCurrentRunway().getName(), flightsAhead, flight.getDestinationAirportCode());
                } catch(RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /* ManagementService */

    @Override
    public void addRunway(String runwayName, RunwayCategory category) throws RemoteException, DuplicateRunwayException {
        synchronized (addRunwayLock) {
            try {
                reorderFlightsLock.readLock().lock();

                Runway runway = new Runway(runwayName, category);
                if (runwayQueueMap.containsKey(runway)) {
                    throw new DuplicateRunwayException(runwayName);
                }
                runwayQueueMap.put(runway, new ConcurrentLinkedQueue<>());
            }
            finally {
                reorderFlightsLock.readLock().unlock();
            }
        }
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
        try {
            reorderFlightsLock.readLock().lock();

            Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
            if (optionalRunway.isPresent()) {
                Runway runway = optionalRunway.get();
                synchronized (optionalRunway.get()) {
                    if (runway.isOpen()) {
                        throw new IllegalStateException("Runway is already open!");
                    } else {
                        runway.setOpen(true);
                        return true;
                    }
                }
            } else {
                throw new NoSuchElementException("The runway expected does not exist!");
            }
        }
        finally {
            reorderFlightsLock.readLock().unlock();
        }
    }

    public boolean openRunwayForTest(String runwayName) throws RemoteException, InterruptedException {
        Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
        if(optionalRunway.isPresent()){
            Runway runway = optionalRunway.get();
            synchronized (optionalRunway.get()) {
                if (runway.isOpen()) {
                    throw new IllegalStateException("Runway is already open!");
                } else {
                    Thread.sleep(1000);
                    runway.setOpen(true);
                    return true;
                }
            }
        }else{
            throw new NoSuchElementException("The runway expected does not exist!");
        }
    }

    @Override
    public boolean closeRunway(String runwayName) throws RemoteException {
        try {
            reorderFlightsLock.readLock().lock();

            Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
            if (optionalRunway.isPresent()) {
                Runway runway = optionalRunway.get();
                synchronized (optionalRunway.get()) {
                    if (!runway.isOpen()) {
                        throw new IllegalStateException("Runway is already closed!");
                    } else {
                        runway.setOpen(false);
                        return true;
                    }
                }
            } else {
                throw new NoSuchElementException("The runway expected does not exist!");
            }
        }
        finally {
            reorderFlightsLock.readLock().unlock();
        }
    }

    public boolean closeRunwayForTest(String runwayName) throws RemoteException, InterruptedException {
        Optional<Runway> optionalRunway = runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(runwayName)).findFirst();
        if(optionalRunway.isPresent()){
            Runway runway = optionalRunway.get();
            synchronized (optionalRunway.get()) {
                if (!runway.isOpen()) {
                    throw new IllegalStateException("Runway is already closed!");
                } else {
                    Thread.sleep(1000);
                    runway.setOpen(false);
                    return true;
                }
            }
        }else{
            throw new NoSuchElementException("The runway expected does not exist!");
        }
    }

    @Override
    public void takeOff() throws RemoteException {
        synchronized (takeOffLock) {
            try {
                reorderFlightsLock.readLock().lock();

                runwayQueueMap.keySet().stream().filter(Runway::isOpen).forEach((runway) -> {
                    Queue<Flight> runwayQueue = runwayQueueMap.get(runway);
                    Flight dispatched = runwayQueue.poll();
                    if (dispatched != null) {
                        flightDetailsDTOS.add(new FlightDetailsDTO(dispatched.getId(), dispatched.getDestinationAirportCode(), dispatched.getAirlineName(), dispatched.getCategory(), dispatched.getTakeOffCounter(), runway.getName(), runway.getCategory(), runway.isOpen()));
                        try {
                            this.informDepartureFromQueueToAllSubscribers(runwayQueue);
                            this.informDepartureOfFlight(dispatched);
                            this.closeSubscriptionsToFlight(dispatched);
                            flightSubscriptions.remove(dispatched);
                        } catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    for (Flight flightInQueue : runwayQueue) {
                        flightInQueue.setTakeOffCounter(flightInQueue.getTakeOffCounter() + 1);
                    }
                });
            }
            finally {
                reorderFlightsLock.readLock().unlock();
            }
        }
    }

    private void closeSubscriptionsToFlight(Flight flight){
        flightSubscriptions.get(flight).forEach((c) -> {
            try {
                UnicastRemoteObject.unexportObject(c,false);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public ReorderFlightsResponseDTO reorderFlights() throws RemoteException {
        try {
            reorderFlightsLock.writeLock().lock();

            Queue<Flight> flightsToReorder = new LinkedList<>();
            boolean runwaysAreEmpty = false;
            while (!runwaysAreEmpty) {
                runwaysAreEmpty = true;
                for (Runway runway : runwayQueueMap.keySet()) {
                    Queue<Flight> flights = runwayQueueMap.get(runway);
                    //Validate that there still are flights in runway
                    if (flights.size() > 0) {
                        flightsToReorder.add(flights.poll());
                        //Validate that after polling one, it still has something
                        if (flights.size() > 0)
                            runwaysAreEmpty = false;
                    }
                }
            }
            ReorderFlightsResponseDTO reorderFlightsResponseDTO = new ReorderFlightsResponseDTO();
            while (flightsToReorder.size() > 0) {
                Flight flight = flightsToReorder.poll();
                if (assignFlightToRunwayIfPossible(flight)) {
                    reorderFlightsResponseDTO.setAssignedFlightsQty(reorderFlightsResponseDTO.getAssignedFlightsQty() + 1);
                    this.informReorderToSubscribers(flight);
                } else {
                    reorderFlightsResponseDTO.getNotAssignedFlights().add(flight.getId());
                }
            }
            return reorderFlightsResponseDTO;
        }
        finally {
            reorderFlightsLock.writeLock().unlock();
        }
    }

    private void informReorderToSubscribers(Flight flight){
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for(FlightEventsCallbackHandler callbackHandler : flightSubscriptions.get(flight)){
            int flightsAhead = new ArrayList<>(runwayQueueMap.get(flight.getCurrentRunway())).indexOf(flight);
            pool.submit( () -> {
                try {
                    callbackHandler.flightAssignedToRunway(flight.getId(), flight.getCurrentRunway().getName(), flightsAhead, flight.getDestinationAirportCode());
                } catch(RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public ReorderFlightsResponseDTO reorderFlightsForTest() throws RemoteException, InterruptedException {
        try {
            reorderFlightsLock.writeLock().lock();

            Queue<Flight> flightsToReorder = new LinkedList<>();
            boolean runwaysAreEmpty = false;
            while (!runwaysAreEmpty) {
                runwaysAreEmpty = true;
                for (Runway runway : runwayQueueMap.keySet()) {
                    Queue<Flight> flights = runwayQueueMap.get(runway);
                    //Validate that there still are flights in runway
                    if (flights.size() > 0) {
                        flightsToReorder.add(flights.poll());
                        //Validate that after polling one, it still has something
                        if (flights.size() > 0)
                            runwaysAreEmpty = false;
                    }
                }
            }
            ReorderFlightsResponseDTO reorderFlightsResponseDTO = new ReorderFlightsResponseDTO();
            while (flightsToReorder.size() > 0) {
                Flight flight = flightsToReorder.poll();
                if (assignFlightToRunwayIfPossible(flight)) {
                    reorderFlightsResponseDTO.setAssignedFlightsQty(reorderFlightsResponseDTO.getAssignedFlightsQty() + 1);
                } else {
                    reorderFlightsResponseDTO.getNotAssignedFlights().add(flight.getId());
                }
                Thread.sleep(100);
            }
            return reorderFlightsResponseDTO;
        }
        finally {
            reorderFlightsLock.writeLock().unlock();
        }
    }


    /* RunwayService */

    @Override
    public boolean requireRunway(int flightCode, String destinationAirport, String airlineName, RunwayCategory minCategory) throws RemoteException {
        try {
            reorderFlightsLock.readLock().lock();

            synchronized (requireRunwayLock) {
                runwayQueueMap.values().forEach((queue) -> queue.forEach((flight -> {
                    if (flight.getId() == flightCode)
                        throw new DuplicatedFlightException((String.valueOf(Integer.valueOf(flightCode))));
                })));
            }

            final Flight flight = new Flight(flightCode, destinationAirport, airlineName, minCategory);
            if (assignFlightToRunwayIfPossible(flight)) {
                flightSubscriptions.put(flight, new ArrayList<>());
                return true;
            }else{
                return false;
            }
        }
        finally {
            reorderFlightsLock.readLock().unlock();
        }
    }


    private boolean assignFlightToRunwayIfPossible(Flight flight) {
        synchronized (assignFlightToRunwayLock){
            Optional<Runway> toBeAddedIn = runwayQueueMap.keySet().stream()
                    .filter((r) -> r.isOpen() && r.getCategory().compareTo(flight.getCategory()) >= 0)
                    .min(runwayComparator);
            if(toBeAddedIn.isPresent()) {
                runwayQueueMap.get(toBeAddedIn.get()).add(flight);
                flight.setCurrentRunway(toBeAddedIn.get());
                return true;
            }else{
                return false;
            }
        }
    }


    /* TakeOffQueryService */

    @Override
    public List<FlightDetailsDTO> getAllTakeoffs() throws RemoteException {
        return flightDetailsDTOS;
    }

    @Override
    public List<FlightDetailsDTO> getTakeoffsByRunway(String runway) throws RemoteException{
        return flightDetailsDTOS.stream().filter((dto) -> dto.getRunwayName().equals(runway))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDetailsDTO> getTakeoffsByAirline(String airline) throws RemoteException {
        return flightDetailsDTOS.stream().filter((dto) -> dto.getAirlineName().equals(airline))
                .collect(Collectors.toList());
    }

    public Queue<Flight> getQueueForRunway(String name){
        return runwayQueueMap.get(runwayQueueMap.keySet().stream().filter((r) -> r.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new));
    }

    @Override
    public long getRunwaysQuantity(){
        return runwayQueueMap.values().stream().mapToLong(Collection::size).count();
    }

    @Override
    public long getFlightsQuantity(){
        return runwayQueueMap.values().stream().map(Collection::size).reduce(0, Integer::sum);
    }

    @Override
    public long getRegisterQuantityForFlight(int flightCode){
        Optional<Flight> optionalFlight = flightSubscriptions.keySet().stream().filter(flight->flight.getId() == flightCode).findFirst();
        if(optionalFlight.isPresent()) {
            final Flight flight = optionalFlight.get();
            return flightSubscriptions.get(flight).size();
        }else{
            return 0;
        }
    }

}
