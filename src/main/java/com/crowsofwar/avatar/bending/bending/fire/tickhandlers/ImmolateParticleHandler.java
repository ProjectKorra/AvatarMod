package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityImmolate;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;


public class ImmolateParticleHandler extends TickHandler {
    private final ParticleSpawner particles;

    public ImmolateParticleHandler(int id) {
        super(id);
        particles = new NetworkParticleSpawner();
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData aD = data.getAbilityData("immolate");
        World world = ctx.getWorld();

        AbilityImmolate immolate = (AbilityImmolate) Abilities.get("immolate");
        int duration = data.getTickHandlerDuration(this);
        float scale = 1.0F + Math.max(0, aD.getLevel()) * 0.25F;

        assert immolate != null;
        int immolateDuration = immolate.getProperty(Ability.DURATION, aD).intValue();

		int r, g, b, fadeR, fadeG, fadeB;
		r = immolate.getProperty(Ability.FIRE_R, aD).intValue();
		g = immolate.getProperty(Ability.FIRE_G, aD).intValue();
		b = immolate.getProperty(Ability.FIRE_B, aD).intValue();
		fadeR = immolate.getProperty(Ability.FADE_R, aD).intValue();
		fadeG = immolate.getProperty(Ability.FADE_G, aD).intValue();
		fadeB = immolate.getProperty(Ability.FADE_B, aD).intValue();

        scale *= (float) aD.getDamageMult() * aD.getXpModifier();

        //The particles take a while to disappear after the ability finishes- so you decrease the time the particles can spawn
        if (world.isRemote) {
            for (int i = 0; i < 12 + Math.max(aD.getLevel(), 1) * 4; i++) {
				int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
						fadeR * 2);
				int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
						fadeG * 2);
				int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
						fadeB * 2);

                double random = world.rand.nextGaussian();
                Vector location = Vector.toRectangular(Math.toRadians(entity.rotationYaw + (i * 30) + (random * 2)), 0).times(aD.getLevel() < 1 ? 0.25 : aD.getLevel() * 0.25).withY(entity.getEyeHeight() - 0.7);
                //Temporary solution to colour fading: randomising the colour between crimson and orangey-yellow for each particle.
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(location.plus(Vector.getEntityPos(entity)).toMinecraft()).time(4 + AvatarUtils.getRandomNumberInRange(1, 4)).
                        vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 2, world.rand.nextGaussian() / 40)
                        .clr(r, g, b, 150).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(50, 140))
                        .element(new Firebending()).scale(scale).spawn(world);
            }
        }
        return duration >= immolateDuration;
    }
}

