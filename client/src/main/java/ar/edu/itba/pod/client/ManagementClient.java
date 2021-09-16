package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.models.ReorderFlightsResponseDTO;
import ar.edu.itba.pod.models.RunwayCategory;
import ar.edu.itba.pod.services.ManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ManagementClient {
    private static final Logger logger = LoggerFactory.getLogger(ManagementClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        String serverAddress = System.getProperty("serverAddress");
        if (Utils.isNullOrEmpty(serverAddress))
            logger.error("You must provide a server address");
        else {
            ManagementService service = (ManagementService) Naming.lookup("//" + serverAddress + "/management");

            String actionName = System.getProperty("action");

            if (Utils.isNullOrEmpty(actionName)) {
                logger.error("You must provide an action");
                return;
            }
            String minCategory, runwayName;

            switch (actionName) {
                case "add":
                    minCategory = System.getProperty("category");
                    runwayName = System.getProperty("runway");
                    if (Utils.isNullOrEmpty(minCategory) || Utils.isNullOrEmpty(runwayName)) {
                        logger.error("You must provide category and runway with action {} ", actionName);
                        break;
                    }
                    try {
                        RunwayCategory runwayCategory = RunwayCategory.valueOf(minCategory.toUpperCase());
                        service.addRunway(runwayName, runwayCategory);
                        System.out.println("Runway " + runwayName + " is open");
                    } catch (IllegalArgumentException e) {
                        logger.error("There is no category with that name");
                    } catch (DuplicateRunwayException e) {
                        logger.error(e.getMessage());
                    }
                    break;
                case "open":
                    runwayName = System.getProperty("runway");
                    if (Utils.isNullOrEmpty(runwayName)) {
                        logger.error("You must provide the Runway name with action {}", actionName);
                        break;
                    }
                    try {
                        service.openRunway(runwayName);
                        System.out.println("Runway " + runwayName + " is now open");
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                    break;
                case "close":
                    runwayName = System.getProperty("runway");
                    if (Utils.isNullOrEmpty(runwayName)) {
                        logger.error("You must provide the Runway name with action {}", actionName);
                        break;
                    }
                    try {
                        service.closeRunway(runwayName);
                        System.out.println("Runway " + runwayName + " is now close");
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                    break;
                case "status":
                    runwayName = System.getProperty("runway");
                    if (Utils.isNullOrEmpty(runwayName)) {
                        logger.error("You must provide the Runway name with action {}", actionName);
                        break;
                    }
                    boolean status = service.getRunwayStatus(runwayName);
                    System.out.println("Runway " + runwayName + " is " + (status?"open":"close"));
                    break;
                case "takeOff":
                    service.takeOff();
                    System.out.println("Flights took off successfully");
                    break;
                case "reorder":
                    ReorderFlightsResponseDTO response = service.reorderFlights();
                    response.getNotAssignedFlights().forEach(flightCode -> System.out.println("Cannot assign Flight " + flightCode));
                    System.out.println(response.getAssignedFlightsQty() + " flights assigned");
                    break;
                default:
                    logger.error("Invalid action {}", actionName);
                    break;
            }
        }
    }
}
