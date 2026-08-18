package com.example.cache.controller;

import com.example.cache.cache.LocalUserCache;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/cache")
public class InternalCacheController {

    private final LocalUserCache localUserCache;

    public InternalCacheController(LocalUserCache localUserCache) {
        this.localUserCache = localUserCache;
    }

    @DeleteMapping("/users/{id}")
    public void invalidateUser(@PathVariable long id) {
        System.out.println("Received request to invalidate cache for user : " + id);
        localUserCache.delete(id);
    }

}
