package com.crowsofwar.gorecore.config;

import static com.crowsofwar.gorecore.config.convert.ConverterRegistry.getConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.crowsofwar.gorecore.config.convert.Converter;
import com.crowsofwar.gorecore.config.convert.ConverterRegistry;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SampleConfiguration {
	
	@Load
	private int settingA;
	
	@Load
	public double settingB;
	
	// @Load
	public Animal bob;
	
	@Load
	public Animal fluffy = new Animal("Fluffers", "Fluff ball", 4, true);
	
	// @Load
	public Animal notSoFluffy;
	
	@Load
	public List<String> people = new ArrayList<>();
	
	@Load
	public static Rock rockA = new Rock(3);
	
	@Load
	public Rock rockB = new Rock(5);
	
	@Load
	public HandHoldingRock hand = new HandHoldingRock(rockB);
	
	public static void main(String[] args) {
		
		ConverterRegistry.addDefaultConverters();
		
		SampleConfiguration cfg = new SampleConfiguration();
		ConfigLoader.load(cfg, "annot-test.cfg");
		
		System.out.println("Fluffy: " + cfg.fluffy);
		System.out.println("People: " + cfg.people);
		
		// addConverter(Integer.class, Double.class, inte ->
		// inte.doubleValue());
		//
		// Converter<Integer, Double> convertItD = getConverter(Integer.class,
		// Double.class);
		// System.out.println(convertItD.convert(4));
		//
		// addConverter(List.class, Set.class, list -> new HashSet(list));
		// List<String> myList = new ArrayList<>();
		// myList.add("A");
		// myList.add("B");
		// myList.add("A");
		//
		// Converter<List, Set> convertLtS = findConverter(myList);
		// System.out.println(convertLtS.convert(myList));
		//
		// addConverter(Animal.class, String.class, animal -> "Animal " +
		// animal.name);
		// Gorilla kingKong = new Gorilla();
		// kingKong.name = "(get scared) KING KONG (get scared)";
		// Converter<Gorilla, String> convertGtS = getConverter(Gorilla.class,
		// String.class);
		// System.out.println(convertGtS.convert(kingKong));
		
	}
	
	/**
	 * Find a converter which converts the object into a Set.
	 */
	private static <T> Converter<T, Set> findConverter(T obj) {
		return (Converter<T, Set>) getConverter(obj.getClass(), Set.class);
	}
	
	public static class Rock {
		
		@Load
		public int size;
		
		public Rock() {}
		
		public Rock(int size) {
			this.size = size;
		}
		
	}
	
	public static class HandHoldingRock {
		
		@Load
		public Rock myRock;
		
		public HandHoldingRock() {}
		
		public HandHoldingRock(Rock rock) {
			myRock = rock;
		}
		
	}
	
}
