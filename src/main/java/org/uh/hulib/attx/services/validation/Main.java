package org.uh.hulib.attx.services.validation;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int maxThreads = 6;
        int minThreads = 5;
        int timeOutMillis = 30000;
        String apiVersion = "0.2";

        port(4306);
        threadPool(maxThreads, minThreads, timeOutMillis);

        get("/health", (request, response) -> "Hello World");


        post(String.format("/%s/validate", apiVersion), "application/json", (request, response) -> {

            String content = request.body();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(content);

            SQLiteConnection data = SQLiteConnection.main();
            String result = null;

            if (jsonNode.has("shapesGraph")) {
                // Load the main data model
                String shapesGraph = jsonNode.get("shapesGraph").asText();
                String dataGraph = jsonNode.get("dataGraph").asText();

                result = SHACLUtils.SHACLValitateText(dataGraph, shapesGraph);
            } else {
                String dataGraph = jsonNode.get("dataGraph").asText();

                result = SHACLUtils.SHACLValitateText(dataGraph, null);
            }

            Random rand = new Random();

            int n = rand.nextInt(500000) + 1;
            data.insert(n, result);

            response.status(200); // 200 Done
            response.type("text/turtle");
            return result;
        });

        get(String.format("/%s/report/:reportID", apiVersion), (request, response) -> {
            String id = request.params(":reportID");
            String result = null;

            SQLiteConnection data = new SQLiteConnection();
            try {
                result = data.retrieve(Integer.parseInt(id));
                response.status(200); // 200 Done
                response.type("text/turtle");

            } catch (Exception e) {
                response.status(404); // 404 Done
                response.type("html/text");
            }
            return result;
        });



    }
}