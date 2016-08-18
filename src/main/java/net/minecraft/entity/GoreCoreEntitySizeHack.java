package net.minecraft.entity;

/**
 * A hacky way of calling {@link Entity#setSize(float, float)}.
 * 
 * @author CrowsOfWar
 */
public final class GoreCoreEntitySizeHack {
	
	public static void setWidth(Entity entity, float width) {
		setSize(entity, width, entity.height);
	}
	
	public static void setHeight(Entity entity, float height) {
		setSize(entity, entity.width, height);
	}
	
	public static void setSize(Entity entity, float width, float height) {
		entity.setSize(width, height);
	}
	
}
