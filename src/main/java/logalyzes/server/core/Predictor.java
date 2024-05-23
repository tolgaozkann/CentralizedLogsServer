package logalyzes.server.core;

import logalyzes.server.utils.Config;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Predictor {
    private  final  HttpClient client;
    private  final String api_url;
    private final ObjectMapper objectMapper;


    public  Predictor(){
        this.client = HttpClient.newHttpClient();
        this.api_url = Config.PREDICTOR_API_URL;
        this.objectMapper = new ObjectMapper();
    }

    public int[] sendLog(String logMessage) throws Exception {
        try {
            int[] res;


            String encodedLogMessage = URLEncoder.encode(logMessage, StandardCharsets.UTF_8.toString());


            URI uri = new URI(this.api_url + "/predict?log=" + encodedLogMessage);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

                System.out.println("Ressponse: " + apiResponse.toString());

                res = apiResponse.getPrediction();
            } else {
                System.out.println("Error: " + response.body());
                throw new  Exception("Api returned arror code " + response.statusCode());
            }

            return  res;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private static class ApiResponse {
        private int[] prediction;

        public  int[]  getPrediction() {
            return prediction;
        }

        public void setPrediction(int[] prediction) {
            this.prediction = prediction;
        }


        @Override
        public  String toString(){
            return  "Predictions: " + Arrays.toString(this.prediction);
        }
    }


}
