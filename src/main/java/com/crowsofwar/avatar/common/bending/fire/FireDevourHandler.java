package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandler.FIRE_DEVOUR_HANDLER;
import static java.lang.Math.toRadians;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireDevourHandler {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onShift(LivingEvent.LivingUpdateEvent event) {
		Entity e = event.getEntity();
		World world = e.getEntityWorld();
		if (e == event.getEntity()) {
			if (e instanceof EntityPlayer || e instanceof EntityBender) {
				BendingData data = BendingData.get((EntityLivingBase) e);
				Bender b = Bender.get((EntityLivingBase) e);
				if (b != null) {
					if (e.isSneaking() && STATS_CONFIG.shiftActivateFireDevour) {
						if (data.hasBendingId(Firebending.ID)) {
							data.addTickHandler(FIRE_DEVOUR_HANDLER);
						}

					}
				}
			}
		}
	}
}
