package org.example;

public class Weather {
    private Location location;
    private Current current;

    // Getters and Setters
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }
    public void setCurrent(Current current) {
        this.current = current;
    }
}

