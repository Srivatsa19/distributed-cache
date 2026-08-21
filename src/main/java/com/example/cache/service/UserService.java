package com.example.cache.service;

import com.example.cache.cache.LocalUserCache;
import com.example.cache.model.User;
import com.example.cache.repository.OutboxRepository;
import com.example.cache.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LocalUserCache cache;
    private final OutboxRepository outboxRepository;

    public UserService(UserRepository userRepository, LocalUserCache cache, OutboxRepository outboxRepository) {
        this.userRepository = userRepository;
        this.cache = cache;
        this.outboxRepository = outboxRepository;
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

    @Transactional
    public void updateUser(long id, String name) {

        System.out.println("Updating DB: user=" + id);
        userRepository.updateName(id, name);

        System.out.println("Inserting outbox event...");

        outboxRepository.insert(
                "USER_UPDATED",
                "USER",
                id,
                """
                {"entityType":"USER","entityId":%d}
                """.formatted(id)
        );

    }

}
