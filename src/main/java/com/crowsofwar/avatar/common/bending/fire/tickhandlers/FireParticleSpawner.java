package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireJump;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class FireParticleSpawner extends TickHandler {
    private static final ParticleSpawner particles = new NetworkParticleSpawner();

    public FireParticleSpawner(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase target = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData data = ctx.getData().getAbilityData(new AbilityFireJump());
        AbilityFireJump jump = (AbilityFireJump) Abilities.get(new AbilityFireJump().getName());
        Vector pos = Vector.getEntityPos(target).minusY(0.05);

        if (world.isRemote)
            for (int i = 0; i < 2 + AvatarUtils.getRandomNumberInRange(0, 4); i++) {
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 10,
                        5).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40).scale(1F +
                        Math.max(data.getLevel(), 0) / 2F).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Firebending()).collide(true)
                        .ability(jump).spawnEntity(target).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 40 + AvatarUtils.getRandomNumberInRange(0, 60),
                        10).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40)
                        .scale(1F + Math.max(data.getLevel(), 0) / 2F).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Firebending()).collide(true)
                        .ability(jump).spawnEntity(target).spawn(world);
            }

        //particles.spawnParticles(world, world.rand.nextBoolean() ? AvatarParticles.getParticleFlames() : AvatarParticles.getParticleFire(),
        //		4, 16, pos, new Vector(0.7, 0.2, 0.7), true);

        return target.isInWater() || target.onGround || bender.isFlying();

    }

}

