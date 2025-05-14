package com.example.postproject.services;

import org.springframework.stereotype.Service;

/**requestCounter class.*/
@Service
public class RequestCounter {
    private int count = 0;
    private final Object lock = new Object(); // Объект для синхронизации

  /**increment.*/
    public int increment() {
        synchronized (lock) {
            count++;
            return count;
        }
    }

   /**get count method.*/
    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }

   /**reset counter.*/
    public void reset() {
        synchronized (lock) {
            count = 0;
        }
    }
}
