package com.crowsofwar.gorecore.config;

import static com.crowsofwar.gorecore.config.Animal.ANIMAL;
import static com.crowsofwar.gorecore.config.Configuration.from;

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
		
		// george = Configuration.from("file.txt").load("george", Animal.FACTORY);
		george = from("file.txt").load("george").as(ANIMAL);
		
		Configuration configuration = Configuration.from("avatar/test.cfg");
		showLoadingBar = configuration.load("showLoadingBar").asBoolean();
		list = configuration.load("allowedBlocks").asStringList();
		
		tabCompletion = configuration.load("tabCompletion").asBoolean();
		remoteFsAccess = configuration.load("enableRemoteFilesystem").asBoolean();
		
		System.out.print("\n\n\n\n\n");
		
		System.out.println("George is: " + george);
		System.out.println("List is: " + list);
		System.out.println("Show loading bar: " + showLoadingBar);
		
	}
	
	public static void main(String[] args) {
		new ConfigTest();
	}
	
}
