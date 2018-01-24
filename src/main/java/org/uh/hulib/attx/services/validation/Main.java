package org.uh.hulib.attx.services.validation;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.topbraid.spin.util.JenaUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.ByteArrayInputStream;

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
            String shapesGraph = jsonNode.get("shapesGraph").asText();
            String dataGraph = jsonNode.get("dataGraph").asText();

            SQLiteConnection data = SQLiteConnection.main();

            // Load the main data model
            Model dataModel = JenaUtil.createMemoryModel();
            Model shapeModel = JenaUtil.createMemoryModel();

            dataModel.read(new ByteArrayInputStream(dataGraph.getBytes()) , "urn:attx", FileUtils.langTurtle);
            shapeModel.read(new ByteArrayInputStream(shapesGraph.getBytes()) , "urn:attx", FileUtils.langTurtle);

            // Perform the validation of everything, using the data model
            // also as the shapes model - you may have them separated
            Resource report = ValidationUtil.validateModel(dataModel, shapeModel, true);


            // Print violations
            System.out.println(ModelPrinter.get().print(report.getModel()));

            String result = ModelPrinter.get().print(report.getModel());

            data.insert("1", result);

            response.status(200); // 200 Done
            response.type("text/plain");
            return result;
        });

        get(String.format("/%s/report/:reportID", apiVersion), (request, response) -> {
            String id = request.params(":reportID");

            return "Hello: " + id;
        });



    }
}