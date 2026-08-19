package com.example.cache.service;

import com.example.cache.cache.LocalUserCache;
import com.example.cache.invalidation.CacheInvalidationPublisher;
import com.example.cache.model.User;
import com.example.cache.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LocalUserCache cache;
    private final CacheInvalidationPublisher invalidationPublisher;

    public UserService(UserRepository userRepository, LocalUserCache cache, CacheInvalidationPublisher invalidationPublisher) {
        this.userRepository = userRepository;
        this.cache = cache;
        this.invalidationPublisher = invalidationPublisher;
    }

    public User getUser(long id) {

        User cachedUser = cache.get(id);

        if (cachedUser != null) {
            System.out.println("CACHE HIT: user=" + id);
            return cachedUser;
        }

        System.out.println("CACHE MISS: user=" + id);
        User user = userRepository.findById(id);
        cache.put(user);

        return user;
    }

    public void updateUser(long id, String name) {

        System.out.println("Updating DB: user=" + id);
        userRepository.updateName(id, name);

        System.out.println("Invalidating LOCAL cache: user=" + id);
        cache.delete(id);

        System.out.println("Publishing invalidation: user : " + id);
        invalidationPublisher.publishUserInvalidation(id);

    }


}
