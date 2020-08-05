package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike.STRIKES;
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
        AbilityFlameStrike strike = (AbilityFlameStrike) Abilities.get(new AbilityFlameStrike().getName());

        int usage = strike.getProperty(STRIKES, abilityData).intValue();
        int particleCount = 1;
        int level = abilityData.getLevel();
        float particleSize = 0.7F;
        int r, g, b, fadeR, fadeG, fadeB;
        r = strike.getProperty(FIRE_R, abilityData).intValue();
        g = strike.getProperty(FIRE_G, abilityData).intValue();
        b = strike.getProperty(FIRE_B, abilityData).intValue();
        fadeR = strike.getProperty(FADE_R, abilityData).intValue();
        fadeG = strike.getProperty(FADE_G, abilityData).intValue();
        fadeB = strike.getProperty(FADE_B, abilityData).intValue();

        if (level == 1 || level == 2) {
            particleCount = 2;
        }

        if (level == 1) {
            particleSize = 0.8F;
        }
        if (level == 2) {
            particleSize = 0.875F;
        }

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

        particleSize *= abilityData.getDamageMult() * abilityData.getXpModifier();
         if ((data.hasStatusControl(FLAME_STRIKE_MAIN) || data.hasStatusControl(FLAME_STRIKE_OFF))) {

            Vec3d height, rightSide;
            if (entity instanceof EntityPlayer) {
                if (!AvatarMod.realFirstPersonRender2Compat && (PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) <= -1)) {
                    height = entity.getPositionVector().add(0, 1.5, 0);
                    height = height.add(entity.getLookVec().scale(0.8));
                    //Right
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT && data.hasStatusControl(FLAME_STRIKE_MAIN)
                            || entity.getPrimaryHand() == EnumHandSide.LEFT && data.hasStatusControl(FLAME_STRIKE_OFF)) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
                    }
                    //Left
                    else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
                    }
                    rightSide = rightSide.add(height);
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
                    //140, 90, 90
                    int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                            fadeR * 2);
                    int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                            fadeG * 2);
                    int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                            fadeB * 2);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                                world.rand.nextGaussian() / 40).clr(r, g, b).collide(true).
                                scale(particleSize / 2F).element(new Firebending()).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(20, 100))
                                .ability(strike).spawnEntity(entity).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                                world.rand.nextGaussian() / 40).clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true).
                                scale(particleSize / 2F).element(new Firebending()).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(20, 100))
                                .ability(strike).spawnEntity(entity).spawn(world);
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
