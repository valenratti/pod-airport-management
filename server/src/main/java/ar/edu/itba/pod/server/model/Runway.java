package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.models.RunwayCategory;

import java.util.Objects;

public class Runway implements Comparable<Runway>{
    private final String name;
    private final RunwayCategory category;
    private boolean isOpen;

    public Runway(String name, RunwayCategory category){
        this.name = name;
        this.category = category;
        this.isOpen = true;
    }

    public String getName() {
        return name;
    }

    public RunwayCategory getCategory() {
        return category;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public int compareTo(Runway o) {
        return category.toString().compareTo(o.getCategory().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Runway runway = (Runway) o;
        return getName().equals(runway.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
