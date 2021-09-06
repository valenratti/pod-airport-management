package ar.edu.itba.pod.server.model;

import ar.edu.itba.pod.models.RunawayCategory;

import java.util.Objects;

public class Runaway implements Comparable<Runaway>{
    private final String name;
    private final RunawayCategory category;
    private boolean isOpen;

    public Runaway(String name, RunawayCategory category){
        this.name = name;
        this.category = category;
        this.isOpen = true;
    }

    public String getName() {
        return name;
    }

    public RunawayCategory getCategory() {
        return category;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public int compareTo(Runaway o) {
        return category.toString().compareTo(o.getCategory().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Runaway runaway = (Runaway) o;
        return getName().equals(runaway.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
