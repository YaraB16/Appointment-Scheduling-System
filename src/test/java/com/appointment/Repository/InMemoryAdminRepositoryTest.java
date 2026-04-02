package com.appointment.Repository;

import com.appointment.Domain.Administrator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAdminRepositoryTest {

    @Test
    void constructor_createsDefaultAdmin() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> admin = repo.findByEmail("admin@mail.com");

        assertTrue(admin.isPresent());
        assertEquals("Admin", admin.get().getName());
        assertEquals("1234", admin.get().getPassword());
    }

    @Test
    void save_addsNewAdmin_andCanBeRetrieved() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Administrator newAdmin = new Administrator("Ali", "ali@mail.com", "9999");

        repo.save(newAdmin);

        Optional<Administrator> found = repo.findByEmail("ali@mail.com");

        assertTrue(found.isPresent());
        assertEquals("Ali", found.get().getName());
    }

    @Test
    void save_overwritesExistingAdmin_withSameEmail() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Administrator admin1 = new Administrator("Ali", "same@mail.com", "1111");
        Administrator admin2 = new Administrator("AliUpdated", "same@mail.com", "2222");

        repo.save(admin1);
        repo.save(admin2);

        Optional<Administrator> found = repo.findByEmail("same@mail.com");

        assertTrue(found.isPresent());
        assertEquals("AliUpdated", found.get().getName());
        assertEquals("2222", found.get().getPassword());
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailNotFound() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> found = repo.findByEmail("notfound@mail.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailIsNull() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> found = repo.findByEmail(null);

        assertTrue(found.isEmpty());
    }

    @Test
    void save_throwsException_whenAdminIsNull() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> repo.save(null)
        );

        assertEquals("admin is required", ex.getMessage());
    }
}