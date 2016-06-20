package com.maxandnoah.avatar.common.util;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import javax.vecmath.Vector3d;

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
	 * Multiply the vector by the value
	 * @param v
	 * @param f
	 */
	public static void mult(Vec3 v, double f) {
		v.xCoord *= f;
		v.yCoord *= f;
		v.zCoord *= f;
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
	 * Perform a raytrace on blocks. Returns the hit position.
	 * @param world The world
	 * @param start Vector to start at
	 * @param direction Unit vector describing direction
	 * @param interval Interval for checking
	 * @return
	 */
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
	
}
