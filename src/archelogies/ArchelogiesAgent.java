package archelogies;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;

public class ArchelogiesAgent extends Agent {
	@Override
	protected void  setup() {
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					String delivered = aclMessage.getContent();
					System.out.println("Architect has receive the oraganisation message" + delivered);
					/*
					 * transaction 
					 */
					ACLMessage aclMessage2 = aclMessage.createReply();
					aclMessage2.setPerformative(ACLMessage.INFORM);
					
//					ACLMessage aclMessage2 = new ACLMessage(ACLMessage.INFORM);
//					aclMessage2.addReceiver(aclMessage.getSender());
					aclMessage2.setContent("reply of request receive");
					send(aclMessage2);
				}
				else {
					block();
				}
			}
			
		});
	}
}
