package com.crowsofwar.avatar.bending.bending.combustion.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.combustion.AbilityCombustionAura;
import com.crowsofwar.avatar.bending.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.bending.bending.custom.light.Lightbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CombustionAuraHandler extends TickHandler {

    public CombustionAuraHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData aD = data.getAbilityData("combustion_aura");
        World world = ctx.getWorld();

        AbilityCombustionAura combustion = (AbilityCombustionAura) Abilities.get("combustion_aura");
        float scale = 0.75F + Math.max(0, aD.getLevel()) * 0.125F;

        assert combustion != null;
        int purifyDuration = combustion.getProperty(Ability.DURATION, aD).intValue();

        int r, g, b, fadeR, fadeG, fadeB;
        r = combustion.getProperty(Ability.R, aD).intValue();
        g = combustion.getProperty(Ability.G, aD).intValue();
        b = combustion.getProperty(Ability.B, aD).intValue();
        fadeR = combustion.getProperty(Ability.FADE_R, aD).intValue();
        fadeG = combustion.getProperty(Ability.FADE_G, aD).intValue();
        fadeB = combustion.getProperty(Ability.FADE_B, aD).intValue();

        scale *= (float) aD.getDamageMult() * aD.getXpModifier();

        //Maybe use swirls instead???

        if (entity.ticksExisted % AvatarUtils.getRandomNumberInRange(1, 20) == 0) {
            world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,
                    1, 1, true);
            if (world.isRemote) {
                int rRandom = AvatarUtils.getRandomNumberInRange((int) (fadeR * 0.75), fadeR * 2);
                int gRandom = AvatarUtils.getRandomNumberInRange((int) (fadeG * 0.75), fadeG * 2);
                int bRandom = AvatarUtils.getRandomNumberInRange((int) (fadeB * 0.75), fadeB * 2);
                Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity);
                pos = entity.onGround ? pos.add(0, entity.getEyeHeight(), 0) : pos.add(0, entity.getEyeHeight() / 2, 0);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).time(25 + AvatarUtils.getRandomNumberInRange(1, 2)).
                        clr(r / 255F, g / 255F, b / 255F, 0.05F).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(10, 40))
                        .element(BendingStyles.get(Combustionbending.ID)).scale(scale).glow(true).swirl((int) (purifyDuration / 20 * scale),
                                (int) (scale * Math.PI), scale, scale / 2, purifyDuration * 20, 3 * (0.75F / scale),
                                entity, world, true, pos,
                                ParticleBuilder.SwirlMotionType.OUT, true, true);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).time(25 + AvatarUtils.getRandomNumberInRange(1, 2)).
                        clr(r / 255F, g / 255F, b / 255F, 0.05F).fade(250, 120, 20, AvatarUtils.getRandomNumberInRange(10, 40))
                        .element(BendingStyles.get(Combustionbending.ID)).scale(scale).glow(true).swirl((int) (purifyDuration / 20 * scale),
                                (int) (scale * Math.PI), scale, scale / 2, purifyDuration * 20, 3 * (0.75F / scale),
                                entity, world, true, pos,
                                ParticleBuilder.SwirlMotionType.OUT, true, true);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).time(25 + AvatarUtils.getRandomNumberInRange(1, 2)).
                        clr(r / 255F, g / 255F, b / 255F, 0.05F).fade(10, 10, 10, AvatarUtils.getRandomNumberInRange(10, 40))
                        .element(BendingStyles.get(Lightbending.ID)).scale(scale).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 95).swirl((int) (purifyDuration / 20 * scale),
                                (int) (scale * Math.PI), scale, scale / 2, purifyDuration * 20,  3 * (0.75F / scale),
                                entity, world, true, pos,
                                ParticleBuilder.SwirlMotionType.OUT, true, true);
            }

            //The particles take a while to disappear after the ability finishes- so you decrease the time the particles can spawn
            if (world.isRemote) {
                for (int i = 0; i < 10 + Math.max(aD.getLevel(), 1) * 2; i++) {
                    int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                            fadeR * 2);
                    int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                            fadeG * 2);
                    int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                            fadeB * 2);

                    double random = world.rand.nextGaussian();
                    double radius = world.rand.nextDouble() * 0.2F * Math.max(aD.getLevel(), 0);
                    Vector location = Vector.toRectangular(Math.toRadians(entity.rotationYaw + (i * 30) + (random * 2)), 0).times(radius).withY(entity.getEyeHeight() - 0.7);
                    //Temporary solution to colour fading: randomising the colour between crimson and orangey-yellow for each particle.
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(location.plus(Vector.getEntityPos(entity)).toMinecraft()).time(4 + AvatarUtils.getRandomNumberInRange(1, 4)).
                            vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 2, world.rand.nextGaussian() / 40)
                            .clr(r, g, b, 150).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(50, 140))
                            .element(BendingStyles.get(Lightbending.ID)).scale(scale).glow(true).spawn(world);
                }
            }
        }
        return false;//duration >= purifyDuration;
    }
}

