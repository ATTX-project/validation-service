package org.uh.hulib.attx.services.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class SHACLUtils {
    public static String SHACLValitateText(String dataGraph, String shapesGraph) {

        Resource report = null;
        Model dataModel = JenaUtil.createMemoryModel();

        try {
            if(urlValidator(dataGraph) == true) {
                dataModel.read(dataGraph, "urn:attx", FileUtils.langTurtle);
            } else {
                dataModel.read(new ByteArrayInputStream(dataGraph.getBytes()), "urn:attx", FileUtils.langTurtle);
            }
            if(shapesGraph != null) {
                Model shapeModel = JenaUtil.createMemoryModel();
                if(urlValidator(shapesGraph) == true) {
                    shapeModel.read(shapesGraph, "urn:attx", FileUtils.langTurtle);
                } else {
                    shapeModel.read(new ByteArrayInputStream(shapesGraph.getBytes()), "urn:attx", FileUtils.langTurtle);
                }
                report = ValidationUtil.validateModel(dataModel, shapeModel, true);
            } else {
                report = ValidationUtil.validateModel(dataModel, dataModel, true);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            return ModelPrinter.get().print(report.getModel());
        }

    }

    public static boolean urlValidator(String url)
    {
        /*validating url*/
        try {
            new URL(url).toURI();
            return true;
        }
        catch (URISyntaxException exception) {
            return false;
        }

        catch (MalformedURLException exception) {
            return false;
        }
    }

}
