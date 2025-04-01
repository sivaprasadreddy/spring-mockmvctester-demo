package com.jetbrains.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final static Map<Long, User> USERS = new HashMap<>();
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0L);

    @PostConstruct
    void init() {
        var user1 = new User(ID_GENERATOR.incrementAndGet(), "Siva", "siva@gmail.com", "siva");
        var user2 = new User(ID_GENERATOR.incrementAndGet(), "Marco", "marco@gmail.com", "marco");
        var user3 = new User(ID_GENERATOR.incrementAndGet(), "Paul", "paul@gmail.com", "paul");
        USERS.put(user1.id(), user1);
        USERS.put(user2.id(), user2);
        USERS.put(user3.id(), user3);
    }


    public User findById(Long id) {
        return USERS.get(id);
    }

    public User create(User user) {
        var newUser = new User(ID_GENERATOR.incrementAndGet(),
                user.name(), user.email(), user.password());
        USERS.put(newUser.id(), newUser);
        return newUser;
    }
}
