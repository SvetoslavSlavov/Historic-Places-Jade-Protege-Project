package uni.fmi.masters;

import jade.core.Agent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentGUI extends Agent{
	private static final long serialVersionUID = 1L;
	private String question = new String("");
	private AID[] answerAgents;
	private JTextField guiTextFieldQuestion = new JTextField("");
	private JTextArea guiTextAreaAnswer = new JTextArea();

	protected void initGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Енциклопедичен асистент на българските възрожденци");
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new FlowLayout());
        panel.add(panelTop, BorderLayout.NORTH);

        JLabel guiLabelQuestion = new JLabel("Въпрос:");
        panelTop.add(guiLabelQuestion, FlowLayout.LEFT);

        guiTextFieldQuestion.setPreferredSize(new Dimension(350, 30));
        panelTop.add(guiTextFieldQuestion, FlowLayout.CENTER);

        JButton guiButtonQuestionSend = new JButton("Питане");
        panelTop.add(guiButtonQuestionSend, FlowLayout.RIGHT);
        guiButtonQuestionSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				question = guiTextFieldQuestion.getText();
			}        	
        });
        
        JLabel guiLabelAnswer = new JLabel("Отговор:");
        panel.add(guiLabelAnswer, BorderLayout.WEST);
        
        JLabel guiLabelEmptyEast = new JLabel(" ");
        panel.add(guiLabelEmptyEast, BorderLayout.EAST);
        
        JLabel guiLabelEmptySouth = new JLabel(" ");
        panel.add(guiLabelEmptySouth, BorderLayout.SOUTH);

        JScrollPane scroller = new JScrollPane(guiTextAreaAnswer);
        panel.add(scroller, BorderLayout.CENTER);
        guiTextAreaAnswer.setEditable(false);
        guiTextAreaAnswer.setLineWrap(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(550, 160));
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);     
	}
	
	protected void setup() {
		initGUI();
		addBehaviour(new TickerBehaviour(this, 500) {
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("bulgarian-revivalists-answers");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					answerAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						answerAgents[i] = result[i].getName();
					}
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
		System.out.println("GUI agent " + getAID().getName() + " is terminated.");
	}

	private class RequestPerformer extends Behaviour {
		private static final long serialVersionUID = 1L;
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;

		public void action() {
			if (step == 0 && question.equals("")) { // if no question set
				return;
			}
			switch (step) {
			case 0:
				ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < answerAgents.length; ++i) {
					req.addReceiver(answerAgents[i]);
				}
				req.setContent(question);
				req.setConversationId("bulgarian-revivalists-answers");
				req.setReplyWith("req" + System.currentTimeMillis()); // Unique value
				myAgent.send(req);
				question  = ""; // question unset
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("bulgarian-revivalists-answers"),
						MessageTemplate.MatchInReplyTo(req.getReplyWith()));
				step = 1;
				break;
			case 1:
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						String answer = reply.getContent();
						guiTextAreaAnswer.setText(answer);
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
