package com.crowsofwar.gorecore.config;

import static com.crowsofwar.gorecore.config.convert.ConverterRegistry.addConverter;
import static com.crowsofwar.gorecore.config.convert.ConverterRegistry.getConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crowsofwar.gorecore.config.convert.Converter;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SampleConfiguration {
	
	@Load
	public static int settingA;
	
	@Load
	public static double settingB;
	
	@Load
	public static Animal bob;
	
	@Load
	public static Animal fluffy = new Animal("Fluffers", "Fluff ball", 4, true);
	
	// @Load
	public static Animal notSoFluffy;
	
	@Load
	public static List<String> people;
	
	public static void main(String[] args) {
		
		addConverter(Integer.class, Double.class, inte -> inte.doubleValue());
		
		Converter<Integer, Double> convertItD = getConverter(Integer.class, Double.class);
		System.out.println(convertItD.convert(4));
		
		addConverter(List.class, Set.class, list -> new HashSet(list));
		List<String> myList = new ArrayList<>();
		myList.add("A");
		myList.add("B");
		myList.add("A");
		
		Converter<List, Set> convertLtS = findConverter(myList);
		System.out.println(convertLtS.convert(myList));
		
		addConverter(Animal.class, String.class, animal -> "Animal " + animal.name);
		Gorilla kingKong = new Gorilla();
		Converter<Gorilla, String> convertGtS = getConverter(Gorilla.class, String.class);
		System.out.println(convertGtS.convert(kingKong));
		
		// ConfigLoader.load(SampleConfiguration.class, "annot-test.cfg");
		// System.out.println("SettingA: " + settingA);
		// System.out.println("SettingB: " + settingB);
		// System.out.println("People: " + people);
		// System.out.println("bob: " + bob);
		// System.out.println("Fluffy: " + fluffy);
	}
	
	private static <T> Converter<T, Set> findConverter(T obj) {
		return (Converter<T, Set>) getConverter(obj.getClass(), Set.class);
	}
	
}
