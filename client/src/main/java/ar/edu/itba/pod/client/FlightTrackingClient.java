package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.services.FlightTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class FlightTrackingClient {
    private static final Logger logger = LoggerFactory.getLogger(FlightTrackingClient.class);

    public static void main(String[] args) throws RemoteException {
        logger.info("Starting flight tracking client...");
        //TODO: RMI
        FlightTrackingService service = null;
        FlightEventsCallbackHandler handler = new FlightEventsCallbackHandlerImpl();

        String airlineName = System.getProperty("airlineName");
        String flightCode = System.getProperty("flightCode");

        if(airlineName.isEmpty() || flightCode.isEmpty()){
            logger.error("You must provide the airline name AND the flight code");
            return;
        }
        try {
            service.registerForFlight(airlineName, Integer.parseInt(flightCode), handler);
        } catch (NumberFormatException e){
            logger.error("Flight code must be an integer number");
        } catch (RuntimeException e){
            logger.error(e.getMessage());
        }
    }
}
