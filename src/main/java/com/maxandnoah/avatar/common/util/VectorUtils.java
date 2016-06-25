package com.maxandnoah.avatar.common.util;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import javax.vecmath.Vector3d;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Contains methods to perform basic operations on vectors.
 * Because, apparently, Mojang was to lazy to do it themselves.
 *
 */
public class VectorUtils {
	
	public static final Vec3 UP = Vec3.createVectorHelper(0, 1, 0);
	public static final Vec3 DOWN = Vec3.createVectorHelper(0, -1, 0);
	public static final Vec3 EAST = Vec3.createVectorHelper(1, 0, 0);
	public static final Vec3 WEST = Vec3.createVectorHelper(-1, 1, 0);
	public static final Vec3 NORTH = Vec3.createVectorHelper(0, 0, -1);
	public static final Vec3 SOUTH = Vec3.createVectorHelper(0, 0, 1);
	
	/**
	 * Add vector b to a, modifying a in the process.
	 * @param a Vector a (will be modified)
	 * @param b Vector b (will not be modified)
	 */
	public static void add(Vec3 a, Vec3 b) {
		a.xCoord += b.xCoord;
		a.yCoord += b.yCoord;
		a.zCoord += b.zCoord;
	}
	
	/**
	 * Subtract vector b from a
	 * @param a Vector a (will be modified)
	 * @param b Vector b (will not be modified)
	 */
	public static void subtract(Vec3 a, Vec3 b) {
		Vec3 inverse = copy(b);
		inverse(inverse);
		add(a, inverse);
	}
	
	/**
	 * Add vector a and b, returning a new vector. Unlike {{@link #add(Vec3, Vec3)},
	 * the arguments are not modified.
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vec3 plus(Vec3 a, Vec3 b) {
		Vec3 result = copy(a);
		add(result, b);
		return result;
	}
	
	/**
	 * Subtract vector a - b, returning a new vector. Unlike {{@link #subtract(Vec3, Vec3)},
	 * the arguments are not modified.
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vec3 minus(Vec3 a, Vec3 b) {
		Vec3 result = copy(a);
		subtract(result, b);
		return result;
	}
	
	/**
	 * Multiply the vector by the value
	 * @param v
	 * @param f
	 */
	public static void mult(Vec3 v, double f) {
		v.xCoord *= f;
		v.yCoord *= f;
		v.zCoord *= f;
	}
	
	public static Vec3 times(Vec3 v, double f) {
		Vec3 result = copy(v);
		mult(result, f);
		return result;
	}
	
	public static void inverse(Vec3 v) {
		mult(v, -1);
	}
	
	public static Vec3 copy(Vec3 v) {
		return Vec3.createVectorHelper(v.xCoord, v.yCoord, v.zCoord);
	}
	
	/**
	 * Create a unit vector from yaw and pitch. Parameters should be in radians.
	 */
	public static Vec3 fromYawPitch(double yaw, double pitch) {
		return Vec3.createVectorHelper(-sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch));
	}
	
	/**
	 * Use {@link Raytrace} instead.
	 * @param world The world
	 * @param start Vector to start at
	 * @param direction Unit vector describing direction
	 * @param interval Interval for checking
	 * @return
	 */
	@Deprecated
	public static Vec3 raytrace(World world, Vec3 start, Vec3 direction, double interval, double length) {
		Vec3 current = copy(start);
		Vec3 add = copy(direction);
		mult(add, interval);
		for (double dist = 0; dist < length; dist += interval) {
			
			int x = (int) (current.xCoord);
			int y = (int) (current.yCoord);
			int z = (int) (current.zCoord);
			if (world.getBlock(x, y, z) != Blocks.air) {
				return Vec3.createVectorHelper(x, y, z);
			}
			
			add(current, add);
			
		}
		
		System.out.println("Ending position: " + current);
		return null;
	}
	
	public static Vec3 getEntityPos(Entity entity) {
		Vec3 pos = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) pos.yCoord -= 1.65;
		return pos;
	}
	
	/**
	 * Get the pitch to lob a projectile in radians. Example:
	 * pitch to target can be used in {@link #fromYawPitch(double, double)}
	 * @param v Force of the projectile, going FORWARDS
	 * @param g Gravity constant
	 * @param x Horizontal distance to target
	 * @param y Vertical distance to target
	 */
	public static double getProjectileAngle(double v, double g, double x, double y) {
		return -Math.atan2((v*v+Math.sqrt(v*v*v*v - g*(g*x*x+2*y*v*v))),g*x); 
	}
	
}
