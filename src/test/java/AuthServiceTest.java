
import com.appointment.Domain.User;
import com.appointment.Repository.InMemoryAdminRepository;
import com.appointment.service.AuthService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void login_success() {
        AuthService auth = new AuthService(new InMemoryAdminRepository());
        User u = auth.login("admin@mail.com", "1234");
        assertNotNull(u);
        assertEquals("admin@mail.com", u.getEmail());
    }

    @Test
    void login_fail_wrongPassword() {
        AuthService auth = new AuthService(new InMemoryAdminRepository());
        assertNull(auth.login("admin@mail.com", "wrong"));
    }

    @Test
    void login_fail_unknownEmail() {
        AuthService auth = new AuthService(new InMemoryAdminRepository());
        assertNull(auth.login("nope@mail.com", "1234"));
    }
}