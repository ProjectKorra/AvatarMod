/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.util;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.crowsofwar.avatar.AvatarLog.WarningType.INVALID_SAVE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AvatarUtils {

	private static final DataParameter<Boolean> POWERED;

	static {
		POWERED = ReflectionHelper.getPrivateValue(EntityCreeper.class, null, "POWERED", "field_184714_b");
	}

	public static <T extends Entity> Comparator<T> getSortByDistanceComparator
			(Function<T, Float> distanceSupplier) {

		return (e1, e2) -> {
			float d1 = distanceSupplier.apply(e1);
			float d2 = distanceSupplier.apply(e2);

			if (d1 < d2) {
				return -1;
			} else {
				return d1 > d2 ? 1 : 0;
			}
		};

	}

	public static void chargeCreeper(EntityCreeper creeper) {
		creeper.getDataManager().set(POWERED, true);
	}

	public static void igniteCreeper(EntityCreeper creeper) {
		creeper.ignite();
	}

	//Methods for pushing stuff.
	public static void pushButton(World world, boolean pushStone, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (pushStone) {
			if (state.getBlock() instanceof BlockButton) {
				BlockButton button = (BlockButton) state.getBlock();
				button.onBlockActivated(world, pos, state, null, null, null, (float) pos.getX(),
						(float) pos.getY(), (float) pos.getZ());
			}
		} else if (state.getBlock() == Blocks.WOODEN_BUTTON) {
			BlockButton button = (BlockButton) state.getBlock();
			button.onBlockActivated(world, pos, state, null, null, null, (float) pos.getX(),
					(float) pos.getY(), (float) pos.getZ());
		}
	}

	public static void pushLever(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.LEVER) {
			BlockLever lever = (BlockLever) state.getBlock();
			lever.onBlockActivated(world, pos, state, null, null, null, (float) pos.getX(),
					(float) pos.getY(), (float) pos.getZ());
		}
	}

	public static void pushTrapDoor(World world, boolean pushIron, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.TRAPDOOR) {
			BlockTrapDoor trap = (BlockTrapDoor) state.getBlock();
			trap.onBlockActivated(world, pos, state, null, null, null, (float) pos.getX(),
					(float) pos.getY(), (float) pos.getZ());
		}
		if (pushIron) {
			if (state.getBlock() == Blocks.TRAPDOOR || state.getBlock() == Blocks.IRON_TRAPDOOR) {
				BlockTrapDoor trap = (BlockTrapDoor) state.getBlock();
				state = state.cycleProperty(BlockTrapDoor.OPEN);
				world.setBlockState(pos, state, 2);
				world.markBlockRangeForRenderUpdate(pos, pos.add(0, 1, 0));
				world.scheduleUpdate(pos, trap, trap.tickRate(world));
			}
		}
	}

	public static void pushDoor(Entity entity, boolean pushIron, BlockPos pos) {
		IBlockState state = entity.world.getBlockState(pos);
		EntityPlayer player = null;
		if (entity instanceof AvatarEntity) {
			if (((AvatarEntity) entity).getOwner() != null && ((AvatarEntity) entity).getOwner() instanceof EntityPlayer) {
				player = (EntityPlayer) ((AvatarEntity) entity).getOwner();
			}
		}
		if (state.getBlock() instanceof BlockDoor && state.getBlock() != Blocks.IRON_DOOR) {
			BlockDoor door = (BlockDoor) state.getBlock();
			door.onBlockActivated(entity.world, pos, state, player, null, null, (float) pos.getX(),
					(float) pos.getY(), (float) pos.getZ());
		}
		if (pushIron) {
			if (state.getBlock() instanceof BlockDoor) {
				BlockDoor door = (BlockDoor) state.getBlock();
				BlockPos blockpos = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
				IBlockState iblockstate = pos.equals(blockpos) ? state : entity.world.getBlockState(blockpos);
				state = iblockstate.cycleProperty(BlockDoor.OPEN);
				int open = door == Blocks.IRON_DOOR ? 1005 : 1006;
				int closed = door == Blocks.IRON_DOOR ? 1011 : 1012;
				entity.world.setBlockState(blockpos, state, 10);
				entity.world.markBlockRangeForRenderUpdate(blockpos, pos);
				entity.world.playEvent(player, state.getValue(BlockDoor.OPEN) ? open : closed, pos, 0);
			}
		}
	}

	public static void pushGate(Entity entity, BlockPos pos) {
		IBlockState state = entity.world.getBlockState(pos);
		EntityPlayer player = null;
		if (state.getBlock() instanceof BlockFenceGate) {
			if (entity instanceof AvatarEntity) {
				if (((AvatarEntity) entity).getOwner() != null && ((AvatarEntity) entity).getOwner() instanceof EntityPlayer) {
					player = (EntityPlayer) ((AvatarEntity) entity).getOwner();
				}
			}
			if (state.getValue(BlockFenceGate.OPEN)) {
				state = state.withProperty(BlockFenceGate.OPEN, false);
				entity.world.setBlockState(pos, state, 10);
			} else {
				state = state.withProperty(BlockFenceGate.OPEN, true);
				entity.world.setBlockState(pos, state, 10);
			}
			entity.world.playEvent(player, state.getValue(BlockFenceGate.OPEN) ? 1008 : 1014, pos, 0);
		}
	}

	/**
	 * Spawns a directional helix that has rotating particles.
	 *
	 * @param world         World the vortex spawns in.
	 * @param entity        Entity that's spawning the vortex.
	 * @param direction     The direction that the vortex is spawning in.
	 * @param maxAngle      The amount of particles/the maximum angle that the circle ticks to. 240 would mean there are 240 particles spiraling away.
	 * @param vortexLength  How long the vortex is. This is initially used at the height, before rotating the vortex.
	 * @param radius        The radius of the helix.
	 *                      otherwise you can get some funky effects. Ex: maxAngle/1.5 would give you a max radius of 1.5 blocks.
	 *                      Note: It might only be a diamater of 1.5 blocks- if so, uhhh... My bad.
	 * @param particle      The enum particle type.
	 * @param position      The starting/reference position of the helix. Used along with the direction position to determine the actual starting position.
	 * @param particleSpeed How fast the particles are moving and in what direction.
	 */
	public static void spawnDirectionalHelix(World world, EntityLivingBase entity, Vector direction, int maxAngle, double vortexLength, double radius, EnumParticleTypes particle, Vector position,
											 double particleSpeed) {
		for (int angle = 0; angle < maxAngle; angle++) {
			double x = radius * cos(angle);
			double y = angle / (maxAngle / vortexLength);
			double z = radius * sin(angle);
			Vector pos = new Vector(x, y, z);
			if (entity != null) {
				pos = Vector.rotateAroundAxisX(pos, entity.rotationPitch + 90);
				pos = Vector.rotateAroundAxisY(pos, entity.rotationYaw);
				world.spawnParticle(particle, pos.x() + position.x() + direction.x(), pos.y() + position.y() + direction.y(),
						pos.z() + position.z() + direction.z(), particleSpeed, particleSpeed, particleSpeed);                //	World.spawnParticle(particle, pos.x() + position.x() + direction.x(), pos.y() + position.y() + direction.y(),
				//			pos.z() + position.z() + direction.z(), particleSpeed, 1, 0, 0, 0, particleSpeed);
			}
		}
	}


	/**
	 * Solves the issue where players on singleplayer/LAN will sometimes not
	 * have velocity or position changed.
	 */
	public static void afterVelocityAdded(Entity entity) {
		if (entity instanceof EntityPlayerMP) {
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityTeleport(entity));
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}
	}


	/**
	 * Ensures that the angle is in the range of 0-360.
	 */
	public static float normalizeAngle(float angle) {
		while (angle < 0) {
			angle += 360;
		}
		angle %= 360;
		return angle;
	}

	/**
	 * Clears the list(or collection), and adds items from the NBT list. Does
	 * not permit null values.
	 *
	 * @param itemProvider Loads items from the list. Takes an NBT for that item, and
	 *                     returns the actual item object.
	 * @param nbt          NBT compound to load the list from
	 * @param listName     The name of the list tag
	 */
	public static <T> void readList(Collection<T> list, Function<NBTTagCompound, T> itemProvider,
									NBTTagCompound nbt, String listName) {

		list.clear();

		NBTTagList listTag = nbt.getTagList(listName, 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);
			T read = itemProvider.apply(item);
			if (read != null) {
				list.add(read);
			} else {
				AvatarLog.warn(INVALID_SAVE,
						"Invalid list " + listName + ", contains unknown value: " + item);
			}
		}

	}

	/**
	 * Writes the list to NBT.
	 *
	 * @param list     The list to write
	 * @param writer   Responsible for actually writing the desired data to NBT.
	 *                 Takes the NBT to write to & the item.
	 * @param nbt      NBT compound to write list to
	 * @param listName The name of the list tag
	 */
	public static <T> void writeList(Collection<T> list, BiConsumer<NBTTagCompound, T> writer,
									 NBTTagCompound nbt, String listName) {

		NBTTagList listTag = new NBTTagList();

		for (T item : list) {

			NBTTagCompound nbtItem = new NBTTagCompound();
			writer.accept(nbtItem, item);
			listTag.appendTag(nbtItem);

		}

		nbt.setTag(listName, listTag);

	}

	/**
	 * Clears the map and adds the saved entries. Does not permit null keys or
	 * values.
	 *
	 * @param map           Map to read into
	 * @param keyProvider   Creates a key object from a given NBT. The NBT is only used by
	 *                      the key.
	 * @param valueProvider Creates a value object from a given NBT. The NBT is only used
	 *                      by the value.
	 * @param nbt           NBT to read from
	 * @param mapName       Name to store it as
	 */
	public static <K, V> void readMap(Map<K, V> map, Function<NBTTagCompound, K> keyProvider,
									  Function<NBTTagCompound, V> valueProvider, NBTTagCompound nbt, String mapName) {

		map.clear();

		NBTTagList listTag = nbt.getTagList(mapName, 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);

			K key = keyProvider.apply(item.getCompoundTag("Key"));
			V value = valueProvider.apply(item.getCompoundTag("Value"));

			if (key == null) {
				// look @ dis industry standard pro debugging techniques
				AvatarLog.error("MapError: Issue reading map " + mapName + "'s key for item " + i);
				AvatarLog.error("MapError: Item compound- " + item);
				AvatarLog.error("MapError: Key compound- " + item.getCompoundTag("Key"));
				throw new DiskException("readMap- Cannot have null key for map (see log for " +
						"details)");
			}
			if (value == null) {
				AvatarLog.error("MapError: Issue reading map " + mapName + "'s value for item" + i);
				AvatarLog.error("MapError: Item compound- " + item);
				AvatarLog.error("MapError: Value compound- " + item.getCompoundTag("Value"));
				throw new DiskException("readMap- Cannot have null value for map (see log for " +
						"details)");
			}

			map.put(key, value);
		}

	}

	/**
	 * Writes the map's entries to the NBT. Does not permit null keys or values.
	 *
	 * @param map         Map to write
	 * @param keyWriter   Given a key object & NBT, writes the key to disk.
	 * @param valueWriter Given a value object & NBT, writes the value to disk.
	 * @param nbt         NBT to read from
	 * @param mapName     Name to store map as
	 */
	public static <K, V> void writeMap(Map<K, V> map, BiConsumer<NBTTagCompound, K> keyWriter,
									   BiConsumer<NBTTagCompound, V> valueWriter, NBTTagCompound nbt, String mapName) {

		NBTTagList listTag = new NBTTagList();
		Set<Map.Entry<K, V>> entries = map.entrySet();

		for (Map.Entry<K, V> entry : entries) {

			if (entry.getKey() == null)
				throw new DiskException("writeMap- does not permit null keys in map " + map);
			if (entry.getValue() == null)
				throw new DiskException("writeMap- does not permit null values in map " + map);

			NBTTagCompound item = new NBTTagCompound();
			NBTTagCompound keyNbt = new NBTTagCompound();
			NBTTagCompound valNbt = new NBTTagCompound();
			keyWriter.accept(keyNbt, entry.getKey());
			valueWriter.accept(valNbt, entry.getValue());
			item.setTag("Key", keyNbt);
			item.setTag("Value", valNbt);
			listTag.appendTag(item);
		}

		nbt.setTag(mapName, listTag);

	}

	/**
	 * Reads the inventory contents. A list called <code>listName</code> is used
	 * under that NBT tag.
	 */
	public static void readInventory(IInventory inventory, NBTTagCompound nbt, String listName) {
		NBTTagList list = nbt.getTagList(listName, 10);
		for (int i = 0; i < list.tagCount(); i++) {

			NBTTagCompound slotNbt = list.getCompoundTagAt(i);
			int slot = slotNbt.getInteger("Slot");

			if (slotNbt.getBoolean("Empty")) {
				inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
			} else {
				inventory.setInventorySlotContents(slot, new ItemStack(slotNbt));
			}

		}
	}

	/**
	 * Writes the inventory contents to the list tag called
	 * <code>listName</code>.
	 */
	public static void writeInventory(IInventory inventory, NBTTagCompound nbt, String listName) {

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {

			ItemStack stack = inventory.getStackInSlot(i);

			NBTTagCompound slotNbt = new NBTTagCompound();
			slotNbt.setBoolean("Empty", stack.isEmpty());
			slotNbt.setInteger("Slot", i);

			if (!stack.isEmpty()) {
				stack.writeToNBT(slotNbt);
			}

			list.appendTag(slotNbt);

		}

		nbt.setTag(listName, list);

	}


	@Nullable
	public static RayTraceResult standardEntityRayTrace(World world, Entity entity, Entity abilityEntity, Vec3d startPos, Vec3d endPos, float borderSize, boolean transparentBlocks, HashSet<Entity> excluded) {
		excluded.add(entity);
		if (abilityEntity != null) {
			excluded.add(abilityEntity);
		}
		return tracePath(world, (float) startPos.x,
				(float) startPos.y, (float) startPos.z,
				(float) endPos.x, (float) endPos.y, (float) endPos.z,
				borderSize, excluded, false, transparentBlocks);
	}

	/**
	 * This method is like the other method, it just uses vanilla damage sources.
	 *
	 * @param world        The world the raytrace is in.
	 * @param caster       The caster of the spell. This is so mobs don't attack each other when you use raytraces from mobs.
	 *                     All damage is done by the caster.
	 * @param startPos     Where the raytrace starts.
	 * @param endPos       Where the raytrace ends.
	 * @param borderSize   The width of the raytrace.
	 * @param spellEntity  The entity that's using this method, if applicable. If this method is directly used in a spell, just make this null.
	 * @param damage       The amount of damage.
	 * @param knockBack    The amount of knockback.
	 * @param fireTime     How long to set an enemy on fire.
	 */

	public static void handlePiercingBeamCollision(World world, EntityLivingBase caster, Vec3d startPos, Vec3d endPos, float borderSize, @Nullable AvatarEntity spellEntity, @Nullable Ability ability,
												   @Nullable BendingStyle element, float damage, Vec3d knockBack, int fireTime, float radius) {
		HashSet<Entity> excluded = new HashSet<>();
		RayTraceResult result = standardEntityRayTrace(world, caster, spellEntity, startPos, endPos, borderSize, false, excluded);
		if (result != null && result.entityHit instanceof EntityLivingBase) {
			EntityLivingBase hit = (EntityLivingBase) result.entityHit;
			String abilityName;
			BendingStyle style;
			//Ensures that the damage source exists.

			if (spellEntity != null) {
				style = spellEntity.getElement();
				abilityName = spellEntity.getAbility().getName();
			}
			else  {
				assert ability != null;
				assert element != null;
				style = element;
				abilityName = ability.getName();
			}

			String elementName = style.getName().substring(0, style.getName().length() - 7);
			String damageName = AvatarDamageSource.getNameFromBendingStyle(elementName);
			DamageSource damageSource = new EntityDamageSourceIndirect("avatar_" + damageName + "_" + abilityName, hit, caster);

			hit.setFire(fireTime);
			hit.attackEntityFrom(damageSource, damage);
			hit.motionX += knockBack.x;
			hit.motionY += knockBack.y;
			hit.motionZ += knockBack.z;
			afterVelocityAdded(hit);
			Vec3d pos = result.hitVec;
			AxisAlignedBB hitBox = new AxisAlignedBB(pos.x + radius, pos.y + radius, pos.z + radius, pos.x - radius, pos.y - radius, pos.z - radius);
			List<Entity> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox);
			excluded.add(hit);
			nearby.remove(hit);
			//This is so it doesn't count the entity that was hit by the raytrace and mess up the chain
			if (!nearby.isEmpty()) {
				for (Entity e : nearby) {
					if (e != caster && e != hit && !excluded.contains(e) && e.getTeam() != caster.getTeam()) {
						e.setFire(fireTime);
						e.attackEntityFrom(damageSource, damage);
						e.motionX += knockBack.x;
						e.motionY += knockBack.y;
						e.motionZ += knockBack.z;
						afterVelocityAdded(e);
						excluded.add(e);
					}
				}
			} else {
				handlePiercingBeamCollision(world, caster, pos, endPos, borderSize, spellEntity, ability, element, damage, knockBack, fireTime, radius);

			}

		}

	}


	/**
	 * Method for ray tracing entities (the useless default method doesn't work,
	 * despite EnumHitType having an ENTITY field...) You can also use this for
	 * seeking.
	 *
	 * @param world                  The world the raytrace is in.
	 * @param x                      startX
	 * @param y                      startY
	 * @param z                      startZ
	 * @param tx                     endX
	 * @param ty                     endY
	 * @param tz                     endZ
	 * @param borderSize             extra area to examine around line for entities
	 * @param excluded               any excluded entities (the player, spell
	 *                               entities, previously hit entities, etc)
	 * @param raytraceNonSolidBlocks This controls whether or not the raytrace goes
	 *                               through non-solid blocks, such as grass,
	 *                               fences, trapdoors, cobwebs, e.t.c.
	 * @return a RayTraceResult of either the block hit (no entity hit), the entity
	 * hit (hit an entity), or null for nothing hit
	 */
	@Nullable
	public static RayTraceResult tracePath(World world, float x, float y, float z, float tx, float ty, float tz,
										   float borderSize, HashSet<Entity> excluded, boolean collideablesOnly, boolean raytraceNonSolidBlocks) {
		Vec3d startVec = new Vec3d(x, y, z);
		Vec3d endVec = new Vec3d(tx, ty, tz);
		float minX = Math.min(x, tx);
		float minY = Math.min(y, ty);
		float minZ = Math.min(z, tz);
		float maxX = Math.max(x, tx);
		float maxY = Math.max(y, ty);
		float maxZ = Math.max(z, tz);
		AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).grow(borderSize, borderSize,
				borderSize);
		List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);
		RayTraceResult blockHit = world.rayTraceBlocks(startVec, endVec);
		if (blockHit != null && !world.getBlockState(blockHit.getBlockPos()).isFullBlock() && !raytraceNonSolidBlocks) {
			blockHit = null;
		}
		startVec = new Vec3d(x, y, z);
		endVec = new Vec3d(tx, ty, tz);
		float maxDistance = (float) endVec.distanceTo(startVec);
		if (blockHit != null) {
			maxDistance = (float) blockHit.hitVec.distanceTo(startVec);
		}
		Entity closestHitEntity = null;
		float closestHit = maxDistance;
		float currentHit;
		AxisAlignedBB entityBb;// = ent.getBoundingBox();
		RayTraceResult intercept;
		for (Entity ent : allEntities) {
			if ((ent.canBeCollidedWith() || !collideablesOnly) && (excluded == null || !excluded.contains(ent))) {
				float entBorder = ent.getCollisionBorderSize();
				entityBb = ent.getEntityBoundingBox();
				entityBb = entityBb.grow(entBorder, entBorder, entBorder);
				if (borderSize != 0)
					entityBb = entityBb.grow(borderSize, borderSize, borderSize);
				intercept = entityBb.calculateIntercept(startVec, endVec);
				if (intercept != null) {
					currentHit = (float) intercept.hitVec.distanceTo(startVec);
					if (currentHit < closestHit || currentHit == 0) {
						closestHit = currentHit;
						closestHitEntity = ent;
					}
				}
			}
		}
		if (closestHitEntity != null) {
			blockHit = new RayTraceResult(closestHitEntity);
		}
		return blockHit;
	}


	public static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * An exception thrown by reading/writing methods for NBT
	 *
	 * @author CrowsOfWar
	 */
	public static class DiskException extends RuntimeException {

		private DiskException(String message) {
			super(message);
		}

	}
}
