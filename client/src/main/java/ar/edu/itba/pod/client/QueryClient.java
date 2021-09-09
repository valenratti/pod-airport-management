package ar.edu.itba.pod.client;

import ar.edu.itba.pod.services.TakeOffQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class QueryClient {
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        logger.info("Starting query client...");

        String serverAddress = System.getProperty("serverAddress");
        if (serverAddress == null || serverAddress.isEmpty()) {
            logger.error("You must provide a server address");
        } else {
            TakeOffQueryService service = (TakeOffQueryService) Naming.lookup("//" + serverAddress + "/query");
        }
    }
}
