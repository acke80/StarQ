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

import java.util.*;

public class QueryParser {

    private HashMap<List<CoreLabel>, Collection<RelationTriple>> naturalParseMap;

    private String question;
    private String statement;

    private Annotation document;

    public QueryParser(String question){
        this.question = question;

        initPipeline();
        generateStatements();

        System.out.println("\nStatement: " + statement);
        System.out.println("Triples: ");

        for(Collection<RelationTriple> crt : naturalParseMap.values()){
            for(RelationTriple rt : crt)
                System.out.println(rt.subjectGloss() + " - " + rt.relationGloss() + " - " + rt.objectGloss());
        }

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
        naturalParseMap = new HashMap<>();

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            List<List<CoreLabel>> coreLabelsSingletonList = qtst.toStatement(sentence.get(CoreAnnotations.TokensAnnotation.class));
            naturalParseMap.put(coreLabelsSingletonList.get(0), generateTriples(coreLabelsSingletonList.get(0)));
        }
    }

    /** Generate Triples from the Statements. */
    private Collection<RelationTriple> generateTriples(List<CoreLabel> coreLabels){
        StringBuilder sb = new StringBuilder();
        for (CoreLabel coreLabel : coreLabels){
            sb.append(coreLabel.lemma()).append(" ");
        }
        statement = sb.toString();

        return new Sentence(statement).openieTriples();

    }

}