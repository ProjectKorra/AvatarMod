package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.fire.statctrls.StatCtrlFlameStrike;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.util.data.StatusControlController.FLAME_STRIKE_OFF;

public class FlameStrikeHandler extends TickHandler {

    public FlameStrikeHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();
        AbilityData abilityData = AbilityData.get(entity, "flame_strike");

        int usage = STATS_CONFIG.flameStrikeSettings.strikeNumber;
        int particleCount = 1;
        int level = abilityData.getLevel();
        boolean charge = false;
        int chargeLevel = StatCtrlFlameStrike.getChargeLevel(entity.getPersistentID());
        float particleSize = 0.7F;

        if (level == 1 || level == 2) {
            particleCount = 2;
        }

        if (level == 1) {
            particleSize = 0.8F;
        }
        if (level == 2) {
            particleSize = 0.875F;
        }
        if (level >= 2)
            charge = true;

        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
            particleCount = 3;
            usage = 5;
            particleSize = 0.825F;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
            particleCount = 2;
            usage = 3;
            particleSize = 1F;
        }

        //particleSize = (float) (particleSize * (0.8 + chargeLevel / 5F));
        charge |= usage - StatCtrlFlameStrike.getTimesUsed(entity.getPersistentID()) == 1;
        if ((data.hasStatusControl(FLAME_STRIKE_MAIN) || data.hasStatusControl(FLAME_STRIKE_OFF))) {

            Vec3d height, rightSide;
            if (entity instanceof EntityPlayer) {
                if (!AvatarMod.realFirstPersonRender2Compat && (PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) <= -1)) {
                    height = entity.getPositionVector().add(0, 1.6, 0);
                    height = height.add(entity.getLookVec().scale(0.8));
                    //Right
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT && data.hasStatusControl(FLAME_STRIKE_MAIN)
                            || entity.getPrimaryHand() == EnumHandSide.LEFT && data.hasStatusControl(FLAME_STRIKE_OFF)) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
                        rightSide = rightSide.add(height);
                    }
                    //Left
                    else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
                        rightSide = rightSide.add(height);
                    }
                } else {
                    height = entity.getPositionVector().add(0, 0.84, 0);
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT && data.hasStatusControl(FLAME_STRIKE_MAIN)
                            || entity.getPrimaryHand() == EnumHandSide.LEFT && data.hasStatusControl(FLAME_STRIKE_OFF)) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                        rightSide = rightSide.add(height);
                    } else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                        rightSide = rightSide.add(height);
                    }
                }
            } else {
                height = entity.getPositionVector().add(0, 0.84, 0);
                if (entity.getPrimaryHand() == EnumHandSide.RIGHT && data.hasStatusControl(FLAME_STRIKE_MAIN)
                        || entity.getPrimaryHand() == EnumHandSide.LEFT && data.hasStatusControl(FLAME_STRIKE_OFF)) {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                    rightSide = rightSide.add(height);
                } else {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                    rightSide = rightSide.add(height);
                }

            }
            if (world.isRemote)
                for (int i = 0; i < particleCount; i++) {
                    if (abilityData.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                                world.rand.nextGaussian() / 40).clr(255, 15, 5).collide(false).
                                scale(particleSize / 1.5F).element(new Firebending()).fade(AvatarUtils.getRandomNumberInRange(75, 200), AvatarUtils.getRandomNumberInRange(1, 180),
                                AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                                world.rand.nextGaussian() / 40).clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(false).
                                scale(particleSize / 1.5F).element(new Firebending()).fade(AvatarUtils.getRandomNumberInRange(75, 200), AvatarUtils.getRandomNumberInRange(1, 180),
                                AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                    } else {
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                                world.rand.nextGaussian() / 40).clr(255, 15, 5).collide(false).
                                scale(particleSize / 1.5F).element(new Firebending()).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                                world.rand.nextGaussian() / 40).clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(false).
                                scale(particleSize / 1.5F).element(new Firebending()).spawn(world);
                    }
                }

        } else return true;
        if (usage - StatCtrlFlameStrike.getTimesUsed(entity.getPersistentID()) <= 0) {
            data.removeStatusControl(FLAME_STRIKE_MAIN);
            data.removeStatusControl(FLAME_STRIKE_OFF);
            return true;
        }
        if (data.getTickHandlerDuration(this) > 240) {
            data.removeStatusControl(FLAME_STRIKE_MAIN);
            data.removeStatusControl(FLAME_STRIKE_OFF);
            return true;
        }
        return false;
    }

}
