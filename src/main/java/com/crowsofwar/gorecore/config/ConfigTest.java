package com.crowsofwar.gorecore.config;

import static com.crowsofwar.gorecore.config.Animal.ANIMAL;

import java.io.IOException;
import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigTest {
	
	private boolean showLoadingBar;
	private List<String> list;
	
	private boolean tabCompletion, remoteFsAccess;
	
	private Animal george;
	
	public ConfigTest() throws IOException {
		
		Configuration config = Configuration.from("avatar/test.cfg").withDefaults("def/test_defaults.cfg");
		
		george = config.load("george").as(ANIMAL);
		showLoadingBar = config.load("showLoadingBar").asBoolean();
		list = config.load("allowedBlocks").asStringList();
		tabCompletion = config.fromMapping("commandOptions").load("tabCompletion").asBoolean();
		remoteFsAccess = config.fromMapping("commandOptions").load("enableRemoteFilesystem").asBoolean();
		System.out.println("Default is: " + config.load("default").asString());
		
		System.out.println("George is: " + george);
		System.out.println("List is: " + list);
		System.out.println("Show loading bar: " + showLoadingBar);
		System.out.println("Remote FS access: " + remoteFsAccess);
		
		config.save();
		
	}
	
	public static void main(String[] args) throws IOException {
		new ConfigTest();
	}
	
}
