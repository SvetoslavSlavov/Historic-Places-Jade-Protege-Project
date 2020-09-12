package organisations;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
public class OrganisationContainer {
	public static void main(String[] args) {
		try {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl(false);
		profileImpl.setParameter(Profile.MAIN_HOST,"localhost");
		
		AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
		AgentController agentController = agentContainer.createNewAgent
				("oragnisationagent", OrganisationAgent.class.getName(), new Object[] {});
		agentController.start();
		
		} catch(ControllerException e) {
			e.printStackTrace();
		}
	}
}
