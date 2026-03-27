package com.appointment.service;

import com.appointment.Domain.Administrator;
import com.appointment.Domain.User;
import com.appointment.Repository.AdminRepository;

import java.util.Objects;

public class AuthService {
    private final AdminRepository adminRepository;

    public AuthService(AdminRepository adminRepository) {
        this.adminRepository = Objects.requireNonNull(adminRepository, "adminRepository");
    }

    public User login(String email, String password) {
        if (email == null || password == null) return null;

        Administrator admin = adminRepository.findByEmail(email).orElse(null);
        if (admin == null) return null;

        return admin.getPassword().equals(password) ? admin : null;
    }
}