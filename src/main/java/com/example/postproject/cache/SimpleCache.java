package com.example.postproject.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;


/**cash realization.*/
@Component
public class SimpleCache {
    /**cash class.*/
    private final Map<String, Object> cache = new HashMap<>();

    /**save by key method.*/
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    /**get by key method.*/
    public Optional<Object> get(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    /**delete by key method.*/
    public void remove(String key) {
        cache.remove(key);
    }

    /**clear by key method.*/
    public void clear() {
        cache.clear();
    }
}