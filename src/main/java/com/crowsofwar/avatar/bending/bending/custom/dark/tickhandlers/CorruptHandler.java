package com.crowsofwar.avatar.bending.bending.custom.dark.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.custom.dark.AbilityCorrupt;
import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
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


public class CorruptHandler extends TickHandler {

    public CorruptHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData aD = data.getAbilityData("corrupt");
        World world = ctx.getWorld();

        AbilityCorrupt corrupt = (AbilityCorrupt) Abilities.get("corrupt");
        int duration = data.getTickHandlerDuration(this);
        float scale = 0.75F + Math.max(0, aD.getLevel()) * 0.125F;

        assert corrupt != null;
        int corruptDuration = corrupt.getProperty(Ability.DURATION, aD).intValue();

		int r, g, b, fadeR, fadeG, fadeB;
		r = corrupt.getProperty(Ability.R, aD).intValue();
		g = corrupt.getProperty(Ability.G, aD).intValue();
		b = corrupt.getProperty(Ability.B, aD).intValue();
		fadeR = corrupt.getProperty(Ability.FADE_R, aD).intValue();
		fadeG = corrupt.getProperty(Ability.FADE_G, aD).intValue();
		fadeB = corrupt.getProperty(Ability.FADE_B, aD).intValue();

        scale *= (float) aD.getDamageMult() * aD.getXpModifier();

        //Maybe use swirls instead???



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
                        .element(new Darkbending()).scale(scale).spawn(world);
            }
        }
        return duration >= corruptDuration;
    }
}
