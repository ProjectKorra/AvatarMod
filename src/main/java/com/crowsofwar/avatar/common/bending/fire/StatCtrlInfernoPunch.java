package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.world.AvatarFireExplosion;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunch extends StatusControl {
	public StatCtrlInfernoPunch() {
		super(15, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}


	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		EntityFireball fireball = new EntityFireball(world);

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				Vector playerPos = getEyePos(entity);
				Vector target = playerPos.plus(getLookRectangular(entity).times(2.5));
				fireball.setPosition(target);
				fireball.setOwner(entity);
				fireball.setDamage(0.5F);
				fireball.setSize(14);
				fireball.addVelocity(Vector.getLookRectangular(entity).times(20));
				fireball.setBehavior(new FireballBehavior.Thrown());
				world.spawnEntity(fireball);
				return true;
			}
		return false;

	}


	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		EntityLivingBase target = (EntityLivingBase) event.getEntity();
		World world = target.getEntityWorld();
		if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer)) {
			Bender ctx = Bender.get(entity);
			if (ctx.getData() != null) {
				Vector direction = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
				AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
				float knockBack = 1F;
				int fireTime = 5;
				float damageModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
				float damage = 3 + (3 * damageModifier);

				if (abilityData.getLevel() >= 1) {
					damage = 4 + (4 * damageModifier);
					knockBack = 1.125F;
					fireTime = 6;
				} else if (abilityData.getLevel() >= 2) {
					damage = 5 + (5 * damageModifier);
					knockBack = 1.25F;
					fireTime = 8;
				}
				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
					damage = 10 + (10 * damageModifier);
					knockBack = 1.5F;
					fireTime = 15;
				} else if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					damage = 2 + (2 * damageModifier);
					knockBack = 0.75F;
					fireTime = 4;
				}
				if (ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
					if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
						if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
							BlockPos blockPos = target.getPosition();
							AvatarFireExplosion fireExplosion = new AvatarFireExplosion(target.world, target, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 3F, true, false);
							fireExplosion.doExplosionA();
							if (world instanceof WorldServer) {
								WorldServer World = (WorldServer) target.getEntityWorld();
								World.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY, target.posZ, 200, 0.05, 0.05, 0.05, 0.75);
								fireExplosion.doExplosionB(true);
							}
						}

						world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
								SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

						target.attackEntityFrom(DamageSource.IN_FIRE, damage);
						target.setFire(fireTime);
						target.motionX += direction.x() * knockBack;
						target.motionY += direction.y() * knockBack >= 0 ? knockBack / 2 + (direction.y() * knockBack / 2) : knockBack / 2;
						target.motionZ += direction.z() * knockBack;
						target.isAirBorne = true;
						// this line is needed to prevent a bug where players will not be pushed in multiplayer
						AvatarUtils.afterVelocityAdded(target);
						ctx.getData().removeStatusControl(INFERNO_PUNCH);
					}
				}
			}
		}
	}
}
