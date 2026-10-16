/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PatchMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.dms.auth.controller;

import com.dms.auth.model.User;
import com.dms.auth.repository.UserRepository;
import com.dms.auth.security.JwtUtil;
import com.dms.auth.service.UserService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/auth"})
@CrossOrigin(origins={"*"})
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping(value={"/login"})
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password required"));
        }
        Optional<User> userOpt = this.userService.findByEmail(email);
        if (userOpt.isEmpty() || !this.userService.checkPassword(password, userOpt.get().getPassword())) {
            return ResponseEntity.status((int)401).body(Map.of("error", "Invalid credentials"));
        }
        User user = userOpt.get();
        if ("suspended".equals(user.getStatus())) {
            return ResponseEntity.status((int)403).body(Map.of("error", "Account suspended"));
        }
        this.userService.updateLastLogin(user);
        String token = this.jwtUtil.generateToken(user.getEmail(), user.getRoles(), user.getDepartments());
        return ResponseEntity.ok(Map.of("token", token, "email", user.getEmail(), "name", user.getName() != null ? user.getName() : user.getEmail().split("@")[0], "roles", user.getRoles(), "departments", user.getDepartments(), "status", user.getStatus(), "type", "Bearer"));
    }

    @PostMapping(value={"/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        List roles;
        String email = (String)body.get("email");
        String password = (String)body.get("password");
        String name = (String)body.getOrDefault("name", "");
        List list = roles = body.containsKey("roles") ? (List)body.get("roles") : List.of("ROLE_USER");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password required"));
        }
        try {
            User created = this.userService.register(email, password, name, roles);
            return ResponseEntity.ok(Map.of("message", "User created", "email", created.getEmail(), "roles", created.getRoles()));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status((int)409).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value={"/users"})
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        List users = this.userRepository.findAll().stream().map(u -> {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", u.getId());
            m.put("email", u.getEmail());
            m.put("name", u.getName() != null ? u.getName() : u.getEmail().split("@")[0]);
            m.put("roles", u.getRoles());
            m.put("departments", u.getDepartments());
            m.put("status", u.getStatus());
            m.put("lastLogin", u.getLastLogin());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping(value={"/users/{id}/departments"})
    public ResponseEntity<?> assignDepartments(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        List departments = (List)body.get("departments");
        return this.userRepository.findById(id).map(user -> {
            user.setDepartments(departments != null ? departments : new ArrayList());
            this.userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Departments updated", "departments", user.getDepartments()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping(value={"/users/{id}/status"})
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return this.userRepository.findById(id).map(user -> {
            user.setStatus(status);
            this.userRepository.save(user);
            return ResponseEntity.ok(Map.of("status", user.getStatus()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value={"/validate"})
    public ResponseEntity<?> validate(@RequestHeader(value="Authorization", required=false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status((int)401).body(Map.of("valid", false));
        }
        String token = authHeader.substring(7);
        if (!this.jwtUtil.validateToken(token)) {
            return ResponseEntity.status((int)401).body(Map.of("valid", false));
        }
        return ResponseEntity.ok(Map.of("valid", true, "email", this.jwtUtil.extractEmail(token), "roles", this.jwtUtil.extractRoles(token), "departments", this.jwtUtil.extractDepartments(token)));
    }

    @PostMapping(value={"/logout"})
    public ResponseEntity<?> logout(@RequestHeader(value="Authorization", required=false) String h) {
        if (h != null && h.startsWith("Bearer ")) {
            this.userService.updateLastLogout(this.jwtUtil.extractEmail(h.substring(7)));
        }
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
