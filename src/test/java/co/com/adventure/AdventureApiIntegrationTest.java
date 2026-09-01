package co.com.adventure;

import co.com.adventure.model.Options;
import co.com.adventure.model.Score;
import co.com.adventure.repository.OptionsRepository;
import co.com.adventure.repository.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración de extremo a extremo sobre los 3 endpoints, con el
 * servidor arrancado en un puerto aleatorio y base de datos H2 en memoria
 * (perfil {@code test}, SMS deshabilitado).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AdventureApiIntegrationTest {

    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private OptionsRepository optionsRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    @BeforeEach
    void seed() {
        scoreRepository.deleteAll();
        optionsRepository.deleteAll();
        optionsRepository.save(Options.builder()
                .id(1).description("Nodo inicial")
                .option1Text("Ir al norte").option1NextId(2)
                .option2Text("Ir al sur").option2NextId(0)
                .option3Text("").option3NextId(0)
                .build());
        optionsRepository.save(Options.builder()
                .id(2).description("Final")
                .option1Text("").option2Text("").option3Text("")
                .build());
    }

    @Test
    void getOption_returnsNode_withSnakeCaseJson() {
        ResponseEntity<String> response = rest.postForEntity("/app/adventure/1", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("\"id\":1")
                .contains("\"description\":\"Nodo inicial\"")
                .contains("\"opt_1_text\":\"Ir al norte\"")
                .contains("\"opt_1_value\":2");
    }

    @Test
    void getOption_unknownId_returns404() {
        ResponseEntity<String> response = rest.postForEntity("/app/adventure/999", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("999");
    }

    @Test
    void getOption_nonNumericId_returns400() {
        ResponseEntity<String> response = rest.postForEntity("/app/adventure/abc", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void saveScore_persistsAndIsListed() {
        String body = "{\"name\":\"CARLOS\",\"hour\":0,\"minute\":7,\"second\":3,\"cellphone\":\"3212664870\"}";

        ResponseEntity<String> save = rest.postForEntity("/app/adventure/saveScore", jsonRequest(body), String.class);
        assertThat(save.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(save.getBody()).contains("\"message\":\"saved\"");

        ResponseEntity<String> list = rest.getForEntity("/app/adventure/scores", String.class);
        assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(list.getBody()).contains("\"name\":\"Carlos\"").contains("\"cellphone\":\"3212664870\"");

        Score stored = scoreRepository.findAll().iterator().next();
        assertThat(stored.getName()).isEqualTo("carlos");
        assertThat(stored.getTimestamp()).isBefore(LocalDateTime.now().plusMinutes(1));
    }

    @Test
    void saveScore_invalidBody_returns400() {
        String body = "{\"name\":\"\",\"minute\":90,\"cellphone\":\"abc\"}";

        ResponseEntity<String> response = rest.postForEntity("/app/adventure/saveScore", jsonRequest(body), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("cellphone").contains("name");
    }

    @Test
    void saveScore_malformedJson_returns400() {
        ResponseEntity<String> response = rest.postForEntity("/app/adventure/saveScore", jsonRequest("{roto"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getScores_emptyByDefault() {
        ResponseEntity<String> response = rest.getForEntity("/app/adventure/scores", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    private static HttpEntity<String> jsonRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
