package com.crowsofwar.avatar.common.bending.air.powermods;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.BuffPowerModifier;
import com.crowsofwar.avatar.common.bending.air.AbilitySlipstream;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class SlipstreamPowerModifier extends BuffPowerModifier {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onSlipstreamInvisibility(RenderPlayerEvent event) {
		if (event.getEntityPlayer() != null) {
			EntityPlayer player = event.getEntityPlayer();
			Bender b = Bender.get(player);
			if (b != null && b.getData() != null)
				if (b.getData().getPowerRatingManager(Airbending.ID).hasModifier(SlipstreamPowerModifier.class))
					if (player.getActivePotionEffect(MobEffects.INVISIBILITY) != null && player.getActivePotionEffect(MobEffects.INVISIBILITY).getDuration() > 0)
						event.setCanceled(true);
		}
	}

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(new AbilitySlipstream());

		double modifier = 20 + 8 * abilityData.getLevel();
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 70;
		}

		return modifier;

	}

	@Override
	public boolean onUpdate(BendingContext ctx) {

		AbilityData data = ctx.getData().getAbilityData("slipstream");
		EntityLivingBase entity = ctx.getBenderEntity();

		if (data.getLevel() >= 2) {

			double invisibilityChance = 0.3;
			int invisiblityDuration = 30;

			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				invisibilityChance = 0.4;
				invisiblityDuration = 40;
			}

			// Intermittently grant invisibility
			if (entity.ticksExisted % 20 == 0) {
				// 40% chance per second for invisibility
				if (Math.random() < invisibilityChance) {
					PotionEffect effect = new PotionEffect(MobEffects.INVISIBILITY, invisiblityDuration, 0, false, false);
					entity.addPotionEffect(effect);
				}
			}
			if (entity.getActivePotionEffect(MobEffects.INVISIBILITY) != null && entity.getActivePotionEffect(MobEffects.INVISIBILITY).getDuration() > 0) {
				World world = entity.world;
				if (world.isRemote) {
					AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
					double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
					double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
					double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
							world.rand.nextGaussian() / 60).time(12).clr(0.8F, 0.8F, 0.8F)
							.scale((1.5F + Math.min(data.getLevel(), 0) / 2F) * 2).spawn(world);
				}
			}

		}
		return super.onUpdate(ctx);
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		ctx.getBenderEntity().setNoGravity(false);
		super.onRemoval(ctx);
	}

	@Override
	protected Vision[] getVisions() {
		return new Vision[]{Vision.SLIPSTREAM_WEAK, Vision.SLIPSTREAM_MEDIUM,
				Vision.SLIPSTREAM_POWERFUL};
	}

	@Override
	protected String getAbilityName() {
		return "slipstream";
	}

}

