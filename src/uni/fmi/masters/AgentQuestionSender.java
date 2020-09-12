package uni.fmi.masters;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentQuestionSender extends Agent{
	private static final long serialVersionUID = 1L;
	private final String[] QUESTION = new String[]{"Списък на възрожденците", "Списък с духовници?", "Списък на революционери", "Списък с писателите?", "Списък на рептилите", "Кога е роден Васил Левски?", "Истинското име на Васил Левски?", "Кога е умрял Васил Иванов Кунчев", "Къде е роден Васил Левски", "Трудове на Васил Левски?", "Кога е роден Иван Вазов", "Иван Вазов е известен с името?", "Списък с трудове на Иван Вазов", "Кога е починал Христо Ботев?", "Христо Ботьов Петков е известен още като", "Къде е умрял Христо Петков?", "Трудовете на Христо Ботев", "Къде е починал Иван Минчов Вазов", "Списък със събития, в които е участвал Васил Левски?", "Списък на активности на Христо Ботев", "Дейности на Иван Вазов", "Кои са родени в град Карлово", "Кой е роден в град София?", "Кой е умрял в град София?", "Кой е написал Под игото?", "Кога е роден Али Баба?", "Колко е часът?"};
	private int question_current = 0;
	private AID[] answerAgents;

	protected void setup() {
		//System.out.println("QuestionSender agent " + getAID().getName() + " is ready.");

		addBehaviour(new TickerBehaviour(this, 5000) {
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("bulgarian-revivalists-answers");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					//System.out.print("Found the following AnswerSender agents: ");
					answerAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						answerAgents[i] = result[i].getName();
						//System.out.print(answerAgents[i].getName() + " ");
					}
					//System.out.println();
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}

				// Perform the request
				myAgent.addBehaviour(new RequestPerformer());
			}
		} );
	}

	protected void takeDown() {
		System.out.println("QuestionSender " + getAID().getName() + " is terminated.");
	}

	private class RequestPerformer extends Behaviour {
		private static final long serialVersionUID = 1L;
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;

		public void action() {
			switch (step) {
			case 0:
				ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < answerAgents.length; ++i) {
					req.addReceiver(answerAgents[i]);
				} 
				req.setContent(QUESTION[question_current]);
				req.setConversationId("bulgarian-revivalists-answers");
				req.setReplyWith("req" + System.currentTimeMillis()); // Unique value
				myAgent.send(req);
				System.out.println("Въпрос:  " + QUESTION[question_current]);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("bulgarian-revivalists-answers"),
						MessageTemplate.MatchInReplyTo(req.getReplyWith()));
				step = 1;
				question_current++;
				if (question_current >= QUESTION.length) {
					question_current = 0;
				}
				break;
			case 1:
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						String answer = reply.getContent();
						System.out.println("Отговор: " + answer);
						System.out.println();
						step = 2;
					}
				}
				else {
					block();					
				}
				break;
			}
		}

		public boolean done() {
			return (step == 2);
		}
	}
	
}
