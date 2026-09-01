package co.com.adventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada de la aplicación Adventure Game ("canivales").
 *
 * <p>API REST que sirve los nodos de una historia tipo "elige tu propia aventura"
 * y persiste los tiempos (scores) de las partidas.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class CanivalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanivalesApplication.class, args);
    }
}
