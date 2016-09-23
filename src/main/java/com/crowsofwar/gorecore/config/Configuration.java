package com.crowsofwar.gorecore.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
	
	private Configuration(Map<String, ?> map) {
		System.out.println("Made cfg " + this + ", " + map);
		this.map = map;
	}
	
	public UnknownTypeProperty load(String key) {
		if (!map.containsKey(key)) throw new IllegalArgumentException("Invalid key: " + key);
		return new UnknownTypeProperty(map.get(key));
	}
	
	public Configuration section(String key) {
		if (!map.containsKey(key)) throw new IllegalArgumentException("Invalid key: " + key);
		return new Configuration((Map) map.get(key));
	}
	
	public static Configuration from(String path) {
		
		System.out.println("Testing config...");
		
		try {
			
			String contents = "";
			
			Scanner scanner = new Scanner(new File("config/avatar/test.cfg"));
			while (scanner.hasNextLine())
				contents += scanner.nextLine() + "\n";
			scanner.close();
			
			Yaml yaml = new Yaml();
			Map<String, ?> map = (Map) yaml.load(contents);
			System.out.println(map);
			
			return new Configuration(map);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// System.exit(0);
		// FMLCommonHandler.instance().exitJava(0, false);
		
	}
	
	public class UnknownTypeProperty {
		
		private final Object object;
		
		public UnknownTypeProperty(Object obj) {
			this.object = obj;
		}
		
		public <T> T as(ConfigurableFactory<T> factory) {
			return factory.load(new Configuration((Map) object));
		}
		
		public String asString() {
			return (String) object;
		}
		
		public int asInt() {
			return (int) object;
		}
		
		public boolean asBoolean() {
			return (boolean) object;
		}
		
		public <T> List<T> asList(ListLoader<T> factory) {
			// Object[] arr = (Object[]) object;
			System.out.println("Converting " + object + " to a list");
			List<T> out = new ArrayList<>();
			
			List<?> list = (List<?>) object;
			for (Object obj : list) {
				System.out.println("Object " + obj);
				out.add(factory.load(obj));
			}
			
			return out;
		}
		
		public List<String> asStringList() {
			return asList(obj -> (String) obj);
		}
		
	}
	
}
