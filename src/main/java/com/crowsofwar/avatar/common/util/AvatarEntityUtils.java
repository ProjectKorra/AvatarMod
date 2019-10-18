package com.crowsofwar.avatar.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class AvatarEntityUtils {
	/**
	 * Shorthand for {@link AvatarEntityUtils#getEntitiesWithinRadius(double, double, double, double, World, Class)}
	 * with EntityLivingBase as the entity type. This is by far the most common use for that method.
	 *
	 * @param radius The search radius
	 * @param x The x coordinate to search around
	 * @param y The y coordinate to search around
	 * @param z The z coordinate to search around
	 * @param world The world to search in
	 */
	public static List<EntityLivingBase> getLivingEntitiesWithinRadius(double radius, double x, double y, double z,
																 World world){
		return getEntitiesWithinRadius(radius, x, y, z, world, EntityLivingBase.class);
	}

	/**
	 * Shorthand for {@link AvatarEntityUtils#getEntitiesWithinRadius(double, double, double, double, World, Class)}
	 * with EntityLivingBase as the entity type. This is by far the most common use for that method.
	 *
	 * @param radius The search radius
	 * @param x The x coordinate to search around
	 * @param y The y coordinate to search around
	 * @param z The z coordinate to search around
	 * @param world The world to search in
	 */
	public static List<Entity> getEntitiesWithinRadius(double radius, double x, double y, double z,
																	   World world){
		return getEntitiesWithinRadius(radius, x, y, z, world, Entity.class);
	}

	/**
	 * Returns all entities of the specified type within the specified radius of the given coordinates. This is
	 * different to using a raw AABB because a raw AABB will search in a cube volume rather than a sphere. Note that
	 * this does not exclude any entities; if any specific entities are to be excluded this must be checked when
	 * iterating through the list.
	 *
	 * @see AvatarEntityUtils#getEntitiesWithinRadius(double, double, double, double, World)
	 * @param radius The search radius
	 * @param x The x coordinate to search around
	 * @param y The y coordinate to search around
	 * @param z The z coordinate to search around
	 * @param world The world to search in
	 * @param entityType The class of entity to search for; pass in Entity.class for all entities
	 */
	public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z,
																	 World world, Class<T> entityType){
		AxisAlignedBB aabb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
		List<T> entityList = world.getEntitiesWithinAABB(entityType, aabb);
		for(int i = 0; i < entityList.size(); i++){
			if(entityList.get(i).getDistance(x, y, z) > radius){
				entityList.remove(i);
				break;
			}
		}
		return entityList;
	}
}
