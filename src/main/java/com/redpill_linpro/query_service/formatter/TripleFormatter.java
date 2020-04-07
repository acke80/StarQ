package com.redpill_linpro.query_service.formatter;


import com.redpill_linpro.query_service.util.SimpleTriple;
import com.redpill_linpro.query_service.util.Vocabulary;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sentiment.SentimentModel;
import edu.stanford.nlp.util.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.mapdb.Atomic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Used to format the OpenIE triples,
 * so they can be used in the SPARQL query */
public class TripleFormatter {

    private List<SimpleTriple> formattedTriples = new ArrayList<>();
    private List<RelationTriple> triples;
    private List<CoreLabel> coreLabels;
    private String[] vocabularyRelations;

    private String rootLabel;
    private String rootEntity;
    private String relation;

    public TripleFormatter(List<CoreLabel> coreLabels, Collection<RelationTriple> triples, Vocabulary vocabulary){
        this.coreLabels = new ArrayList<>(coreLabels);
        this.triples = new ArrayList<>(triples);
        vocabularyRelations = vocabulary.getRelations();

        for(RelationTriple t : triples)
            System.out.println(t.subjectGloss()+"-"+t.relationGloss()+"-"+t.objectGloss());

        findRootEntity();
        findRelation();

        formattedTriples.add(getRootTypeTriple());
        formattedTriples.add(getRootResourceTriple());
        formattedTriples.add(getAnswerRelationTriple());

        for(SimpleTriple s : formattedTriples)
            System.out.println(s.SUBJECT+" "+s.RELATION+" "+s.OBJECT);
    }

    /** Checks if the Entity type has the relation in the repository.
     * @return false if no triples exist in the repository, else true. */
    public boolean checkValidEntityRelation(String entity, String relation){
        return true;
    }

    public List<SimpleTriple> getFormattedTriples(){
        return formattedTriples;
    }

    public boolean hasRootLabel(){
        return rootLabel != null;
    }

    public boolean hasRelation(){
        return relation != null;
    }

    public String getRootLabel() {
        return rootLabel;
    }

    /** Finds the entity in the triple(s), and gets the label and entityType. */
    private void findRootEntity(){
        for(CoreLabel coreLabel : coreLabels){
            if(!coreLabel.ner().equals("O")){
                if(!hasRootLabel()){
                    rootLabel = coreLabel.word();
                    rootEntity = "voc:" +
                            StringUtils.capitalize(coreLabel.ner().toLowerCase());
                }else
                    rootLabel = (rootLabel + " " + coreLabel.word());
            }
        }
    }

    /** Finds the relation we are looking for. */
    private void findRelation(){
        if(triples.size() == 1){
            RelationTriple triple = triples.get(0);
            String relation = triple.relationGloss();

            if(!hasRootLabel()){
                if(relation.equals("be") || relation.equals("have")) {
                    rootEntity = triple.objectGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }else if(relation.contains("be") && (relation.contains("of"))){
                    this.relation = relation.replace("be ", "").replace (" of", "");
                    this.relation = "voc:" + getMatchedRelation(this.relation);
                    rootEntity = triple.objectGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }else if(relation.contains("be") && (relation.contains("on"))){
                    this.relation = relation.replace("be ", "").replace(" on", "");
                    this.relation = "voc:" + getMatchedRelation(this.relation);
                    rootEntity = triple.objectGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }
            }else{
                if(relation.equals("be") || relation.equals("have")){
                    if(triple.subjectGloss().equals(rootLabel))
                        this.relation = triple.objectGloss();
                    else
                        this.relation = triple.subjectGloss();

                    this.relation = "voc:" + getMatchedRelation(this.relation);

                }else if(relation.contains("be") && relation.contains("with")){
                    this.relation = relation.replace("be ", "").replace(" with", "");
                    this.relation = "voc:" + getMatchedRelation(this.relation);
                }else if(relation.contains("be") && relation.contains("of")){
                    this.relation =  relation.replace("be ", "").replace(" of", "");
                    this.relation = "voc:" + getMatchedRelation(this.relation);
                }
            }
        }else{
            String relationWord = "";
            boolean isCorrectTriples = false;

            for(RelationTriple triple : triples){
                String subject = triple.subjectGloss();
                String relation = triple.relationGloss();
                String object = triple.objectGloss();

                if(subject.equals("thing")){
                    if(relation.equals("be") && object.contains(rootLabel)){
                        isCorrectTriples = true;
                    }else if(relation.equals("be")) {
                        relationWord = object;
                    }else if(object.equals(rootLabel)) {
                        isCorrectTriples = true;
                    }
                }
            }

            if(isCorrectTriples)
                relation = "voc:" + getMatchedRelation(relationWord);

        }
    }

    /**@return A SimpleTriple for selecting the root Entity Type*/
    private SimpleTriple getRootTypeTriple(){
        if(rootEntity == null)
            return new SimpleTriple("","","");
        return new SimpleTriple("?root", "a", rootEntity);
    }

    /**@return A SimpleTriple for selecting the resource of root.*/
    private SimpleTriple getRootResourceTriple(){
        return new SimpleTriple("?root", "rdfs:label", "?rootLabel");
    }

    /**@return A SimpleTriple in the form root-relation-answer,
     * which describes the relation between the root resource and the
     * answer we want to select. */
    private SimpleTriple getAnswerRelationTriple(){
        if(hasRelation())
            return new SimpleTriple("?root", relation, "?answer");
        return new SimpleTriple("","","");
    }

    /**Match the relation with a relation in the vocabulary.
     * If the relation is short for a real word, for example
     * 'desc' is short for description; the method will match
     * description with desc. Obs. only for ASCII chars. */
    private String getMatchedRelation(String relation){
        SentimentModel model =
                SentimentModel.loadSerialized("edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

        boolean wordsRecognize = false;
        String[] relationSplit = relation.split("(?=\\p{Upper})");

        for(String s : relationSplit){
            wordsRecognize = model.wordVectors.containsKey(s);
            if(!model.wordVectors.containsKey(s)) {
                wordsRecognize = false;
                break;
            }
        }

        String relationLemma = relation.replace(" ", "").toLowerCase();
        String[] vocRelationSplit;
        String word1, word2;

        for(String r : vocabularyRelations){
            vocRelationSplit = r.split("(?=\\p{Upper})");
            word1 = vocRelationSplit[0];
            word2 = vocRelationSplit.length == 2 ? vocRelationSplit[1] : "";

            if(word2.equals("")) {
                if (relationLemma.equals(word1))
                    return word1;
                else if(wordsRecognize && relationLemma.contains(word1))
                    return word1;
            }else {
                if (wordsRecognize && word1.contains(relationSplit[0]) && word2.toLowerCase().contains(relationSplit[1]))
                    return word1 + word2;
                else if(wordsRecognize && relationLemma.contains(word1) && relationLemma.contains(word2.toLowerCase()))
                    return word1 + word2;
                else if(relationLemma.equals(word1 + word2.toLowerCase()))
                    return word1 + word2;
            }
        }
        return relationLemma;
    }
}
