package com.redpill_linpro.query_service.formatter;


import com.redpill_linpro.query_service.util.SimpleTriple;
import com.redpill_linpro.query_service.util.Vocabulary;
import edu.stanford.nlp.ie.util.RelationTriple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Used to format the OpenIE triples, so they match the
 * vocabulary and can be used in the SPARQL query */
public class TripleFormatter {

    private List<SimpleTriple> formattedTriples;
    private List<RelationTriple> triples;
    private Vocabulary vocabulary;

    public TripleFormatter(Collection<RelationTriple> triples, Vocabulary vocabulary){
        this.vocabulary = vocabulary;
        this.triples = new ArrayList<>(triples);

        for(RelationTriple t : triples)
            System.out.println(t.subjectLemmaGloss()+"-"+t.relationLemmaGloss()+"-"+t.objectLemmaGloss());
    }

    /** Checks if the Entity type has the relation in the repository.
     * @return false if no triples exist in the repository, else true. */
    public boolean checkValidEntityRelation(String entity, String relation) {
        return true;
    }

    /**@return A SimpleTriple for selecting the resource linked
     * with the rdfs:label rootLabel.*/
    private SimpleTriple getRootResourceTriple(String rootLabel) {
        return new SimpleTriple("?root", "rdfs:label", rootLabel);
    }

    /**@return A SimpleTriple in the form root-relation-answer,
     * which describes the relation between the root resource and the
     * answer we want to select. */
    private SimpleTriple getAnswerRelationTriple(String relation){
        return null;
    }

    /**Match the relation with a relation in the vocabulary. */
    private String matchRelation(String relation){
        return null;
    }

    public List<SimpleTriple> getFormattedTriples(){
        return formattedTriples;
    }
}
