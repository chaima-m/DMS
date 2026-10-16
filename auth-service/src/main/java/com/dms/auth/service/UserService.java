package com.dms.auth.service;

import com.dms.auth.model.User;
import com.dms.auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
        initDefaults();
    }

    private void initDefaults() {
        if (repo.count() == 0) {
            createUser("admin@dms.com", "admin123", "Admin",
                    List.of("ROLE_ADMIN", "ROLE_USER"), List.of("IT", "Finance"));

        }
    }

    public Optional<User> findByEmail(String email) { return repo.findByEmail(email); }

    public boolean checkPassword(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }

    public User register(String email, String password, String name, List<String> roles) {
        if (repo.findByEmail(email).isPresent())
            throw new IllegalArgumentException("User already exists: " + email);
        return createUser(email, password, name, roles, new ArrayList<>());
    }

    public void updateLastLogin(User u) {
        u.setLastLogin(LocalDateTime.now()); repo.save(u);
    }

    public void updateLastLogout(String email) {
        repo.findByEmail(email).ifPresent(u -> { u.setLastLogout(LocalDateTime.now()); repo.save(u); });
    }

    private User createUser(String email, String pw, String name,
                             List<String> roles, List<String> depts) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(encoder.encode(pw));
        u.setSalt("bcrypt");
        u.setName(name);
        u.setRoles(new ArrayList<>(roles));
        u.setDepartments(new ArrayList<>(depts));
        u.setStatus("active");
        return repo.save(u);
    }
}
