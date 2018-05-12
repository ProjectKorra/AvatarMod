package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunch extends StatusControl {
	public StatCtrlInfernoPunch() {
		super(15, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}


	@Override
	public boolean execute(BendingContext ctx) {
		return false;

	}


	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		EntityLivingBase target = (EntityLivingBase) event.getEntity();


		if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer)) {
			Bender ctx = Bender.get(entity);
			if (ctx.getData() != null) {
				Vector direction = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
				AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
				float knockBack = 1F;
				int fireTime = 5;
				float damage = 3;
				int punchesLeft = 1;


				if (abilityData.getLevel() >= 1) {
					damage = 4;
					knockBack = 1.125F;
					fireTime = 6;
				}
				if (abilityData.getLevel() >= 2) {
					damage = 5;
					knockBack = 1.25F;
					fireTime = 8;
					punchesLeft = 2;
				}

				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
					damage = 10;
					knockBack = 1.5F;
					fireTime = 15;
					//Creates a bunch of fire blocks around the target
				}
				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					damage = 2;
					knockBack = 0.75F;
					fireTime = 4;
					punchesLeft = 3;
				}
				if (ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
					for (int i = 0; i < punchesLeft; i++) {
						if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
							target.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_BLAZE_HURT,
									SoundCategory.PLAYERS, 1, .7f);
							//DamageSource ds = DamageSource.LAVA;
							DamageSource ds = DamageSource.MAGIC;
							target.attackEntityFrom(ds, damage);
							target.setFire(fireTime);
							System.out.println(damage);
							target.motionX += direction.x() * knockBack;
							target.motionY += direction.y() * knockBack >= 0 ? knockBack / 2 + (direction.y() * knockBack / 2) : knockBack / 2;
							target.motionZ += direction.z() * knockBack;
							target.isAirBorne = true;
							// this line is needed to prevent a bug where players will not be pushed in multiplayer
							AvatarUtils.afterVelocityAdded(target);


						}
					}
					ctx.getData().removeStatusControl(INFERNO_PUNCH);
				}
			}
		}
	}
}


