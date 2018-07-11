package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class WaterRegenChi {

	@SubscribeEvent
	public static void waterRegenChi(LivingEvent.LivingUpdateEvent event) {
		//TODO: Use configurable lists; they aren't working rn, I need to figure out why
		EntityLivingBase entity = (EntityLivingBase) event.getEntity();
		World world = entity.getEntityWorld();
		if (entity instanceof EntityBender || entity instanceof EntityPlayerMP) {
			Bender bender = Bender.get(entity);
			if (bender.getData() != null) {
				BendingData ctx = BendingData.get(entity);
				if (ctx.hasBendingId(Waterbending.ID)) {
					BlockPos block = entity.getPosition();
					Block currentBlock = world.getBlockState(block).getBlock();
					if (currentBlock == Blocks.WATER || currentBlock == Blocks.FLOWING_WATER) {
						Chi chi = ctx.chi();
						if (world.getWorldTime() % 24000 <= 2) {
							chi.changeTotalChi(CHI_CONFIG.regenInWater);
							if (chi.getTotalChi() > chi.getMaxChi()) {
								chi.setTotalChi(chi.getMaxChi());
							}
						}
					}
				}

			}
		}
	}
}
