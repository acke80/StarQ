package com.redpill_linpro.query_service;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.QuestionToStatementTranslator;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueInteger;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;

import java.util.*;

public class QueryParser {

    private HashMap<List<CoreLabel>, Collection<RelationTriple>> naturalParseMap;

    private List<String> sparqlQueries;

    private String question;
    private List<String> statements;

    private Annotation document;

    public QueryParser(String question){
        this.question = question;
        statements = new ArrayList<>();

        initPipeline();

        try{
            generateStatements();
        } catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Question could not be translated to Statement");
        }

        sparqlQueries = generateSparql();

        int i = 0;
        for(Collection<RelationTriple> crt : naturalParseMap.values()){
            System.out.println("Statement: " + statements.get(i));
            System.out.println("\nTriples: ");
            for(RelationTriple rt : crt){
                System.out.println(rt.subjectGloss() + " - " + rt.relationGloss() + " - " + rt.objectGloss());
            }
            System.out.println("\nSparql Query: \n" + sparqlQueries.get(i));
            System.out.println("____________________________________________");
            i++;
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
        StringBuilder sb = new StringBuilder();
        for (CoreLabel coreLabel : coreLabels){
            sb.append(coreLabel.lemma()).append(" ");
        }
        statements.add(sb.toString());
        return new Sentence(sb.toString()).openieTriples();
    }

    /** Generate a Sparql Query for each sentence
     * in the document. */
    private List<String> generateSparql(){
        List<String> queries = new ArrayList<>();

        for(Collection<RelationTriple> crt : naturalParseMap.values()) {
            ElementTriplesBlock block = new ElementTriplesBlock();

            for (RelationTriple rt : crt)
                block.addTriple(
                        Triple.create(
                                Var.alloc(rt.subjectLemmaGloss()),
                                Var.alloc(rt.relationLemmaGloss()),
                                Var.alloc(rt.objectLemmaGloss())));

            ElementGroup body = new ElementGroup();
            body.addElement(block);

            Query q = QueryFactory.make();
            q.setQueryPattern(body);
            q.setQuerySelectType();
            q.addResultVar(Var.ANON);

            queries.add(q.toString());
        }

        return queries;
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

}