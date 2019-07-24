package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunchMain extends StatusControl {
	private ParticleSpawner particleSpawner;

	public StatCtrlInfernoPunchMain() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		particleSpawner = new NetworkParticleSpawner();
	}

	@Override
	public boolean execute(BendingContext ctx) {
		//TODO: Raytrace instead of event
		EntityLivingBase entity = ctx.getBenderEntity();
		if (entity instanceof EntityPlayer) {
			double reach = ctx.getBenderEntity().getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
			AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
			BendingData data = ctx.getData();
			boolean hasInfernoPunch = data.hasStatusControl(INFERNO_PUNCH_MAIN) || data.hasStatusControl(INFERNO_PUNCH_FIRST);
			float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
			float damage = STATS_CONFIG.InfernoPunchDamage * powerModifier;
			float knockBack = 1 * powerModifier;
			int fireTime = 5 + (int) (powerModifier * 10);

			if (abilityData.getLevel() == 1) {
				damage = STATS_CONFIG.InfernoPunchDamage * 4 / 3 * powerModifier;
				knockBack = 1.125F * powerModifier;
				fireTime = 6;
			}
			if (abilityData.getLevel() >= 2) {
				damage = STATS_CONFIG.InfernoPunchDamage * 5 / 3 * powerModifier;
				knockBack = 1.25F + powerModifier;
				fireTime = 8 + (int) (powerModifier * 10);
			}
			if (data.hasStatusControl(INFERNO_PUNCH_FIRST)) {
				damage = STATS_CONFIG.InfernoPunchDamage * 7 / 3 * powerModifier;
				knockBack = 1.5F + powerModifier;
				fireTime = 15 + (int) (powerModifier * 10);
			}

			if (entity.isPotionActive(MobEffects.STRENGTH)) {
				damage += (Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
			}

		}
		return true;
	}
}
