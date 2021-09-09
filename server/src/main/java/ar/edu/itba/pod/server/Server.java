package ar.edu.itba.pod.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws RemoteException {
        logger.info("pod-airport-management Server Starting ...");

        final AirportManagementImpl server = new AirportManagementImpl();
        final Remote remote = UnicastRemoteObject.exportObject(server,0);
        final Registry registry = LocateRegistry.getRegistry();

        registry.rebind("management", remote);
        logger.info("Management service bound");
        registry.rebind("runway", remote);
        logger.info("Runway service bound");
        registry.rebind("tracker", remote);
        logger.info("Flight tracker service bound");
        registry.rebind("query", remote);
        logger.info("Query service bound");
    }
}
