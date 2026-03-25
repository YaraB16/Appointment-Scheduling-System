package com.appointment.service;

import com.appointment.Domain.User;

import java.util.ArrayList;
import java.util.List;
import static com.appointment.Domain.UserRole.*;

public class AuthService {

    private List<User> users = new ArrayList<>();

    public AuthService() {
        users.add(new User("Admin", "admin@mail.com", "1234",ADMIN ));
        users.add(new User("Yara", "yara@mail.com", "1111", USER));
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) &&
                    user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
