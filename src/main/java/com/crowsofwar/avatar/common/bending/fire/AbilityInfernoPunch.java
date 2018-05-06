package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.common.bending.StatusControl.FIRE_JUMP;
import static com.crowsofwar.avatar.common.bending.StatusControl.INFERNO_PUNCH;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AbilityInfernoPunch extends Ability {
	public AbilityInfernoPunch() {
		super(Firebending.ID, "inferno_punch");
	}

	public int punchesLeft = 1;

	public float damage = 5;

	public float knockBack = 1;

	public int fireTime = 5;

	public boolean haveStatusControl = false;

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		AbilityData abilityData = data.getAbilityData(this);

		if (!data.hasStatusControl(INFERNO_PUNCH) && bender.consumeChi(STATS_CONFIG.chiFireball)) {
			data.addStatusControl(INFERNO_PUNCH);
			haveStatusControl = true;
			if (abilityData.getLevel() >= 1) {
				damage = 6;
				knockBack = 1.25F;
				fireTime = 6;

			}
			if (abilityData.getLevel() >= 2) {
				damage = 8;
				knockBack = 1.5F;
				fireTime = 8;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = 10;
				knockBack = 3;
				fireTime = 15;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage = 4;
				punchesLeft = 3;
				knockBack = 1.25F;
				fireTime = 4;
			}
		}
	}

	/*@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		AbilityInfernoPunch punch = new AbilityInfernoPunch();
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();

		if (event.getSource().getTrueSource() == entity && event.getSource().getTrueSource().getHeldEquipment().equals(ItemStack.EMPTY)) {
			DamageSource ds = AvatarDamageSource.causeFireDamage(event.getEntity(), entity);
			if (punch.haveStatusControl && punch.punchesLeft() && entity instanceof EntityBender) {
				event.getEntity().attackEntityFrom(ds, punch.damage);
				punch.punchesLeft--;
			}
		}
	}**/



	public boolean punchesLeft() {
		return punchesLeft > 0;
	}

}



