package co.com.adventure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Comprueba que el contexto de Spring arranca. Usa el perfil "test" (H2 en memoria),
 * por lo que no requiere Docker ni PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class CanivalesApplicationTests {

    @Test
    void contextLoads() {
    }
}
