package ar.edu.itba.pod.server.impl;

import ar.edu.itba.pod.services.ManagementService;
import ar.edu.itba.pod.models.RunawayCategory;
import ar.edu.itba.pod.server.AirportDataManagement;

import java.rmi.RemoteException;

public class ManagementServiceImpl implements ManagementService {

    private final AirportDataManagement airportDataManagement;

    public ManagementServiceImpl(){
        this.airportDataManagement = AirportDataManagement.getInstance();
    }

    @Override
    public void addRunway(String runwayName, RunawayCategory category) throws RemoteException {
        airportDataManagement.addRunway(runwayName, category);
    }

    @Override
    public boolean getRunwayStatus(String runwayName) throws RemoteException {
        return airportDataManagement.getRunawayStatus(runwayName);
    }

    @Override
    public boolean openRunway(String runwayName) throws RemoteException {
        return airportDataManagement.openRunaway(runwayName);
    }

    @Override
    public boolean closeRunway(String runwayName) throws RemoteException {
        return airportDataManagement.closeRunaway(runwayName);
    }

    @Override
    public void takeOff() throws RemoteException {
        airportDataManagement.dispatchFlights();
    }

    @Override
    public void reorderFlights() throws RemoteException {
        airportDataManagement.reorderFlights();
    }
}
