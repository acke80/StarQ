package com.redpill_linpro.query_service.formatter;


import com.redpill_linpro.query_service.util.SimpleTriple;
import com.redpill_linpro.query_service.util.Vocabulary;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.StringUtils;
import org.eclipse.jetty.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Used to format the OpenIE triples,
 * so they can be used in the SPARQL query */
public class TripleFormatter {

    private List<SimpleTriple> formattedTriples = new ArrayList<>();
    private List<RelationTriple> triples;
    private List<CoreLabel> coreLabels;

    private String rootLabel;
    private String rootEntity;
    private String relation;

    public TripleFormatter(List<CoreLabel> coreLabels, Collection<RelationTriple> triples){
        this.coreLabels = new ArrayList<>(coreLabels);
        this.triples = new ArrayList<>(triples);

        for(RelationTriple t : triples)
            System.out.println(t.subjectLemmaGloss()+"-"+t.relationLemmaGloss()+"-"+t.objectLemmaGloss());

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

    /** Finds the relation between the root and answer. */
    private void findRelation(){
        if(triples.size() == 1){
            RelationTriple triple = triples.get(0);
            String relation = triple.relationLemmaGloss();

            if(!hasRootLabel()){
                if(relation.equals("be") || relation.equals("have")) {
                    //this.relation = "voc:" + triple.objectLemmaGloss();
                    rootEntity = triple.objectLemmaGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }else if(relation.contains("be") && (relation.contains("of"))){
                    this.relation =  "voc:" + relation.replace("be ", "").replace (" of", "");
                    rootEntity = triple.objectLemmaGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }else if(relation.contains("be") && (relation.contains("on"))){
                    this.relation =  "voc:" + relation.replace("be ", "").replace(" on", "");
                    rootEntity = triple.objectLemmaGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }
            }else{
                if(relation.equals("be") || relation.equals("have")){
                    if(triple.subjectLemmaGloss().equals(rootLabel))
                        this.relation = "voc:" + triple.objectLemmaGloss();
                    else
                        this.relation = "voc:" + triple.subjectLemmaGloss();
                }else if(relation.contains("be") && relation.contains("with")){
                    this.relation =  "voc:" + relation.replace("be ", "").replace(" with", "");
                }else if(relation.contains("be") && relation.contains("of")){
                    this.relation =  "voc:" + relation.replace("be ", "").replace(" of", "");
                }
            }
        }else{
            String relationWord = "";
            boolean isCorrectTriples = false;

            for(RelationTriple triple : triples){
                String subject = triple.subjectLemmaGloss();
                String relation = triple.relationLemmaGloss();
                String object = triple.objectLemmaGloss();

                if(subject.equals("thing")){
                    if(relation.equals("be"))
                        relationWord = object;
                    else if(object.equals(rootLabel))
                        isCorrectTriples = true;
                }
            }

            if(isCorrectTriples)
                relation = "voc:" + relationWord;

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

    /*
    TODO: check for camelCase relations in the voc, and match them with lowercase etc.
    TODO: for example; voc has eyeColor relation, but user types eyecolor.
    TODO: maybe implement Word2Vec with Stanford GloVe.
    TODO: for example; person should map to human in the Star Wars voc.
     */
    /**Match the relation with a relation in the vocabulary. */
    private String matchRelation(String relation){
        return null;
    }
}
