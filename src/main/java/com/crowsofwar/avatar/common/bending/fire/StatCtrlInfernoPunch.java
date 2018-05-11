package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.DataCategory;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.data.ctx.PlayerBender;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunch extends StatusControl {
	public StatCtrlInfernoPunch() {
		super(15, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}


	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");

		return false;


	}


	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		EntityLivingBase target = (EntityLivingBase) event.getEntity();
		Bender ctx = Bender.get(entity);
		if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer) && ctx.getData() != null ) {
			Vector direction= Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			int punchesLeft = 1;
			float knockBack = 0.5F;
			int fireTime = 5;
			float damage = 3;
			AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");

			if (abilityData.getLevel() >= 1) {
				damage = 4;
				knockBack = 0.75F;
				fireTime = 6;


			}
			if (abilityData.getLevel() >= 2) {
				damage = 5;
				knockBack = 1F;
				fireTime = 8;
				punchesLeft = 2;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = 10;
				knockBack = 1.5F;
				fireTime = 15;
				punchesLeft = 1;
				//Creates a bunch of fire blocks around the target
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage = 2;
				punchesLeft = 3;
				knockBack = 0.75F;
				fireTime = 4;
			}

			if (ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
				if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
					//DamageSource ds = DamageSource.LAVA;
					DamageSource ds  = DamageSource.MAGIC;
					target.attackEntityFrom(ds, damage);
					target.setFire(fireTime);
					ctx.getData().removeStatusControl(INFERNO_PUNCH);
					punchesLeft--;
					target.motionX += direction.x() * knockBack;
					target.motionY += knockBack ;//+ (direction.y() * infernoPunch.knockBack);
					target.motionZ += direction.z() * knockBack;
					target.isAirBorne = true;
					// this line is needed to prevent a bug where players will not be pushed in multiplayer
					AvatarUtils.afterVelocityAdded(target);
					if (punchesLeft > 0) {
						ctx.getData().addStatusControl(INFERNO_PUNCH);
					}
				}
			}
		}
	}
}


