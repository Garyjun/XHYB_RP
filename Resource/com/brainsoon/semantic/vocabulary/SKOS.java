package com.brainsoon.semantic.vocabulary; 
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
 

public class SKOS {    
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );    
    
    public static final String NS = "http://www.w3.org/2004/02/skos/core#";
    
    public static String getURI() {return NS;}    
    
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final ObjectProperty broadMatch = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#broadMatch" );
    
    public static final ObjectProperty broader = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#broader" );
    
    public static final ObjectProperty broaderTransitive = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#broaderTransitive" );
    
    public static final ObjectProperty closeMatch = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#closeMatch" );
    
    public static final ObjectProperty exactMatch = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#exactMatch" );
    
    public static final ObjectProperty hasTopConcept = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#hasTopConcept" );
    
    public static final ObjectProperty inScheme = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#inScheme" );
    
    public static final ObjectProperty mappingRelation = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#mappingRelation" );
    
    public static final ObjectProperty member = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#member" );
    
    public static final ObjectProperty memberList = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#memberList" );
    
    public static final ObjectProperty narrowMatch = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#narrowMatch" );
    
    public static final ObjectProperty narrower = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#narrower" );
    
    public static final ObjectProperty narrowerTransitive = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#narrowerTransitive" );
    
    public static final ObjectProperty related = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#related" );
    
    public static final Property relatedTransitive = m_model.createProperty( "http://www.w3.org/2004/02/skos/core#relatedTransitive" ); 
    
    public static final ObjectProperty relatedMatch = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#relatedMatch" );
    
    public static final ObjectProperty semanticRelation = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#semanticRelation" );
    
    public static final ObjectProperty topConceptOf = m_model.createObjectProperty( "http://www.w3.org/2004/02/skos/core#topConceptOf" );
    
    public static final DatatypeProperty notation = m_model.createDatatypeProperty( "http://www.w3.org/2004/02/skos/core#notation" );
    
    public static final AnnotationProperty altLabel = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#altLabel" );
    
    public static final AnnotationProperty changeNote = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#changeNote" );
    
    public static final AnnotationProperty definition = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#definition" );
    
    public static final AnnotationProperty editorialNote = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#editorialNote" );
    
    public static final AnnotationProperty example = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#example" );
    
    public static final AnnotationProperty hiddenLabel = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#hiddenLabel" );
    
    public static final AnnotationProperty historyNote = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#historyNote" );
    
    public static final AnnotationProperty note = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#note" );
    
    public static final AnnotationProperty prefLabel = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#prefLabel" );
    
    public static final AnnotationProperty scopeNote = m_model.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#scopeNote" );
    
    public static final Property ConceptType = m_model.createProperty("http://www.w3.org/2004/02/skos/core#Concept");
    
    public static final OntClass Collection = m_model.createClass( "http://www.w3.org/2004/02/skos/core#Collection" );
    
    public static final OntClass Concept = m_model.createClass( "http://www.w3.org/2004/02/skos/core#Concept" );
    
    public static final OntClass ConceptScheme = m_model.createClass( "http://www.w3.org/2004/02/skos/core#ConceptScheme" );
    
    public static final OntClass OrderedCollection = m_model.createClass( "http://www.w3.org/2004/02/skos/core#OrderedCollection" );

}
