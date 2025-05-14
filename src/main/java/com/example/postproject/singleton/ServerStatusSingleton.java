package com.example.postproject.singleton;

import com.example.postproject.models.ServerStatus;

/**singleton for serverStatus.*/
public class ServerStatusSingleton {
    private static ServerStatus instance;

    private ServerStatusSingleton() {

    }

    /**getInstance method.*/
    public static ServerStatus getInstance() {
        if (instance == null) {
            synchronized (ServerStatusSingleton.class) {
                if (instance == null) {
                    instance = new ServerStatus();
                }
            }
        }
        return instance;
    }
}
