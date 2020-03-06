package com.redpill_linpro.query_service;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.QuestionToStatementTranslator;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class QueryParser {

    private List<List<CoreLabel>> coreLabelsList;

    private String question;

    private Annotation document;

    public QueryParser(String question){
        this.question = question;

        initPipeline();
        generateStatements();

        for (List<CoreLabel> coreLabels : coreLabelsList)
            generateTriples(coreLabels);

    }

    /** Initialize the CoreNLP Pipeline and annotate the
     * sentences in the question. */
    private void initPipeline(){
        document = new Annotation(question);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(document);
    }

    /** Convert questions from the annotated document to statements.
     * The Questions must end with a '?' for translator to work. */
    private void generateStatements(){
        QuestionToStatementTranslator qtst = new QuestionToStatementTranslator();
        coreLabelsList = new ArrayList<>();

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            List<List<CoreLabel>> coreLabelsSingletonList = qtst.toStatement(sentence.get(CoreAnnotations.TokensAnnotation.class));
            coreLabelsList.add(coreLabelsSingletonList.get(0));
        }
    }

    /** Generate Triples from the Statements. */
    private void generateTriples(List<CoreLabel> coreLabels){
        StringBuilder sb = new StringBuilder();
        for (CoreLabel coreLabel : coreLabels){
            /*if(coreLabel.word().contains("of"))
                sb.append("in").append(" ");
            else*/
                sb.append(coreLabel.lemma()).append(" ");
        }
        String statement = sb.toString();

        Collection<Quadruple<String, String, String, Double>> triples = new Sentence(statement).openie();

        System.out.println("\nStatement: " + statement);
        System.out.println("Triples: ");


        for(Quadruple<String, String, String, Double> rt : triples){
            System.out.println(rt.first + " - " + rt.second + " - " + rt.third);
        }


    }

}