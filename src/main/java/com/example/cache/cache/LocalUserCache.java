package com.example.cache.cache;

import com.example.cache.model.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalUserCache {

    private final ConcurrentHashMap<Long, User> cache =
            new ConcurrentHashMap<>();

    public User get(long id) {
        return cache.get(id);
    }

    public void put(User user) {
        cache.put(user.id(), user);
    }

    public void delete(long id) {
        cache.remove(id);
    }

}
