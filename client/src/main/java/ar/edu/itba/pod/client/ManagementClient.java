package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
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
        logger.info("Starting management client...");

        // TODO: Funcion null or empty

        String serverAddress = System.getProperty("serverAddress");
        if (serverAddress == null || serverAddress.isEmpty())
            logger.error("You must provide a server address");
        else {
            ManagementService service = (ManagementService) Naming.lookup("//" + serverAddress + "/management");

            String actionName = System.getProperty("action");

            if (actionName.isEmpty()) {
                logger.error("You must provide an action");
                return;
            }
            String minCategory, runwayName;

            switch (actionName) {
                case "add":
                    minCategory = System.getProperty("category");
                    runwayName = System.getProperty("runway");
                    if (minCategory.isEmpty() || runwayName.isEmpty()) {
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
                    runwayName = System.getProperty("runwayName");
                    if (runwayName.isEmpty()) {
                        logger.error("You must provide the Runway name with action {}", actionName);
                        break;
                    }
                    service.openRunway(runwayName);
                    break;
                case "close":
                    runwayName = System.getProperty("runwayName");
                    if (runwayName.isEmpty()) {
                        logger.error("You must provide the Runway name with action {}", actionName);
                        break;
                    }
                    service.closeRunway(runwayName);
                    break;
                case "status":
                    runwayName = System.getProperty("runwayName");
                    if (runwayName.isEmpty()) {
                        logger.error("You must provide the Runway name with action {}", actionName);
                        break;
                    }
                    service.getRunwayStatus(runwayName);
                    break;
                case "takeOff":
                    service.takeOff();
                    break;
                case "reorder":
                    service.reorderFlights();
                    break;
                default:
                    logger.error("Invalid action {}", actionName);
                    break;
            }
        }
    }
}
