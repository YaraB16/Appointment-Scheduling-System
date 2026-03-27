package com.appointment.Repository;

import com.appointment.Domain.Administrator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAdminRepository implements AdminRepository {
    private final Map<String, Administrator> store = new ConcurrentHashMap<>();

    public InMemoryAdminRepository() {
        // admin افتراضي للتجربة
        save(new Administrator("Admin", "admin@mail.com", "1234"));
    }

    @Override
    public Optional<Administrator> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(store.get(email));
    }

    @Override
    public Administrator save(Administrator admin) {
        if (admin == null) throw new IllegalArgumentException("admin is required");
        store.put(admin.getEmail(), admin);
        return admin;
    }
}