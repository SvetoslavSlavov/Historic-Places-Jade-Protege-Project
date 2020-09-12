package uni.fmi.masters;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uni.fmi.masters.entities.EntityPerson;
import uni.fmi.masters.entities.EntityPlace;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentQuestionAnswerer extends Agent{
	private static final long serialVersionUID = 1L;
	private RevivalistsOntology revivalistsOntology;
	
	protected void setup() {
		//Init ontology
		revivalistsOntology = new RevivalistsOntology();
		
		//System.out.println("QuestionAnswerer agent " + getAID().getName() + " is ready.");

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bulgarian-revivalists-answers");
		sd.setName("JADE-question-answering");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new AnswerGiveServer());
	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("QuestionAnswerer agent " + getAID().getName() + " terminating.");
	}

	private class AnswerGiveServer extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private String getAnswer(String question){
			
			// Списък (с|на) (...групи...)(те)[\.!\?]?
			{
				String pattern = "^\\s*Списък\\s+(?:с|на)\\s+(\\S+?)(?:те)?[\\.!\\?]?\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					String clsstr;
					switch(m.group(1).toLowerCase()) {
						case "възрожденци": clsstr = "Person"; break;
						case "писатели": clsstr = "Writer"; break;
						case "духовници": clsstr = "Cleric"; break;
						case "революционери": clsstr = "Revolutionary"; break;
						default: return "Няма данни за такава група.";
					}
					OWLClass cls = revivalistsOntology.getOWLClassByName(clsstr);
					Set<OWLIndividual> indvs = revivalistsOntology.getIndividualsByClass(cls);
					StringBuilder str = new StringBuilder("");
					for(OWLIndividual indv: indvs) {
						if (!str.toString().equals("")) {
							str.append("\n");
						}
						str.append(revivalistsOntology.getPersonFromIndividual(indv));
					}
					if (str.toString().trim().equals("")) {
						return "Празен.";
					}
					return str.toString();
				}
			}
			
			// Списък (на|със) (активности|дейности|събития), в които е участвал (?)?
			{
				String pattern = "^\\s*(?:Списък\\s+(?:на|с|със)\\s+)?(?:активности|дейности|събития)(?:(?:\\s*,\\s*|\\s+)в\\s+които\\s+е\\s+участвала?|\\s+на)\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("participatedIn");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					if (indvsrel == null || indvsrel.toString().equals("[]")) {
						return "Няма информация за активности, в които е участвал.";
					}
					StringBuilder str = new StringBuilder("");
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						if (!str.toString().equals("")) {
							str.append("\n");
						}
						str.append(revivalistsOntology.getActivityFromIndividual(one));
					}					
					return str.toString();
				}
			}
			
			// Списък (на|със) трудове на (?)?
			{
				String pattern = "^\\s*(?:Списък\\s+(?:на|с|със)\\s+)?трудове(?:те)?\\s+на\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("creatorOf");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					if (indvsrel == null || indvsrel.toString().equals("[]")) {
						return "Няма информация за трудове на тази личност.";
					}
					StringBuilder str = new StringBuilder("");
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						if (!str.toString().equals("")) {
							str.append("\n");
						}
						str.append(revivalistsOntology.getWorkFromIndividual(one));
					}					
					return str.toString();
				}
			}
			
			// Кой е (написал|създал|автор(?:а|ът)?\\s+на) (?)?
			{
				String pattern = "^\\s*Кой\\s+е\\s+(написал|създал|автор(?:а|ът)?\\s+на)\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualWorkByName(m.group(2));
					if (indv == null) {
						return "Няма данни за тази творба.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("createdBy");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					if (indvsrel == null || indvsrel.toString().equals("[]")) {
						return "Няма данни за това кой е " + m.group(1) + " тази творба.";
					}
					StringBuilder str = new StringBuilder("");
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						if (!str.toString().equals("")) {
							str.append("\n");
						}
						str.append(revivalistsOntology.getPersonFromIndividual(one));
					}					
					return str.toString();
				}
			}
			
			// Истинското име на (?)?
			{
				String pattern = "^\\s*(?:(?:Как|Кое|Какво)\\s+е\\s+)?истинското\\s+име\\s+на\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					EntityPerson p = revivalistsOntology.getPersonFromIndividual(indv);
					return p.getRealName();
				}
			}
			
			// (?) е известен (?:още)? като?
			{
				String pattern = "^\\s*(\\S.*?)\\s+е\\s+(извест(?:ен|на)|познат[аи]?)(?:\\s+още)?\\s+(?:като|с\\s+името)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					EntityPerson p = revivalistsOntology.getPersonFromIndividual(indv);
					if (p.getNameAlt() != null) {
						return p.getNameAlt();
					}
					return "Няма информация да е " + m.group(2) + " с по-специално име.";
				}
			}
			
			//"Кога е роден(а) (?)?"
			{
				String pattern = "^\\s*Кога\\s+е\\s+роден(?:а)?\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					EntityPerson p = revivalistsOntology.getPersonFromIndividual(indv);
					String ret = p.getBornDate();
					if (ret == null) {
						return "Няма налични данни за това.";
					}
					return ret;
				}
			}
			
			//"Кога е (починал|умрял)a? (?)?"
			{
				String pattern = "^\\s*Кога\\s+е\\s+(?:починал|умрял)(?:а)?\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					EntityPerson p = revivalistsOntology.getPersonFromIndividual(indv);
					String ret = p.getDiedDate();
					if (ret == null) {
						return "Няма налични данни за това.";
					}
					return p.getDiedDate();
				}
			}
			
			// Къде е роден(а) (?)?
			{
				String pattern = "^\\s*Къде\\s+е\\s+(?:роден)(?:а)?\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("bornIn");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					String bornIn = "Няма налични данни за това.";
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						EntityPlace p = revivalistsOntology.getPlaceFromIndividual(one);
						bornIn = p.getName();
					}					
					return bornIn;
				}
			}
			
			//"Ко(и|й) (e|са) роден в (?)?"
			{
				String pattern = "^\\s*(?:Ко[и|й]\\s+(?:е|са)\\s+)?роден[аи]?\\s+в(?:ъв)?\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPlaceByName(m.group(1));
					if (indv == null) {
						return "Няма данни за това място.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("bornPlaceOf");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					if (indvsrel == null || indvsrel.toString().equals("[]")) {
						return "Няма информация за родени на това място.";
					}
					StringBuilder str = new StringBuilder("");
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						if (!str.toString().equals("")) {
							str.append("\n");
						}
						str.append(revivalistsOntology.getPersonFromIndividual(one));
					}					
					return str.toString();
				}
			}
			
			// //"Кога е (починал|умрял)a? (?)?"
			{
				String pattern = "^\\s*Къде\\s+е\\s+(?:починал|умрял)(?:а)?\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPersonByName(m.group(1));
					if (indv == null) {
						return "Няма данни за тази личност.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("diedIn");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					String bornIn = "Няма налични данни за това.";
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						EntityPlace p = revivalistsOntology.getPlaceFromIndividual(one);
						bornIn = p.getName();
					}					
					return bornIn;
				}
			}
			
			//"Ко(и|й) (e|са) (починал|умрял) в (?)?"
			{
				String pattern = "^\\s*(?:Ко[и|й]\\s+(?:е|са)\\s+)?((?:починал|умрял)[аи]?)\\s+в(?:ъв)?\\s+(.*?)\\??\\s*$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher m = r.matcher(question);
				if (m.find()) {
					OWLIndividual indv = revivalistsOntology.getIndividualPlaceByName(m.group(2));
					if (indv == null) {
						return "Няма данни за това място.";
					}
					OWLObjectPropertyExpression prop = revivalistsOntology.getOWLObjectPropertyByName("diedPlaceOf");
					Set<OWLIndividual> indvsrel = revivalistsOntology.getRelatedIndividuals(indv, prop);
					if (indvsrel == null || indvsrel.toString().equals("[]")) {
						return "Няма информация за " + m.group(1) + " на това място.";
					}
					StringBuilder str = new StringBuilder("");
					for(OWLIndividual one: indvsrel) { // no more than one loop happens
						if (!str.toString().equals("")) {
							str.append("\n");
						}
						str.append(revivalistsOntology.getPersonFromIndividual(one));
					}					
					return str.toString();
				}
			}

			return "Не разбирам въпроса.";
		}
		
		public void action() {			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String question = msg.getContent();
				String answer = getAnswer(question); // get answer for question that was sent
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(answer);
				myAgent.send(reply);
			}
			else {
				block();
			}
		}	
	}
	
}
