package co.com.adventure.service;

import co.com.adventure.dto.ScoreDto;
import co.com.adventure.model.Score;
import co.com.adventure.repository.ScoreRepository;
import co.com.adventure.util.Constants;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class ScoreServiceImp implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public void saveScoreByUser(ScoreDto score) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.date_format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
        String var = dateFormat.format(new Date());
        score.setTimestamp(dateFormat.parse(var));

        Score varScore = Score.builder()
                .name(score.getName().toLowerCase())
                .hour(score.getHour())
                .minute(score.getMinute())
                .second(score.getSecond())
                .timestamp(score.getTimestamp())
                .cellphone(score.getCellphone()).build();

        scoreRepository.save(varScore);

        sendSMS(score);
    }

    @Override
    public List<ScoreDto> getAllScores() throws Exception {

        Iterable<Score> scoresBD = scoreRepository.findAll();
        List<ScoreDto> listScores = new ArrayList<>();
        scoresBD.forEach(var -> {
            ScoreDto varScore = ScoreDto.builder()
                    .id(var.getId())
                    .name(var.getName().substring(0, 1).toUpperCase() + var.getName().substring(1, var.getName().length()))
                    .hour(var.getHour())
                    .minute(var.getMinute())
                    .second(var.getSecond())
                    .timestamp(var.getTimestamp())
                    .cellphone(var.getCellphone()).build();

            listScores.add(varScore);
        });

        return listScores;
    }

    @Override
    public void sendSMS(ScoreDto score) throws Exception {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(60000)
                .build();

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(config);
        CloseableHttpClient httpClient = builder.build();

        HttpPost post = new HttpPost(Constants.url_Altiria);

        List parametersList = new ArrayList();
        parametersList.add(new BasicNameValuePair(Constants.attribute_name_1, Constants.attribute_value_1));
        // parametersList.add(new BasicNameValuePair("domainId", "XX"));
        parametersList.add(new BasicNameValuePair(Constants.attribute_name_2, Constants.attribute_value_2));
        parametersList.add(new BasicNameValuePair(Constants.attribute_name_3, Constants.attribute_value_3));
        parametersList.add(new BasicNameValuePair(Constants.attribute_name_4, Constants.indiCOL + score.getCellphone()));

        //----------------- Construcci??n de le fecha para el SMS -----------------
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.date_format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Bogota"));

        String hours = (score.getHour() / 10 == 0) ? "0" + score.getHour() : String.valueOf(score.getHour());
        String mins = (score.getMinute() / 10 == 0) ? "0" + score.getMinute() : String.valueOf(score.getMinute());
        String seconds = (score.getSecond() / 10 == 0) ? "0" + score.getSecond() : String.valueOf(score.getSecond());

        String answer = Constants.msg_SMS;
        answer = answer.replace(Constants.rname, score.getName())
                .replace(Constants.rscore, hours + "h:" + mins + "m:" + seconds + "s")
                .replace(Constants.rdate, simpleDateFormat.format(score.getTimestamp()));

        parametersList.add(new BasicNameValuePair(Constants.attribute_name_5, answer));

        try {
            post.setEntity(new UrlEncodedFormEntity(parametersList, "UTF-8"));
        } catch (UnsupportedEncodingException uex) {
            throw new Exception("ERROR: codificaci??n de caracteres no soportada");
        }

        CloseableHttpResponse response = null;

        try {
//            System.out.println("Enviando peticion");
            response = httpClient.execute(post);
            String resp = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new Exception("ERROR: C??digo de error HTTP:  " + response.getStatusLine().getStatusCode() +
                        "Compruebe que ha configurado correctamente la direccion/url suministrada por Altiria");
            } else {
                if (resp.startsWith("ERROR")) {
                    throw new Exception(resp + " Codigo de error de Altiria. Compruebe las especificaciones");
                } else {
                    System.out.println(resp);
                }
            }
        } catch (Exception e) {
            throw new Exception("ERROR: " + e.getMessage());
        } finally {
            post.releaseConnection();
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ioe) {
                    throw new Exception("ERROR: Cerrando Recursos: " + ioe.getMessage());
                }
            }
        }
    }
}
