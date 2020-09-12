package organisations;

import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class OrganisationAgent extends GuiAgent {
	private OrganisationGUI gui;
	@Override
	protected void setup() {
		gui = new OrganisationGUI();
		gui.setOrganisationAgent(this);
		System.out.println("This is a historic organisation agent");

		addBehaviour(new CyclicBehaviour() {
			private int counter;
			@Override
			public void action() {
				++counter;
				gui.showMessage("Cyclic Behaviour" + counter, true);
				
				/* MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(
						ACLMessage.INFORM), MessageTemplate.MatchOntology("ontology"));
				
				ACLMessage aclMessage = receive(messageTemplate); */
				
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					gui.showMessage("Sender" + aclMessage.getSender(), true);
					gui.showMessage("Communication" + aclMessage.getPerformative(), true);
					gui.showMessage("Content:" + aclMessage.getContent(), true);
					gui.showMessage("Language:" + aclMessage.getLanguage(), true);
					gui.showMessage("Ontology:" + aclMessage.getOntology(), true);
					gui.showMessage("X :" + aclMessage.getUserDefinedParameter("x"), true);
					gui.showMessage("Y :" + aclMessage.getUserDefinedParameter("y"), true);
				}
				else {
					block();
				}
			}
		});
		

		
		
	}
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		switch(ev.getType()) {
		case 1: 
			 
			Map<String, Object> params = (Map<String, Object>)ev.getParameter(0);
			
			String delivered = (String)params.get("delivered");
			String agentArchelogies = (String)params.get("agentArchelogies");
					
			ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
			aclMessage.addReceiver(new AID(agentArchelogies, AID.ISLOCALNAME));
			aclMessage.setContent(delivered);
			aclMessage.setOntology("history-ontology");
			send(aclMessage);
			break;
		default:
			break;
		}
	}
	
//	ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
//	
//	addBehaviour(parallelBehaviour);
//	
//	parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
//		
//		@Override
//		public void action() {
//			gui.showMessage("Behaviour OneShot", true);
//		}
//	});
//	
//	parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 1000) {
//		private int counter;
//		@Override
//		public void onTick() {
//			++counter;
//			gui.showMessage("Ticker behaviour" + counter, true);
//		}
//	});
//		
//	parallelBehaviour.addSubBehaviour(new Behaviour() {
//	private int counter;
//	@Override
//	public void action() {
//		++counter;
//		gui.showMessage("Generic Behaviour" + counter, true);
//	}
//	@Override
//	public boolean done() {
//		if(counter == 8) {
//			return true;
//		}
//		else {
//			return false;
//		}
//	}
//});
}
