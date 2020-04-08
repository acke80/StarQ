package com.redpill_linpro.query_service.parser;

import com.redpill_linpro.query_service.formatter.SparqlFormatter;
import com.redpill_linpro.query_service.formatter.TripleFormatter;
import com.redpill_linpro.query_service.util.FileHandler;
import com.redpill_linpro.query_service.util.Vocabulary;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.QuestionToStatementTranslator;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import java.util.*;

public final class QueryParser {

    private HashMap<List<CoreLabel>, Collection<RelationTriple>> naturalParseMap;

    private List<String> sparqlQueries = new ArrayList<>();

    private String question;
    private List<String> statements;

    private Annotation document;

    private static Properties properties;
    private static StanfordCoreNLP pipeline;

    private Vocabulary vocabulary;

    public QueryParser(String question, Vocabulary vocabulary){
        this.vocabulary = vocabulary;
        statements = new ArrayList<>();

        if(question.substring(question.length() - 1).equals("?"))
            this.question = question;
        else
            this.question = question + "?";

        try{
            annotateDocument();
        }catch (InstantiationException e){
            e.printStackTrace();
        }

        try{
            generateStatements();
        } catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Question could not be translated to Statement");
        }

        for(Map.Entry<List<CoreLabel>, Collection<RelationTriple>> entry : naturalParseMap.entrySet()){
            TripleFormatter tf = new TripleFormatter(entry.getKey(), entry.getValue(), vocabulary);
            sparqlQueries.add(generateSparql(tf));
        }
    }

    /** Initialize the CoreNLP Properties */
    public static void initProperties(FileHandler nerModel){
        System.out.print("Loading Properties...");
        properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, pos, depparse, lemma, ner, natlog, openie");
        properties.setProperty("ner.model", nerModel.getPath());
        pipeline = new StanfordCoreNLP(properties);
        System.out.println("Properties loaded");
    }

    /** Annotate the document with the properties of the pipeline*/
    private void annotateDocument() throws InstantiationException{
        if(Objects.isNull(properties))
            throw new InstantiationException("Initialize properties before annotation");

        document = new Annotation(question);
        pipeline.annotate(document);
    }

    /** Convert questions from the annotated document to statements.
     * The Questions must end with a '?' for translator to work. */
    private void generateStatements() throws ArrayIndexOutOfBoundsException{
        QuestionToStatementTranslator qtst = new QuestionToStatementTranslator();
        naturalParseMap = new HashMap<>();

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            List<List<CoreLabel>> coreLabelsSingletonList = qtst.toStatement(sentence.get(CoreAnnotations.TokensAnnotation.class));
            naturalParseMap.put(coreLabelsSingletonList.get(0), generateTriples(coreLabelsSingletonList.get(0)));

        }
    }

    /** Generate Triples from the Statements. */
    private Collection<RelationTriple> generateTriples(List<CoreLabel> coreLabels){
        for(CoreLabel c : coreLabels)
            System.out.println(c.lemma() + " . " + c.tag());

        String formattedStatement = getFormattedRelationStatement(coreLabels);
        statements.add(formattedStatement);
        return new Sentence(formattedStatement).openieTriples();
    }

    /** Formats the relation nouns in the statement into one word.
     * The relation can consist of two nouns, or start with an
     * adjective followed by a noun. Alternative the second
     * noun can be a NNS (plural). More info on:
     * https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html*/
    private String getFormattedRelationStatement(List<CoreLabel> coreLabels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coreLabels.size(); i++) {
            CoreLabel iCoreLabel = coreLabels.get(i);

            if (!iCoreLabel.lemma().equals("thing") &&
                    (iCoreLabel.tag().equals("NN") || iCoreLabel.tag().equals("JJ"))) {

                StringBuilder nounCamelSb = new StringBuilder().append(iCoreLabel.lemma());
                for(int j = i + 1; j < coreLabels.size(); j++){
                    CoreLabel jCoreLabel = coreLabels.get(j);
                    System.out.println("J: " + jCoreLabel.lemma());
                    if(jCoreLabel.tag().equals("NN") || jCoreLabel.tag().equals("NNS")){
                        nounCamelSb.append(StringUtils.capitalize(jCoreLabel.word()));
                        i++;
                        iCoreLabel.setLemma(nounCamelSb.toString());
                        continue;
                    }
                    if(j + 1 < coreLabels.size()) {
                        if (jCoreLabel.tag().equals("IN") &&
                                (coreLabels.get(j + 1).tag().equals("NN") ||
                                        coreLabels.get(j + 1).tag().equals("NNS"))) {
                            nounCamelSb.append(StringUtils.capitalize(jCoreLabel.word())).
                                        append(StringUtils.capitalize(coreLabels.get(j + 1).word()));
                            j++;
                            i+=2;
                            iCoreLabel.setLemma(nounCamelSb.toString());
                            continue;
                        }
                    }
                    System.out.println("END J: " + jCoreLabel.lemma() + "  :" + nounCamelSb.toString());
                    iCoreLabel.setLemma(nounCamelSb.toString());
                    break;
                }
            }
            System.out.println("APPEND: " + iCoreLabel.lemma());
            sb.append(iCoreLabel.lemma()).append(" ");
        }
        return sb.toString();
    }

    /** Generate a Sparql Query for each sentence in the document. */
    private String generateSparql(TripleFormatter tripleFormatter){
        SparqlFormatter sf = new SparqlFormatter(tripleFormatter, vocabulary);
        return sf.createSparqlQuery();
    }

    public HashMap<List<CoreLabel>, Collection<RelationTriple>> getNaturalParseMap() {
        return naturalParseMap;
    }

    public List<String> getSparqlQueries() {
        return sparqlQueries;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getStatements() {
        return statements;
    }

    public Vocabulary getVocabulary(){
        return vocabulary;
    }

}