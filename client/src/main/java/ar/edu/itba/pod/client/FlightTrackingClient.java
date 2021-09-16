package ar.edu.itba.pod.client;

import ar.edu.itba.pod.callbacks.FlightEventsCallbackHandler;
import ar.edu.itba.pod.services.FlightTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FlightTrackingClient {
    private static final Logger logger = LoggerFactory.getLogger(FlightTrackingClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        logger.info("Starting flight tracking client...");

        // TODO: Funcion null or empty

        String serverAddress = System.getProperty("serverAddress");
        if (Utils.isNullOrEmpty(serverAddress))
            logger.error("You must provide the server address");
        else {
            FlightTrackingService service = (FlightTrackingService) Naming.lookup("//" + serverAddress +  "/tracker");

            String airlineName = System.getProperty("airline");
            String flightCode = System.getProperty("flightCode");

            if (Utils.isNullOrEmpty(airlineName) || Utils.isNullOrEmpty(flightCode)) {
                logger.error("You must provide the airline name AND the flight code");
                return;
            }
            FlightEventsCallbackHandler handler = new FlightEventsCallbackHandlerImpl();
            try {
                service.registerForFlight(airlineName, Integer.parseInt(flightCode), handler);
            } catch (NumberFormatException e) {
                logger.error("Flight code must be an integer number");
                UnicastRemoteObject.unexportObject(handler,true);
            } catch (RuntimeException e) {
                logger.error(e.getMessage());
                UnicastRemoteObject.unexportObject(handler,true);
            }
        }
    }
}
