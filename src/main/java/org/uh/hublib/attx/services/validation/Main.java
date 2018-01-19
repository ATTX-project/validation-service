package org.uh.hublib.attx.services.validation;

import static spark.Spark.*;
import org.topbraid.spin.util.JenaUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

public class Main {
    public static void main(String[] args) {
        int maxThreads = 6;
        int minThreads = 5;
        int timeOutMillis = 30000;
        String apiVersion = "0.2";

        port(4306);
        threadPool(maxThreads, minThreads, timeOutMillis);

        get("/health", (req, res) -> "Hello World");


        post(String.format("/%s/validation/report", apiVersion), "application/json", (request, response) -> {
            String content = request.body();

            // Load the main data model
            Model dataModel = JenaUtil.createMemoryModel();
//            dataModel.read(ValidationExample.class.getResourceAsStream("/sh/tests/core/property/class-001.test.ttl"), "urn:dummy", FileUtils.langTurtle);
            dataModel.read("https://raw.githubusercontent.com/TopQuadrant/shacl/master/src/test/resources/sh/tests/core/property/class-001.test.ttl", "urn:dummy", FileUtils.langTurtle);

            // Perform the validation of everything, using the data model
            // also as the shapes model - you may have them separated
            Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);


            // Print violations
            System.out.println(ModelPrinter.get().print(report.getModel()));

            String result = ModelPrinter.get().print(report.getModel());

            response.status(200); // 200 Created
            response.type("text/plain");
            return result;
        });

    }
}