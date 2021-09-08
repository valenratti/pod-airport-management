package ar.edu.itba.pod.models;

import java.util.ArrayList;
import java.util.List;

public class ReorderFlightsResponseDTO {
    List<Integer> notAssignedFlights;
    private int assignedFlightsQty;

    public ReorderFlightsResponseDTO() {
        notAssignedFlights = new ArrayList<>();
        assignedFlightsQty = 0;
    }

    public List<Integer> getNotAssignedFlights() {
        return notAssignedFlights;
    }

    public void setNotAssignedFlights(List<Integer> notAssignedFlights) {
        this.notAssignedFlights = notAssignedFlights;
    }

    public int getAssignedFlightsQty() {
        return assignedFlightsQty;
    }

    public void setAssignedFlightsQty(int assignedFlightsQty) {
        this.assignedFlightsQty = assignedFlightsQty;
    }
}
