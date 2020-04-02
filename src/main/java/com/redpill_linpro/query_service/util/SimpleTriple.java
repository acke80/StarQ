package com.redpill_linpro.query_service.util;

import edu.stanford.nlp.ie.util.RelationTriple;

public class SimpleTriple {
    public final String SUBJECT;
    public final String RELATION;
    public final String OBJECT;

    public SimpleTriple(String subject, String relation, String object){
        SUBJECT = subject;
        RELATION = relation;
        OBJECT = object;
    }

    public static SimpleTriple RelationTripleToSimpleTriple(RelationTriple triple){
        return new SimpleTriple(
                triple.subjectLemmaGloss(),
                triple.relationLemmaGloss(),
                triple.objectLemmaGloss());
    }

    public boolean isEmpty() {
        return SUBJECT.isEmpty()
                && RELATION.isEmpty()
                && OBJECT.isEmpty();
    }
}
