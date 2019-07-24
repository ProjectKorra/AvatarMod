package com.crowsofwar.avatar.common.bending.fire;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

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

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunchFirst extends StatusControl {
	private ParticleSpawner particleSpawner;

	public StatCtrlInfernoPunchFirst() {
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
			float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
			float damage = STATS_CONFIG.InfernoPunchDamage * 7 / 3 * powerModifier;
			float knockBack = 1.5F * powerModifier;
			int fireTime = 15 + (int) (powerModifier * 10);

			if (entity.isPotionActive(MobEffects.STRENGTH)) {
				damage += (Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
			}

		}
		return true;
	}
}
