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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

public class StatCtrlInfernoPunch extends StatusControl {
	public StatCtrlInfernoPunch() {
		super(15, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	public boolean haveStatusControl = true;
	@Override
	public boolean execute(BendingContext ctx) {
		//this.haveStatusControl = false;
		return true;

	}
	@SubscribeEvent
	public static void onInfernoPunch(LivingHurtEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		EntityLivingBase target = (EntityLivingBase) event.getEntity();
		StatCtrlInfernoPunch punch = new StatCtrlInfernoPunch();
		AbilityInfernoPunch infernoPunch = new AbilityInfernoPunch();
		//System.out.println(event.getSource().getTrueSource());

		if (event.getSource().getTrueSource() == entity) {
			if (entity instanceof EntityPlayer && entity.getHeldItemMainhand() == ItemStack.EMPTY) {
				DamageSource ds = DamageSource.ON_FIRE;
				System.out.println("Step One Accomplished!");
				//System.out.println(punch.haveStatusControl);
				if (punch.haveStatusControl && infernoPunch.punchesLeft()) {
					target.attackEntityFrom(ds, infernoPunch.damage);
					target.setFire(infernoPunch.fireTime);
					System.out.println("Attack Successful!");
					punch.haveStatusControl = false;
					//	event.getEntity().attackEntityFrom(ds, punch.damage);
					infernoPunch.punchesLeft--;

				}
				if (infernoPunch.punchesLeft <= 0) {
					punch.haveStatusControl = false;
				}
			}
		}
	}

}
