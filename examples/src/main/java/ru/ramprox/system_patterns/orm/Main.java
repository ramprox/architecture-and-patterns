package ru.ramprox.system_patterns.orm;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository(DB.getConnection());

        // ---- CREATE -----
        userRepository.beginTransaction();
        User newUser = new User(1L, "user1", "password1");
        userRepository.insert(newUser);
        userRepository.commitTransaction();

        // ----- READ and UPDATE -----
        userRepository.beginTransaction();
        Optional<User> userOpt = userRepository.findById(1L);
        userOpt.ifPresent(user -> {
            user.setUsername("user2");
            userRepository.update(user);
        });
        userRepository.commitTransaction();

        // ----- DELETE ------
        userRepository.beginTransaction();
        userOpt = userRepository.findById(1L);
        userOpt.ifPresent(userRepository::delete);
        userRepository.commitTransaction();
    }
}
