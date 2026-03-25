package com.appointment.Domain;
/**
 * Administrator user with username/password.
 * (Phase 1: simple in-memory auth)
 * @author team
 * @version 1.0
 */

import com.appointment.Domain.UserRole;

/**
 * Administrator user (inherits from User).
 */
public class Administrator extends User {

    public Administrator(String name, String email, String password) {
        super(name, email, password, UserRole.ADMIN);
    }
}
