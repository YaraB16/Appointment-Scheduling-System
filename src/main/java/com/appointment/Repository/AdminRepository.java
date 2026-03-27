package com.appointment.Repository;

import com.appointment.Domain.Administrator;

import java.util.Optional;

public interface AdminRepository {
    Optional<Administrator> findByEmail(String email);
    Administrator save(Administrator admin);
}