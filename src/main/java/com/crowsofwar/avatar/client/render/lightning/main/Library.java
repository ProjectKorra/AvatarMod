package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.client.render.lightning.math.BobMathUtil;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

@Spaghetti("this whole class")
public class Library {

	static Random rand = new Random();
	
	//this is a list of UUIDs used for various things, primarily for accessories.
	//for a comprehensive list, check RenderAccessoryUtility.java
	public static String HbMinecraft = "192af5d7-ed0f-48d8-bd89-9d41af8524f8";
	public static String TacoRedneck = "5aee1e3d-3767-4987-a222-e7ce1fbdf88e";
	// Earl0fPudding
	public static String LPkukin = "937c9804-e11f-4ad2-a5b1-42e62ac73077";
	public static String Dafnik = "3af1c262-61c0-4b12-a4cb-424cc3a9c8c0";
	// anna20
	public static String a20 = "4729b498-a81c-42fd-8acd-20d6d9f759e0";
	public static String rodolphito = "c3f5e449-6d8c-4fe3-acc9-47ef50e7e7ae";
	public static String Ducxkskiziko = "122fe98f-be19-49ca-a96b-d4dee4f0b22e";
	public static String Drillgon = "41ebd03f-7a12-42f3-b037-0caa4d6f235b";


	//the old list that allowed superuser mode for the ZOMG
	//currently unused
	public static List<String> superuser = new ArrayList<String>();

	public static boolean isObstructed(World world, double x, double y, double z, double a, double b, double c) {
		RayTraceResult pos = world.rayTraceBlocks(new Vec3d(x, y, z), new Vec3d(a, b, c), false, true, true);
		return pos != null && pos.typeOfHit != Type.MISS;
	}
	
	public static EntityPlayer getClosestPlayerForSound(World world, double x, double y, double z, double radius) {
		double d4 = -1.0D;
		EntityPlayer entity = null;

		for (int i = 0; i < world.loadedEntityList.size(); ++i) {
				Entity entityplayer1 = (Entity)world.loadedEntityList.get(i);

				if (entityplayer1.isEntityAlive() && entityplayer1 instanceof EntityPlayer) {
					double d5 = entityplayer1.getDistanceSq(x, y, z);
					double d6 = radius;

					if ((radius < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4)) {
						d4 = d5;
						entity = (EntityPlayer)entityplayer1;
					}
			}
		}

		return entity;
	}

	public static RayTraceResult rayTrace(EntityPlayer player, double length, float interpolation) {
		Vec3d vec3 = getPosition(interpolation, player);
		vec3 = vec3.add(0D, (double) player.eyeHeight, 0D);
		Vec3d vec31 = player.getLook(interpolation);
		Vec3d vec32 = vec3.add(vec31.x * length, vec31.y * length, vec31.z * length);
		return player.world.rayTraceBlocks(vec3, vec32, false, false, true);
	}
	
	public static RayTraceResult rayTrace(EntityPlayer player, double length, float interpolation, boolean b1, boolean b2, boolean b3) {
		Vec3d vec3 = getPosition(interpolation, player);
		vec3 = vec3.add(0D, (double) player.eyeHeight, 0D);
		Vec3d vec31 = player.getLook(interpolation);
		Vec3d vec32 = vec3.add(vec31.x * length, vec31.y * length, vec31.z * length);
		return player.world.rayTraceBlocks(vec3, vec32, b1, b2, b3);
	}
	
	public static AxisAlignedBB rotateAABB(AxisAlignedBB box, EnumFacing facing){
		switch(facing){
		case NORTH:
			return new AxisAlignedBB(box.minX, box.minY, 1-box.minZ, box.maxX, box.maxY, 1-box.maxZ);
		case SOUTH:
			return box;
		case EAST:
			return new AxisAlignedBB(box.minZ, box.minY, box.minX, box.maxZ, box.maxY, box.maxX);
		case WEST:
			return new AxisAlignedBB(1-box.minZ, box.minY, box.minX, 1-box.maxZ, box.maxY, box.maxX);
		default:
			return box;
		}
	}
	
	public static RayTraceResult rayTraceIncludeEntities(EntityPlayer player, double d, float f) {
		Vec3d vec3 = getPosition(f, player);
		vec3 = vec3.add(0D, (double) player.eyeHeight, 0D);
		Vec3d vec31 = player.getLook(f);
		Vec3d vec32 = vec3.add(vec31.x * d, vec31.y * d, vec31.z * d);
		return rayTraceIncludeEntities(player.world, vec3, vec32, player);
	}
	
	public static RayTraceResult rayTraceIncludeEntitiesCustomDirection(EntityPlayer player, Vec3d look, double d, float f) {
		Vec3d vec3 = getPosition(f, player);
		vec3 = vec3.add(0D, (double) player.eyeHeight, 0D);
		Vec3d vec32 = vec3.add(look.x * d, look.y * d, look.z * d);
		return rayTraceIncludeEntities(player.world, vec3, vec32, player);
	}
	
	public static Vec3d changeByAngle(Vec3d oldDir, float yaw, float pitch){
		Vec3d dir = new Vec3d(0, 0, 1);
		dir = dir.rotatePitch((float) Math.toRadians(pitch)).rotateYaw((float) Math.toRadians(yaw));
		Vec3d angles = BobMathUtil.getEulerAngles(oldDir);
		return dir.rotatePitch((float) Math.toRadians(angles.y+90)).rotateYaw((float)Math.toRadians(angles.x));
	}
	
	public static RayTraceResult rayTraceIncludeEntities(World w, Vec3d vec3, Vec3d vec32, @Nullable Entity excluded) {
		RayTraceResult result = w.rayTraceBlocks(vec3, vec32, false, true, true);
		if(result != null)
			vec32 = result.hitVec;
		
		AxisAlignedBB box = new AxisAlignedBB(vec3.x, vec3.y, vec3.z, vec32.x, vec32.y, vec32.z).grow(1D);
		List<Entity> ents = w.getEntitiesInAABBexcluding(excluded, box, Predicates.and(EntitySelectors.IS_ALIVE, entity -> entity instanceof EntityLivingBase));
		for(Entity ent : ents){
			RayTraceResult test = ent.getEntityBoundingBox().grow(0.3D).calculateIntercept(vec3, vec32);
			if(test != null){
				if(result == null || vec3.squareDistanceTo(result.hitVec) > vec3.squareDistanceTo(test.hitVec)){
					test.typeOfHit = Type.ENTITY;
					test.entityHit = ent;
					result = test;
				}
			}
		}
		
		return result;
	}
	
	public static Pair<RayTraceResult, List<Entity>> rayTraceEntitiesOnLine(EntityPlayer player, double d, float f){
		Vec3d vec3 = getPosition(f, player);
		vec3 = vec3.add(0D, (double) player.eyeHeight, 0D);
		Vec3d vec31 = player.getLook(f);
		Vec3d vec32 = vec3.add(vec31.x * d, vec31.y * d, vec31.z * d);
		RayTraceResult result = player.world.rayTraceBlocks(vec3, vec32, false, true, true);
		if(result != null)
			vec32 = result.hitVec;
		AxisAlignedBB box = new AxisAlignedBB(vec3.x, vec3.y, vec3.z, vec32.x, vec32.y, vec32.z).grow(1D);
		List<Entity> ents = player.world.getEntitiesInAABBexcluding(player, box, Predicates.and(EntitySelectors.IS_ALIVE, entity -> entity instanceof EntityLiving));
		Iterator<Entity> itr = ents.iterator();
		while(itr.hasNext()){
			Entity ent = itr.next();
			AxisAlignedBB entityBox = ent.getEntityBoundingBox().grow(0.1);
			RayTraceResult entTrace = entityBox.calculateIntercept(vec3, vec32);
			if(entTrace == null || entTrace.typeOfHit == Type.MISS){
				itr.remove();
			}
		}
		return Pair.of(rayTraceIncludeEntities(player, d, f), ents);
	}
	
	public static RayTraceResult rayTraceEntitiesInCone(EntityPlayer player, double d, float f, float degrees) {
		double cosDegrees = Math.cos(Math.toRadians(degrees));
		Vec3d vec3 = getPosition(f, player);
		vec3 = vec3.add(0D, (double) player.eyeHeight, 0D);
		Vec3d vec31 = player.getLook(f);
		Vec3d vec32 = vec3.add(vec31.x * d, vec31.y * d, vec31.z * d);
		
		RayTraceResult result = player.world.rayTraceBlocks(vec3, vec32, false, true, true);
		double runningDot = Double.MIN_VALUE;
		
		AxisAlignedBB box = new AxisAlignedBB(vec3.x, vec3.y, vec3.z, vec3.x, vec3.y, vec3.z).grow(1D+d);
		List<Entity> ents = player.world.getEntitiesInAABBexcluding(player, box, Predicates.and(EntitySelectors.IS_ALIVE, entity -> entity instanceof EntityLiving));
		for(Entity ent : ents){
			Vec3d entPos = closestPointOnBB(ent.getEntityBoundingBox(), vec3, vec32);
			Vec3d relativeEntPos = entPos.subtract(vec3).normalize();
			double dot = relativeEntPos.dotProduct(vec31);
			
			if(dot > cosDegrees && dot > runningDot && !isObstructed(player.world, vec3.x, vec3.y, vec3.z, ent.posX, ent.posY + ent.getEyeHeight()*0.75, ent.posZ)){
				runningDot = dot;
				result = new RayTraceResult(ent);
				result.hitVec = new Vec3d(ent.posX, ent.posY + ent.getEyeHeight()/2, ent.posZ);
			}
			
		}
		
		return result;
	}
	
	//Drillgon200: Turns out the closest point on a bounding box to a line is a pretty good method for determine if a cone and an AABB intersect.
	//Actually that was a pretty garbage method. Changing it out for a slightly less efficient sphere culling algorithm that only gives false positives.
	//https://bartwronski.com/2017/04/13/cull-that-cone/
	//Idea is that we find the closest point on the cone to the center of the sphere and check if it's inside the sphere.
	public static boolean isBoxCollidingCone(AxisAlignedBB box, Vec3d coneStart, Vec3d coneEnd, float degrees){
		Vec3d center = box.getCenter();
		double radius = center.distanceTo(new Vec3d(box.maxX, box.maxY, box.maxZ));
		Vec3d V = center.subtract(coneStart);
		double VlenSq = V.lengthSquared();
		Vec3d direction = coneEnd.subtract(coneStart);
		double size = direction.length();
		double V1len  = V.dotProduct(direction.normalize());
		double angRad = Math.toRadians(degrees);
		double distanceClosestPoint = Math.cos(angRad) * Math.sqrt(VlenSq - V1len*V1len) - V1len * Math.sin(angRad);
		 
		boolean angleCull = distanceClosestPoint > radius;
		boolean frontCull = V1len >  radius + size;
		boolean backCull  = V1len < -radius;
		return !(angleCull || frontCull || backCull);
	}
	
	//Drillgon200: Basically the AxisAlignedBB calculateIntercept method except it clamps to edge instead of returning null
	public static Vec3d closestPointOnBB(AxisAlignedBB box, Vec3d vecA, Vec3d vecB){
		
		Vec3d vec3d = collideWithXPlane(box, box.minX, vecA, vecB);
        Vec3d vec3d1 = collideWithXPlane(box, box.maxX, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vecB, vec3d, vec3d1))
        {
            vec3d = vec3d1;
        }

        vec3d1 = collideWithYPlane(box, box.minY, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vecB, vec3d, vec3d1))
        {
            vec3d = vec3d1;
        }

        vec3d1 = collideWithYPlane(box, box.maxY, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vecB, vec3d, vec3d1))
        {
            vec3d = vec3d1;
        }

        vec3d1 = collideWithZPlane(box, box.minZ, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vecB, vec3d, vec3d1))
        {
            vec3d = vec3d1;
        }

        vec3d1 = collideWithZPlane(box, box.maxZ, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vecB, vec3d, vec3d1))
        {
            vec3d = vec3d1;
        }
		
		return vec3d;
	}
	
	protected static Vec3d collideWithXPlane(AxisAlignedBB box, double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
    {
        Vec3d vec3d = getIntermediateWithXValue(p_186671_3_, p_186671_4_, p_186671_1_);
        return clampToBox(box, vec3d);
        //return vec3d != null && box.intersectsWithYZ(vec3d) ? vec3d : null;
    }

	protected static Vec3d collideWithYPlane(AxisAlignedBB box, double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
    {
        Vec3d vec3d = getIntermediateWithYValue(p_186663_3_, p_186663_4_, p_186663_1_);
        return clampToBox(box, vec3d);
        //return vec3d != null && box.intersectsWithXZ(vec3d) ? vec3d : null;
    }

	protected static Vec3d collideWithZPlane(AxisAlignedBB box, double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
    {
        Vec3d vec3d = getIntermediateWithZValue(p_186665_3_, p_186665_4_, p_186665_1_);
        return clampToBox(box, vec3d);
        //return vec3d != null && box.intersectsWithXY(vec3d) ? vec3d : null;
    }
	
	protected static Vec3d clampToBox(AxisAlignedBB box, Vec3d vec)
    {
		return new Vec3d(MathHelper.clamp(vec.x, box.minX, box.maxX), MathHelper.clamp(vec.y, box.minY, box.maxY), MathHelper.clamp(vec.z, box.minZ, box.maxZ));
    }
	
	protected static boolean isClosest(Vec3d line1, Vec3d line2, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_)
    {
		if(p_186661_2_ == null)
			return true;
		double d1 = dist_to_segment_squared(p_186661_3_, line1, line2);
		double d2 = dist_to_segment_squared(p_186661_2_, line1, line2);
		if(Math.abs(d1-d2) < 0.01)
			return line1.squareDistanceTo(p_186661_3_) < line1.squareDistanceTo(p_186661_2_);
        return d1 < d2;
    }
	
	//Drillgon200: https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
	//Drillgon200: I'm not figuring this out myself.
	protected static double dist_to_segment_squared(Vec3d point, Vec3d linePoint1, Vec3d linePoint2) {
		  double line_dist = linePoint1.squareDistanceTo(linePoint2);
		  if (line_dist == 0) return point.squareDistanceTo(linePoint1);
		  double t = ((point.x - linePoint1.x) * (linePoint2.x - linePoint1.x) + (point.y - linePoint1.y) * (linePoint2.y - linePoint1.y) + (point.z - linePoint1.z) * (linePoint2.z - linePoint1.z)) / line_dist;
		  t = MathHelper.clamp(t, 0, 1);
		  Vec3d pointOnLine = new Vec3d(linePoint1.x + t * (linePoint2.x - linePoint1.x), linePoint1.y + t * (linePoint2.y - linePoint1.y), linePoint1.z + t * (linePoint2.z - linePoint1.z));
		  return point.squareDistanceTo(pointOnLine);
	}
	
	/**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    @Nullable
    public static Vec3d getIntermediateWithXValue(Vec3d vec1, Vec3d vec, double x)
    {
        double d0 = vec.x - vec1.x;
        double d1 = vec.y - vec1.y;
        double d2 = vec.z - vec1.z;

        if (d0 * d0 < 1.0000000116860974E-7D)
        {
            return vec;
        }
        else
        {
            double d3 = (x - vec1.x) / d0;
            if(d3 < 0){
            	return new Vec3d(x, vec.y, vec.z);
            } else if(d3 > 1){
            	return new Vec3d(x, vec1.y, vec1.z);
            } else {
            	return new Vec3d(vec1.x + d0 * d3, vec1.y + d1 * d3, vec1.z + d2 * d3);
            }
            //return d3 >= 0.0D && d3 <= 1.0D ? new Vec3d(vec1.x + d0 * d3, vec1.y + d1 * d3, vec1.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    @Nullable
    public static Vec3d getIntermediateWithYValue(Vec3d vec1, Vec3d vec, double y)
    {
        double d0 = vec.x - vec1.x;
        double d1 = vec.y - vec1.y;
        double d2 = vec.z - vec1.z;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return vec;
        }
        else
        {
            double d3 = (y - vec1.y) / d1;
            if(d3 < 0){
            	return new Vec3d(vec.x, y, vec.z);
            } else if(d3 > 1){
            	return new Vec3d(vec1.x, y, vec1.z);
            } else {
            	return new Vec3d(vec1.x + d0 * d3, vec1.y + d1 * d3, vec1.z + d2 * d3);
            }
            //return d3 >= 0.0D && d3 <= 1.0D ? new Vec3d(vec1.x + d0 * d3, vec1.y + d1 * d3, vec1.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    @Nullable
    public static Vec3d getIntermediateWithZValue(Vec3d vec1, Vec3d vec, double z)
    {
        double d0 = vec.x - vec1.x;
        double d1 = vec.y - vec1.y;
        double d2 = vec.z - vec1.z;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return vec;
        }
        else
        {
            double d3 = (z - vec1.z) / d2;
            if(d3 < 0){
            	return new Vec3d(vec.x, vec.y, z);
            } else if(d3 > 1){
            	return new Vec3d(vec1.x, vec1.y, z);
            } else {
            	return new Vec3d(vec1.x + d0 * d3, vec1.y + d1 * d3, vec1.z + d2 * d3);
            }
            //return d3 >= 0.0D && d3 <= 1.0D ? new Vec3d(vec1.x + d0 * d3, vec1.y + d1 * d3, vec1.z + d2 * d3) : null;
        }
    }
    
    public static Vec3d getEuler(Vec3d vec){
    	double yaw = Math.toDegrees(Math.atan2(vec.x, vec.z));
		double sqrt = MathHelper.sqrt(vec.x * vec.x + vec.z * vec.z);
		double pitch = Math.toDegrees(Math.atan2(vec.y, sqrt));
		return new Vec3d(yaw, pitch, 0);
    }
    
    //Drillgon200: https://thebookofshaders.com/glossary/?search=smoothstep
    public static double smoothstep(double t, double edge0, double edge1){
    	t = MathHelper.clamp((t - edge0) / (edge1 - edge0), 0.0, 1.0);
        return t * t * (3.0 - 2.0 * t);	
    }
    public static float smoothstep(float t, float edge0, float edge1){
    	t = MathHelper.clamp((t - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);	
    }
	
	public static Vec3d getPosition(float interpolation, EntityPlayer player) {
		if(interpolation == 1.0F) {
			return new Vec3d(player.posX, player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()), player.posZ);
		} else {
			double d0 = player.prevPosX + (player.posX - player.prevPosX) * interpolation;
			double d1 = player.prevPosY + (player.posY - player.prevPosY) * interpolation + (player.getEyeHeight() - player.getDefaultEyeHeight());
			double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * interpolation;
			return new Vec3d(d0, d1, d2);
		}
	}

	public static boolean hasInventoryItem(InventoryPlayer inventory, Item ammo) {
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack.getItem() == ammo) {
				return true;
			}
		}
		return false;
	}
	
	public static int countInventoryItem(InventoryPlayer inventory, Item ammo) {
		int count = 0;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack.getItem() == ammo) {
				count += stack.getCount();
			}
		}
		return count;
	}

	/**
	 * Same as ItemStack.areItemStacksEqual, except the second one's tag only has to contain all the first one's tag, rather than being exactly equal.
	 */
	public static boolean areItemStacksCompatible(ItemStack base, ItemStack toTest, boolean shouldCompareSize){
		if (base.isEmpty() && toTest.isEmpty())
        {
            return true;
        }
        else
        {
            if(!base.isEmpty() && !toTest.isEmpty()){

            	if(shouldCompareSize && base.getCount() != toTest.getCount()){
            		return false;
            	} 
            	else if (base.getItem() != toTest.getItem())
                {
                    return false;
                }
                else if (base.getMetadata() != toTest.getMetadata() && !(base.getMetadata() == OreDictionary.WILDCARD_VALUE))
                {
                    return false;
                }
                else if (base.getTagCompound() == null && toTest.getTagCompound() != null)
                {
                    return false;
                }
                else
                {
                    return (base.getTagCompound() == null || tagContainsOther(base.getTagCompound(), toTest.getTagCompound())) && base.areCapsCompatible(toTest);
                }
            }
        }
		return false;
	}

	/**
	 * Returns true if the second compound contains all the tags and values of the first one, but it can have more. This helps with intermod compatibility
	 */
	public static boolean tagContainsOther(NBTTagCompound tester, NBTTagCompound container){
		if(tester == null && container == null){
			return true;
		} if(tester == null ^ container == null){
			return false;
		} else {
			for(String s : tester.getKeySet()){
				if(!container.hasKey(s)){
					return false;
				} else {
					NBTBase nbt1 = tester.getTag(s);
					NBTBase nbt2 = container.getTag(s);
					if(nbt1 instanceof NBTTagCompound && nbt2 instanceof NBTTagCompound){
						if(!tagContainsOther((NBTTagCompound)nbt1, (NBTTagCompound) nbt2))
							return false;
					} else {
						if(!nbt1.equals(nbt2))
							return false;
					}
				}
			}
		}
		return true;
	}

	public static Vec3d normalFromRayTrace(RayTraceResult r) {
		Vec3i n = r.sideHit.getDirectionVec();
		return new Vec3d(n.getX(), n.getY(), n.getZ());
	}
	
}
