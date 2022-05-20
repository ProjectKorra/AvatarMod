package com.crowsofwar.avatar.bending.bending.custom.abyss.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.abyss.AbilityAbyssalForm;
import com.crowsofwar.avatar.bending.bending.custom.abyss.Abyssbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class AbyssFormHandler extends TickHandler {

    public AbyssFormHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData aD = data.getAbilityData("abyss_form");
        World world = ctx.getWorld();

        AbilityAbyssalForm hyperForm = (AbilityAbyssalForm) Abilities.get("abyss_form");
        int duration = data.getTickHandlerDuration(this);
        float scale = 1F;

        assert hyperForm != null;
        int kaioKenDuration = hyperForm.getProperty(Ability.DURATION, aD).intValue();

        int r, g, b, fadeR, fadeG, fadeB;
        r = hyperForm.getProperty(Ability.R, aD).intValue();
        g = hyperForm.getProperty(Ability.G, aD).intValue();
        b = hyperForm.getProperty(Ability.B, aD).intValue();
        fadeR = hyperForm.getProperty(Ability.FADE_R, aD).intValue();
        fadeG = hyperForm.getProperty(Ability.FADE_G, aD).intValue();
        fadeB = hyperForm.getProperty(Ability.FADE_B, aD).intValue();

        scale *= (float) aD.getDamageMult() * aD.getXpModifier();

        if (world.isRemote) {
            int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                    fadeR * 2);
            int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                    fadeG * 2);
            int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                    fadeB * 2);
            Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity);
            pos = entity.onGround ? pos.add(0, entity.getEyeHeight(), 0) : pos.add(0, entity.getEyeHeight() / 2, 0);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).time(25 + AvatarUtils.getRandomNumberInRange(1, 2)).
                    clr(r, g, b, 10).fade(getClrRand(), getClrRand(), getClrRand(), AvatarUtils.getRandomNumberInRange(40, 90))
                    .element(BendingStyles.get(Abyssbending.ID)).scale(scale).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 98).swirl((int) (kaioKenDuration / 20 * scale),
                            (int) (scale * Math.PI), scale, scale / 2, kaioKenDuration * 20, (1.5F / scale),
                            entity, world, true, pos,
                            ParticleBuilder.SwirlMotionType.OUT, false, true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).time(25 + AvatarUtils.getRandomNumberInRange(1, 2)).
                    clr(r, g, b, 10).fade(getClrRand(), getClrRand(), getClrRand(), AvatarUtils.getRandomNumberInRange(49, 90))
                    .element(BendingStyles.get(Abyssbending.ID)).scale(scale).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 96).swirl((int) (kaioKenDuration / 20 * scale),
                            (int) (scale * Math.PI), scale, scale / 2, kaioKenDuration * 20, (1.5F / scale),
                            entity, world, true, pos,
                            ParticleBuilder.SwirlMotionType.OUT, false, true);
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
                        .clr(r, g, b, 150).fade(getClrRand(), getClrRand(), getClrRand(), AvatarUtils.getRandomNumberInRange(20, 40))
                        .scale(scale * 1.5F).glow(world.rand.nextBoolean()).spawn(world);
            }
        }
        return false;// duration >= kaioKenDuration;
    }

    private int getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 20);
    }
}

