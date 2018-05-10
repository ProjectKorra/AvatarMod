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
		AbilityInfernoPunch infernoPunch = new AbilityInfernoPunch();
		return infernoPunch.punchesLeft <= 0;


	}


	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		EntityLivingBase target = (EntityLivingBase) event.getEntity();
		Bender ctx = Bender.get(entity);

		if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer)) {
			AbilityInfernoPunch infernoPunch = new AbilityInfernoPunch();
			if (ctx.getData() != null && ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
				if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
					DamageSource ds = DamageSource.ON_FIRE;
					System.out.println("Step One Accomplished!");
					target.attackEntityFrom(ds, infernoPunch.damage);
					target.setFire(infernoPunch.fireTime);
					System.out.println("Attack Successful!");
					if (infernoPunch.punchesLeft > 0){
						infernoPunch.punchesLeft--;
						if(infernoPunch.punchesLeft <= 0){
							ctx.getData().removeStatusControl(INFERNO_PUNCH);
						}
					}

				}
			}
		}
	}
}


