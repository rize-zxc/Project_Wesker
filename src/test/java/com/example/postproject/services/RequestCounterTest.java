package com.example.postproject.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestCounterTest {

    @Test
    void increment() {
        RequestCounter counter = new RequestCounter();
        assertEquals(1, counter.increment());
    }

    @Test
    void getCount() {
        RequestCounter counter = new RequestCounter();
        counter.increment();
        assertEquals(1, counter.getCount());
    }

    @Test
    void reset() {
        RequestCounter counter = new RequestCounter();
        counter.increment();
        counter.reset();
        assertEquals(0, counter.getCount());
    }
}
