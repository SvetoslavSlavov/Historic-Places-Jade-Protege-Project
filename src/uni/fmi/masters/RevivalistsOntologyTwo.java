package uni.fmi.masters;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uni.fmi.masters.entities.EntityActivity;
import uni.fmi.masters.entities.EntityPerson;
import uni.fmi.masters.entities.EntityPlace;
import uni.fmi.masters.entities.EntityWork;

public class RevivalistsOntology {

  private OWLOntologyManager ontoManager;
  private OWLOntology revivalistsOntology;
  private OWLDataFactory dataFactory;
  private OWLReasoner reasoner;

  final private String ontologyPath = "src/ontology/historicPlaces.owl";
  private String IRIStr;

  public RevivalistsOntology() {
    ontoManager = OWLManager.createOWLOntologyManager();
    dataFactory = ontoManager.getOWLDataFactory();
    loadOntologyFromFile();
    IRIStr = revivalistsOntology.getOntologyID().getOntologyIRI().toString() + "#";
    runReasoner(); // eager
  }

  public void loadOntologyFromFile() {
    File ontoFile = new File(ontologyPath);
    try {
      revivalistsOntology = ontoManager.loadOntologyFromOntologyDocument(ontoFile);
    } catch (OWLOntologyCreationException e) {
      e.printStackTrace();
    }
  }

  public void runReasoner() { // singleton
    if (reasoner == null) {
      OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
      reasoner = reasonerFactory.createReasoner(revivalistsOntology);
    }
    reasoner.precomputeInferences();
  }

  public OWLClass getOWLClassByName(String className) {
    return dataFactory.getOWLClass(IRI.create(IRIStr + className));
  }

  public OWLDataProperty getOWLDataPropertyByName(String prop) {
    return dataFactory.getOWLDataProperty(IRI.create(IRIStr + prop));
  }

  public OWLObjectProperty getOWLObjectPropertyByName(String prop) {
    return dataFactory.getOWLObjectProperty(IRI.create(IRIStr + prop));
  }

  public Set<OWLClass> getClassesThisIndividualBelongsTo(OWLIndividual indv) { // return a set of all classes this
                                                                               // individual belongs to
    return reasoner.getTypes(indv.asOWLNamedIndividual(), false).getFlattened();
  }

  public boolean isIndividualBelongsTo(OWLIndividual indv, OWLClass cls) { // check if an individual belongs to some
                                                                           // class (including subclasses)
    for (OWLClass one : getClassesThisIndividualBelongsTo(indv)) {
      if (one.equals(cls)) {
        return true;
      }
    }
    return false;
  }

  public Set<OWLIndividual> getIndividualsByClass(OWLClass cls) { // get individuals belongs to some class (including
                                                                  // from subclasses; without duplication)
    Set<OWLIndividual> ret = new HashSet<OWLIndividual>();
    for (OWLNamedIndividual indv : revivalistsOntology.getIndividualsInSignature()) {
      if (isIndividualBelongsTo(indv, cls)) {
        ret.add(indv);
      }
    }
    return ret;
  }

  public String getValueFromDataProperty(OWLIndividual indv, OWLDataProperty dataPropertyName) {
    Set<OWLLiteral> props = indv.getDataPropertyValues(dataPropertyName, revivalistsOntology);
    for (OWLLiteral prop : props) {
      return prop.getLiteral();
    }
    return null;
  }

  public String getValueFromDataProperty(OWLIndividual indv, String dataPropertyName) {
    return getValueFromDataProperty(indv, getOWLDataPropertyByName(dataPropertyName));
  }

  public Set<OWLIndividual> getRelatedIndividuals(OWLIndividual indv, OWLObjectPropertyExpression prop) {
    Set<OWLNamedIndividual> res = reasoner.getObjectPropertyValues(indv.asOWLNamedIndividual(), prop).getFlattened();
    Set<OWLIndividual> ret = new HashSet<OWLIndividual>();
    for (OWLIndividual one : res) { // convert OWLNamedIndividual to OWLIndividual
      ret.add(one);
    }
    return ret;
  }

  public Set<OWLIndividual> getRelatedIndividuals(OWLIndividual indv, String prop) {
    return getRelatedIndividuals(indv, getOWLObjectPropertyByName(prop));
  }

  public EntityActivity getActivityFromIndividual(OWLIndividual indv) {
    if (indv != null && isIndividualBelongsTo(indv, getOWLClassByName("Activity"))) {
      // Convert to Integer
      String tmp;
      tmp = getValueFromDataProperty(indv, "activity_started_d");
      Integer activityStartedD = null;
      if (tmp != null) {
        activityStartedD = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "activity_started_m");
      Integer activityStartedM = null;
      if (tmp != null) {
        activityStartedM = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "activity_started_y");
      Integer activityStartedY = null;
      if (tmp != null) {
        activityStartedY = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "activity_finished_d");
      Integer activityFinishedD = null;
      if (tmp != null) {
        activityFinishedD = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "activity_finished_m");
      Integer activityFinishedM = null;
      if (tmp != null) {
        activityFinishedM = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "activity_finished_y");
      Integer activityFinishedY = null;
      if (tmp != null) {
        activityFinishedY = Integer.parseInt(tmp);
      }
      String activityName = getValueFromDataProperty(indv, "activity_name");
      if (activityName == null) { // if it's part of abstract activity
        OWLObjectPropertyExpression prop = getOWLObjectPropertyByName("partOf");
        Set<OWLIndividual> indvsrel = getRelatedIndividuals(indv, prop);
        activityName = "?";
        for (OWLIndividual one : indvsrel) { // no more than one loop happens
          activityName = getValueFromDataProperty(one, "activity_name");
          if (activityName == null) {
            activityName = "?";
          }
        }
      }
      return new EntityActivity(activityName, activityStartedD, activityStartedM, activityStartedY, activityFinishedD,
          activityFinishedM, activityFinishedY);
    }
    return null;
  }

  public EntityPerson getPersonFromIndividual(OWLIndividual indv) {
    if (indv != null && isIndividualBelongsTo(indv, getOWLClassByName("Person"))) {
      // Convert to Integer
      String tmp;
      tmp = getValueFromDataProperty(indv, "person_born_d");
      Integer personBornD = null;
      if (tmp != null) {
        personBornD = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "person_born_m");
      Integer personBornM = null;
      if (tmp != null) {
        personBornM = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "person_born_y");
      Integer personBornY = null;
      if (tmp != null) {
        personBornY = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "person_died_d");
      Integer personDiedD = null;
      if (tmp != null) {
        personDiedD = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "person_died_m");
      Integer personDiedM = null;
      if (tmp != null) {
        personDiedM = Integer.parseInt(tmp);
      }
      tmp = getValueFromDataProperty(indv, "person_died_y");
      Integer personDiedY = null;
      if (tmp != null) {
        personDiedY = Integer.parseInt(tmp);
      }
      ///
      return new EntityPerson(getValueFromDataProperty(indv, "person_name_first"),
          getValueFromDataProperty(indv, "person_name_sur"), getValueFromDataProperty(indv, "person_name_family"),
          getValueFromDataProperty(indv, "person_name_alt"), personBornD, personBornM, personBornY, personDiedD,
          personDiedM, personDiedY);
    }
    return null;
  }

  public EntityPlace getPlaceFromIndividual(OWLIndividual indv) {
    if (indv != null && isIndividualBelongsTo(indv, getOWLClassByName("Place"))) {
      // Convert to Double
      String tmp;
      tmp = getValueFromDataProperty(indv, "place_latitude");
      Double placeLatitude = null;
      if (tmp != null) {
        placeLatitude = Double.parseDouble(tmp);
      }
      tmp = getValueFromDataProperty(indv, "place_longitude");
      Double placeLongitude = null;
      if (tmp != null) {
        placeLongitude = Double.parseDouble(tmp);
      }
      ///
      return new EntityPlace(getValueFromDataProperty(indv, "place_name"),
          getValueFromDataProperty(indv, "place_name_alt"), placeLatitude, placeLongitude);
    }
    return null;

  }

  public EntityWork getWorkFromIndividual(OWLIndividual indv) {
    if (indv != null && isIndividualBelongsTo(indv, getOWLClassByName("Work"))) {
      // Convert to Integer
      String tmp;
      tmp = getValueFromDataProperty(indv, "work_created_y");
      Integer workCreatedY = null;
      if (tmp != null) {
        workCreatedY = Integer.parseInt(tmp);
      }
      ///
      return new EntityWork(getValueFromDataProperty(indv, "work_name"), workCreatedY);
    }
    return null;
  }

  public OWLIndividual getIndividualPersonByName(String name) {
    OWLClass cls = getOWLClassByName("Person");
    Set<OWLIndividual> indvs = getIndividualsByClass(cls);
    for (OWLIndividual indv : indvs) {
      EntityPerson p = getPersonFromIndividual(indv);
      if (name.equals(p.getNameFirst() + " " + p.getNameFamily())
          || name.equals(p.getNameFirst() + " " + p.getNameSur() + " " + p.getNameFamily())
          || name.equals(p.getNameAlt())) {
        return indv;
      }
    }
    return null;
  }

  public OWLIndividual getIndividualPlaceByName(String name) {
    OWLClass cls = getOWLClassByName("Place");
    Set<OWLIndividual> indvs = getIndividualsByClass(cls);
    for (OWLIndividual indv : indvs) {
      EntityPlace p = getPlaceFromIndividual(indv);
      if ((p.getName() != null && p.getName().equals(name))
          || (p.getNameAlt() != null && p.getNameAlt().equals(name))) {
        return indv;
      }
    }
    return null;
  }

  public OWLIndividual getIndividualWorkByName(String name) {
    OWLClass cls = getOWLClassByName("Work");
    Set<OWLIndividual> indvs = getIndividualsByClass(cls);
    for (OWLIndividual indv : indvs) {
      EntityWork p = getWorkFromIndividual(indv);
      if (p.getName() != null && p.getName().equals(name)) {
        return indv;
      }
    }
    return null;
  }

}
