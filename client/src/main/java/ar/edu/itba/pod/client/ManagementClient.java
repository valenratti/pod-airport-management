package ar.edu.itba.pod.client;

import ar.edu.itba.pod.exceptions.DuplicateRunwayException;
import ar.edu.itba.pod.models.RunwayCategory;
import ar.edu.itba.pod.services.ManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class ManagementClient {
    private static final Logger logger = LoggerFactory.getLogger(ManagementClient.class);

    public static void main(String[] args) throws RemoteException {
        logger.info("Starting management client...");
        //TODO: RMI
        ManagementService service = null;

        String actionName= System.getProperty("actionName");

        if(actionName.isEmpty()){
            logger.error("You must provide an action");
            return;
        }
        String minCategory = null, runwayName = null;

        switch (actionName){
            case "add":
                minCategory = System.getProperty("minCategory");
                runwayName = System.getProperty("runwayName");
                if(minCategory.isEmpty() || runwayName.isEmpty()){
                    logger.error("You must provide minCategory and runwayName with action {} ", actionName);
                    break;
                }
                try{
                    RunwayCategory runwayCategory = RunwayCategory.valueOf(minCategory.toUpperCase());
                    service.addRunway(runwayName, runwayCategory);
                    System.out.println("Runway " + runwayName + " is open");
                } catch (IllegalArgumentException e){
                    logger.error("There is no category with that name");
                } catch (DuplicateRunwayException e){
                    logger.error(e.getMessage());
                }
                break;
            case "open":
                runwayName = System.getProperty("runwayName");
                if(runwayName.isEmpty()) {
                    logger.error("You must provide the Runway name with action {}", actionName);
                    break;
                }
                service.openRunway(runwayName);
                break;
            case "close":
                runwayName = System.getProperty("runwayName");
                if(runwayName.isEmpty()) {
                    logger.error("You must provide the Runway name with action {}", actionName);
                    break;
                }
                service.closeRunway(runwayName);
                break;
            case "status":
                runwayName = System.getProperty("runwayName");
                if(runwayName.isEmpty()) {
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
