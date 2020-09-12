package organisations;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jade.gui.GuiEvent;

public class OrganisationGUI extends JFrame {
	private JLabel jLabelAgent = new JLabel("Agent:");
	private JTextField jTextFieldAget = new JTextField(12);
	private JLabel jLabelDelivered = new JLabel("Delivered");
	private JTextField jTextField = new JTextField(12);
	private JButton jButtonEnvoyer = new JButton("Send To");
	private JTextArea jTextAreaMess = new JTextArea();
	private OrganisationAgent organisationAgent;
	
	public OrganisationGUI() {
		jTextAreaMess.setFont(new Font("Arial", Font.BOLD, 14));
		jTextAreaMess.setEditable(false);
		
		JPanel jPanelN = new JPanel();
		
		jPanelN.setLayout(new FlowLayout());
		jPanelN.add(jLabelAgent);
		jPanelN.add(jTextFieldAget);
		jPanelN.add(jLabelDelivered);
		jPanelN.add(jTextField);
		jPanelN.add(jButtonEnvoyer);
		jPanelN.add(jTextField);
		
		this.setLayout(new BorderLayout());
		this.add(jPanelN, BorderLayout.NORTH);
		this.add(new JScrollPane(jTextAreaMess), BorderLayout.CENTER);
		this.setSize(600, 400);
		this.setVisible(true);
		jButtonEnvoyer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String agentName = jTextFieldAget.getText();
				String delivered = jTextFieldAget.getText();
				
				
				GuiEvent gev = new GuiEvent(this, 1);
				Map<String, Object> params = new HashMap<>();
				
				
				params.put("agentArchelogies", agentName);
				params.put("delivered", delivered);
				
				
				gev.addParameter(params);
				
				organisationAgent.onGuiEvent(gev);
			}
		});
	}
	public OrganisationAgent getOrganisationAgent() {
		return organisationAgent;
	}
	public void setOrganisationAgent(OrganisationAgent organisationAgent) {
		this.organisationAgent = organisationAgent;
	}
	public void showMessage(String msg, boolean append) {
		if(append == true) {
			jTextAreaMess.append(msg + "\n");
		}
		else {
			jTextAreaMess.setText(msg);
		}
	}
}
