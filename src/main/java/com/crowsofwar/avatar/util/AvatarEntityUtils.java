package com.crowsofwar.avatar.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class AvatarEntityUtils {

    /**
     * Applies a velocity that an entity in the provided cardinal. Does not support UP & DOWN
     */
    public static void applyMotionToEntityInDirection(Entity entity, EnumFacing cardinal, double velocity) {
        switch (cardinal) {
            case NORTH:
                entity.motionZ = -velocity;
                break;
            case EAST:
                entity.motionX = velocity;
                break;
            case SOUTH:
                entity.motionZ = velocity;
                break;
            case WEST:
                entity.motionX = -velocity;
                break;
            default:
                break;
        }
    }


    //Smelts any item, really unabalanced
    public static void smeltItemEntity(EntityItem entity) {
        ItemStack stack = entity.getItem();
        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(stack);
        EntityItem item = new EntityItem(entity.world, entity.posX, entity.posY + 0.75, entity.posZ, smelted);
        item.setDefaultPickupDelay();
        item.setEntityInvulnerable(true);
        if (!entity.world.isRemote)
            entity.world.spawnEntity(item);
        entity.setDead();
    }

    public static void smeltItemEntity(EntityItem entity, int smeltLevel) {
        ItemStack stack = entity.getItem();
        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(stack);
        float exp = FurnaceRecipes.instance().getSmeltingExperience(stack);
        boolean canSmelt = false;
        switch (smeltLevel) {
            default:
                canSmelt = true;
                break;
            case 4:
                canSmelt = stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword;
			case 3:
				canSmelt |= stack.getItem() instanceof ItemTool;
            case 2:
                canSmelt |= stack.getItem() instanceof ItemFood;
            case 1:
                canSmelt |= stack.getItem() instanceof ItemBlock;
                break;

        }
        if (smeltLevel / 4F > exp && canSmelt) {
            smeltLevel = Math.max(0, Math.min(smeltLevel, 4) - 1);
            boolean rarity = smelted.getRarity().ordinal() <= smeltLevel;

            if (rarity) {
                EntityItem item = new EntityItem(entity.world, entity.posX, entity.posY + 0.75, entity.posZ, smelted);
                item.setDefaultPickupDelay();
                item.setEntityInvulnerable(true);
                item.setNoDespawn();
                if (!entity.world.isRemote)
                    entity.world.spawnEntity(item);
                entity.setDead();
            }
        }
    }

    /**
     *
     */
    public static void setRotationFromPosition(Entity toChange, Entity lookingAt) {
        double dx = toChange.posX - lookingAt.posX;
        double dz = toChange.posZ - lookingAt.posZ;
        double angle = Math.atan2(dz, dx) * 180 / Math.PI;
        double pitch = Math.atan2((toChange.posY + toChange.getEyeHeight()) - (lookingAt.posY + (lookingAt.height / 2.0F)), Math.sqrt(dx * dx + dz * dz)) * 180 / Math.PI;
        double distance = toChange.getDistance(lookingAt);
        float rYaw = (float) (angle - toChange.rotationYaw);
        while (rYaw > 180) {
            rYaw -= 360;
        }
        while (rYaw < -180) {
            rYaw += 360;
        }
        rYaw += 90F;
        float rPitch = (float) pitch - (float) (10.0F / Math.sqrt(distance)) + (float) (distance * Math.PI / 90);
        toChange.turn(rYaw, -(rPitch - toChange.rotationPitch));
    }

    /**
     *
     */
    public static void setRotationFromPosition(Entity toChange, Vec3d lookingAt) {
        double dx = toChange.posX - lookingAt.x;
        double dz = toChange.posZ - lookingAt.z;
        double angle = Math.atan2(dz, dx) * 180 / Math.PI;
        double pitch = Math.atan2((toChange.posY + toChange.getEyeHeight()) - (lookingAt.y), Math.sqrt(dx * dx + dz * dz)) * 180 / Math.PI;
        double distance = toChange.getDistance(lookingAt.x, lookingAt.y, lookingAt.z);
        float rYaw = (float) (angle - toChange.rotationYaw);
        while (rYaw > 180) {
            rYaw -= 360;
        }
        while (rYaw < -180) {
            rYaw += 360;
        }
        rYaw += 90F;
        float rPitch = (float) pitch - (float) (10.0F / Math.sqrt(distance)) + (float) (distance * Math.PI / 90);
        turnEntity(toChange, rYaw, -(rPitch - toChange.rotationPitch), 1.25F);
        //toChange.turn(rYaw, -(rPitch - toChange.rotationPitch));
    }

    public static Entity getEntityFromStringID(String UUID) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(java.util.UUID.fromString(UUID));
    }

    public static EntityPlayer getPlayerFromStringID(String UUID) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(java.util.UUID.fromString(UUID));
    }

    public static EntityPlayer getPlayerFromUsername(String username) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);
    }

    @SideOnly(Side.CLIENT)
    public static void turnEntity(Entity entity, float yaw, float pitch, float turnSpeed) {
        float f = entity.rotationPitch;
        float f1 = entity.rotationYaw;
        entity.rotationYaw = (float) ((double) entity.rotationYaw + (double) yaw * 0.15D);
        entity.rotationPitch = (float) ((double) entity.rotationPitch - (double) pitch * 0.15D);
        entity.rotationPitch = MathHelper.clamp(entity.rotationPitch, -90.0F, 90.0F);
        entity.prevRotationPitch += entity.rotationPitch * turnSpeed - f;
        entity.prevRotationYaw += entity.rotationYaw * turnSpeed - f1;
        if (entity.getRidingEntity() != null) {
            entity.getRidingEntity().applyOrientationToEntity(entity);
        }
    }


    /**
     * Shorthand for {@link AvatarEntityUtils#getEntitiesWithinRadius(double, double, double, double, World, Class)}
     * with EntityLivingBase as the entity type. This is by far the most common use for that method.
     *
     * @param radius The search radius
     * @param x      The x coordinate to search around
     * @param y      The y coordinate to search around
     * @param z      The z coordinate to search around
     * @param world  The world to search in
     */
    public static List<EntityLivingBase> getLivingEntitiesWithinRadius(double radius, double x, double y, double z,
                                                                       World world) {
        return getEntitiesWithinRadius(radius, x, y, z, world, EntityLivingBase.class);
    }

    /**
     * Shorthand for {@link AvatarEntityUtils#getEntitiesWithinRadius(double, double, double, double, World, Class)}
     * with EntityLivingBase as the entity type. This is by far the most common use for that method.
     *
     * @param radius The search radius
     * @param x      The x coordinate to search around
     * @param y      The y coordinate to search around
     * @param z      The z coordinate to search around
     * @param world  The world to search in
     */
    public static List<Entity> getEntitiesWithinRadius(double radius, double x, double y, double z,
                                                       World world) {
        return getEntitiesWithinRadius(radius, x, y, z, world, Entity.class);
    }

    /**
     * Returns all entities of the specified type within the specified radius of the given coordinates. This is
     * different to using a raw AABB because a raw AABB will search in a cube volume rather than a sphere. Note that
     * this does not exclude any entities; if any specific entities are to be excluded this must be checked when
     * iterating through the list.
     *
     * @param radius     The search radius
     * @param x          The x coordinate to search around
     * @param y          The y coordinate to search around
     * @param z          The z coordinate to search around
     * @param world      The world to search in
     * @param entityType The class of entity to search for; pass in Entity.class for all entities
     * @see AvatarEntityUtils#getEntitiesWithinRadius(double, double, double, double, World)
     */
    public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z,
                                                                     World world, Class<T> entityType) {
        AxisAlignedBB aabb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = world.getEntitiesWithinAABB(entityType, aabb);
        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i).getDistance(x, y, z) > radius) {
                entityList.remove(i);
                break;
            }
        }
        return entityList;
    }

    public static Vec3d getMiddleOfEntity(Entity entity) {
        double x = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX;
        double y = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY;
        double z = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ;
        return new Vec3d(entity.getEntityBoundingBox().minX + x / 2, entity.getEntityBoundingBox().minY + y / 2, entity.getEntityBoundingBox().minZ + z / 2);
    }

    @Nullable
    public static Vec3d getMiddleOfBlock(IBlockState block, World world, BlockPos pos) {
        AxisAlignedBB box = block.getCollisionBoundingBox(world, pos);
        if (box != null) {
            double x = box.maxX - box.minX;
            double y = box.maxY - box.minY;
            double z = box.maxZ - box.minZ;
            return new Vec3d(box.minX + x / 2, box.minY + y / 2, box.minZ + z / 2);
        }
        return null;

    }

    //Same as the above method, but the bottom y value of the entity instead of the middle y value.
    public static Vec3d getBottomMiddleOfEntity(Entity entity) {
        double x = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX;
        double z = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ;
        return new Vec3d(entity.getEntityBoundingBox().minX + x / 2, entity.getEntityBoundingBox().minY, entity.getEntityBoundingBox().minZ + z / 2);

    }

    public static void attackDragon(EntityDragon hurt, DamageSource source, float damage) {
        hurt.attackEntityFromPart(hurt.dragonPartBody, source, damage);
    }

    /**
     * Returns an array of {@code Vec3d} objects representing the vertices of the given bounding box.
     *
     * @param box The bounding box whose vertices are to be returned.
     * @return The list of vertices, which will contain 8 elements. Using EnumFacing initials, the order is:
     * DNW, DNE, DSE, DSW, UNW, UNE, USE, USW. The returned coordinates are absolute (i.e. measured from the world origin).
     */
    public static Vec3d[] getVertices(AxisAlignedBB box) {
        return new Vec3d[]{
                new Vec3d(box.minX, box.minY, box.minZ),
                new Vec3d(box.maxX, box.minY, box.minZ),
                new Vec3d(box.maxX, box.minY, box.maxZ),
                new Vec3d(box.minX, box.minY, box.maxZ),
                new Vec3d(box.minX, box.maxY, box.minZ),
                new Vec3d(box.maxX, box.maxY, box.minZ),
                new Vec3d(box.maxX, box.maxY, box.maxZ),
                new Vec3d(box.minX, box.maxY, box.maxZ)
        };
    }
}
