package co.com.adventure.sms;

import co.com.adventure.config.SmsProperties;
import co.com.adventure.exception.SmsDeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Adaptador de {@link SmsGateway} sobre la API HTTP de Altiria.
 *
 * <p>Toda la configuración (URL, credenciales, timeouts, prefijo de país) procede de
 * {@link SmsProperties}. Solo se instancia cuando {@code adventure.sms.enabled=true}.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "adventure.sms", name = "enabled", havingValue = "true")
public class AltiriaSmsGateway implements SmsGateway {

    private static final String REQUEST_CHARSET = StandardCharsets.UTF_8.name();

    private final SmsProperties properties;

    public AltiriaSmsGateway(SmsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void send(String nationalNumber, String message) {
        HttpPost request = new HttpPost(properties.getUrl());
        request.setEntity(buildForm(nationalNumber, message));

        try (CloseableHttpClient client = buildClient();
             CloseableHttpResponse response = client.execute(request)) {
            verify(response);
        } catch (IOException ex) {
            throw new SmsDeliveryException("No se pudo contactar con el proveedor de SMS", ex);
        } finally {
            request.releaseConnection();
        }
    }

    private CloseableHttpClient buildClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout((int) properties.getConnectTimeout().toMillis())
                .setSocketTimeout((int) properties.getSocketTimeout().toMillis())
                .build();
        return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    private UrlEncodedFormEntity buildForm(String nationalNumber, String message) {
        List<NameValuePair> params = List.of(
                new BasicNameValuePair(AltiriaApiParams.COMMAND, AltiriaApiParams.COMMAND_SEND_SMS),
                new BasicNameValuePair(AltiriaApiParams.LOGIN, properties.getLogin()),
                new BasicNameValuePair(AltiriaApiParams.PASSWORD, properties.getPassword()),
                new BasicNameValuePair(AltiriaApiParams.DESTINATION, properties.getSenderCountryCode() + nationalNumber),
                new BasicNameValuePair(AltiriaApiParams.MESSAGE, message));
        try {
            return new UrlEncodedFormEntity(params, REQUEST_CHARSET);
        } catch (IOException ex) {
            throw new SmsDeliveryException("Codificación de caracteres no soportada: " + REQUEST_CHARSET, ex);
        }
    }

    private void verify(CloseableHttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        String body = EntityUtils.toString(response.getEntity());

        if (status != HttpStatus.SC_OK) {
            throw new SmsDeliveryException("El proveedor de SMS respondió con HTTP " + status);
        }
        if (body.startsWith(AltiriaApiParams.ERROR_RESPONSE_PREFIX)) {
            throw new SmsDeliveryException("El proveedor de SMS rechazó el mensaje: " + body.trim());
        }
        log.info("SMS aceptado por el proveedor: {}", body.trim());
    }
}
