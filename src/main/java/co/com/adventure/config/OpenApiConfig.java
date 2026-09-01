package co.com.adventure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la documentación OpenAPI 3 servida por springdoc.
 *
 * <ul>
 *   <li>Swagger UI:  <code>/swagger-ui.html</code></li>
 *   <li>Spec JSON:   <code>/v3/api-docs</code></li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adventureOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Canibales App")
                .version("1.0.0")
                .description("API para navegar los nodos de la historia y administrar los scores "
                        + "del juego de aventura Canibales."));
    }
}
