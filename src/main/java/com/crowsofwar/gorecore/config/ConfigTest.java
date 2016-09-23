package com.crowsofwar.gorecore.config;

import static com.crowsofwar.gorecore.config.Animal.ANIMAL;

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
	
	public ConfigTest() {
		
		Configuration config = Configuration.from("avatar/test.cfg");
		
		george = config.load("george").as(ANIMAL);
		showLoadingBar = config.load("showLoadingBar").asBoolean();
		list = config.load("allowedBlocks").asStringList();
		tabCompletion = config.inSection("commandOptions").load("tabCompletion").asBoolean();
		remoteFsAccess = config.inSection("commandOptions").load("enableRemoteFilesystem").asBoolean();
		
		System.out.println("George is: " + george);
		System.out.println("List is: " + list);
		System.out.println("Show loading bar: " + showLoadingBar);
		System.out.println("Remote FS access: " + remoteFsAccess);
		
	}
	
	public static void main(String[] args) {
		new ConfigTest();
	}
	
}
