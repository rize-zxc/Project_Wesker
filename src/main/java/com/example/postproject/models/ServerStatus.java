package com.example.postproject.models;

/**class of ServerStatus.*/
public class ServerStatus {
    private boolean available;

    /**constructor of ServerStatus.*/
    public ServerStatus() {
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}