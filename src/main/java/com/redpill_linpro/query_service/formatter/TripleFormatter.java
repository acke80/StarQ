package com.redpill_linpro.query_service.triple;


import com.redpill_linpro.query_service.util.Vocabulary;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Used to format the OpenIE triples, so they match the
 * vocabulary and can be used in the SPARQL query */
public class TripleFormatter {

    private List<SimpleTriple> formattedTriples = new ArrayList<>();
    private List<RelationTriple> triples;
    private List<CoreLabel> coreLabels;

    Vocabulary vocabulary;

    private String rootLabel;
    private String rootEntity;
    private String relation;

    public TripleFormatter(List<CoreLabel> coreLabels, Collection<RelationTriple> triples, Vocabulary vocabulary){
        this.coreLabels = new ArrayList<>(coreLabels);
        this.triples = new ArrayList<>(triples);
        this.vocabulary = vocabulary;

        for(RelationTriple t : triples)
            System.out.println(t.subjectLemmaGloss()+"-"+t.relationLemmaGloss()+"-"+t.objectLemmaGloss());

        findRootEntity();
        findRelation();

        System.out.println("LABEL: " + rootLabel);
        System.out.println("TYPE : " + rootEntity);

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

    /** Finds the entity in the triple(s), and gets the label and entityType. */
    private void findRootEntity(){
        for(CoreLabel coreLabel : coreLabels){
            if(!coreLabel.ner().equals("O")){
                if(rootLabel == null){
                    rootLabel = coreLabel.word();
                    rootEntity = coreLabel.ner();
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

            if(rootLabel == null){
                if(relation.equals("be"))
                    this.relation = "voc:" + triple.objectLemmaGloss();
                else if(relation.contains("be") && relation.contains("of")){
                    this.relation =  "voc:" + relation.replace("be ", "").replace(" of", "");
                    rootEntity = triple.objectLemmaGloss();
                    rootEntity = "voc:" + StringUtils.capitalize(rootEntity);
                }
            }else{
                if(relation.equals("be"))
                    if(triple.subjectLemmaGloss().equals(rootLabel))
                        this.relation = "voc:" + triple.objectLemmaGloss();
                    else
                        this.relation = "voc:" + triple.subjectLemmaGloss();
            }
        }else{

        }
    }

    /**@return A SimpleTriple for selecting the resource linked
     * with the rdfs:label rootLabel.*/
    private SimpleTriple getRootResourceTriple(){
        if(rootLabel == null){
            if(rootEntity == null)
                return new SimpleTriple("","","");
            else
                return new SimpleTriple("?root", "a", rootEntity);
        }
        return new SimpleTriple("?root", "rdfs:label", "\""+ rootLabel +"\"");
    }

    /**@return A SimpleTriple in the form root-relation-answer,
     * which describes the relation between the root resource and the
     * answer we want to select. */
    private SimpleTriple getAnswerRelationTriple(){
        return new SimpleTriple("?root", relation, "?answer");
    }

    /**Match the relation with a relation in the vocabulary. */
    private String matchRelation(String relation){
        return null;
    }
}
