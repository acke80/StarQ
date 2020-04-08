package com.redpill_linpro.query_service;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;

import java.util.Properties;
import java.util.Scanner;

import static com.redpill_linpro.query_service.ApplicationProp.*;

public final class NerTrain {

    private static boolean running;

    private NerTrain(){
    }

    public static void run(){
        Scanner scanner = new Scanner(System.in);
        String input;

        while(running){
            System.out.println("model:        " + getAppPropertyFile("model").getPath());
            System.out.println("properties:   " + getAppPropertyFile("trainProp").getPath());
            System.out.println("trainingData: " + getAppPropertyFile("trainingData").getPath());
            System.out.println("Type 'train' to train the model");
            input = scanner.nextLine();

            if(input.equals("train")){
                train(
                        getAppPropertyFile("model").getPath(),
                        getAppPropertyFile("trainProp").getPath(),
                        getAppPropertyFile("trainingData").getPath());

                System.out.println("\nTraining Successful");
            }else if(input.equals("exit"))
                break;
        }
    }

    /** Trains the model with the CRFClassifier, using the Conditional Random Field model.
     * @param trainingDataPath is null, the path defined in the properties file need to be set.
     * @param modelPath the path were we write the model to. write to model if the file already exist,
     * creates the file otherwise. */
    private static void train(String modelPath, String propertiesPath, String trainingDataPath) {
        Properties properties = StringUtils.propFileToProperties(propertiesPath);
        properties.setProperty("serializeTo", modelPath);

        if (trainingDataPath != null)
            properties.setProperty("trainFile", trainingDataPath);

        SeqClassifierFlags flags = new SeqClassifierFlags(properties);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
        crf.train();
        crf.serializeClassifier(modelPath);
    }

    public static void main(String[] args){
        NerTrain.running = true;
        NerTrain.run();
    }
}
