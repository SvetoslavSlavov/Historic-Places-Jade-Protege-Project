package main;

import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.wrapper.ControllerException;

public class MainContainer {

	public static void main(String[] args) {
		// TODO: Find a better solution to start jade.core.BaseService
		// profileImpl.setParameter(jade.core.ProfileImpl.MAIN_HOST, host); profileImpl.setParameter(jade.core.ProfileImpl.MAIN_PORT, port); profileImpl.setParameter(jade.core.ProfileImpl.DETECT_MAIN,"false");
		// Issue -> http://jade.tilab.com/pipermail/jade-develop/2007q4/011330.html
        String host = "localhost"; 
        String port = "7778"; 
        String argsv[] = {"-host", host,"-detect-main","false",port,"-port",port, "-container", "start:start.main.StartAgent"};
        jade.Boot.main(argsv); 
        
		Runtime runtime = Runtime.instance();
		Properties properties = new ExtendedProperties();
		
		ProfileImpl profileImpl = new ProfileImpl(properties);
		properties.setProperty(Profile.GUI, "true");
		
		AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
		try {
			agentContainer.start();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

}
