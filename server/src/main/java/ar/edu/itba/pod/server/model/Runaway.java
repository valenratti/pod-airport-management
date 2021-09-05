package ar.edu.itba.pod.server.model;

public class Runaway implements Comparable{
    private String name;
    private RunawayCategory category;
    private boolean isOpen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RunawayCategory getCategory() {
        return category;
    }

    public void setCategory(RunawayCategory category) {
        this.category = category;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public int compareTo(Object o) {
        Runaway otherRunaway = (Runaway) o;
        return category.toString().compareTo(otherRunaway.getCategory().toString());
    }
}
