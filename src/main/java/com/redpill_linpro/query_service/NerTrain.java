package com.redpill_linpro.query_service;

import com.redpill_linpro.query_service.util.FileHandler;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;

import java.util.Properties;
import java.util.Scanner;

public final class NerTrain {

    public static FileHandler propertiesFile = new FileHandler("roth.properties");
    public static FileHandler modelFile = new FileHandler("model.ser");
    public static FileHandler trainFile = new FileHandler("trainingData.txt");

    private static boolean running;

    private NerTrain(){
    }

    public static void run(){
        Scanner scanner = new Scanner(System.in);
        String input;

        while(running){
            System.out.println("model:        " + modelFile.getPath());
            System.out.println("properties:   " + propertiesFile.getPath());
            System.out.println("trainingData: " + trainFile.getPath());
            System.out.println("Type 'train' to train the model");
            input = scanner.nextLine();

            if(input.equals("train")){
                train(
                        modelFile.getPath(),
                        propertiesFile.getPath(),
                        trainFile.getPath());
                System.out.println("\nTraining Succesful");
            }
        }
    }

    /** Trains the model with the CRFClassifier, using the Conditional Random Field model.
     * @param trainingDataPath is null, the path defined in the properties file need to be set.
     * @param modelPath the path were we write the model to. Overwrites if the file already exist,
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

    private static CRFClassifier getModel(String modelPath) {
        return CRFClassifier.getClassifierNoExceptions(modelPath);
    }

    public static void main(String[] args){
        NerTrain.running = true;
        NerTrain.run();
    }
}
