package co.com.adventure.sms;

import co.com.adventure.config.SmsProperties;
import co.com.adventure.model.Score;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Renderiza el texto del SMS a partir de un {@link Score} y la plantilla configurada
 * en {@link SmsProperties#getMessageTemplate()}.
 */
@Component
public class ScoreSmsMessageBuilder {

    private static final String NAME_PLACEHOLDER = "{name}";
    private static final String SCORE_PLACEHOLDER = "{score}";
    private static final String DATE_PLACEHOLDER = "{date}";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String ELAPSED_TIME_FORMAT = "%02dh:%02dm:%02ds";

    private final SmsProperties properties;

    public ScoreSmsMessageBuilder(SmsProperties properties) {
        this.properties = properties;
    }

    public String build(Score score) {
        String elapsed = String.format(ELAPSED_TIME_FORMAT, score.getHour(), score.getMinute(), score.getSecond());
        String date = score.getTimestamp().format(DATE_FORMAT);

        return properties.getMessageTemplate()
                .replace(NAME_PLACEHOLDER, score.getName())
                .replace(SCORE_PLACEHOLDER, elapsed)
                .replace(DATE_PLACEHOLDER, date);
    }
}
