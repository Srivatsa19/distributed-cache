package com.example.cache.invalidation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class CacheInvalidationClient {

    private final List<String> nodes;
    private final String self;
    private final RestClient restClient;

    public CacheInvalidationClient(@Value("${cache.nodes}") String nodes, @Value("${cache.self}") String self) {
        this.nodes = Arrays.stream(nodes.split(",")).map(String::trim).toList();
        this.self = self;
        this.restClient = RestClient.create();
    }

    public void invalidateUser(Long userId) {
        for (String node : nodes) {
            if (Objects.equals(node, self)) {
                System.out.println("In the if block");
                continue;
            }
            String url = node + "/internal/cache/users/" + userId;
            try {
                System.out.println("Sending invalidation to : " + url);
                restClient.delete().uri(url).retrieve().toBodilessEntity();
            } catch (Exception e) {
                System.err.println("Failed to invalidate user=" + userId + " on node=" + node + ". Reason: " + e.getMessage());
            }
        }
    }

}
