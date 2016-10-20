package com.crowsofwar.avatar.common.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	public static final ConfigurableProperty<Double> blockDamage = new FinalDouble(0.25),
			blockPush = new FinalDouble(1), ravinePush = new FinalDouble(0.25), wavePush = new FinalDouble(6);
	public static final ConfigurableProperty<Integer> ravineDamage = new FinalInteger(7),
			waveDamage = new FinalInteger(9);
	
	public static class FinalDouble implements ConfigurableProperty<Double> {
		
		private final Double value;
		
		public FinalDouble(double value) {
			this.value = value;
		}
		
		@Override
		public Double currentValue() {
			return value;
		}
		
		@Override
		public void setValue(Object value) {}
		
	}
	
	public static class FinalInteger implements ConfigurableProperty<Integer> {
		
		private final Integer value;
		
		public FinalInteger(int value) {
			this.value = value;
		}
		
		@Override
		public Integer currentValue() {
			return value;
		}
		
		@Override
		public void setValue(Object value) {}
		
	}
	
}
