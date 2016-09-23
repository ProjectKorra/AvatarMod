package com.crowsofwar.gorecore.config;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

/**
 * Represents a configuration, where String keys are mapped to {@link UnknownTypeProperty unknown
 * type values}.
 * 
 * @author CrowsOfWar
 */
public class Configuration {
	
	private final Map<String, ?> map;
	
	Configuration(Map<String, ?> map) {
		this.map = map;
	}
	
	/**
	 * Load a property from this map. Returns
	 * 
	 * @param key
	 * @return
	 */
	public UnknownTypeProperty load(String key) {
		if (!map.containsKey(key)) throw new IllegalArgumentException("Invalid key: " + key);
		return new UnknownTypeProperty(key, map.get(key));
	}
	
	public Configuration section(String key) {
		if (!map.containsKey(key)) throw new IllegalArgumentException("Invalid key: " + key);
		return new Configuration((Map) map.get(key));
	}
	
	/**
	 * Load a Configuration instance from the given path.
	 */
	public static Configuration from(String path) {
		
		try {
			
			String contents = "";
			
			Scanner scanner = new Scanner(new File("config/avatar/test.cfg"));
			while (scanner.hasNextLine())
				contents += scanner.nextLine() + "\n";
			scanner.close();
			
			Yaml yaml = new Yaml();
			Map<String, ?> map = (Map) yaml.load(contents);
			
			return new Configuration(map);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
